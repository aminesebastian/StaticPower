package theking530.staticpower.data.tiers;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierCreative extends StaticPowerTier {

	public StaticPowerTierCreative(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "creative");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.creative";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 6;
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return Double.MAX_VALUE;
	}

	@Override
	protected int getPumpRate() {
		return 10;
	}

	@Override
	protected double getCablePowerMaxCurrent() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getCablePowerResistancePerBlock() {
		return 0;
	}

	@Override
	protected double getCableIndustrialPowerMaxCurrent() {
		return Double.MAX_VALUE;
	}

	@Override
	protected double getCableIndustrialPowerResistancePerBlock() {
		return 0;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected double getBatteryCapacity() {
		return Double.MAX_VALUE;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 10;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.0f;
	}

	@Override
	protected double getItemCableFriction() {
		return 0;
	}

	@Override
	protected int getDrillBitUses() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getChainsawBladeUses() {
		return Integer.MAX_VALUE;
	}

	protected double getDefaultMachinePowerCapacity() {
		return Double.MAX_VALUE;
	}

	protected List<Double> internalGetDefaultMachineInputVoltageRange() {
		return Arrays.asList(0.0, Double.MAX_VALUE);
	}

	protected double getDefaultMachineMaximumCurrent() {
		return Double.MAX_VALUE;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getCapsuleCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getCableFluidCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 5.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getHammerUses() {
		return Integer.MAX_VALUE;
	}

	protected double getHammerSwingSpeed() {
		return 0;

	}

	protected double getHammerDamage() {
		return 10;
	}

	protected int getHammerCooldown() {
		return 1;
	}

	@Override
	protected int getWireCutterUses() {
		return Integer.MAX_VALUE;
	}
}
