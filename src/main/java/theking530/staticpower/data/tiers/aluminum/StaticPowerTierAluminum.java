package theking530.staticpower.data.tiers.aluminum;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.cables.TierHeatCableConfiguration;

public class StaticPowerTierAluminum extends StaticPowerTier {

	public StaticPowerTierAluminum(Builder builder) {
		super(builder);
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
	protected int getHeatsinkOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(256);
	}

	@Override
	protected int getHeatsinkMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(512);
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 3f;
	}

	@Override
	protected int getHeatSinkElectricHeatGeneration() {
		return 1000;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 1000;
	}

	public static class HeatCableConfiguration extends TierHeatCableConfiguration {
		public HeatCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getHeatCableCapacity() {
			return CapabilityHeatable.convertHeatToMilliHeat(256);
		}

		@Override
		protected float getHeatCableConductivity() {
			return 3;
		}
	}

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new HeatCableConfiguration(builder);
	}

}
