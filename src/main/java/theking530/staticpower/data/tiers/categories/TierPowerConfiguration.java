package theking530.staticpower.data.tiers.categories;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticpower.StaticPower;

public abstract class TierPowerConfiguration {
	/***********************
	 * Power Configuration
	 ***********************/
	public final ConfigValue<Double> defaultPowerCapacity;
	public final ConfigValue<List<StaticPowerVoltage>> defaultInputVoltageRange;
	public final ConfigValue<Double> defaultMaximumPowerInput;

	public final ConfigValue<StaticPowerVoltage> defaultOutputVoltage;
	public final ConfigValue<Double> defaultMaximumPowerOutput;

	/*********************
	 * Battery Configuration
	 *********************/
	public final ConfigValue<Double> batteryCapacity;
	public final ConfigValue<Double> batteryMaximumPowerInput;
	public final ConfigValue<Double> batteryMaximumPowerOutput;
	public final ConfigValue<StaticPowerVoltage> batteryOutputVoltage;

	/*********************************
	 * Portable Battery Configuration
	 *********************************/
	public final ConfigValue<Double> portableBatteryCapacity;
	public final ConfigValue<List<StaticPowerVoltage>> portableBatteryInputVoltageRange;
	public final ConfigValue<Double> portableBatteryMaximumPowerInput;
	public final ConfigValue<Double> portableBatteryMaximumPowerOutput;
	public final ConfigValue<StaticPowerVoltage> portableBatteryOutputVoltage;

	/*********************
	 * Transformer Configuration
	 *********************/
	public final ConfigValue<List<StaticPowerVoltage>> transformerVoltageRange;
	public final ConfigValue<Integer> transfomerRatio;

	/*************
	 * Solar Panel
	 *************/
	public final ConfigValue<Double> solarPanelPowerGeneration;
	public final ConfigValue<Double> solarPanelPowerStorage;

	public TierPowerConfiguration(ForgeConfigSpec.Builder builder) {
		defaultPowerCapacity = builder.comment("The amount of power a block of this tier can store (in SW).").translation(StaticPower.MOD_ID + ".config." + "defaultPowerCapacity")
				.define("DefaultPowerCapacity", getDefaultPowerCapacity());

		defaultInputVoltageRange = builder.comment("The input voltage range for powered blocks of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "defaultInputVoltageRange").define("DefaultInputVoltageRange", internalGetDefaultInputVoltageRange());
		defaultMaximumPowerInput = builder.comment("The maximum power a block of this tier can take (in SW/t).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMaximumPowerInput").define("DefaultMaximumPowerInput", getDefaultMaximumPowerInput());

		defaultOutputVoltage = builder.comment("The voltage a block outputs and uses internally when performing work.")
				.translation(StaticPower.MOD_ID + ".config." + "defaultOutputVoltage").define("DefaultOutputVoltage", getDefaultOutputVoltage());
		defaultMaximumPowerOutput = builder.comment("The maximum power a machine of this tier can use while performing work (in SW/t).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMaximumPowerOutput").define("DefaultMaximumPowerOutput", getDefaultMaximumPowerOutput());

		builder.push("Transformer");

		transformerVoltageRange = builder.comment("The voltage range that can be handled by a transformer of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "transformerVoltageRange").define("TransformerVoltageRange", internalGetTransformerVoltageRange());
		transfomerRatio = builder.comment("The number of tiers of voltage a transformer of this tier will upgrade. For example, a ratio of 2 will raise LV to HV or MV to BV.")
				.translation(StaticPower.MOD_ID + ".config." + "transfomerRatio").define("TransfomerRatio", getTransfomerRatio());

		builder.pop();

		builder.push("Battery");
		batteryCapacity = builder.comment("The amount of power that a battery of this tier can store (in SW).").translation(StaticPower.MOD_ID + ".config." + "batteryCapacity")
				.define("BatteryCapacity", getBatteryCapacity());
		batteryMaximumPowerInput = builder.comment("The maximum power that a battery of this tier can accept (in SW/t).")
				.translation(StaticPower.MOD_ID + ".config." + "batteryMaximumPowerInput").define("BatteryMaximumPowerInput", getBatteryMaximumPowerInput());
		batteryMaximumPowerOutput = builder.comment("The maximum power that a battery of this tier can output (in SW/t).")
				.translation(StaticPower.MOD_ID + ".config." + "batteryMaximumPowerOutput").define("BatteryMaximumPowerOutput", getBatteryMaximumPowerOutput());
		batteryOutputVoltage = builder.comment("The output voltage of a batter of this tier.").translation(StaticPower.MOD_ID + ".config." + "batteryOutputVoltage")
				.define("BatteryOutputVoltage", getBatteryOutputVoltage());
		builder.pop();

		builder.push("Portable_Battery");
		portableBatteryCapacity = builder.comment("The amount of power that can be stored in a portable battery of this tier (in SW).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryCapacity").define("PortableBatteryCapacity", getPortableBatteryCapacity());
		portableBatteryMaximumPowerOutput = builder.comment("The maximum amount of power a portable battery of this tier can output (in SW/t).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryMaximumPowerOutput")
				.define("PortableBatteryMaximumPowerOutput", this.getPortableBatteryMaxPowerOutput());
		portableBatteryInputVoltageRange = builder.comment("The voltage range that can be used to charge a portable battery of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryInputVoltageRange")
				.define("PortableBatteryInputVoltageRange", internalGetPortableBatteryChargingVoltage());

		portableBatteryMaximumPowerInput = builder.comment("The maximum amount of power a portable battery of this tier can input (in SW/t).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryMaximumPowerInput").define("PortableBatteryMaximumPowerInput", getPortableBatteryMaxPowerInput());
		portableBatteryOutputVoltage = builder.comment("The output voltage of a portable battery of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryOutputVoltage").define("BortableBatteryOutputVoltage", getPortableBatteryOutputVoltage());
		builder.pop();

		builder.push("Solar_Panel");
		solarPanelPowerGeneration = builder.comment("The amount of power generated by a solar panel of this tier per tick (in SW/t).")
				.translation(StaticPower.MOD_ID + ".config." + "solarPanelPowerGeneration").define("SolarPanelPowerGeneration", this.getSolarPanelPowerGeneration());
		solarPanelPowerStorage = builder.comment("The amount of power a solar panel of this tier can store (in SW).")
				.translation(StaticPower.MOD_ID + ".config." + "solarPanelPowerStorage").define("SolarPanelPowerStorage", this.getSolarPanelPowerStorage());
		builder.pop();
	}

	protected abstract double getDefaultPowerCapacity();

	protected List<StaticPowerVoltage> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(StaticPowerVoltage.LOW, getDefaultOutputVoltage());
	}

	public final StaticVoltageRange getDefaultInputVoltageRange() {
		return new StaticVoltageRange(defaultInputVoltageRange.get().get(0), defaultInputVoltageRange.get().get(1));
	}

	protected abstract double getDefaultMaximumPowerInput();

	protected abstract StaticPowerVoltage getDefaultOutputVoltage();

	protected abstract double getDefaultMaximumPowerOutput();

	protected double getBatteryCapacity() {
		return getDefaultPowerCapacity() * 100;
	}

	protected double getBatteryMaximumPowerInput() {
		return getDefaultMaximumPowerInput();

	}

	protected abstract double getBatteryMaximumPowerOutput();

	protected StaticPowerVoltage getBatteryOutputVoltage() {
		return getDefaultOutputVoltage();
	}

	protected List<StaticPowerVoltage> internalGetTransformerVoltageRange() {
		return Arrays.asList(StaticPowerVoltage.LOW, getDefaultOutputVoltage().upgrade());
	}

	protected abstract int getTransfomerRatio();

	public final StaticVoltageRange getTransformerVoltageRange() {
		return new StaticVoltageRange(transformerVoltageRange.get().get(0), transformerVoltageRange.get().get(1));
	}

	protected abstract double getSolarPanelPowerGeneration();

	protected double getSolarPanelPowerStorage() {
		return getSolarPanelPowerGeneration();
	}

	protected double getPortableBatteryCapacity() {
		return getBatteryCapacity() / 6;
	}

	protected List<StaticPowerVoltage> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(StaticPowerVoltage.LOW, getDefaultOutputVoltage());
	}

	public StaticVoltageRange getPortableBatteryChargingVoltage() {
		return new StaticVoltageRange(portableBatteryInputVoltageRange.get().get(0), portableBatteryInputVoltageRange.get().get(1));
	}

	protected double getPortableBatteryMaxPowerInput() {
		return getDefaultMaximumPowerInput();
	}

	protected double getPortableBatteryMaxPowerOutput() {
		return getDefaultMaximumPowerOutput();
	}

	protected StaticPowerVoltage getPortableBatteryOutputVoltage() {
		return getDefaultOutputVoltage();
	}
}
