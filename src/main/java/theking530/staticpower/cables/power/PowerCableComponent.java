package theking530.staticpower.cables.power;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class PowerCableComponent extends AbstractCableProviderComponent implements IEnergyStorage {
	public PowerCableComponent(String name) {
		super(name, CableNetworkModuleTypes.POWER_NETWORK_MODULE);
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
		ServerCable cable = manager.getCable(getTileEntity().getPos());
		if (cable.getNetwork() != null) {
			return Optional.of(cable.getNetwork().getModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE));
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
	protected CableConnectionState cacheConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null && otherProvider.shouldConnectionToCable(this, side)) {
			return CableConnectionState.CABLE;
		} else if (te != null && otherProvider == null) {
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
