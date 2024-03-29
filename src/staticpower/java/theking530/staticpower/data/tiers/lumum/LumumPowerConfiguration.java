package theking530.staticpower.data.tiers.lumum;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class LumumPowerConfiguration extends TierPowerConfiguration {

	public LumumPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.BONKERS;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 5000;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 20;
	}

	@Override
	protected int getTransfomerRatio() {
		return 4;
	}
}
