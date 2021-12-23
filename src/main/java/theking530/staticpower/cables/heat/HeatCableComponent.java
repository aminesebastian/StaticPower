package theking530.staticpower.cables.heat;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.AtomicDouble;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
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
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class HeatCableComponent extends AbstractCableProviderComponent implements IHeatStorage {
	public static final String HEAT_CAPACITY_DATA_TAG_KEY = "heat_capacity";
	public static final String HEAT_CONDUCTIVITY_TAG_KEY = "heat_transfer_rate";
	public static final String HEAT_GENERATION_RATE = "heat_generation";
	public static final String HEAT_GENERATION_POWER_USAGE = "heat_generation_power_cost";
	private final double capacity;
	private final double transferRate;
	@UpdateSerialize
	private double clientSideHeat;
	@UpdateSerialize
	private double clientSideHeatCapacity;
	@UpdateSerialize
	private double heatGeneration;
	@UpdateSerialize
	private int heatGenerationPowerUsage;
	private EnergyStorageComponent energyStorageComponent;

	public HeatCableComponent(String name, double capacity, double transferRate) {
		this(name, capacity, transferRate, 0.0f, 0);
	}

	public HeatCableComponent(String name, double capacity, double transferRate, double heatGeneration, int heatGenerationPowerUsage) {
		super(name, CableNetworkModuleTypes.HEAT_NETWORK_MODULE);
		this.capacity = capacity;
		this.transferRate = transferRate;
		this.heatGeneration = heatGeneration;
		this.heatGenerationPowerUsage = heatGenerationPowerUsage;
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (!getWorld().isClientSide) {
			this.<HeatNetworkModule>getNetworkModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE).ifPresent(network -> {
				boolean shouldUpdate = Math.abs(network.getHeatStorage().getCurrentHeat() - clientSideHeat) >= HeatStorageComponent.HEAT_SYNC_MAX_DELTA;
				shouldUpdate |= network.getHeatStorage().getMaximumHeat() != clientSideHeatCapacity;
				shouldUpdate |= clientSideHeat == 0 && network.getHeatStorage().getCurrentHeat() > 0;
				shouldUpdate |= clientSideHeat > 0 && network.getHeatStorage().getCurrentHeat() == 0;
				if (shouldUpdate) {
					updateClientValues();
				}
			});
		}
	}

	public void updateClientValues() {
		if (!getWorld().isClientSide) {
			this.<HeatNetworkModule>getNetworkModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE).ifPresent(network -> {
				clientSideHeat = network.getHeatStorage().getCurrentHeat();
				clientSideHeatCapacity = network.getHeatStorage().getMaximumHeat();

				// Only send the packet to nearby players since these packets get sent
				// frequently.
				HeatCableUpdatePacket packet = new HeatCableUpdatePacket(getPos(), clientSideHeat, clientSideHeatCapacity);
				StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 128, packet);
			});
		}
	}

	public HeatCableComponent setEnergyStorageComponent(EnergyStorageComponent energyComponent) {
		energyStorageComponent = energyComponent;
		return this;
	}

	public void generateHeat(HeatNetworkModule networkModule) {
		if (!getWorld().isClientSide) {
			if (energyStorageComponent != null && heatGeneration != 0.0f) {
				double transferableHeat = networkModule.getHeatStorage().getMaximumHeat() - networkModule.getHeatStorage().getCurrentHeat();
				transferableHeat = Math.min(transferableHeat, heatGeneration);

				long maxPowerUsage = Math.min(heatGenerationPowerUsage, energyStorageComponent.getStorage().getStoredPower());
				long powerUsage = (int) Math.max(1, maxPowerUsage * (transferableHeat / heatGeneration));
				if (energyStorageComponent.hasEnoughPower(powerUsage)) {
					energyStorageComponent.useBulkPower(powerUsage);
					networkModule.getHeatStorage().heat(transferableHeat, false);
				}
			}
		}
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	public double getCurrentHeat() {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicDouble recieve = new AtomicDouble(0);
			getHeatNetworkModule().ifPresent(module -> {
				recieve.set(module.getHeatStorage().getCurrentHeat());
			});
			return recieve.get();
		} else {
			return clientSideHeat;
		}
	}

	@Override
	public double getMaximumHeat() {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicDouble recieve = new AtomicDouble(0);
			getHeatNetworkModule().ifPresent(module -> {
				recieve.set(module.getHeatStorage().getMaximumHeat());
			});
			return recieve.get();
		} else {
			return clientSideHeatCapacity;
		}
	}

	@Override
	public double getConductivity() {
		return transferRate;
	}

	@Override
	public double heat(double amountToHeat, boolean simulate) {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicDouble recieve = new AtomicDouble(0);
			getHeatNetworkModule().ifPresent(module -> {
				recieve.set(module.getHeatStorage().heat(amountToHeat, simulate));
			});
			return (float) recieve.get();
		} else {
			return 0;
		}
	}

	@Override
	public double cool(double amountToCool, boolean simulate) {
		if (!getTileEntity().getLevel().isClientSide) {
			AtomicDouble recieve = new AtomicDouble(0);
			getHeatNetworkModule().ifPresent(module -> {
				recieve.set(module.getHeatStorage().cool(amountToCool, simulate));
			});
			return (float) recieve.get();
		} else {
			return 0;
		}
	}

	public void updateFromNetworkUpdatePacket(double clientHeat, double clientCapacity) {
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
		cable.setProperty(HEAT_GENERATION_RATE, heatGeneration);
		cable.setProperty(HEAT_GENERATION_POWER_USAGE, heatGenerationPowerUsage);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
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
				if (getWorld().isClientSide) {
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
