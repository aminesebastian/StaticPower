package theking530.staticpower.data.tiers.energized;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
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
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return 75;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 250;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 8;
	}
}
