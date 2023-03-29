package theking530.staticpower.data.tiers.iron;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierIron extends StaticCoreTier {

	public StaticCoreTierIron(Builder builder, String modId) {
		super(builder, modId);
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
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new ToolConfiguration(builder, modId);
	}

	public class ToolConfiguration extends TierToolConfiguration {

		public ToolConfiguration(Builder builder, String modId) {
			super(builder, modId);
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
