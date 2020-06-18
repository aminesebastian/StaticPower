package theking530.staticpower.tileentities.cables.power;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.cables.network.CableNetworkManager;
import theking530.staticpower.tileentities.cables.network.factories.cables.CableTypes;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.cables.network.modules.PowerNetworkModule;

public class PowerCableComponent extends AbstractCableProviderComponent implements IEnergyStorage {
	public PowerCableComponent(String name) {
		super(name, CableTypes.BASIC_POWER);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicInteger recieve = new AtomicInteger(0);
			getPowerNetworkModule().ifPresent(PowerNetworkModule -> {
				recieve.set(PowerNetworkModule.getEnergyStorage().receiveEnergy(maxReceive, simulate));
			});
			return recieve.get();
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
			AtomicInteger recieve = new AtomicInteger(0);
			getPowerNetworkModule().ifPresent(PowerNetworkModule -> {
				recieve.set(PowerNetworkModule.getEnergyStorage().getEnergyStored());
			});
			return recieve.get();
		} else {
			return 0;
		}
	}

	@Override
	public int getMaxEnergyStored() {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicInteger recieve = new AtomicInteger(0);
			getPowerNetworkModule().ifPresent(PowerNetworkModule -> {
				recieve.set(PowerNetworkModule.getEnergyStorage().getMaxEnergyStored());
			});
			return recieve.get();
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
		return getPowerNetworkModule() != null;
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
		AbstractCableWrapper cable = manager.getCable(getTileEntity().getPos());
		if (cable instanceof PowerCableWrapper) {
			PowerCableWrapper powerCable = (PowerCableWrapper) cable;
			if (powerCable.getNetwork() != null) {
				return Optional.of(powerCable.getNetwork().getModule(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT));
			}
		}
		return Optional.empty();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, BlockPos blockPosition) {
		AbstractCableProviderComponent overProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (overProvider != null && overProvider.getCableType() == getCableType()) {
			return CableConnectionState.CABLE;
		} else if (getWorld().getTileEntity(blockPosition) != null) {
			TileEntity te = getWorld().getTileEntity(blockPosition);
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
