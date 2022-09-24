package theking530.staticpower.data.tiers.energized;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class EnergizedToolConfiguration extends TierToolConfiguration {

	public EnergizedToolConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected int getDrillBitUses() {
		return 12000;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 24000;
	}

	@Override
	protected int getMagnetRadius() {
		return 6;
	}

	@Override
	protected float getDrillSpeedMultiplier() {
		return 1.5f;
	}

	@Override
	protected float getChainsawSpeedMultiplier() {
		return 1.5f;
	}
}
