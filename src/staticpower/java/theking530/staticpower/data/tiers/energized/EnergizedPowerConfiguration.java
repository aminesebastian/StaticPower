package theking530.staticpower.data.tiers.energized;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class EnergizedPowerConfiguration extends TierPowerConfiguration {

	public EnergizedPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return 2500;
	}

	@Override
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.EXTREME;
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return 75;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 35;
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return 250;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 8;
	}

	@Override
	protected int getTransfomerRatio() {
		return 4;
	}
}