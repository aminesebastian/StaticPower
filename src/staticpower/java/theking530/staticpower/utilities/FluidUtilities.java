package theking530.staticpower.utilities;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtilities {
	public static int getFluidTemperature(FluidStack fluid) {
		return fluid.getFluid().getFluidType().getTemperature(fluid);
	}

	public static int getFluidStackHash(FluidStack stack) {
		CompoundTag serialized = new CompoundTag();
		stack.writeToNBT(serialized);
		serialized.remove("Amount");
		return Objects.hash(serialized);
	}
}
