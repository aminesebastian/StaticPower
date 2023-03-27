package theking530.staticcore.data.tiers.emerald;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class StaticCoreTierEmerald extends StaticCoreTier {

	public StaticCoreTierEmerald(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticCore.MOD_ID, "diamond");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.emerald";
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
			return 10000.0;
		}

		@Override
		protected boolean isHardenedDurabilityBoostAdditive() {
			return true;
		}
	}
}
