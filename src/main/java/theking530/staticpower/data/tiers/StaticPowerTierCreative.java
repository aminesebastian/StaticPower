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
	protected long getPortableBatteryCapacity() {
		return 2147483647;
	}

	@Override
	protected long getSolarPanelPowerGeneration() {
		return 2147483647;
	}

	@Override
	protected long getSolarPanelPowerStorage() {
		return 2147483647;
	}

	@Override
	protected int getPumpRate() {
		return 10;
	}

	@Override
	protected long getCablePowerCapacity() {
		return 2147483647;
	}

	@Override
	protected long getCablePowerDelivery() {
		return 2147483647;
	}

	@Override
	protected long getCableIndustrialPowerCapacity() {
		return 2147483647;
	}

	@Override
	protected long getCableIndustrialPowerDelivery() {
		return 2147483647;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 2147483647;
	}

	@Override
	protected long getBatteryCapacity() {
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
	protected long getDefaultMachinePowerCapacity() {
		return 2147483647;
	}

	@Override
	protected long getDefaultMachinePowerInput() {
		return 2147483647;
	}

	@Override
	protected long getDefaultMachinePowerOutput() {
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

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 5.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return Integer.MAX_VALUE;
	}
}
