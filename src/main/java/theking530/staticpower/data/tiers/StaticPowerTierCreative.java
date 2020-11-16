package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierCreative extends StaticPowerTier {

	public StaticPowerTierCreative(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "energized");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.energized";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 6;
	}

	@Override
	protected int getPortableBatteryCapacity() {
		return 2147483647;
	}

	@Override
	protected int getSolarPanelPowerGeneration() {
		return 2147483647;
	}

	@Override
	protected int getSolarPanelPowerStorage() {
		return 2147483647;
	}

	@Override
	protected int getCablePowerCapacity() {
		return 2147483647;
	}

	@Override
	protected int getCablePowerDelivery() {
		return 2147483647;
	}

	@Override
	protected int getDigistoreCapacity() {
		return 2147483647;
	}

	@Override
	protected int getBatteryCapacity() {
		return 2147483647;
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
		return 2147483647;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 2147483647;
	}

	@Override
	protected int getDefaultMachinePowerCapacity() {
		return 2147483647;
	}

	@Override
	protected int getDefaultMachinePowerInput() {
		return 2147483647;
	}

	@Override
	protected int getDefaultMachinePowerOutput() {
		return 2147483647;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 2147483647;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 2147483647;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 2147483647;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 2147483647;
	}
}
