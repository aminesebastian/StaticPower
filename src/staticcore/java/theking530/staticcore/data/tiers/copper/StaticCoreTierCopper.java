package theking530.staticcore.data.tiers.copper;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.cables.TierHeatCableConfiguration;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;

public class StaticCoreTierCopper extends StaticCoreTier {

	public StaticCoreTierCopper(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticCore.MOD_ID, "copper");
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
		public HeatCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
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
