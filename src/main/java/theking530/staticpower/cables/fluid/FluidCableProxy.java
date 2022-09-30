package theking530.staticpower.cables.fluid;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.fluid.StaticPowerFluidTank;

public class FluidCableProxy {
	public static final String FLUID_DEFAULT_CAPACITY_DATA_TAG_KEY = "c";
	public static final String FLUID_TRANSFER_RATE_DATA_TAG_KEY = "r";
	public static final String FLUID_CABLE_INDUSTRIAL_DATA_TAG_KEY = "i";

	private final BlockPos pos;
	private final Set<Direction> suppliedFromDirections;
	private int adjacentDestinationCount;
	protected StaticPowerFluidTank fluidStorage;
	protected float pressure;
	protected int transferRate;
	protected boolean isIndustrial;

	protected Queue<FluidStack> queuedTransfers;
	
	public FluidCableProxy(BlockPos pos, int capacity, int transferRate, boolean isIndustrial) {
		this.pos = pos;
		this.transferRate = transferRate;
		this.isIndustrial = isIndustrial;
		this.suppliedFromDirections = new HashSet<>();

		adjacentDestinationCount = 0;
		pressure = 0;
		fluidStorage = new StaticPowerFluidTank(capacity);
	}

	public void tick() {

	}

	public int getAdjacentDestinationCount() {
		return adjacentDestinationCount;
	}

	public void setAdjacentDestinationCount(int count) {
		adjacentDestinationCount = count;
	}

	public int getSplitFactor() {
		return Math.max(adjacentDestinationCount - suppliedFromDirections.size(), 1);
	}

	public void clearSuppliedFromDirections() {
		this.suppliedFromDirections.clear();
	}

	public void addSuppliedFromDirection(Direction direction) {
		this.suppliedFromDirections.add(direction);
	}

	public boolean wasSuppliedFromDirection(Direction direction) {
		return this.suppliedFromDirections.contains(direction);
	}

	public int getCapacity() {
		return fluidStorage.getCapacity();
	}

	public void setCapacity(int capacity) {
		fluidStorage.setCapacity(capacity);
	}

	public FluidStack getStored() {
		return fluidStorage.getFluid();
	}

	public void setStored(FluidStack stored) {
		fluidStorage.setFluid(stored);
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = Math.max(0, pressure);
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getTransferRate() {
		return transferRate;
	}

	public boolean isIndustrial() {
		return isIndustrial;
	}

	public int fill(FluidStack fluid, float pressure, FluidAction action) {
		FluidStack limitedStack = fluid.copy();
		limitedStack.setAmount(Math.min(getTransferRate(), fluid.getAmount()));
		int filled = fluidStorage.fill(limitedStack, action);
		if (action == FluidAction.EXECUTE) {
			setPressure(Math.max(getPressure(), pressure));
		}
		return filled;
	}

	public FluidStack drain(int amount, FluidAction action) {
		return fluidStorage.drain(amount, action);
	}

	@Override
	public String toString() {
		return "FluidCableProxy [pos=" + getPos() + ", capacity=" + getCapacity() + ", stored=" + getStored() + "]";
	}

	public static FluidCableProxy deserialize(CompoundTag tag) {
		BlockPos pos = BlockPos.of(tag.getLong("pos"));
		FluidCableProxy output = new FluidCableProxy(pos, 0, 0, false);
		output.fluidStorage.deserializeNBT(tag.getCompound("f"));
		output.pressure = tag.getFloat("p");
		output.transferRate = tag.getInt("t");
		output.isIndustrial = tag.getBoolean("i");
		return output;
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.put("f", this.fluidStorage.serializeNBT());
		tag.putFloat("p", pressure);
		tag.putInt("t", transferRate);
		tag.putBoolean("i", isIndustrial);
		tag.putLong("pos", this.getPos().asLong());
		return tag;
	}
}