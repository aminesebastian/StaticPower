package theking530.staticpower.data.tiers.copper;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierHeatCableConfiguration;

public class StaticPowerTierCopper extends StaticPowerTier {

	public StaticPowerTierCopper(Builder builder) {
		super(builder);
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
	protected int getHeatsinkOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(512);
	}

	@Override
	protected int getHeatsinkMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(1024);
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 6f;
	}

	@Override
	protected int getHeatSinkElectricHeatGeneration() {
		return 8000;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 4000;
	}

	public static class HeatCableConfiguration extends TierHeatCableConfiguration {
		public HeatCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getHeatCableCapacity() {
			return CapabilityHeatable.convertHeatToMilliHeat(512);
		}

		@Override
		protected float getHeatCableConductivity() {
			return 6;
		}
	}

	@Override
	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new HeatCableConfiguration(builder);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new ToolConfiguration(builder);
	}

	public class ToolConfiguration extends TierToolConfiguration {

		public ToolConfiguration(Builder builder) {
			super(builder);
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
