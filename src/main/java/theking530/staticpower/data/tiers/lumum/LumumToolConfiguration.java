package theking530.staticpower.data.tiers.lumum;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class LumumToolConfiguration extends TierToolConfiguration {

	public LumumToolConfiguration(Builder builder) {
		super(builder);
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
