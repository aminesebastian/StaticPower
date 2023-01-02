package theking530.staticpower.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
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
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return 10;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 5;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 25;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 1;
	}

	@Override
	protected int getTransfomerRatio() {
		return 1;
	}

}
