package theking530.staticpower.data.tiers.categories.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticpower.StaticPower;

public abstract class TierFluidCableConfiguration {
	/***************************
	 * Fluid Cable Configuration
	 ***************************/
	public final ConfigValue<Integer> cableFluidTransferRate;
	public final ConfigValue<Float> cableFluidPressureDissipation;

	public final ConfigValue<Integer> cableCapillaryFluidTransferRate;
	public final ConfigValue<Float> cableCapillaryPressureDissipation;

	public final ConfigValue<Integer> cableIndustrialFluidTransferRate;
	public final ConfigValue<Float> cableIndustrialPressureDissipation;

	public TierFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		builder.push("Fluid");
		cableFluidTransferRate = builder.comment("The amount of fluid that can be transfered by a regular fluid pipe of this tier into a connected destination.")
				.translation(StaticPower.MOD_ID + ".config." + "cableFluidTransferRate").define("CableFluidTransferRate", getCableFluidtransferRate());
		cableFluidPressureDissipation = builder
				.comment("The amount of pressure that is dissapated along a horizontal connection to a pipe of this tier (a value of 1 == 0.5 pressure.")
				.translation(StaticPower.MOD_ID + ".config." + "cableFluidPressureDissipation").define("CableFluidPressureDissipation", getCableFluidPressureDissipation());

		cableCapillaryFluidTransferRate = builder.comment("The amount of fluid that can be transfered by a regular fluid pipe of this tier into a connected destination.")
				.translation(StaticPower.MOD_ID + ".config." + "cableCapillaryFluidTransferRate").define("CableCapillaryFluidTransferRate", getCableCapillaryFluidTransferRate());
		cableCapillaryPressureDissipation = builder
				.comment("The amount of pressure that is dissapated along a horizontal connection to a pipe of this tier (a value of 1 == 0.5 pressure.")
				.translation(StaticPower.MOD_ID + ".config." + "cableCapillaryPressureDissipation")
				.define("CableCapillaryPressureDissipation", getCableCapillaryPressureDissipation());

		cableIndustrialFluidTransferRate = builder.comment("The amount of fluid that can be transfered by an industrial fluid pipe of this tier into a connected destination.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialFluidTransferRate")
				.define("CableIndustrialFluidTransferRate", getCableIndustrialFluidTransferRate());
		cableIndustrialPressureDissipation = builder
				.comment("The amount of pressure that is dissapated along a horizontal connection to an industrial pipe of this tier (a value of 1 == 0.5 pressure.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialPressureDissipation")
				.define("CableIndustrialPressureDissipation", getCableIndustrialPressureDissipation());
		builder.pop();
	}

	protected abstract int getCableFluidtransferRate();

	protected float getCableFluidPressureDissipation() {
		return 1.0f;
	}

	protected abstract int getCableCapillaryFluidTransferRate();

	public float getCableCapillaryPressureDissipation() {
		return 1.0f;
	}

	protected abstract int getCableIndustrialFluidTransferRate();

	protected float getCableIndustrialPressureDissipation() {
		return 1.0f;
	}
}
