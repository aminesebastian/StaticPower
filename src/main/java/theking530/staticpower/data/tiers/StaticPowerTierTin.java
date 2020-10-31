package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierTin extends StaticPowerTier {

	public StaticPowerTierTin(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "tin");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.tin";
	}

	@Override
	protected double getHeatSinkCapacity() {
		return 35;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 30.0;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 5000.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 50.0;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 25.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 2;
	}
}
