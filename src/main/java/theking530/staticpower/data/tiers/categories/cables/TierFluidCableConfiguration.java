package theking530.staticpower.data.tiers.categories.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticpower.StaticPower;

public abstract class TierFluidCableConfiguration {
	/***************************
	 * Fluid Cable Configuration
	 ***************************/
	public final ConfigValue<Integer> cableFluidTransferRate;

	public final ConfigValue<Integer> cableIndustrialFluidTransferRate;

	public TierFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		builder.push("Fluid");
		cableFluidTransferRate = builder.comment("The amount of fluid that can be transfered by a regular fluid pipe of this tier into a connected destination.")
				.translation(StaticPower.MOD_ID + ".config." + "cableFluidTransferRate").define("CableFluidTransferRate", getCableFluidtransferRate());

		cableIndustrialFluidTransferRate = builder.comment("The amount of fluid that can be transfered by an industrial fluid pipe of this tier into a connected destination.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialFluidTransferRate").define("CableIndustrialFluidTransferRate", getCableIndustrialFluidTransferRate());
		builder.pop();
	}

	protected abstract int getCableFluidtransferRate();

	protected abstract int getCableIndustrialFluidTransferRate();
}
