package theking530.staticcore.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtilities {
	public static void setFluidTemperature(FluidStack fluid, float temperature) {
		CompoundTag tag = fluid.getOrCreateTag();
		tag.putFloat(StaticCoreFluidType.TEMPERATURE_TAG, temperature);
	}
}
