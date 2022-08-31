package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

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


	protected int getHeatsinkOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(256);
	}

	protected int getHeatsinkMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(512);
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 3f;
	}

	@Override
	protected int getHeatCableCapacity() {
		return CapabilityHeatable.convertHeatToMilliHeat(256);
	}

	@Override
	protected float getHeatCableConductivity() {
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
}
