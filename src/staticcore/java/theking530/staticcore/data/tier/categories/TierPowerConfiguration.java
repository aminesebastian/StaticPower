package theking530.staticcore.data.tier.categories;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;

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
	public final ConfigValue<StaticPowerVoltage> maximumBatteryInputVoltage;
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

	public TierPowerConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		defaultPowerCapacity = builder.comment("The amount of power a block of this tier can store (in SW).")
				.translation(modId + ".config." + "defaultPowerCapacity")
				.define("DefaultPowerCapacity", getDefaultPowerCapacity());

		defaultInputVoltageRange = builder.comment("The input voltage range for powered blocks of this tier.")
				.translation(modId + ".config." + "defaultInputVoltageRange")
				.define("DefaultInputVoltageRange", internalGetDefaultInputVoltageRange());
		defaultMaximumPowerInput = builder.comment("The maximum power a block of this tier can take (in SW/t).")
				.translation(modId + ".config." + "defaultMaximumPowerInput")
				.define("DefaultMaximumPowerInput", getDefaultMaximumPowerInput());

		defaultOutputVoltage = builder.comment("The voltage a block outputs and uses internally when performing work.")
				.translation(modId + ".config." + "defaultOutputVoltage")
				.define("DefaultOutputVoltage", getDefaultOutputVoltage());
		defaultMaximumPowerOutput = builder
				.comment("The maximum power a machine of this tier can use while performing work (in SW/t).")
				.translation(modId + ".config." + "defaultMaximumPowerOutput")
				.define("DefaultMaximumPowerOutput", getDefaultMaximumPowerOutput());

		builder.push("Transformer");

		transformerVoltageRange = builder
				.comment("The voltage range that can be handled by a transformer of this tier.")
				.translation(modId + ".config." + "transformerVoltageRange")
				.define("TransformerVoltageRange", internalGetTransformerVoltageRange());
		transfomerRatio = builder.comment(
				"The number of tiers of voltage a transformer of this tier will upgrade. For example, a ratio of 2 will raise LV to HV or MV to BV.")
				.translation(modId + ".config." + "transfomerRatio").define("TransfomerRatio", getTransfomerRatio());

		builder.pop();

		builder.push("Battery");
		batteryCapacity = builder.comment("The amount of power that a battery of this tier can store (in SW).")
				.translation(modId + ".config." + "batteryCapacity").define("BatteryCapacity", getBatteryCapacity());
		batteryMaximumPowerInput = builder
				.comment("The maximum power that a battery of this tier can accept (in SW/t).")
				.translation(modId + ".config." + "batteryMaximumPowerInput")
				.define("BatteryMaximumPowerInput", getBatteryMaximumPowerInput());
		batteryMaximumPowerOutput = builder
				.comment("The maximum power that a battery of this tier can output (in SW/t).")
				.translation(modId + ".config." + "batteryMaximumPowerOutput")
				.define("BatteryMaximumPowerOutput", getBatteryMaximumPowerOutput());
		maximumBatteryInputVoltage = builder.comment("The maximum input voltage of a batter of this tier.")
				.translation(modId + ".config." + "maximumBatteryInputVoltage")
				.define("MaximumBatteryInputVoltage", internalGetMaximumBatteryInputVoltage());
		batteryOutputVoltage = builder.comment("The output voltage of a batter of this tier.")
				.translation(modId + ".config." + "batteryOutputVoltage")
				.define("BatteryOutputVoltage", getBatteryOutputVoltage());
		builder.pop();

		builder.push("Portable_Battery");
		portableBatteryCapacity = builder
				.comment("The amount of power that can be stored in a portable battery of this tier (in SW).")
				.translation(modId + ".config." + "portableBatteryCapacity")
				.define("PortableBatteryCapacity", getPortableBatteryCapacity());
		portableBatteryMaximumPowerOutput = builder
				.comment("The maximum amount of power a portable battery of this tier can output (in SW/t).")
				.translation(modId + ".config." + "portableBatteryMaximumPowerOutput")
				.define("PortableBatteryMaximumPowerOutput", this.getPortableBatteryMaxPowerOutput());
		portableBatteryInputVoltageRange = builder
				.comment("The voltage range that can be used to charge a portable battery of this tier.")
				.translation(modId + ".config." + "portableBatteryInputVoltageRange")
				.define("PortableBatteryInputVoltageRange", internalGetPortableBatteryChargingVoltage());

		portableBatteryMaximumPowerInput = builder
				.comment("The maximum amount of power a portable battery of this tier can input (in SW/t).")
				.translation(modId + ".config." + "portableBatteryMaximumPowerInput")
				.define("PortableBatteryMaximumPowerInput", getPortableBatteryMaxPowerInput());
		portableBatteryOutputVoltage = builder.comment("The output voltage of a portable battery of this tier.")
				.translation(modId + ".config." + "portableBatteryOutputVoltage")
				.define("BortableBatteryOutputVoltage", getPortableBatteryOutputVoltage());
		builder.pop();

		builder.push("Solar_Panel");
		solarPanelPowerGeneration = builder
				.comment("The amount of power generated by a solar panel of this tier per tick (in SW/t).")
				.translation(modId + ".config." + "solarPanelPowerGeneration")
				.define("SolarPanelPowerGeneration", this.getSolarPanelPowerGeneration());
		solarPanelPowerStorage = builder.comment("The amount of power a solar panel of this tier can store (in SW).")
				.translation(modId + ".config." + "solarPanelPowerStorage")
				.define("SolarPanelPowerStorage", this.getSolarPanelPowerStorage());
		builder.pop();
	}

	protected abstract double getDefaultMaximumPowerOutput();

	protected abstract StaticPowerVoltage internalGetMaximumBatteryInputVoltage();

	protected abstract int getTransfomerRatio();

	protected abstract double getSolarPanelPowerGeneration();

	protected double getBatteryMaximumPowerOutput() {
		return getDefaultMaximumPowerInput() * 5; // Should be able to support 5 machines of this tier at max output.
	}

	protected double getDefaultPowerCapacity() {
		// Machines should be able to sustain max output for 10 seconds.
		return getDefaultMaximumPowerOutput() * 20 * 10;
	}

	protected List<StaticPowerVoltage> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(StaticPowerVoltage.LOW, getDefaultOutputVoltage());
	}

	public final StaticVoltageRange getDefaultInputVoltageRange() {
		return new StaticVoltageRange(defaultInputVoltageRange.get().get(0), defaultInputVoltageRange.get().get(1));
	}

	protected double getDefaultMaximumPowerInput() {
		return getDefaultMaximumPowerOutput() / 2;
	}

	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	protected double getBatteryCapacity() {
		return getDefaultPowerCapacity() * 10;
	}

	protected double getBatteryMaximumPowerInput() {
		return getDefaultMaximumPowerInput() * 2;
	}

	public final StaticVoltageRange getMaximumBatteryInputVoltage() {
		return new StaticVoltageRange(StaticPowerVoltage.LOW, maximumBatteryInputVoltage.get());
	}

	protected StaticPowerVoltage getBatteryOutputVoltage() {
		return getDefaultOutputVoltage();
	}

	protected List<StaticPowerVoltage> internalGetTransformerVoltageRange() {
		return Arrays.asList(StaticPowerVoltage.LOW, getDefaultOutputVoltage().upgrade());
	}

	public final StaticVoltageRange getTransformerVoltageRange() {
		return new StaticVoltageRange(transformerVoltageRange.get().get(0), transformerVoltageRange.get().get(1));
	}

	protected double getSolarPanelPowerStorage() {
		return getSolarPanelPowerGeneration();
	}

	protected double getPortableBatteryCapacity() {
		return getDefaultPowerCapacity() / 2;
	}

	protected List<StaticPowerVoltage> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(StaticPowerVoltage.LOW, getDefaultOutputVoltage());
	}

	public StaticVoltageRange getPortableBatteryChargingVoltage() {
		return new StaticVoltageRange(portableBatteryInputVoltageRange.get().get(0),
				portableBatteryInputVoltageRange.get().get(1));
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
