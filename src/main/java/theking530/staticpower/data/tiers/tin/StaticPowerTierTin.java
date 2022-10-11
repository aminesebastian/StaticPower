package theking530.staticpower.data.tiers.tin;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticPowerTierTin extends StaticPowerTier {

	public StaticPowerTierTin(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "tin");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.tin";
	}

	public class ToolConfiguration extends TierToolConfiguration {

		public ToolConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getHammerUses() {
			return 200;
		}

		@Override
		protected double getHammerSwingSpeed() {
			return -3;

		}

		@Override
		protected double getHammerDamage() {
			return 4;
		}

		@Override
		protected int getHammerCooldown() {
			return 40;
		}

		@Override
		protected int getWireCutterUses() {
			return 200;
		}
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new ToolConfiguration(builder);
	}
}
