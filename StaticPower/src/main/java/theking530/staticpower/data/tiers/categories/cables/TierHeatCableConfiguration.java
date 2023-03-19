package theking530.staticpower.data.tiers.categories.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticpower.StaticPower;

public abstract class TierHeatCableConfiguration {
	/********************
	 * Heat Configuration
	 ********************/
	public final ConfigValue<Integer> heatCableCapacity;
	public final ConfigValue<Float> heatCableConductivity;

	public TierHeatCableConfiguration(ForgeConfigSpec.Builder builder) {
		builder.push("Heat");
		heatCableCapacity = builder.comment("The amount of heat that a heat pipe of this tier can store.").translation(StaticPower.MOD_ID + ".config." + "heatCableCapacity")
				.define("HeatCableCapacity", this.getHeatCableCapacity());

		heatCableConductivity = builder
				.comment("The conductivity multiplier for a heat pipe of this tier. The higher it is, the faster it is able to dissipate heat. This value is PER BLOCK SIDE.")
				.translation(StaticPower.MOD_ID + ".config." + "heatCableConductivity").define("HeatCableConductivity", this.getHeatCableConductivity());
		builder.pop();
	}

	protected abstract int getHeatCableCapacity();

	protected abstract float getHeatCableConductivity();
}
