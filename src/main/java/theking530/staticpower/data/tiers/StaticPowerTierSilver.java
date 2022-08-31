package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

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

	protected int getHeatsinkOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(2048);
	}

	protected int getHeatsinkMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(4096);
	}
	@Override
	protected float getHeatSinkConductivity() {
		return 12f;
	}

	@Override
	protected int getHeatCableCapacity() {
		return CapabilityHeatable.convertHeatToMilliHeat(1024);
	}

	@Override
	protected float getHeatCableConductivity() {
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
}
