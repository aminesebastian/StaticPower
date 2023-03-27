package theking530.staticcore.data.tiers.advanced;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class AdvancedPowerConfiguration extends TierPowerConfiguration {

	public AdvancedPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
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
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.MEDIUM;
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

	@Override
	protected int getTransfomerRatio() {
		return 2;
	}
}
