package theking530.staticpower.tileentities.components;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.energy.StaticPowerFEStorage;

public class EnergyStorageComponent extends AbstractTileEntityComponent {

	protected StaticPowerFEStorage EnergyStorage;

	protected int lastEnergyStored;
	protected int energyPerTick;
	protected long lastUpdateTime;

	public EnergyStorageComponent(String name, int capacity) {
		this(name, capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput) {
		this(name, capacity, maxInput, 0);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput, int maxExtract) {
		super(name);
		EnergyStorage = new StaticPowerFEStorage(capacity, maxInput, maxExtract);
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
			return LazyOptional.of(() -> EnergyStorage).cast();
		}
		return LazyOptional.empty();
	}
}
