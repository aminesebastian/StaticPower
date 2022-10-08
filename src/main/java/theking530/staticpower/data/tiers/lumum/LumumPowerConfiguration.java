package theking530.staticpower.data.tiers.lumum;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
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
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return 100;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 500;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 16;
	}
}
