package theking530.staticpower.data.tiers.statictier;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class StaticPowerConfiguration extends TierPowerConfiguration {

	public StaticPowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return 1000;
	}

	@Override
	protected List<Double> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(0.0, 48.0);
	}

	@Override
	protected double getDefaultMaximumInputCurrent() {
		return 5;
	}

	@Override
	protected double getDefaultOutputVoltage() {
		return 48;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 40;
	}

	@Override
	protected double getBatteryCapacity() {
		return 10000;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 100;
	}

	@Override
	protected double getBatteryOutputVoltage() {
		return 48;
	}

	@Override
	protected List<Double> internalGetTransformerVoltageRange() {
		return Arrays.asList(0.0, 480.0);
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 4;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 20;
	}
}
