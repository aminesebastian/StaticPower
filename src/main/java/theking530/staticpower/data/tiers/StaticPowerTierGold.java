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
	protected int getHeatSinkCapacity() {
		return 512000;
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 3f;
	}

	@Override
	protected int getHeatCableCapacity() {
		return 256000;
	}

	@Override
	protected float getHeatCableConductivity() {
		return 3f;
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
