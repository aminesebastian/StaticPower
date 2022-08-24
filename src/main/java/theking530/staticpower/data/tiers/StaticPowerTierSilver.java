package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
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

	@Override
	protected double getHeatSinkCapacity() {
		return 256.0;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 2.0;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 8.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 0.2;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 16.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 8000;
	}
}
