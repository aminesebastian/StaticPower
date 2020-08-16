package theking530.staticpower.data;

import net.minecraft.util.ResourceLocation;

public class StaticPowerTier {
	private ResourceLocation tierId;
	private String unlocalizedTierName;
	private int portableBatteryCapacity;
	private int digistoreCapacity;
	private int solarPanelPowerGeneration;
	private int solarPanelPowerStorage;
	private int cableExtractorRate;
	private int cableExtractionStackSize;
	private int cableExtractionFluidRate;
	private int cableExtractionFilterSize;
	private int cableRetrievalRate;
	private int cableRetrievalStackSize;
	private int cableRetrievalFilterSize;
	private int cableFilterSize;
	private int cablePowerCapacity;
	private int cablePowerDelivery;
	private int batteryCapacity;
	private int itemCableMaxSpeed;
	private int itemFilterSlots;
	private float itemCableAcceleration;
	private float itemCableFriction;
	private int drillBitUses;
	private float heatCableCapacity;
	private float heatCableConductivity;
	private float heatSinkCapacity;
	private float heatSinkConductivity;

	private int upgradeOrdinal;

	/********************
	 * Processing Upgrade
	 ********************/
	private float processingSpeedUpgrade;
	private float processingSpeedPowerCost;

	/**************
	 * Tank Upgrade
	 **************/
	private float tankCapacityUpgrade;

	/***************
	 * Range Upgrade
	 ***************/
	private float rangeUpgrade;

	/***************
	 * Power Upgrade
	 ***************/
	private float powerUpgrade;
	private float powerIoUpgrade;

	/***************************
	 * Output Multiplier Upgrade
	 ***************************/
	private float outputMultiplierUpgrade;
	private float outputMultiplierPowerCostUpgrade;

	public float getOutputMultiplierPowerCostUpgrade() {
		return outputMultiplierPowerCostUpgrade;
	}

	public float getOutputMultiplierUpgrade() {
		return outputMultiplierUpgrade;
	}

	public float getPowerUpgrade() {
		return powerUpgrade;
	}

	public float getPowerIoUpgrade() {
		return powerIoUpgrade;
	}

	public float getRangeUpgrade() {
		return rangeUpgrade;
	}

	public float getTankCapacityUpgrade() {
		return tankCapacityUpgrade;
	}

	public float getProcessingSpeedUpgrade() {
		return processingSpeedUpgrade;
	}

	public float getProcessingSpeedPowerCost() {
		return processingSpeedPowerCost;
	}

	public int getUpgradeOrdinal() {
		return upgradeOrdinal;
	}

	public float getHeatSinkCapacity() {
		return heatSinkCapacity;
	}

	public float getHeatSinkConductivity() {
		return heatSinkConductivity;
	}

	public float getHeatCableCapacity() {
		return heatCableCapacity;
	}

	public float getHeatCableConductivity() {
		return heatCableConductivity;
	}

	public int getDrillBitUses() {
		return drillBitUses;
	}

	public int getItemFilterSlots() {
		return itemFilterSlots;
	}

	public int getItemCableMaxSpeed() {
		return itemCableMaxSpeed;
	}

	public float getItemCableAcceleration() {
		return itemCableAcceleration;
	}

	public float getItemCableFriction() {
		return itemCableFriction;
	}

	public int getBatteryCapacity() {
		return batteryCapacity;
	}

	public int getCablePowerCapacity() {
		return cablePowerCapacity;
	}

	public int getCablePowerDelivery() {
		return cablePowerDelivery;
	}

	public int getCableFilterSize() {
		return cableFilterSize;
	}

	public ResourceLocation getTierId() {
		return tierId;
	}

	public String getUnlocalizedTierName() {
		return unlocalizedTierName;
	}

	public int getPortableBatteryCapacity() {
		return portableBatteryCapacity;
	}

	public int getDigistoreCapacity() {
		return digistoreCapacity;
	}

	public int getSolarPanelPowerGeneration() {
		return solarPanelPowerGeneration;
	}

	public int getSolarPanelPowerStorage() {
		return solarPanelPowerStorage;
	}

	public int getCableExtractorRate() {
		return cableExtractorRate;
	}

	public int getCableExtractionStackSize() {
		return cableExtractionStackSize;
	}

	public int getCableExtractionFluidRate() {
		return cableExtractionFluidRate;
	}

	public int getCableExtractionFilterSize() {
		return cableExtractionFilterSize;
	}

	public int getCableRetrievalRate() {
		return cableRetrievalRate;
	}

	public int getCableRetrievalStackSize() {
		return cableRetrievalStackSize;
	}

	public int getCableRetrievalFilterSize() {
		return cableRetrievalFilterSize;
	}

}
