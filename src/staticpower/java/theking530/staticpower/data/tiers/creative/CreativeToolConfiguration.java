package theking530.staticpower.data.tiers.creative;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class CreativeToolConfiguration extends TierToolConfiguration {

	public CreativeToolConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getHammerSwingSpeed() {
		return 0;
	}

	@Override
	protected double getHammerDamage() {
		return 10;
	}

	@Override
	protected int getHammerCooldown() {
		return 1;
	}

	@Override
	protected int getDrillBitUses() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getChainsawBladeUses() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getHammerUses() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getWireCutterUses() {
		return Integer.MAX_VALUE;
	}

}
