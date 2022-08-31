package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

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

	protected int getHeatsinkOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(4096);
	}

	protected int getHeatsinkMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(8192);
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 15f;
	}

	@Override
	protected int getHeatCableCapacity() {
		return CapabilityHeatable.convertHeatToMilliHeat(2048);
	}

	@Override
	protected float getHeatCableConductivity() {
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
}
