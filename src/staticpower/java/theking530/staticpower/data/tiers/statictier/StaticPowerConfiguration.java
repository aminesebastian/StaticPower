package theking530.staticpower.data.tiers.statictier;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;

public class StaticPowerConfiguration extends TierPowerConfiguration {

	public StaticPowerConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.HIGH;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return 500;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return 10;
	}

	@Override
	protected int getTransfomerRatio() {
		return 3;
	}
}
