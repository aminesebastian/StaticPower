package theking530.staticpower.data.tiers.silver;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.cables.TierHeatCableConfiguration;

public class StaticPowerTierSilver extends StaticPowerTier {

	public StaticPowerTierSilver(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "silver");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.silver";
	}

	@Override
	protected int getHeatsinkOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(2048);
	}

	@Override
	protected int getHeatsinkMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(4096);
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 12f;
	}

	@Override
	protected int getHeatSinkElectricHeatGeneration() {
		return 16000;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 8000;
	}

	public static class HeatCableConfiguration extends TierHeatCableConfiguration {
		public HeatCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getHeatCableCapacity() {
			return CapabilityHeatable.convertHeatToMilliHeat(1024);
		}

		@Override
		protected float getHeatCableConductivity() {
			return 12;
		}
	}

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new HeatCableConfiguration(builder);
	}
}
