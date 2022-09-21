package theking530.staticpower.data.tiers.sapphire;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticPowerTierSapphire extends StaticPowerTier {

	public StaticPowerTierSapphire(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "sapphire");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.sapphire";
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
			return 1.5;
		}

		@Override
		protected boolean isHardenedDurabilityBoostAdditive() {
			return false;
		}
	}
}
