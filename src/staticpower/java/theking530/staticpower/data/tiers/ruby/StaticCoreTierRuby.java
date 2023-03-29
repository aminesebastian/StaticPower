package theking530.staticpower.data.tiers.ruby;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierRuby extends StaticCoreTier {

	public StaticCoreTierRuby(Builder builder, String modId) {
		super(builder, modId);
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
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new ToolConfiguration(builder, modId);
	}

	public class ToolConfiguration extends TierToolConfiguration {

		public ToolConfiguration(Builder builder, String modId) {
			super(builder, modId);
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
