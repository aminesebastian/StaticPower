package theking530.staticpower.data.tiers.advanced;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class AdvancedPowerConfiguration extends TierPowerConfiguration {

	public AdvancedPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.MEDIUM;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 100;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 5;
	}

	@Override
	protected int getTransfomerRatio() {
		return 2;
	}
}
