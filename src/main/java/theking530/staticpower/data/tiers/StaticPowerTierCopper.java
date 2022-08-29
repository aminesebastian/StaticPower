package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
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
	protected int getHeatSinkCapacity() {
		return 128000;
	}

	@Override
	protected float getHeatSinkConductivity() {
		return 1.5f;
	}

	@Override
	protected int getHeatCableCapacity() {
		return 32000;
	}

	@Override
	protected float getHeatCableConductivity() {
		return 1.5f;
	}

	@Override
	protected int getHeatSinkElectricHeatGeneration() {
		return 8000;
	}

	@Override
	protected int getHeatSinkElectricHeatPowerUsage() {
		return 4000;
	}

	@Override
	protected int getHammerUses() {
		return 100;
	}

	protected double getHammerSwingSpeed() {
		return -3;

	}

	protected double getHammerDamage() {
		return 6;
	}

	protected int getHammerCooldown() {
		return 40;
	}

	@Override
	protected int getWireCutterUses() {
		return 100;
	}
}
