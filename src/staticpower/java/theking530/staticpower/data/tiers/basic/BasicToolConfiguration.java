package theking530.staticpower.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class BasicToolConfiguration extends TierToolConfiguration {

	public BasicToolConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected int getMagnetRadius() {
		return 2;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 0.5f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 0.5f;
	}
}
