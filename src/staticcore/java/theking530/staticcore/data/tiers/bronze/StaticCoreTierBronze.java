package theking530.staticcore.data.tiers.bronze;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class StaticCoreTierBronze extends StaticCoreTier {

	public StaticCoreTierBronze(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticCore.MOD_ID, "bronze");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.bronze";
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
