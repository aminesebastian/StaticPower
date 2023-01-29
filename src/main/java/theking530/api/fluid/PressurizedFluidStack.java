package theking530.api.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PressurizedFluidStack extends FluidStack {
	public static final PressurizedFluidStack EMPTY = new PressurizedFluidStack(Fluids.EMPTY, 0, 0);
	private float pressure;

	public PressurizedFluidStack(Fluid fluid, int amount, float pressure) {
		super(fluid, amount);
		this.pressure = pressure;
	}

	public PressurizedFluidStack(FluidStack stack, float pressure) {
		super(stack, stack.getAmount());
		this.pressure = pressure;
	}

	public PressurizedFluidStack(Fluid fluid, int amount, float pressure, CompoundTag nbt) {
		super(fluid, amount, nbt);
		this.pressure = pressure;
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		if (getRawFluid() == Fluids.EMPTY) {
			throw new IllegalStateException("Can't modify the empty stack.");
		}
		this.pressure = pressure;
	}

	@Override
	public PressurizedFluidStack copy() {
		return new PressurizedFluidStack(getFluid(), getAmount(), pressure, getTag());
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		nbt.putFloat("Pressure", pressure);
		return nbt;
	}

	public static PressurizedFluidStack readFromTag(CompoundTag nbt) {
		if (nbt == null) {
			return EMPTY;
		}
		if (!nbt.contains("FluidName", Tag.TAG_STRING)) {
			return EMPTY;
		}
		if (!nbt.contains("Pressure", Tag.TAG_FLOAT)) {
			return EMPTY;
		}

		ResourceLocation fluidName = new ResourceLocation(nbt.getString("FluidName"));
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
		if (fluid == null) {
			return EMPTY;
		}
		PressurizedFluidStack stack = new PressurizedFluidStack(fluid, nbt.getInt("Amount"), nbt.getFloat("Pressure"));

		if (nbt.contains("Tag", Tag.TAG_COMPOUND)) {
			stack.setTag(nbt.getCompound("Tag"));
		}
		return stack;
	}

	@Override
	public String toString() {
		return "PressurizedFluidStack [pressure=" + pressure + ", fluid=" + getFluid() + ", amount=" + getAmount() + ", tag=" + getTag() + "]";
	}
}
