package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
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
		return 512.0;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 3.0;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 32.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 0.25;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 32.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 16000;
	}
}
