package theking530.staticpower.data.tiers.categories.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticpower.StaticPower;

public abstract class TierPowerCableConfiguration {
	public final ConfigValue<Double> cablePowerMaxCurrent;
	public final ConfigValue<Double> cablePowerResistancePerBlock;

	public final ConfigValue<Double> cableIndustrialPowerMaxCurrent;
	public final ConfigValue<Double> cableIndustrialPowerResistancePerBlock;

	public TierPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		builder.push("Power");

		cablePowerMaxCurrent = builder.comment("The amount of current this cable can transfer before breaking.").translation(StaticPower.MOD_ID + ".config." + "cablePowerCapacity")
				.define("CablePowerMaxCurrent", this.getCablePowerMaxCurrent());
		cablePowerResistancePerBlock = builder.comment(
				"The resistance of this cable per block. This value is totaled along the path from the power provider to the power destination to determine how much power is lost during the transfer.")
				.translation(StaticPower.MOD_ID + ".config." + "cablePowerResistancePerBlock").define("CablePowerResistancePerBlock", this.getCablePowerResistancePerBlock());

		cableIndustrialPowerMaxCurrent = builder.comment("The amount of current that an industrial cable can transfer before breaking.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialPowerMaxCurrent").define("CableIndustrialPowerMaxCurrent", this.getCableIndustrialPowerMaxCurrent());
		cableIndustrialPowerResistancePerBlock = builder.comment(
				"The resistance of this industrial cable per block. This value is totaled along the path from the power provider to the power destination to determine how much power is lost during the transfer.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialPowerResistancePerBlock")
				.define("CableIndustrialPowerResistancePerBlock", this.getCableIndustrialPowerResistancePerBlock());
		builder.pop();
	}

	protected abstract double getCablePowerMaxCurrent();

	protected abstract double getCablePowerResistancePerBlock();

	protected abstract double getCableIndustrialPowerMaxCurrent();

	protected abstract double getCableIndustrialPowerResistancePerBlock();
}
