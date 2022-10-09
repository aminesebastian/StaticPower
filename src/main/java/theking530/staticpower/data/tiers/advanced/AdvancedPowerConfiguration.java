package theking530.staticpower.data.tiers.advanced;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
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
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return 20;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 10;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 50;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 2;
	}
}
