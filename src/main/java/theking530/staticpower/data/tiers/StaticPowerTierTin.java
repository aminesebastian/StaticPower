package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
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
		return 64.0;
	}

	@Override
	protected double getHeatSinkConductivity() {
		return 0.5;
	}

	@Override
	protected double getHeatCableCapacity() {
		return 8.0;
	}

	@Override
	protected double getHeatCableConductivity() {
		return 0.1;
	}

	@Override
	protected double getHeatSinkElectricHeatGeneration() {
		return 4.0;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 2000;
	}

	@Override
	protected int getHammerUses() {
		return 200;
	}

	protected double getHammerSwingSpeed() {
		return -3;

	}

	protected double getHammerDamage() {
		return 4;
	}

	protected int getHammerCooldown() {
		return 40;
	}

	@Override
	protected int getWireCutterUses() {
		return 200;
	}
}
