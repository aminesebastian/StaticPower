package theking530.staticpower.data.tiers.bronze;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticPowerTierBronze extends StaticPowerTier {

	public StaticPowerTierBronze(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "bronze");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.bronze";
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new ToolConfiguration(builder);
	}

	public class ToolConfiguration extends TierToolConfiguration {

		public ToolConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getHammerSwingSpeed() {
			return -2;
		}

		@Override
		protected double getHammerDamage() {
			return 7;
		}

		@Override
		protected int getHammerCooldown() {
			return 30;
		}

		@Override
		protected int getWireCutterUses() {
			return 400;
		}

		@Override
		protected int getDrillBitUses() {
			return 2000;
		}

		@Override
		protected int getChainsawBladeUses() {
			return 4000;
		}

		@Override
		protected int getHammerUses() {
			return 400;
		}
	}
}
