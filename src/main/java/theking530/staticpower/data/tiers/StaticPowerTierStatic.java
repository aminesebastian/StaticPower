package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierStatic extends StaticPowerTier {

	public StaticPowerTierStatic(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "static");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.static";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 3;
	}

	@Override
	protected int getPortableBatteryCapacity() {
		return 2500;
	}

	@Override
	protected int getSolarPanelPowerGeneration() {
		return 16;
	}

	@Override
	protected int getSolarPanelPowerStorage() {
		return 32;
	}

	@Override
	protected int getCableFilterSlots() {
		return 7;
	}

	@Override
	protected int getCableExtractorRate() {
		return 20;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 16;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 256;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 7;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return 1.0;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 20;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 16;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 3;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return 1.0;
	}

	@Override
	protected int getCablePowerCapacity() {
		return 128;
	}

	@Override
	protected int getCablePowerDelivery() {
		return 64;
	}

	@Override
	protected int getDigistoreCapacity() {
		return 65536;
	}

	@Override
	protected int getBatteryCapacity() {
		return 25000;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 1.0;
	}

	@Override
	protected int getItemFilterSlots() {
		return 7;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.4f;
	}

	@Override
	protected double getItemCableFriction() {
		return 2.0f;
	}

	@Override
	protected int getDrillBitUses() {
		return 8000;
	}
	@Override
	protected int getChainsawBladeUses() {
		return 16000;
	}
	@Override
	protected double getProcessingSpeedUpgrade() {
		return 5.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 6.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 5.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 5.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 4.5f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.75f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1000;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 4.0f;
	}

	@Override
	protected int getDefaultMachinePowerCapacity() {
		return 2500;
	}

	@Override
	protected int getDefaultMachinePowerInput() {
		return 30;
	}

	@Override
	protected int getDefaultMachinePowerOutput() {
		return 30;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 10000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 16000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 1000;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 10000;
	}
}
