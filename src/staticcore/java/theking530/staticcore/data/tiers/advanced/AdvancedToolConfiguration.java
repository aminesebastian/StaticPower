package theking530.staticcore.data.tiers.advanced;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class AdvancedToolConfiguration extends TierToolConfiguration {

	public AdvancedToolConfiguration(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected int getMagnetRadius() {
		return 3;
	}

	@Override
	protected int getDrillBitUses() {
		return 6500;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 0.75f;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 13000;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 0.75f;
	}
}
