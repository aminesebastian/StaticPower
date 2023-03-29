package theking530.staticpower.data.tiers.diamond;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierDiamond extends StaticCoreTier {

	public StaticCoreTierDiamond(Builder builder, String modId) {
		super(builder, modId);
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
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new ToolConfiguration(builder, modId);
	}

	public class ToolConfiguration extends TierToolConfiguration {

		public ToolConfiguration(Builder builder, String modId) {
			super(builder, modId);
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
