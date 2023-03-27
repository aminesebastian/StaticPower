package theking530.staticcore.data.tiers.lumum;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class LumumToolConfiguration extends TierToolConfiguration {

	public LumumToolConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected int getDrillBitUses() {
		return 16000;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 32000;
	}

	@Override
	protected int getMagnetRadius() {
		return 8;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 2f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 2f;
	}
}
