package theking530.staticpower.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class BasicPowerConfiguration extends TierPowerConfiguration {

	public BasicPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 50;
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
