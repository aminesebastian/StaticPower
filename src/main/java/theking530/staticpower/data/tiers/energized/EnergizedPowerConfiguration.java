package theking530.staticpower.data.tiers.energized;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class EnergizedPowerConfiguration extends TierPowerConfiguration {

	public EnergizedPowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return 1000;
	}

	@Override
	protected List<Double> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(0.0, 240.0);
	}

	@Override
	protected double getDefaultMaximumInputCurrent() {
		return 10;
	}

	@Override
	protected double getDefaultOutputVoltage() {
		return 240;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 100;
	}

	@Override
	protected double getBatteryCapacity() {
		return 25000;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 1000;
	}

	@Override
	protected double getBatteryOutputVoltage() {
		return 240;
	}

	@Override
	protected List<Double> internalGetTransformerVoltageRange() {
		return Arrays.asList(0.0, 960.0);
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 8;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 40;
	}
}
