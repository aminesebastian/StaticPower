package theking530.staticpower.data.tiers.energized;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class EnergizedPowerConfiguration extends TierPowerConfiguration {

	public EnergizedPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.EXTREME;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 1000;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 15;
	}

	@Override
	protected int getTransfomerRatio() {
		return 4;
	}
}
