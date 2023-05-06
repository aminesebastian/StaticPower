package theking530.staticpower.cables.heat;

import java.util.Optional;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatTicker;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class HeatCableComponent extends AbstractCableProviderComponent implements IHeatStorage {
	public static final String HEAT_CAPACITY_DATA_TAG_KEY = "heat_capacity";
	public static final String HEAT_CONDUCTIVITY_TAG_KEY = "heat_transfer_rate";
	private final float capacity;
	private final float transferRate;
	@UpdateSerialize
	private float clientSideHeat;
	@UpdateSerialize
	private float clientSideHeatCapacity;

	public HeatCableComponent(String name, float capacity, float conductivity) {
		super(name, ModCableModules.Heat.get());
		this.capacity = capacity;
		this.transferRate = conductivity;
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (!isClientSide()) {
			this.<HeatNetworkModule>getNetworkModule(ModCableModules.Heat.get()).ifPresent(network -> {
				boolean shouldUpdate = Math.abs(network.getHeatStorage().getCurrentHeat()
						- clientSideHeat) >= HeatStorageComponent.HEAT_SYNC_MAX_DELTA;
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
				HeatCableUpdatePacket packet = new HeatCableUpdatePacket(getPos(), clientSideHeat,
						clientSideHeatCapacity);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL,
						getLevel(), getPos(), 32, packet);
			});
		}
	}

	@Override
	public boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	public float getCurrentHeat() {
		if (!getBlockEntity().getLevel().isClientSide) {
			return getHeatNetworkModule().isPresent() ? getHeatNetworkModule().get().getHeatStorage().getCurrentHeat()
					: 0.0f;
		} else {
			return clientSideHeat;
		}
	}

	@Override
	public float getMaximumHeat() {
		if (!getBlockEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().getMaximumHeat() : 0;
		} else {
			return clientSideHeatCapacity;
		}
	}

	@Override
	public float getOverheatThreshold() {
		if (!getBlockEntity().getLevel().isClientSide) {
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
	public float heat(float amountToHeat, HeatTransferAction action) {
		if (!getBlockEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().heat(amountToHeat, action) : 0;
		} else {
			return 0;
		}
	}

	@Override
	public float cool(float amountToCool, HeatTransferAction action) {
		if (!getBlockEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().cool(amountToCool, action) : 0;
		} else {
			return 0;
		}
	}

	@Override
	public HeatTicker getTicker() {
		if (!getBlockEntity().getLevel().isClientSide) {
			HeatNetworkModule module = getHeatNetworkModule().orElse(null);
			return module != null ? module.getHeatStorage().getTicker() : null;
		} else {
			return null;
		}
	}

	public void updateFromNetworkUpdatePacket(float clientHeat, float clientCapacity) {
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
	protected void initializeCableProperties(Cable cable, BlockPlaceContext context, BlockState state,
			LivingEntity placer, ItemStack stack) {
		super.initializeCableProperties(cable, context, state, placer, stack);
		cable.getDataTag().putFloat(HEAT_CAPACITY_DATA_TAG_KEY, capacity);
		cable.getDataTag().putFloat(HEAT_CONDUCTIVITY_TAG_KEY, transferRate);
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Heat.get());
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityHeatable.HEAT_STORAGE_CAPABILITY) {
			if (side == null || !isSideDisabled(side)) {
				// On the client, return this. On the server, return the heat network's storage.
				if (isClientSide()) {
					return LazyOptional.of(() -> this).cast();
				} else if (getHeatNetworkModule().isPresent()) {
					return LazyOptional.of(() -> getHeatNetworkModule().get().getHeatStorage()).cast();
				}
			}
		}
		return LazyOptional.empty();
	}

}
