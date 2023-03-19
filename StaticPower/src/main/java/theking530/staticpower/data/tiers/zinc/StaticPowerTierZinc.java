package theking530.staticpower.data.tiers.zinc;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticPowerTierZinc extends StaticPowerTier {

	public StaticPowerTierZinc(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "zinc");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.zinc";
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
		protected int getHammerUses() {
			return 300;
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
			return 50;
		}

		@Override
		protected int getWireCutterUses() {
			return 300;
		}
	}
}
