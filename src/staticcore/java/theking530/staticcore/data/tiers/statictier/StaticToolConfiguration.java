package theking530.staticcore.data.tiers.statictier;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class StaticToolConfiguration extends TierToolConfiguration {

	public StaticToolConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected int getDrillBitUses() {
		return 8000;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 16000;
	}

	@Override
	protected int getMagnetRadius() {
		return 4;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 1.0f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 1.0f;
	}
}
