package theking530.staticpower.data.tiers.statictier;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class StaticPowerConfiguration extends TierPowerConfiguration {

	public StaticPowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return 500;
	}

	@Override
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return 40;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 20;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 100;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 4;
	}

	@Override
	protected int getTransfomerRatio() {
		return 3;
	}
}
