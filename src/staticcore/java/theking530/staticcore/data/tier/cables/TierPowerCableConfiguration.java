package theking530.staticcore.data.tier.cables;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.api.energy.StaticPowerVoltage;

public abstract class TierPowerCableConfiguration {
	public final ConfigValue<Double> cableMaxCurrent;
	public final ConfigValue<Double> cablePowerResistance;

	public final ConfigValue<Double> cableIndustrialPowerMaxPower;
	public final ConfigValue<Double> cableIndustrialPowerResistance;

	public final ConfigValue<StaticPowerVoltage> wireTerminalMaxVoltage;
	public final ConfigValue<Double> wireTerminalMaxCurrent;

	public final ConfigValue<Integer> wireCoilMaxDistance;
	public final ConfigValue<Integer> insulatedWireCoilMaxDistance;

	public final ConfigValue<Double> wireCoilMaxCurrent;
	public final ConfigValue<Double> wireCoilPowerLossPerBlock;

	public TierPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		builder.push("Power");
		cableMaxCurrent = builder.comment("The amount of current a cable of this tier can transfer.").translation(modId + ".config." + "cableMaxCurrent")
				.define("CableMaxCurrent", getCableMaxCurrent());
		cablePowerResistance = builder.comment(
				"The resistance of this cable per block. This value is totaled along the path from the power provider to the power destination to determine how much power is lost during the transfer.")
				.translation(modId + ".config." + "cablePowerResistance").define("CablePowerResistance", this.getCablePowerResistance());

		cableIndustrialPowerMaxPower = builder.comment("The amount of power an industrial cable of this tier can transfer.")
				.translation(modId + ".config." + "cableIndustrialPowerMaxPower").define("CableIndustrialPowerMaxPower", getCableIndustrialPowerMaxCurrent());
		cableIndustrialPowerResistance = builder.comment(
				"The resistance of this industrial cable per block. This value is totaled along the path from the power provider to the power destination to determine how much power is lost during the transfer.")
				.translation(modId + ".config." + "cableIndustrialPowerResistance").define("CableIndustrialPowerResistance", this.getCableIndustrialPowerResistance());
		builder.pop();

		builder.push("Wire Terminal");
		wireTerminalMaxVoltage = builder.comment("The highest voltage a wire terminal of this tier can transfer.")
				.translation(modId + ".config." + "wireTerminalMaxVoltage").define("WireTerminalMaxVoltage", getWireTerminalMaxVoltage());
		wireTerminalMaxCurrent = builder.comment("The amount of current a wire terminal of this tier can transfer.")
				.translation(modId + ".config." + "wireTerminalMaxCurrent").define("WireTerminalMaxCurrent", getWireTerminalMaxCurrent());
		builder.pop();

		builder.push("Wire Coil");
		wireCoilMaxDistance = builder.comment("The maximum distance a wire of this tier can extend.").translation(modId + ".config." + "wireCoilMaxDistance")
				.define("WireCoilMaxDistance", getWireCoilMaxDistance());
		insulatedWireCoilMaxDistance = builder.comment("The maximum distance an insulated wire of this tier can extend.")
				.translation(modId + ".config." + "insulatedWireCoilMaxDistance").define("InsulatedWireCoilMaxDistance", getInsulatedWireCoilMaxDistance());

		wireCoilMaxCurrent = builder.comment("The amount of current a wire of this tier can transfer.").translation(modId + ".config." + "wireCoilMaxCurrent")
				.define("WireCoilMaxCurrent", getWireCoilMaxCurrent());
		wireCoilPowerLossPerBlock = builder.comment(
				"The resistance of this cable per block. This value is totaled along the path from the power provider to the power destination to determine how much power is lost during the transfer.")
				.translation(modId + ".config." + "wireTerminalPowerLossPerBlock").define("WireTerminalPowerLossPerBlock", getWireCoilPowerLossPerBlock());
		builder.pop();
	}

	protected abstract StaticPowerVoltage getCableMaxVoltage();

	protected abstract double getCableMaxCurrent();

	protected abstract double getCablePowerResistance();

	protected abstract StaticPowerVoltage getWireTerminalMaxVoltage();

	protected abstract double getWireTerminalMaxCurrent();

	protected abstract int getWireCoilMaxDistance();

	protected int getInsulatedWireCoilMaxDistance() {
		return getWireCoilMaxDistance() * 2;
	}

	protected abstract StaticPowerVoltage getWireCoilMaxVoltage();

	protected abstract double getWireCoilMaxCurrent();

	protected abstract double getWireCoilPowerLossPerBlock();

	protected StaticPowerVoltage getCableIndustrialMaxVoltage() {
		return getCableMaxVoltage();
	}

	protected double getCableIndustrialPowerMaxCurrent() {
		return getCableMaxCurrent() * 10;
	}

	protected double getCableIndustrialPowerResistance() {
		return getCablePowerResistance() / 10;
	}
}
