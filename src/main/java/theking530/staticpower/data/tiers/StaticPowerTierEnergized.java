package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierEnergized extends StaticPowerTier {

	public StaticPowerTierEnergized(Builder builder) {
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
		return 4;
	}

	@Override
	protected long getPortableBatteryCapacity() {
		return 5000000;
	}

	@Override
	protected long getSolarPanelPowerGeneration() {
		return 8000;
	}

	@Override
	protected long getSolarPanelPowerStorage() {
		return 16000;
	}

	@Override
	protected int getPumpRate() {
		return 40;
	}

	@Override
	protected int getCableFilterSlots() {
		return 9;
	}

	@Override
	protected int getCableExtractorRate() {
		return 20;
	}

	@Override
	protected int getCableExtractionStackSize() {
		return 32;
	}

	@Override
	protected int getCableExtractionFluidRate() {
		return 512;
	}

	@Override
	protected int getCableExtractionFilterSlots() {
		return 9;
	}

	@Override
	protected double getExtractedItemInitialSpeed() {
		return 2;
	}

	@Override
	protected int getCableRetrievalRate() {
		return 20;
	}

	@Override
	protected int getCableRetrievalStackSize() {
		return 32;
	}

	@Override
	protected int getCableRetrievalFilterSlots() {
		return 4;
	}

	@Override
	protected double getRetrievedItemInitialSpeed() {
		return 2;
	}

	@Override
	protected long getCablePowerCapacity() {
		return 512000;
	}

	@Override
	protected long getCablePowerDelivery() {
		return 512000;
	}

	@Override
	protected long getCableIndustrialPowerCapacity() {
		return 4096000;
	}

	@Override
	protected long getCableIndustrialPowerDelivery() {
		return 4096000;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 131072;
	}

	@Override
	protected long getBatteryCapacity() {
		return 50000000;
	}

	@Override
	protected long getBatteryMaxIO() {
		return 1024000;
	}

	@Override
	protected double getItemCableMaxSpeed() {
		return 2;
	}

	@Override
	protected int getItemFilterSlots() {
		return 9;
	}

	@Override
	protected double getItemCableAcceleration() {
		return 1.75f;
	}

	@Override
	protected double getItemCableFriction() {
		return 1.75f;
	}

	@Override
	protected int getDrillBitUses() {
		return 12000;
	}

	@Override
	protected int getChainsawBladeUses() {
		return 24000;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 7.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 8.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 7.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 5.0f;
	}

	@Override
	public double getHeatCapacityUpgrade() {
		return 2.0f;
	}

	@Override
	public double getHeatConductivityUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 7.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 5.5f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 2.0f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1250;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 5.0f;
	}

	@Override
	protected long getDefaultMachinePowerCapacity() {
		return 5000000;
	}

	@Override
	protected long getDefaultMachinePowerInput() {
		return 64000;
	}

	@Override
	protected long getDefaultMachinePowerOutput() {
		return 64000;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 15000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 32000;
	}

	@Override
	protected int getCableFluidCapacity() {
		return 2500;
	}

	@Override
	protected int getCableIndustrialFluidCapacity() {
		return 25000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 2.5;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 864000;
	}
	
	@Override
	protected long getMagnetPowerCapacity() {
		return getPortableBatteryCapacity() * 2;
	}

	@Override
	protected int getMagnetRadius() {
		return 6;
	}
}
