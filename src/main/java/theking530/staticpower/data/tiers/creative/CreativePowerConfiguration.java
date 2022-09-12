package theking530.staticpower.data.tiers.creative;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class CreativePowerConfiguration extends TierPowerConfiguration {

	public CreativePowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return Double.MAX_VALUE;
	}

	@Override
	protected List<Double> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(0.0, Double.MAX_VALUE);
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return Double.MAX_VALUE;
	}

	@Override
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getBatteryCapacity() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getBatteryOutputVoltage() {
		return 12;
	}

	@Override
	protected List<Double> internalGetTransformerVoltageRange() {
		return Arrays.asList(0.0, Double.MAX_VALUE);
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return Double.MAX_VALUE;
	}
}
