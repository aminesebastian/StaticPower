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
		return 512;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 8.0;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 64.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 1.0;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 32.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 32;
	}
}
