package theking530.staticpower.data.tiers.basic;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class BasicPowerConfiguration extends TierPowerConfiguration {

	public BasicPowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return 100;
	}

	@Override
	protected List<Double> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(0.0, 12.0);
	}

	@Override
	protected double getDefaultMaximumInputCurrent() {
		return 2;
	}

	@Override
	protected double getDefaultOutputVoltage() {
		return 12;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 10;
	}

	@Override
	protected double getBatteryCapacity() {
		return 3200;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 60;
	}

	@Override
	protected double getBatteryOutputVoltage() {
		return 12;
	}

	@Override
	protected List<Double> internalGetTransformerVoltageRange() {
		return Arrays.asList(0.0, 24.0);
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 1;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 1;
	}
}
