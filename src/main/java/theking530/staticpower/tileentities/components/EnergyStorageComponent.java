package theking530.staticpower.tileentities.components;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.common.utilities.TriFunction;
import theking530.staticpower.energy.StaticPowerFEStorage;

public class EnergyStorageComponent extends AbstractTileEntityComponent {
	public enum EnergyManipulationAction {
		PROVIDE, RECIEVE
	}

	protected StaticPowerFEStorage EnergyStorage;
	protected TriFunction<Integer, Direction, EnergyManipulationAction, Boolean> filter;
	protected int lastEnergyStored;
	protected int energyPerTick;
	protected long lastUpdateTime;
	private EnergyComponentCapabilityAccess capabilityAccessor;

	public EnergyStorageComponent(String name, int capacity) {
		this(name, capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput) {
		this(name, capacity, maxInput, 0);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput, int maxExtract) {
		super(name);
		EnergyStorage = new StaticPowerFEStorage(capacity, maxInput, maxExtract);
		capabilityAccessor = new EnergyComponentCapabilityAccess();
	}

	public StaticPowerFEStorage getStorage() {
		return EnergyStorage;
	}

	public int getEnergyIO() {
		return energyPerTick;
	}

	public boolean hasEnoughPower(int power) {
		return EnergyStorage.getEnergyStored() >= power;
	}

	public boolean hasPower() {
		return EnergyStorage.getEnergyStored() > 0;
	}

	public void setCapabiltiyFilter(TriFunction<Integer, Direction, EnergyManipulationAction, Boolean> filter) {
		this.filter = filter;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		EnergyStorage.readFromNbt(nbt);
		energyPerTick = nbt.getInt("PerTick");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		EnergyStorage.writeToNbt(nbt);

		long ticksSinceLastUpdate = Math.max(getTileEntity().getWorld().getGameTime() - lastUpdateTime, 1);
		int energyUsedPerTickSinceLastPacket = (int) ((EnergyStorage.getEnergyStored() - lastEnergyStored) / ticksSinceLastUpdate);
		nbt.putInt("PerTick", energyUsedPerTickSinceLastPacket);
		lastUpdateTime = getTileEntity().getWorld().getGameTime();
		lastEnergyStored = EnergyStorage.getEnergyStored();
		return nbt;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) {
			capabilityAccessor.currentSide = side;
			return LazyOptional.of(() -> capabilityAccessor).cast();
		}
		return LazyOptional.empty();
	}

	private class EnergyComponentCapabilityAccess implements IEnergyStorage {
		protected Direction currentSide;

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (EnergyStorageComponent.this.filter != null && !EnergyStorageComponent.this.filter.apply(maxReceive, currentSide, EnergyManipulationAction.RECIEVE)) {
				return 0;
			}
			return EnergyStorageComponent.this.getStorage().receiveEnergy(maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (EnergyStorageComponent.this.filter != null && !EnergyStorageComponent.this.filter.apply(maxExtract, currentSide, EnergyManipulationAction.PROVIDE)) {
				return 0;
			}
			return EnergyStorageComponent.this.getStorage().extractEnergy(maxExtract, simulate);
		}

		@Override
		public int getEnergyStored() {
			return EnergyStorageComponent.this.getStorage().getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			return EnergyStorageComponent.this.getStorage().getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return EnergyStorageComponent.this.getStorage().canExtract();
		}

		@Override
		public boolean canReceive() {
			return EnergyStorageComponent.this.getStorage().canReceive();
		}

	}
}
