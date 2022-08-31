package theking530.staticpower.cables.heat;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.IHeatStorage;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

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
		super(name, CableNetworkModuleTypes.HEAT_NETWORK_MODULE);
		this.capacity = capacity;
		this.transferRate = conductivity;
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (!getLevel().isClientSide) {
			this.<HeatNetworkModule>getNetworkModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE).ifPresent(network -> {
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
		if (!getLevel().isClientSide) {
			this.<HeatNetworkModule>getNetworkModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE).ifPresent(network -> {
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
	protected boolean canAttachAttachment(ItemStack attachment) {
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
		return getNetworkModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE);
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		cable.setProperty(HEAT_CAPACITY_DATA_TAG_KEY, capacity);
		cable.setProperty(HEAT_CONDUCTIVITY_TAG_KEY, transferRate);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getLevel(), blockPosition);
		if (otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionState.CABLE;
			}
		} else if (te != null && otherProvider == null) {
			if (te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).isPresent()) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityHeatable.HEAT_STORAGE_CAPABILITY) {
			boolean disabled = false;
			if (side != null) {
				if (getLevel().isClientSide) {
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
