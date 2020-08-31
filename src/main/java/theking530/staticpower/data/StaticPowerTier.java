package theking530.staticpower.data;

import net.minecraft.util.ResourceLocation;

public class StaticPowerTier {
	private ResourceLocation tierId;
	private String unlocalizedTierName;

	/***********
	 * Digistore
	 ***********/
	private int digistoreCapacity;

	/*************
	 * Solar Panel
	 *************/
	private int solarPanelPowerGeneration;
	private int solarPanelPowerStorage;

	/***************************
	 * Extraction Configuration
	 ***************************/
	private int cableExtractorRate;
	private int cableExtractionStackSize;
	private int cableExtractionFluidRate;
	private int cableExtractionFilterSlots;

	/*************************
	 * Retrieval Configuration
	 *************************/
	private int cableRetrievalRate;
	private int cableRetrievalStackSize;
	private int cableRetrievalFilterSlots;

	/**********************
	 * Filter Configuration
	 **********************/
	private int cableFilterSlots;

	/***************************
	 * Power Cable Configuration
	 ***************************/
	private int cablePowerCapacity;
	private int cablePowerDelivery;

	/**************************
	 * Item Cable Configuration
	 **************************/
	private float itemCableAcceleration;
	private float itemCableFriction;
	private int itemCableMaxSpeed;

	/********************
	 * Heat Configuration
	 ********************/
	private float heatCableCapacity;
	private float heatCableConductivity;
	private float heatSinkCapacity;
	private float heatSinkConductivity;
	private float heatSinkElectricHeatGeneration;
	private int heatSinkElectricHeatPowerUsage;

	/*********************
	 * Power Configuration
	 *********************/
	private int batteryCapacity;
	private int portableBatteryCapacity;

	/***********************
	 * Machine Configuration
	 ***********************/
	private int defaultMachinePowerCapacity;
	private int defaultMachinePowerInput;
	private int defaultMachinePowerOutput;

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

	/********************
	 * Centrifuge Upgrade
	 ********************/
	private int maxCentrifugeSpeedUpgrade;
	private float centrifugeUpgradedPowerIncrease;

	/*******
	 * Misc
	 *******/
	private int defaultTankCapacity;
	private int capsuleCapacity;
	private int drillBitUses;
	private int itemFilterSlots;

	private int upgradeOrdinal;

	public int getDefaultTankCapacity() {
		return defaultTankCapacity;
	}

	public int getCapsuleCapacity() {
		return capsuleCapacity;
	}

	public int getDefaultMachinePowerCapacity() {
		return defaultMachinePowerCapacity;
	}

	public int getDefaultMachinePowerInput() {
		return defaultMachinePowerInput;
	}

	public int getDefaultMachinePowerOutput() {
		return defaultMachinePowerOutput;
	}

	public float getHeatSinkElectricHeatGeneration() {
		return heatSinkElectricHeatGeneration;
	}

	public int getHeatSinkElectricHeatPowerUsage() {
		return heatSinkElectricHeatPowerUsage;
	}

	public int getMaxCentrifugeSpeedUpgrade() {
		return maxCentrifugeSpeedUpgrade;
	}

	public float getCentrifugeUpgradedPowerIncrease() {
		return centrifugeUpgradedPowerIncrease;
	}

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

	public int getCableFilterSlots() {
		return cableFilterSlots;
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

	public int getCableExtractionFilterSlots() {
		return cableExtractionFilterSlots;
	}

	public int getCableRetrievalRate() {
		return cableRetrievalRate;
	}

	public int getCableRetrievalStackSize() {
		return cableRetrievalStackSize;
	}

	public int getCableRetrievalFilterSlots() {
		return cableRetrievalFilterSlots;
	}

}
