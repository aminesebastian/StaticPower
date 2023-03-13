package theking530.staticpower.data.tiers.statictier;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticToolConfiguration extends TierToolConfiguration {

	public StaticToolConfiguration(Builder builder) {
		super(builder);
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
