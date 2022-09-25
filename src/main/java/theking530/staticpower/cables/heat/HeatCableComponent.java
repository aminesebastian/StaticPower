package theking530.staticpower.cables.heat;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticpower.blockentities.components.heat.HeatStorageComponent;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.init.ModCableDestinations;
import theking530.staticpower.init.ModCableModules;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class HeatCableComponent extends AbstractCableProviderComponent implements IHeatStorage {
	public static final String HEAT_CAPACITY_DATA_TAG_KEY = "heat_capacity";
	public static final String HEAT_CONDUCTIVITY_TAG_KEY = "heat_transfer_rate";
	private final double capacity;
	private final float transferRate;
	@UpdateSerialize
	private int clientSideHeat;
	@UpdateSerialize
	private int clientSideHeatCapacity;

	public HeatCableComponent(String name, int capacity, float conductivity) {
		super(name, ModCableModules.Heat.get());
		this.capacity = capacity;
		this.transferRate = conductivity;
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (!isClientSide()) {
			this.<HeatNetworkModule>getNetworkModule(ModCableModules.Heat.get()).ifPresent(network -> {
				boolean shouldUpdate = Math.abs(network.getHeatStorage().getCurrentHeat() - clientSideHeat) >= HeatStorageComponent.HEAT_SYNC_MAX_DELTA;
				shouldUpdate |= network.getHeatStorage().getOverheatThreshold() != clientSideHeatCapacity;
				shouldUpdate |= clientSideHeat == 0 && network.getHeatStorage().getCurrentHeat() > 0;
				shouldUpdate |= clientSideHeat > 0 && network.getHeatStorage().getCurrentHeat() == 0;
				if (shouldUpdate) {
					updateClientValues();
				}
			});
		}
	}

	public void updateClientValues() {
		if (!isClientSide()) {
			this.<HeatNetworkModule>getNetworkModule(ModCableModules.Heat.get()).ifPresent(network -> {
				clientSideHeat = network.getHeatStorage().getCurrentHeat();
				clientSideHeatCapacity = network.getHeatStorage().getOverheatThreshold();

				// Only send the packet to nearby players since these packets get sent
				// frequently.
				HeatCableUpdatePacket packet = new HeatCableUpdatePacket(getPos(), clientSideHeat, clientSideHeatCapacity);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 32, packet);
			});
		}
	}

	@Override
	public boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	public int getCurrentHeat() {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicInteger recieve = new AtomicInteger(0);
			getHeatNetworkModule().ifPresent(module -> {
				recieve.set(module.getHeatStorage().getCurrentHeat());
			});
			return recieve.get();
		} else {
			return clientSideHeat;
		}
	}

	@Override
	public int getMaximumHeat() {
		if (!getTileEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().getMaximumHeat() : 0;
		} else {
			return clientSideHeatCapacity;
		}
	}

	@Override
	public int getOverheatThreshold() {
		if (!getTileEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().getOverheatThreshold() : 0;
		} else {
			return clientSideHeatCapacity;
		}
	}

	@Override
	public float getConductivity() {
		return transferRate;
	}

	@Override
	public int heat(int amountToHeat, HeatTransferAction action) {
		if (!getTileEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().heat(amountToHeat, action) : 0;
		} else {
			return 0;
		}
	}

	@Override
	public int cool(int amountToCool, HeatTransferAction action) {
		if (!getTileEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().cool(amountToCool, action) : 0;
		} else {
			return 0;
		}
	}

	public void updateFromNetworkUpdatePacket(int clientHeat, int clientCapacity) {
		this.clientSideHeat = clientHeat;
		this.clientSideHeatCapacity = clientCapacity;
	}

	/**
	 * Gets the heat network module for the network this cable belongs to. We have
	 * to wrap it in an optional because while we can guarantee once this component
	 * is validated that the network is valid, since this component exposes external
	 * methods, other tile entities that are made valid before us may call some of
	 * our methods.
	 * 
	 * @return
	 */
	public Optional<HeatNetworkModule> getHeatNetworkModule() {
		return getNetworkModule(ModCableModules.Heat.get());
	}

	@Override
	protected void initializeCableProperties(ServerCable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		cable.getDataTag().putDouble(HEAT_CAPACITY_DATA_TAG_KEY, capacity);
		cable.getDataTag().putDouble(HEAT_CONDUCTIVITY_TAG_KEY, transferRate);
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Heat.get());
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityHeatable.HEAT_STORAGE_CAPABILITY) {
			boolean disabled = false;
			if (side != null) {
				if (isClientSide()) {
					disabled = isSideDisabled(side);
				} else {
					Optional<ServerCable> cable = getCable();
					disabled = cable.isPresent() ? cable.get().isDisabledOnSide(side) : true;
				}
			}

			if (!disabled) {
				return LazyOptional.of(() -> this).cast();
			}
		}
		return LazyOptional.empty();
	}
}
