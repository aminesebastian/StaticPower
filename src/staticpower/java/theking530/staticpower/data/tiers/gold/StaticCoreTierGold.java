package theking530.staticpower.data.tiers.gold;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
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
		public HeatCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
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

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new HeatCableConfiguration(builder, modId);
	}
}
