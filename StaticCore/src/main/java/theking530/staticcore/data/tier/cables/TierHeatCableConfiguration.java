package theking530.staticcore.data.tier.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class TierHeatCableConfiguration {
	/********************
	 * Heat Configuration
	 ********************/
	public final ConfigValue<Integer> heatCableCapacity;
	public final ConfigValue<Float> heatCableConductivity;

	public TierHeatCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		builder.push("Heat");
		heatCableCapacity = builder.comment("The amount of heat that a heat pipe of this tier can store.").translation(modId + ".config." + "heatCableCapacity")
				.define("HeatCableCapacity", this.getHeatCableCapacity());

		heatCableConductivity = builder
				.comment("The conductivity multiplier for a heat pipe of this tier. The higher it is, the faster it is able to dissipate heat. This value is PER BLOCK SIDE.")
				.translation(modId + ".config." + "heatCableConductivity").define("HeatCableConductivity", this.getHeatCableConductivity());
		builder.pop();
	}

	protected abstract int getHeatCableCapacity();

	protected abstract float getHeatCableConductivity();
}
