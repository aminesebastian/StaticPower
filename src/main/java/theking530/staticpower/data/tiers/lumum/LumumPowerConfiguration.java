package theking530.staticpower.data.tiers.lumum;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class LumumPowerConfiguration extends TierPowerConfiguration {

	public LumumPowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return 2000;
	}

	@Override
	protected List<Double> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(0.0, 480.0);
	}

	@Override
	protected double getDefaultMaximumInputCurrent() {
		return 20;
	}

	@Override
	protected double getDefaultOutputVoltage() {
		return 480;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 200;
	}

	@Override
	protected double getBatteryCapacity() {
		return 100000;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 2000;
	}

	@Override
	protected double getBatteryOutputVoltage() {
		return 480;
	}

	@Override
	protected List<Double> internalGetTransformerVoltageRange() {
		return Arrays.asList(0.0, 1920.0);
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 16;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return 80;
	}
}
