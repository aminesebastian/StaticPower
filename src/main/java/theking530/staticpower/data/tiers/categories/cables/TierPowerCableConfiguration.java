package theking530.staticpower.data.tiers.categories.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.StaticPower;

public abstract class TierPowerCableConfiguration {
	public final ConfigValue<StaticPowerVoltage> cableMaxVoltage;
	public final ConfigValue<Double> cablePowerMaxPower;
	public final ConfigValue<Double> cablePowerLossPerBlock;

	public final ConfigValue<StaticPowerVoltage> cableIndustrialMaxVoltage;
	public final ConfigValue<Double> cableIndustrialPowerMaxPower;
	public final ConfigValue<Double> cableIndustrialPowerLossPerBlock;

	public TierPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		builder.push("Power");
		cableMaxVoltage = builder.comment("The highest voltage a cable of this tier can transfer before burning.").translation(StaticPower.MOD_ID + ".config." + "cableMaxVoltage")
				.define("CableMaxVoltage", getCableMaxVoltage());
		cablePowerMaxPower = builder.comment("The amount of power a cable of this tier can transfer.").translation(StaticPower.MOD_ID + ".config." + "cablePowerMaxPower")
				.define("CablePowerMaxPower", getCableMaxPower());
		cablePowerLossPerBlock = builder.comment(
				"The resistance of this cable per block. This value is totaled along the path from the power provider to the power destination to determine how much power is lost during the transfer.")
				.translation(StaticPower.MOD_ID + ".config." + "cablePowerLossPerBlock").define("CablePowerLossPerBlock", this.getCablePowerLossPerBlock());

		cableIndustrialMaxVoltage = builder.comment("The highest voltage an industrial cable of this tier can transfer before burning.")
				.translation(StaticPower.MOD_ID + ".config." + "CableIndustrialMaxVoltage").define("cableIndustrialMaxVoltage", this.getCableIndustrialMaxVoltage());
		cableIndustrialPowerMaxPower = builder.comment("The amount of power an industrial cable of this tier can transfer.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialPowerMaxPower").define("CableIndustrialPowerMaxPower", getCableIndustrialPowerMaxPower());
		cableIndustrialPowerLossPerBlock = builder.comment(
				"The resistance of this industrial cable per block. This value is totaled along the path from the power provider to the power destination to determine how much power is lost during the transfer.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialPowerLossPerBlock")
				.define("CableIndustrialPowerLossPerBlock", this.cableIndustrialPowerLossPerBlock());
		builder.pop();
	}

	protected abstract StaticPowerVoltage getCableMaxVoltage();

	protected abstract double getCableMaxPower();

	protected abstract double getCablePowerLossPerBlock();

	protected StaticPowerVoltage getCableIndustrialMaxVoltage() {
		return getCableMaxVoltage();
	}

	protected double getCableIndustrialPowerMaxPower() {
		return getCableMaxPower() * 10;
	}

	protected double cableIndustrialPowerLossPerBlock() {
		return getCablePowerLossPerBlock() * 10;
	}
}
