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
		return 128;
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
		return 0.25;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 4.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 4;
	}
}
