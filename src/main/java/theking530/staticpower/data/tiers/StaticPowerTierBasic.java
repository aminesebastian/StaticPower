package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierBasic extends StaticPowerTier {

	public StaticPowerTierBasic(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "basic");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.basic";
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 1;
	}

	@Override
	protected int getPortableBatteryCapacity() {
		return 500;
	}

	@Override
	protected int getSolarPanelPowerGeneration() {
		return 2;
	}

	@Override
	protected int getSolarPanelPowerStorage() {
		return 4;
	}

	@Override
	protected int getPumpRate() {
		return 100;
	}

	@Override
	protected int getCableFilterSlots() {
		return 3;
	}

	@Override
	protected int getCableExtractorRate() {
		return 40;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 4;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 32;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 3;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return .2;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 40;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 4;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 1;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return .2;
	}

	@Override
	protected int getCablePowerCapacity() {
		return 8;
	}

	@Override
	protected int getCablePowerDelivery() {
		return 8;
	}

	@Override
	protected int getCableIndustrialPowerCapacity() {
		return 64;
	}

	@Override
	protected int getCableIndustrialPowerDelivery() {
		return 64;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 4096;
	}

	@Override
	protected int getBatteryCapacity()  {
		return 5000;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return .4;
	}

	@Override
	protected int getItemFilterSlots() {
		return 3;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.1f;
	}

	@Override
	protected double getItemCableFriction() {
		return 2.5f;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 3.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 1.5f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 2.0f;
	}

	@Override
	public double getHeatCapacityUpgrade() {
		return 0.5f;
	}

	@Override
	public double getHeatConductivityUpgrade() {
		return 0.5f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 1.5f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 1.25f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.25f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 750;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 2.0f;
	}

	@Override
	protected int getDefaultMachinePowerCapacity() {
		return 500;
	}

	@Override
	protected int getDefaultMachinePowerInput() {
		return 8;
	}

	@Override
	protected int getDefaultMachinePowerOutput() {
		return 8;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 5000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 4000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 100;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 1000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 1.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 108000;
	}
}
