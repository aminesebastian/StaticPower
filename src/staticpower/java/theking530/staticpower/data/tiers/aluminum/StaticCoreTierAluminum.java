package theking530.staticpower.data.tiers.aluminum;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.cables.TierHeatCableConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierAluminum extends StaticCoreTier {

	public StaticCoreTierAluminum(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "aluminum");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.aluminum";
	}

	@Override
	protected float getHeatsinkOverheatTemperature() {
		return 256.0f;
	}

	@Override
	protected float getHeatsinkMaximumTemperature() {
		return 512.0f;
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 100f;
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
			return 200;
		}
	}

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new HeatCableConfiguration(builder, modId);
	}

}
