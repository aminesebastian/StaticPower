package theking530.staticpower.data.tiers.iron;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;

public class StaticPowerTierIron extends StaticPowerTier {

	public StaticPowerTierIron(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "iron");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.iron";
	}

	@Override
	protected int getPumpRate() {
		return 120;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 2000;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 2500;
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
		protected int getDrillBitUses() {
			return 2000;
		}

		@Override
		protected int getChainsawBladeUses() {
			return 4000;
		}

		@Override
		protected int getHammerUses() {
			return 250;
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
			return 500;
		}
	}
}