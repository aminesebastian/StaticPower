package theking530.staticpower.data.tiers.advanced;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class AdvancedPowerConfiguration extends TierPowerConfiguration {

	public AdvancedPowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return 250;
	}

	@Override
	protected List<Double> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(0.0, 24.0);
	}

	@Override
	protected double getDefaultMaximumInputCurrent() {
		return 4;
	}

	@Override
	protected double getDefaultOutputVoltage() {
		return 24;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 20;
	}

	@Override
	protected double getBatteryCapacity() {
		return 6400;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 60;
	}

	@Override
	protected double getBatteryOutputVoltage() {
		return 24;
	}

	@Override
	protected List<Double> internalGetTransformerVoltageRange() {
		return Arrays.asList(0.0, 48.0);
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 2;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 2;
	}
}
