package theking530.staticpower.data.tiers.gold;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.cables.TierHeatCableConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierGold extends StaticCoreTier {

	public StaticCoreTierGold(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "gold");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.gold";
	}

	public static class HeatCableConfiguration extends TierHeatCableConfiguration {
		public HeatCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected float getHeatCableCapacity() {
			return 1024.0f;
		}

		@Override
		protected float getHeatCableConductivity() {
			return 300;
		}
	}

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new HeatCableConfiguration(builder, modId);
	}
}
