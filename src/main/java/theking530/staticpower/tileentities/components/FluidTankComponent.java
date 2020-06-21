package theking530.staticpower.tileentities.components;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTankComponent extends AbstractTileEntityComponent {

	protected FluidTank FluidStorage;

	protected int lastFluidStored;
	protected int fluidPerTick;
	protected long lastUpdateTime;

	public FluidTankComponent(String name, int capacity) {
		super(name);
		FluidStorage = new FluidTank(capacity);
	}

	public FluidTank getTank() {
		return FluidStorage;
	}

	public int getEnergyIO() {
		return fluidPerTick;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		FluidStorage.readFromNBT(nbt);
		fluidPerTick = nbt.getInt("PerTick");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		FluidStorage.writeToNBT(nbt);

		long ticksSinceLastUpdate = Math.max(getTileEntity().getWorld().getGameTime() - lastUpdateTime, 1);
		int energyUsedPerTickSinceLastPacket = ((int) ((FluidStorage.getFluidAmount() - lastFluidStored) / ticksSinceLastUpdate));
		nbt.putInt("PerTick", energyUsedPerTickSinceLastPacket);
		lastUpdateTime = getTileEntity().getWorld().getGameTime();
		lastFluidStored = FluidStorage.getFluidAmount();
		return nbt;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> FluidStorage).cast();
		}
		return LazyOptional.empty();
	}
}
