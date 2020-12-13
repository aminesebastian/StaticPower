package theking530.staticpower.data;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticpower.StaticPower;

public abstract class StaticPowerTier {
	/****************
	 * Tier Settings
	 ****************/
	public ConfigValue<String> tierId;
	public ConfigValue<String> unlocalizedTierName;

	/***********
	 * Digistore
	 ***********/
	public ConfigValue<Integer> digistoreCardCapacity;

	/*************
	 * Solar Panel
	 *************/
	public ConfigValue<Integer> solarPanelPowerGeneration;
	public ConfigValue<Integer> solarPanelPowerStorage;

	/*******
	 * Pump
	 *******/
	public ConfigValue<Integer> pumpRate;

	/***************************
	 * Extraction Configuration
	 ***************************/
	public ConfigValue<Integer> cableExtractorRate;
	public ConfigValue<Integer> cableExtractionStackSize;
	public ConfigValue<Integer> cableExtractionFluidRate;
	public ConfigValue<Integer> cableExtractionFilterSlots;
	public ConfigValue<Double> cableExtractedItemInitialSpeed;

	/*************************
	 * Retrieval Configuration
	 *************************/
	public ConfigValue<Integer> cableRetrievalRate;
	public ConfigValue<Integer> cableRetrievalStackSize;
	public ConfigValue<Integer> cableRetrievalFilterSlots;
	public ConfigValue<Double> cableRetrievedItemInitialSpeed;

	/**********************
	 * Filter Configuration
	 **********************/
	public ConfigValue<Integer> cableFilterSlots;

	/***************************
	 * Power Cable Configuration
	 ***************************/
	public ConfigValue<Integer> cablePowerCapacity;
	public ConfigValue<Integer> cablePowerDelivery;

	/**************************
	 * Item Cable Configuration
	 **************************/
	public ConfigValue<Double> itemCableAcceleration;
	public ConfigValue<Double> itemCableFriction;
	public ConfigValue<Double> itemCableMaxSpeed;

	/***************************
	 * Fluid Cable Configuration
	 ***************************/
	public ConfigValue<Integer> cableFluidCapacity;
	public ConfigValue<Integer> cableIndustrialFluidCapacity;

	/********************
	 * Heat Configuration
	 ********************/
	public ConfigValue<Double> heatCableCapacity;
	public ConfigValue<Double> heatCableConductivity;
	public ConfigValue<Double> heatSinkCapacity;
	public ConfigValue<Double> heatSinkConductivity;
	public ConfigValue<Double> heatSinkElectricHeatGeneration;
	public ConfigValue<Integer> heatSinkElectricHeatPowerUsage;

	/*********************
	 * Power Configuration
	 *********************/
	public ConfigValue<Integer> batteryCapacity;
	public ConfigValue<Integer> portableBatteryCapacity;

	/***********************
	 * Machine Configuration
	 ***********************/
	public ConfigValue<Integer> defaultMachinePowerCapacity;
	public ConfigValue<Integer> defaultMachinePowerInput;
	public ConfigValue<Integer> defaultMachinePowerOutput;

	/********************
	 * Processing Upgrade
	 ********************/
	public ConfigValue<Double> processingSpeedUpgrade;
	public ConfigValue<Double> processingSpeedPowerCost;

	/**************
	 * Tank Upgrade
	 **************/
	public ConfigValue<Double> tankCapacityUpgrade;

	/***************
	 * Range Upgrade
	 ***************/
	public ConfigValue<Double> rangeUpgrade;

	/***************
	 * Power Upgrade
	 ***************/
	public ConfigValue<Double> powerUpgrade;
	public ConfigValue<Double> powerIOUpgrade;

	/************************
	 * Heat Capacity Upgrade
	 ************************/
	public ConfigValue<Double> heatCapacityUpgrade;

	/************************
	 * Heat Conductivity Upgrade
	 ************************/
	public ConfigValue<Double> heatConductivityUpgrade;

	/***************************
	 * Output Multiplier Upgrade
	 ***************************/
	public ConfigValue<Double> outputMultiplierUpgrade;
	public ConfigValue<Double> outputMultiplierPowerCostUpgrade;

	/********************
	 * Centrifuge Upgrade
	 ********************/
	public ConfigValue<Integer> maxCentrifugeSpeedUpgrade;
	public ConfigValue<Double> centrifugeUpgradedPowerIncrease;

	/********
	 * Tools
	 ********/
	public ConfigValue<Integer> drillBitUses;
	public ConfigValue<Integer> chainsawBladeUses;
	public ConfigValue<Double> hardenedDurabilityBoost;
	public ConfigValue<Boolean> hardenedDurabilityBoostAdditive;

	/*******
	 * Misc
	 *******/
	public ConfigValue<Integer> defaultTankCapacity;
	public ConfigValue<Integer> capsuleCapacity;
	public ConfigValue<Integer> itemFilterSlots;

	public ConfigValue<Integer> upgradeOrdinal;

	public StaticPowerTier(ForgeConfigSpec.Builder builder) {
		// Establish field for the tier Id.
		tierId = builder.comment("The unique id of the tier in the format of 'MOD_ID:TIER_NAME'.").translation(StaticPower.MOD_ID + ".config." + "tierId").define("TierId",
				getTierId().toString());

		// Establish a field for the unlocalized name.
		unlocalizedTierName = builder.comment("The unlocalized name of the tier.").translation(StaticPower.MOD_ID + ".config." + "unlocalizedTierName").define("UnlocalizedTierName",
				getUnlocalizedName());

		builder.push("Digistore");
		digistoreCardCapacity = builder.comment("The number of items that can be contained in a regular digistore card of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "digistoreCardCapacity").define("DigistoreCardCapacity", getDigistoreCapacity());
		builder.pop();

		builder.push("Machines");
		defaultMachinePowerCapacity = builder.comment("The base amount of power a machine of this tier can store.")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachinePowerCapacity").define("DefaultMachinePowerCapacity", this.getDefaultMachinePowerCapacity());

		defaultMachinePowerInput = builder.comment("The base amount of power a machine of this tier can consume from a power providing source (a cable or battery).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachinePowerInput").define("DefaultMachinePowerInput", this.getDefaultMachinePowerInput());

		defaultMachinePowerOutput = builder.comment("The base amount of power that can be extracted or provided by a machine of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachinePowerOutput").define("DefaultMachinePowerOutput", this.getDefaultMachinePowerOutput());

		defaultTankCapacity = builder.comment("The base amount of fluid a machine of this tier can store..").translation(StaticPower.MOD_ID + ".config." + "defaultTankCapacity")
				.define("DefaultTankCapacity", this.getDefaultTankCapacity());

		builder.push("Battery");
		batteryCapacity = builder.comment("The amount of power that a non-portable battery of this tier can store..").translation(StaticPower.MOD_ID + ".config." + "batteryCapacity")
				.define("BatteryCapacity", this.getBatteryCapacity());
		builder.pop();

		builder.push("Solar_Panel");
		solarPanelPowerGeneration = builder.comment("The amount of power generated by a solar panel of this tier per tick.")
				.translation(StaticPower.MOD_ID + ".config." + "solarPanelPowerGeneration").define("SolarPanelPowerGeneration", this.getSolarPanelPowerGeneration());

		solarPanelPowerStorage = builder.comment("The amount of power a solar panel of this tier can store.").translation(StaticPower.MOD_ID + ".config." + "solarPanelPowerStorage")
				.define("SolarPanelPowerStorage", this.getSolarPanelPowerGeneration());
		builder.pop();

		builder.push("Pump");
		pumpRate = builder.comment("The amount of ticks that will elapse between each pump operation. The higher this number, the slower the pump will operate (20 ticks == 1 second).")
				.translation(StaticPower.MOD_ID + ".config." + "pumpRate").define("PumpRate", this.getPumpRate());
		builder.pop();

		builder.push("Heatsink");
		heatSinkCapacity = builder.comment("The amount of heat a heatsink of this tier can store.").translation(StaticPower.MOD_ID + ".config." + "heatSinkCapacity")
				.define("HeatSinkCapacity", this.getHeatSinkCapacity());

		heatSinkConductivity = builder.comment("The conductivity multiplier for a heatsink of this tier. The higher it is, the faster it is able to dissipate heat.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkConductivity").define("HeatSinkConductivity", this.getHeatSinkConductivity());

		heatSinkElectricHeatGeneration = builder.comment("The amount of heat generated per tick by a heatsink of this tier when supplied with power.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkElectricHeatGeneration").define("HeatSinkElectricHeatGeneration", this.getHeatSinkElectricHeatGeneration());

		heatSinkElectricHeatPowerUsage = builder.comment("The amount of power used per tick to generate heat in a heatsink of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkElectricHeatPowerUsage").define("HeatSinkElectricHeatPowerUsage", this.getHeatSinkElectricHeatPowerUsage());
		builder.pop();
		builder.pop();

		builder.push("Cables_Attachments");
		builder.push("Extractor");
		cableExtractorRate = builder.comment("How many ticks inbetween each extraction. The lower, the more frequently it extracts. Lower values impact performance.")
				.translation(StaticPower.MOD_ID + ".config." + "cableExtractorRate").define("CableExtractorRate", this.getCableExtractorRate());

		cableExtractionStackSize = builder.comment("The number of items that are extracted per extraction.").translation(StaticPower.MOD_ID + ".config." + "cableExtractionStackSize")
				.define("CableExtractionStackSize", this.getCableExtractionStackSize());

		cableExtractionFluidRate = builder.comment("The amount of fluid extracted per extraction").translation(StaticPower.MOD_ID + ".config." + "cableExtractionFluidRate")
				.define("CableExtractionFluidRate", this.getCableExtractionFluidRate());

		cableExtractionFilterSlots = builder.comment("The number of filter slots available on an extractor of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "cableExtractionFilterSlots").define("CableExtractionFilterSlots", this.getCableExtractionFilterSlots());

		cableExtractedItemInitialSpeed = builder.comment("The initial speed of the item that is input into the tube when an extractor of this tier extracts an item.")
				.translation(StaticPower.MOD_ID + ".config." + "cableExtractedItemInitialSpeed").define("CableExtractedItemInitialSpeed", this.getExtractedItemInitialSpeed());
		builder.pop();

		builder.push("Retriever");
		cableRetrievalRate = builder.comment("How many ticks inbetween each extraction. The lower, the more frequently it extracts. Lower values impact performance.")
				.translation(StaticPower.MOD_ID + ".config." + "cableRetrievalRate").define("CableRetrievalRate", this.getCableRetrievalRate());

		cableRetrievalStackSize = builder.comment("The number of items that are retrieved per operation.").translation(StaticPower.MOD_ID + ".config." + "cableRetrievalStackSize")
				.define("CableRetrievalStackSize", this.getCableRetrievalStackSize());

		cableRetrievalFilterSlots = builder.comment("The number of filter slots available on a retriever of this tier. Higher numbers will impact performance.")
				.translation(StaticPower.MOD_ID + ".config." + "cableRetrievalFilterSlots").define("CableRetrievalFilterSlots", this.getCableRetrievalFilterSlots());

		cableRetrievedItemInitialSpeed = builder.comment("The initial speed of the item that is input into the tube when an retriever of this tier retrieves an item.")
				.translation(StaticPower.MOD_ID + ".config." + "cableRetrievedItemInitialSpeed").define("CableRetrievedItemInitialSpeed", this.getRetrievedItemInitialSpeed());
		builder.pop();

		builder.push("Filter");
		cableFilterSlots = builder.comment("The number slots in a filter of this tier.").translation(StaticPower.MOD_ID + ".config." + "cableFilterSlots").define("CableFilterSlots",
				this.getCableFilterSlots());
		builder.pop();
		builder.pop();

		/********
		 * Cables
		 ********/
		builder.push("Cables");

		/********
		 * Power
		 ********/
		builder.push("Power");
		cablePowerCapacity = builder.comment("The amount of power that a power cable of this tier can store.").translation(StaticPower.MOD_ID + ".config." + "cablePowerCapacity")
				.define("CablePowerCapacity", this.getCablePowerCapacity());

		cablePowerDelivery = builder.comment("The amount of power that a power cable of this tier can supply to a single destination.")
				.translation(StaticPower.MOD_ID + ".config." + "cablePowerDelivery").define("CablePowerDelivery", this.getCablePowerDelivery());
		builder.pop();

		/********
		 * Item
		 ********/
		builder.push("Item");
		itemCableAcceleration = builder.comment("The rate (as a percentage 1.0+) at which items in this tube will accelerate up to the max speed.")
				.translation(StaticPower.MOD_ID + ".config." + "itemCableAcceleration").define("ItemCableAcceleration", this.getItemCableAcceleration());

		itemCableFriction = builder.comment("The rate (as a percentage 1.0+) at which items in this tube will decelerate down to the max speed.")
				.translation(StaticPower.MOD_ID + ".config." + "itemCableFriction").define("ItemCableFriction", this.getItemCableFriction());

		itemCableMaxSpeed = builder.comment(
				"The max speed (in blocks traveled per second) of an item tube of this tier. Faster items will slow down to match this rate, and slower items will speed up to match this rate. This value cannot be higher than 19, otherwise items will cease to transfer.")
				.translation(StaticPower.MOD_ID + ".config." + "itemCableMaxSpeed").define("ItemCableMaxSpeed", this.getItemCableMaxSpeed());
		builder.pop();

		/********
		 * Fluid
		 ********/
		builder.push("Fluid");
		cableFluidCapacity = builder.comment("The amount of fluid that can be stored in a regular fluid pipe of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "cableFluidCapacity").define("CableFluidCapacity", this.getCableFluidCapacity());

		cableIndustrialFluidCapacity = builder.comment("The amount of fluid that can be stored in an industrial fluid pipe of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialFluidCapacity").define("CableIndustrialFluidCapacity", this.getCableIndustrialFluidCapacity());
		builder.pop();

		/********
		 * Heat
		 ********/
		builder.push("Heat");
		heatCableCapacity = builder.comment("The amount of heat that a heat pipe of this tier can store.").translation(StaticPower.MOD_ID + ".config." + "heatCableCapacity")
				.define("HeatCableCapacity", this.getHeatCableCapacity());

		heatCableConductivity = builder
				.comment("The conductivity multiplier for a heat pipe of this tier. The higher it is, the faster it is able to dissipate heat. This value is PER BLOCK SIDE.")
				.translation(StaticPower.MOD_ID + ".config." + "heatCableConductivity").define("HeatCableConductivity", this.getHeatCableConductivity());
		builder.pop();
		builder.pop();

		/********
		 * Items
		 ********/
		builder.push("Items");
		capsuleCapacity = builder.comment("The amount of fluid that can be stored in a fluid capsule of this tier.").translation(StaticPower.MOD_ID + ".config." + "capsuleCapacity")
				.define("CapsuleCapacity", this.getCapsuleCapacity());

		portableBatteryCapacity = builder.comment("The amount of power that can be stored in a portable battery of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryCapacity").define("PortableBatteryCapacity", this.getPortableBatteryCapacity());

		itemFilterSlots = builder.comment("The number of slots that exist on an item filter of this tier (not the filter attachment, the actual item).")
				.translation(StaticPower.MOD_ID + ".config." + "itemFilterSlots").define("ItemFilterSlots", this.getItemFilterSlots());

		/********
		 * Tools
		 ********/
		builder.push("Tools");
		drillBitUses = builder.comment("The number of blocks that can be mined by a drill bit of this tier.").translation(StaticPower.MOD_ID + ".config." + "drillBitUses")
				.define("DrillBitUses", this.getDrillBitUses());
		chainsawBladeUses = builder.comment("The number of blocks that can be mined by a chainsaw blade of this tier.").translation(StaticPower.MOD_ID + ".config." + "chainsawBladeUses")
				.define("ChainsawBladeUses", this.getChainsawBladeUses());

		hardenedDurabilityBoost = builder.comment("The amount of durability gained when the diamond hardened modifier is added.")
				.translation(StaticPower.MOD_ID + ".config." + "diamondHardenedDurabilityBoost").define("DiamondHardenedDurabilityBoost", this.getHardenedDurabilityBoost());
		hardenedDurabilityBoostAdditive = builder.comment("Defines whether the hardened durability boost is additive or multaplicative.")
				.translation(StaticPower.MOD_ID + ".config." + "hardenedDurabilityBoostAdditive").define("HardenedDurabilityBoostAdditive", this.isHardenedDurabilityBoostAdditive());

		builder.pop();

		builder.push("Upgrade");
		upgradeOrdinal = builder.comment(
				"The upgrade ordinal of this tier. Higher value will take precedence. For example, if a machine has both Basic and Energized power upgrades installed, the Energized upgrades will be used when calculating power values because it has the higher upgrade ordinal. In the case of a tie, it comes down to which one appears in later in the upgrade slots.")
				.translation(StaticPower.MOD_ID + ".config." + "upgradeOrdinal").define("UpgradeOrdinal", this.getUpgradeOrdinal());

		/************************
		 * Heat Conductivity Upgrade
		 ************************/
		builder.push("Heat");
		heatCapacityUpgrade = builder.comment("The heat capacity for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "heatCapacityUpgrade").define("HeatCapacityUpgrade", this.getHeatCapacityUpgrade());

		heatConductivityUpgrade = builder.comment("The heat conductivtiy boost for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "heatConductivityUpgrade").define("HeatConductivityUpgrade", this.getHeatConductivityUpgrade());
		builder.pop();

		/********************
		 * Processing Upgrade
		 ********************/
		builder.push("Processing");
		processingSpeedUpgrade = builder.comment("The processing speed upgrade for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "processingSpeedUpgrade").define("ProcessingSpeedUpgrade", this.getProcessingSpeedUpgrade());

		processingSpeedPowerCost = builder.comment("The processing speed power cost penality for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "processingSpeedPowerCost").define("ProcessingSpeedPowerCost", this.getProcessingSpeedPowerCost());
		builder.pop();

		/********************
		 * Power Upgrade
		 ********************/
		builder.push("Power");
		powerUpgrade = builder.comment("The power upgrade multiplier of a power upgrade for a full stack of this tier.").translation(StaticPower.MOD_ID + ".config." + "powerUpgrade")
				.define("PowerUpgrade", this.getPowerUpgrade());

		powerIOUpgrade = builder.comment("The power I/O (input/output) upgrade multiplier for a full stack of a power upgrade of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "powerIOUpgrade").define("PowerIOUpgrade", this.getPowerIoUpgrade());
		builder.pop();

		/********************
		 * Tank Upgrade
		 ********************/
		builder.push("Tank");
		tankCapacityUpgrade = builder.comment("The tank capacity multiplier for a full stack of a tank upgrade of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "tankCapacityUpgrade").define("TankCapacityUpgrade", this.getTankCapacityUpgrade());
		builder.pop();

		/********************
		 * Range Upgrade
		 ********************/
		builder.push("Range");
		rangeUpgrade = builder.comment("The increase in radius in percent for a single range upgrade of this tier.").translation(StaticPower.MOD_ID + ".config." + "rangeUpgrade")
				.define("RangeUpgrade", this.getRangeUpgrade());
		builder.pop();

		/********************
		 * Centrifuge Upgrade
		 ********************/
		builder.push("Centrifuge");
		maxCentrifugeSpeedUpgrade = builder.comment("The new maximum speed set by a centrifuge upgrade of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "maxCentrifugeSpeedUpgrade").define("MaxCentrifugeSpeedUpgrade", this.getMaxCentrifugeSpeedUpgrade());

		centrifugeUpgradedPowerIncrease = builder.comment("The amount of increased power cost due to a centrifuge upgrade of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "centrifugeUpgradedPowerIncrease").define("CentrifugeUpgradedPowerIncrease", this.getCentrifugeUpgradedPowerIncrease());
		builder.pop();

		/********************
		 * Output Upgrade
		 ********************/
		builder.push("Output_Multiplier");
		outputMultiplierUpgrade = builder
				.comment("The multiplier applied to the output change of any probability outputs for a full stack of an output multiplier upgrade (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "outputMultiplierUpgrade").define("OutputMultiplierUpgrade", this.getOutputMultiplierUpgrade());

		outputMultiplierPowerCostUpgrade = builder.comment("The power usage increase for a full stack of an output multiplier upgrade (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "outputMultiplierPowerCostUpgrade").define("OutputMultiplierPowerCostUpgrade", this.getOutputMultiplierPowerCostUpgrade());
		builder.pop();

		builder.pop();
		builder.pop();
	}

	protected abstract ResourceLocation getTierId();

	protected abstract String getUnlocalizedName();

	protected int getCableFluidCapacity() {
		return 0;
	}

	protected int getCableIndustrialFluidCapacity() {
		return 0;
	}

	protected int getDefaultTankCapacity() {
		return 0;
	}

	protected int getCapsuleCapacity() {
		return 0;
	}

	protected int getDefaultMachinePowerCapacity() {
		return 0;
	}

	protected int getDefaultMachinePowerInput() {
		return 0;
	}

	protected int getDefaultMachinePowerOutput() {
		return 0;
	}

	protected double getHeatSinkElectricHeatGeneration() {
		return 0;
	}

	protected int getHeatSinkElectricHeatPowerUsage() {
		return 0;
	}

	protected int getMaxCentrifugeSpeedUpgrade() {
		return 0;
	}

	protected double getCentrifugeUpgradedPowerIncrease() {
		return 0;
	}

	protected double getOutputMultiplierPowerCostUpgrade() {
		return 0;
	}

	protected double getOutputMultiplierUpgrade() {
		return 0;
	}

	public double getHeatCapacityUpgrade() {
		return 0;
	}

	public double getHeatConductivityUpgrade() {
		return 0;
	}

	protected double getPowerUpgrade() {
		return 0;
	}

	protected double getPowerIoUpgrade() {
		return 0;
	}

	protected double getRangeUpgrade() {
		return 0;
	}

	protected double getTankCapacityUpgrade() {
		return 0;
	}

	protected double getProcessingSpeedUpgrade() {
		return 0;
	}

	protected double getProcessingSpeedPowerCost() {
		return 0;
	}

	protected int getUpgradeOrdinal() {
		return 0;
	}

	protected double getHeatSinkCapacity() {
		return 0;
	}

	protected double getHeatSinkConductivity() {
		return 0;
	}

	protected double getHeatCableCapacity() {
		return 0;
	}

	protected double getHeatCableConductivity() {
		return 0;
	}

	protected int getDrillBitUses() {
		return 0;
	}

	protected int getChainsawBladeUses() {
		return 0;
	}

	protected Double getHardenedDurabilityBoost() {
		return 0.0;
	}

	protected Boolean isHardenedDurabilityBoostAdditive() {
		return false;
	}

	protected int getItemFilterSlots() {
		return 0;
	}

	protected double getItemCableMaxSpeed() {
		return 0;
	}

	protected double getItemCableAcceleration() {
		return 0;
	}

	protected double getItemCableFriction() {
		return 0;
	}

	protected int getBatteryCapacity() {
		return 0;
	}

	protected int getCablePowerCapacity() {
		return 0;
	}

	protected int getCablePowerDelivery() {
		return 0;
	}

	protected int getCableFilterSlots() {
		return 0;
	}

	protected int getPortableBatteryCapacity() {
		return 0;
	}

	protected int getDigistoreCapacity() {
		return 0;
	}

	protected int getSolarPanelPowerGeneration() {
		return 0;
	}

	protected int getSolarPanelPowerStorage() {
		return 0;
	}

	protected int getPumpRate() {
		return 0;
	}

	protected int getCableExtractorRate() {
		return 0;
	}

	protected int getCableExtractionStackSize() {
		return 0;
	}

	protected int getCableExtractionFluidRate() {
		return 0;
	}

	protected int getCableExtractionFilterSlots() {
		return 0;
	}

	protected double getExtractedItemInitialSpeed() {
		return 0;
	}

	protected int getCableRetrievalRate() {
		return 0;
	}

	protected int getCableRetrievalStackSize() {
		return 0;
	}

	protected int getCableRetrievalFilterSlots() {
		return 0;
	}

	protected double getRetrievedItemInitialSpeed() {
		return 0;
	}
}
