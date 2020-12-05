package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierAdvanced extends StaticPowerTier {

	public StaticPowerTierAdvanced(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "advanced");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.advanced";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 2;
	}

	@Override
	protected int getPortableBatteryCapacity() {
		return 1000;
	}

	@Override
	protected int getSolarPanelPowerGeneration() {
		return 8;
	}

	@Override
	protected int getSolarPanelPowerStorage() {
		return 16;
	}

	@Override
	protected int getPumpRate() {
		return 80;
	}

	@Override
	protected int getCableFilterSlots() {
		return 5;
	}

	@Override
	protected int getCableExtractorRate() {
		return 40;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 8;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 64;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 5;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return 0.5;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 40;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 8;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 2;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return 0.5;
	}

	@Override
	protected int getCablePowerCapacity() {
		return 32;
	}

	@Override
	protected int getCablePowerDelivery() {
		return 16;
	}

	@Override
	protected int getDigistoreCapacity() {
		return 16384;
	}

	@Override
	protected int getBatteryCapacity() {
		return 10000;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 1.0;
	}

	@Override
	protected int getItemFilterSlots() {
		return 5;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.25f;
	}

	@Override
	protected double getItemCableFriction() {
		return 2.25f;
	}

	@Override
	protected int getDrillBitUses() {
		return 6500;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 13000;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 4.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 2.5f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 1.5f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.5f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 850;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 3.0f;
	}

	@Override
	protected int getDefaultMachinePowerCapacity() {
		return 1000;
	}

	@Override
	protected int getDefaultMachinePowerInput() {
		return 20;
	}

	@Override
	protected int getDefaultMachinePowerOutput() {
		return 20;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 7500;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 8000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 250;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 2500;
	}
}
