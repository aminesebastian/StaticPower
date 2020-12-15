package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierCopper extends StaticPowerTier {

	public StaticPowerTierCopper(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "copper");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.copper";
	}

	@Override
	protected double getHeatSinkCapacity() {
		return 128.0;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 1.0;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 16.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 1.0;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 8.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 8;
	}
}
