package theking530.staticcore.data.tiers.sapphire;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class StaticCoreTierSapphire extends StaticCoreTier {

	public StaticCoreTierSapphire(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticCore.MOD_ID, "sapphire");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.sapphire";
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
			return 1.5;
		}

		@Override
		protected boolean isHardenedDurabilityBoostAdditive() {
			return false;
		}
	}
}
