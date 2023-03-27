package theking530.staticcore.data.tiers.zinc;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class StaticCoreTierZinc extends StaticCoreTier {

	public StaticCoreTierZinc(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticCore.MOD_ID, "zinc");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.zinc";
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
