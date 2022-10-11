package theking530.staticpower.data.tiers.diamond;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticPowerTierDiamond extends StaticPowerTier {

	public StaticPowerTierDiamond(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "diamond");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.diamond";
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
		protected double getHardenedDurabilityBoost() {
			return 2.0;
		}

		@Override
		protected boolean isHardenedDurabilityBoostAdditive() {
			return false;
		}
	}
}
