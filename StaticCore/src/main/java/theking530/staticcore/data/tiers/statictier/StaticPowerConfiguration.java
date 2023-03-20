package theking530.staticcore.data.tiers.statictier;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class StaticPowerConfiguration extends TierPowerConfiguration {

	public StaticPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
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
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.HIGH;
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
