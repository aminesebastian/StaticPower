package theking530.staticpower.data.tiers.gold;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.cables.TierHeatCableConfiguration;

public class StaticPowerTierGold extends StaticPowerTier {

	public StaticPowerTierGold(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "gold");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.gold";
	}

	@Override
	protected int getHeatsinkOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(4096);
	}

	@Override
	protected int getHeatsinkMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(8192);
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 15f;
	}

	@Override
	protected int getHeatSinkElectricHeatGeneration() {
		return 32000;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 16000;
	}

	public static class HeatCableConfiguration extends TierHeatCableConfiguration {
		public HeatCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getHeatCableCapacity() {
			return CapabilityHeatable.convertHeatToMilliHeat(2048);
		}

		@Override
		protected float getHeatCableConductivity() {
			return 15;
		}
	}

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new HeatCableConfiguration(builder);
	}
}
