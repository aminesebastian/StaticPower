package theking530.staticpower.data.tiers.copper;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.cables.TierHeatCableConfiguration;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierCopper extends StaticCoreTier {

	public StaticCoreTierCopper(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "copper");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.copper";
	}

	@Override
	protected float getHeatsinkOverheatTemperature() {
		return 512.0f;
	}

	@Override
	protected float getHeatsinkMaximumTemperature() {
		return 1024.0f;
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 6f;
	}

	public static class HeatCableConfiguration extends TierHeatCableConfiguration {
		public HeatCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected float getHeatCableCapacity() {
			return 512.0f;
		}

		@Override
		protected float getHeatCableConductivity() {
			return 6;
		}
	}

	@Override
	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new HeatCableConfiguration(builder, modId);
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
			return 100;
		}

		@Override
		protected double getHammerSwingSpeed() {
			return -3;

		}

		@Override
		protected double getHammerDamage() {
			return 6;
		}

		@Override
		protected int getHammerCooldown() {
			return 40;
		}

		@Override
		protected int getWireCutterUses() {
			return 100;
		}
	}
}
