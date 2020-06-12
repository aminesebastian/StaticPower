package theking530.staticpower.tileentities.cables.power;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.components.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.network.CableNetworkManager;
import theking530.staticpower.tileentities.network.factories.cables.CableTypes;
import theking530.staticpower.tileentities.network.factories.modules.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.network.modules.PowerNetworkModule;

public class PowerCableComponent extends AbstractCableProviderComponent implements IEnergyStorage {
	private PowerNetworkModule networkModule;

	public PowerCableComponent(String name) {
		super(name, CableTypes.BASIC_POWER);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!getTileEntity().getWorld().isRemote) {
			return networkModule.getEnergyStorage().receiveEnergy(maxReceive, simulate);
		} else {
			return 0;
		}
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		if (!getTileEntity().getWorld().isRemote) {
			return networkModule.getEnergyStorage().getEnergyStored();
		} else {
			return 0;
		}
	}

	@Override
	public int getMaxEnergyStored() {
		if (!getTileEntity().getWorld().isRemote) {
			return networkModule.getEnergyStorage().getMaxEnergyStored();
		} else {
			return 0;
		}
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Cache the network module lazily if we haven't already.
		if (networkModule == null && !getTileEntity().getWorld().isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
			AbstractCableWrapper cable = manager.getCable(getTileEntity().getPos());
			if (cable instanceof PowerCableWrapper) {
				PowerCableWrapper powerCable = (PowerCableWrapper) cable;
				try {
					networkModule = powerCable.getNetwork().getModule(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}

			}
		}

		if (cap == CapabilityEnergy.ENERGY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	protected void updateConnectionStates() {
		for (Direction dir : Direction.values()) {
			BlockPos position = getTileEntity().getPos().offset(dir);
			AbstractCableProviderComponent overProvider = CableUtilities.getCableWrapperComponent(getWorld(), position);
			if (overProvider != null && overProvider.getCableType() == getCableType()) {
				ConnectionStates[dir.ordinal()] = CableConnectionState.CABLE;
			} else if (getWorld().getTileEntity(position) != null) {
				TileEntity te = getWorld().getTileEntity(position);
				if (te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).isPresent()) {
					ConnectionStates[dir.ordinal()] = CableConnectionState.TILE_ENTITY;
				}
			} else {
				ConnectionStates[dir.ordinal()] = CableConnectionState.NONE;
			}
		}
	}
}
