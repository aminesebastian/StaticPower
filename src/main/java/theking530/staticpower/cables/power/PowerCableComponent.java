package theking530.staticpower.cables.power;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class PowerCableComponent extends AbstractCableProviderComponent implements IEnergyStorage, IStaticVoltHandler {
	public static final String POWER_CAPACITY_DATA_TAG_KEY = "power_capacity";
	public static final String POWER_RATE_DATA_TAG_KEY = "power_transfer_rate";
	private final int capacity;
	private final int transferRate;

	public PowerCableComponent(String name, int capacity, int transferRate) {
		super(name, CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		this.capacity = capacity;
		this.transferRate = transferRate;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return recievePower(maxReceive, simulate, true);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyStorage().getEnergyStored();
			}
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyStorage().getMaxEnergyStored();
			}
		}
		return 0;
	}

	@Override
	public boolean canExtract() {
		return canDrainPower();
	}

	@Override
	public boolean canReceive() {
		return canRecievePower();
	}

	@Override
	public int getStoredPower() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyStorage().getStoredPower();
			}
		}
		return 0;
	}

	@Override
	public int getCapacity() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyStorage().getCapacity();
			}
		}
		return 0;
	}

	@Override
	public int receivePower(int power, boolean simulate) {
		return recievePower(power, simulate, false);
	}

	private int recievePower(int power, boolean simulate, boolean forge) {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				if (forge) {
					return module.getEnergyStorage().receiveEnergy(Math.min(transferRate * IStaticVoltHandler.FE_TO_SV_CONVERSION, power), simulate);
				} else {
					return module.getEnergyStorage().receivePower(Math.min(transferRate, power), simulate);
				}
			}
		}
		return 0;
	}

	@Override
	public int drainPower(int power, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canRecievePower() {
		return getPowerNetworkModule() != null;
	}

	@Override
	public boolean canDrainPower() {
		return false;
	}

	/**
	 * Gets the power network module for the network this cable belongs to. We have
	 * to wrap it in an optional because while we can guarantee once this component
	 * is validated that the network is valid, since this component exposes external
	 * methods, other tile entity that are made valid before us may call some of our
	 * methods.
	 * 
	 * @return
	 */
	protected Optional<PowerNetworkModule> getPowerNetworkModule() {
		CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
		ServerCable cable = manager.getCable(getTileEntity().getPos());
		if (cable.getNetwork() != null) {
			return Optional.of(cable.getNetwork().getModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE));
		}
		return Optional.empty();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityEnergy.ENERGY || cap == CapabilityStaticVolt.STATIC_VOLT_CAPABILITY) {
			boolean disabled = false;
			if (side != null) {
				if (getWorld().isRemote) {
					disabled = isSideDisabled(side);
				} else {
					ServerCable cable = CableNetworkManager.get(getWorld()).getCable(getPos());
					disabled = cable.isDisabledOnSide(side);
				}
			}

			if (!disabled) {
				return LazyOptional.of(() -> this).cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		cable.setProperty(POWER_CAPACITY_DATA_TAG_KEY, capacity);
		cable.setProperty(POWER_RATE_DATA_TAG_KEY, transferRate);
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionState.CABLE;
			}
		}
		if (te != null) {
			if (te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).isPresent()) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

}
