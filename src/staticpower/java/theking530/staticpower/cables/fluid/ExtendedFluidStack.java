package theking530.staticpower.cables.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

public class ExtendedFluidStack {
	private FluidStack stack;
	private float flowRate;

	public ExtendedFluidStack(FluidStack stack, float flowRate) {
		this.stack = stack;
		this.flowRate = flowRate;
	}

	public FluidStack getStack() {
		return stack;
	}

	public void setStack(FluidStack stack) {
		this.stack = stack;
	}

	public float getFlowRate() {
		return flowRate;
	}

	public void setFlowRate(float flowRate) {
		this.flowRate = flowRate;
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		stack.writeToNBT(output);
		output.putFloat("f", flowRate);
		return output;
	}

	public static ExtendedFluidStack loadFromNBT(CompoundTag nbt) {
		return new ExtendedFluidStack(FluidStack.loadFluidStackFromNBT(nbt), nbt.getFloat("f"));
	}
}
