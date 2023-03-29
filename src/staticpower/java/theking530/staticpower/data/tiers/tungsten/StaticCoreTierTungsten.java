package theking530.staticpower.data.tiers.tungsten;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierTungsten extends StaticCoreTier {

	public StaticCoreTierTungsten(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "tungsten");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.tungsten";
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
			return 48000;
		}

		@Override
		protected int getChainsawBladeUses() {
			return 96000;
		}

		@Override
		protected int getHammerUses() {
			return 1000;
		}

		@Override
		protected double getHammerSwingSpeed() {
			return -1;
		}

		@Override
		protected double getHammerDamage() {
			return 8;
		}

		@Override
		protected int getHammerCooldown() {
			return 20;
		}

		@Override
		protected int getWireCutterUses() {
			return 1000;
		}
	}
}
