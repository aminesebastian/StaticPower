package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierAluminium extends StaticPowerTier {

	public StaticPowerTierAluminium(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "aluminium");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.aluminium";
	}

	@Override
	protected double getHeatSinkCapacity() {
		return 32.0;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 0.5;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 4.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 0.5;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 1.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 1000;
	}
}
