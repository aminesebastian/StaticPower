package theking530.staticpower.data.tiers.categories.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticpower.StaticPower;

public abstract class TierFluidCableConfiguration {
	/***************************
	 * Fluid Cable Configuration
	 ***************************/
	public final ConfigValue<Integer> cableFluidCapacity;
	public final ConfigValue<Integer> cableIndustrialFluidCapacity;

	public TierFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		builder.push("Fluid");
		cableFluidCapacity = builder.comment("The amount of fluid that can be stored in a regular fluid pipe of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "cableFluidCapacity").define("CableFluidCapacity", this.getCableFluidCapacity());

		cableIndustrialFluidCapacity = builder.comment("The amount of fluid that can be stored in an industrial fluid pipe of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialFluidCapacity").define("CableIndustrialFluidCapacity", this.getCableIndustrialFluidCapacity());
		builder.pop();
	}

	protected abstract int getCableFluidCapacity();

	protected abstract int getCableIndustrialFluidCapacity();
}
