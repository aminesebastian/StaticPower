package theking530.staticpower.cables.power;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
	public static final String POWER_INDUSTRIAL_DATA_TAG_KEY = "power_cable_industrial";

	private final long capacity;
	private final long transferRate;
	private final boolean isIndustrial;

	private long clientCurrentPower;
	private long clientCapacity;
	private long clientMaxReceive;
	private long clientMaxDrain;
	private float clientLastTickReceive;
	private float clientLastTickDraint;

	public PowerCableComponent(String name, boolean isIndustrial, long capacity, long transferRate) {
		super(name, CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		this.capacity = capacity;
		this.transferRate = transferRate;
		this.isIndustrial = isIndustrial;
		this.clientCurrentPower = 0;
		this.clientCapacity = 0;
		this.clientMaxReceive = 0;
		this.clientMaxDrain = 0;
		this.clientLastTickReceive = 0;
		this.clientLastTickDraint = 0;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return (int) recievePower(maxReceive, simulate, true);
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
				return module.getEnergyAutoConverter().getEnergyStored();
			}
		}
		return CapabilityStaticVolt.convertmSVtoFE(clientCurrentPower);
	}

	@Override
	public int getMaxEnergyStored() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyAutoConverter().getMaxEnergyStored();
			}
		}
		return CapabilityStaticVolt.convertmSVtoFE(clientCapacity);
	}

	public float getClientLastEnergyDrain() {
		return this.clientLastTickDraint;
	}

	public float getClientLastEnergyReceieve() {
		return this.clientLastTickReceive;
	}

	@Override
	public boolean canExtract() {
		return canBeDrained();
	}

	@Override
	public boolean canReceive() {
		return canRecievePower();
	}

	@Override
	public long getMaxReceive() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyAutoConverter().getMaxReceive();
			}
		}
		return clientMaxReceive;
	}

	@Override
	public long getMaxDrain() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyAutoConverter().getMaxDrain();
			}
		}
		return clientMaxDrain;
	}

	@Override
	public long getStoredPower() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyAutoConverter().getStoredPower();
			}
		}
		return clientCurrentPower;
	}

	@Override
	public long getCapacity() {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyAutoConverter().getCapacity();
			}
		}
		return clientCapacity;
	}

	@Override
	public long receivePower(long power, boolean simulate) {
		return recievePower(power, simulate, false);
	}

	private long recievePower(long power, boolean simulate, boolean forge) {
		if (!getTileEntity().getWorld().isRemote) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				if (forge) {
					return module.getEnergyAutoConverter().receiveEnergy((int) Math.min(CapabilityStaticVolt.convertmSVtoFE(transferRate), power), simulate);
				} else {
					return module.getEnergyAutoConverter().receivePower(Math.min(transferRate, power), simulate);
				}
			}
		}
		return 0;
	}

	@Override
	public long drainPower(long power, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canRecievePower() {
		return getPowerNetworkModule() != null;
	}

	@Override
	public boolean canBeDrained() {
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
		cable.setProperty(POWER_INDUSTRIAL_DATA_TAG_KEY, isIndustrial);
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		if (!this.getWorld().isRemote) {
			getPowerNetworkModule().ifPresent(module -> {
				CompoundNBT powerCableNBT = new CompoundNBT();
				powerCableNBT.putLong("power", module.getEnergyStorage().getStoredPower());
				powerCableNBT.putLong("capacity", module.getEnergyStorage().getCapacity());
				powerCableNBT.putFloat("max_drain", module.getEnergyStorage().getMaxDrain());
				powerCableNBT.putFloat("max_receive", module.getEnergyStorage().getMaxReceive());
				powerCableNBT.putFloat("drained", module.getEnergyStorage().getExtractedPerTick());
				powerCableNBT.putFloat("received", module.getEnergyStorage().getReceivedPerTick());
				nbt.put("power_cable", powerCableNBT);
			});
		}
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		if (nbt.contains("power_cable")) {
			CompoundNBT powerCableNBT = nbt.getCompound("power_cable");
			clientCurrentPower = powerCableNBT.getLong("power");
			clientCapacity = powerCableNBT.getLong("capacity");
			clientLastTickDraint = powerCableNBT.getFloat("drained");
			clientLastTickReceive = powerCableNBT.getFloat("received");
			clientMaxDrain = powerCableNBT.getInt("max_drain");
			clientMaxReceive = powerCableNBT.getInt("max_recieve");
		}
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
