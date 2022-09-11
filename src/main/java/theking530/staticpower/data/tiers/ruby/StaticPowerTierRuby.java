package theking530.staticpower.data.tiers.ruby;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticPowerTierRuby extends StaticPowerTier {

	public StaticPowerTierRuby(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "ruby");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.ruby";
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
			return 5000.0;
		}

		@Override
		protected boolean isHardenedDurabilityBoostAdditive() {
			return true;
		}
	}
}
