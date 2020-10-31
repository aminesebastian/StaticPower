package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
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

	@Override
	protected double getHeatSinkCapacity() {
		return 100;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 40.0;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 10000.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 50.0;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 75.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 25;
	}
}
