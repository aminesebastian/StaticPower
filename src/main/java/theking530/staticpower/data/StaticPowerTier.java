package theking530.staticpower.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import theking530.staticpower.StaticPower;

public abstract class StaticPowerTier {
	/****************
	 * Tier Settings
	 ****************/
	public final ConfigValue<String> tierId;
	public final ConfigValue<String> unlocalizedTierName;

	/***********
	 * Digistore
	 ***********/
	public final ConfigValue<Integer> digistoreCardCapacity;

	/*************
	 * Solar Panel
	 *************/
	public final LongValue solarPanelPowerGeneration;
	public final LongValue solarPanelPowerStorage;

	/*******
	 * Pump
	 *******/
	public final ConfigValue<Integer> pumpRate;

	/***************************
	 * Extraction Configuration
	 ***************************/
	public final ConfigValue<Integer> cableExtractorRate;
	public final ConfigValue<Integer> cableExtractionStackSize;
	public final ConfigValue<Integer> cableExtractionFluidRate;
	public final ConfigValue<Integer> cableExtractionFilterSlots;
	public final ConfigValue<Double> cableExtractedItemInitialSpeed;

	/*************************
	 * Retrieval Configuration
	 *************************/
	public final ConfigValue<Integer> cableRetrievalRate;
	public final ConfigValue<Integer> cableRetrievalStackSize;
	public final ConfigValue<Integer> cableRetrievalFilterSlots;
	public final ConfigValue<Double> cableRetrievedItemInitialSpeed;

	/**********************
	 * Filter Configuration
	 **********************/
	public final ConfigValue<Integer> cableFilterSlots;

	/***************************
	 * Power Cable Configuration
	 ***************************/
	public final LongValue cablePowerCapacity;
	public final LongValue cablePowerDelivery;
	public final LongValue cableIndustrialPowerCapacity;
	public final LongValue cableIndustrialPowerDelivery;

	/**************************
	 * Item Cable Configuration
	 **************************/
	public final ConfigValue<Double> itemCableAcceleration;
	public final ConfigValue<Double> itemCableFriction;
	public final ConfigValue<Double> itemCableMaxSpeed;

	/***************************
	 * Fluid Cable Configuration
	 ***************************/
	public final ConfigValue<Integer> cableFluidCapacity;
	public final ConfigValue<Integer> cableIndustrialFluidCapacity;

	/********************
	 * Heat Configuration
	 ********************/
	public final ConfigValue<Double> heatCableCapacity;
	public final ConfigValue<Double> heatCableConductivity;
	public final ConfigValue<Double> heatSinkCapacity;
	public final ConfigValue<Double> heatSinkConductivity;
	public final ConfigValue<Double> heatSinkElectricHeatGeneration;
	public final ConfigValue<Integer> heatSinkElectricHeatPowerUsage;

	/*********************
	 * Power Configuration
	 *********************/
	public final LongValue batteryCapacity;
	public final LongValue batteryMaxIO;
	public final LongValue portableBatteryCapacity;

	/***********************
	 * Machine Configuration
	 ***********************/
	public final LongValue defaultMachinePowerCapacity;
	public final LongValue defaultMachinePowerInput;
	public final LongValue defaultMachinePowerOutput;

	/**********
	 * Conveyer
	 **********/
	public final ConfigValue<Double> conveyorSpeedMultiplier;
	public final IntValue conveyorSupplierStackSize;
	public final IntValue conveyorExtractorStackSize;

	/********************
	 * Processing Upgrade
	 ********************/
	public final ConfigValue<Double> processingSpeedUpgrade;
	public final ConfigValue<Double> processingSpeedPowerCost;

	/**************
	 * Tank Upgrade
	 **************/
	public final ConfigValue<Double> tankCapacityUpgrade;

	/***************
	 * Range Upgrade
	 ***************/
	public final ConfigValue<Double> rangeUpgrade;

	/***************
	 * Power Upgrade
	 ***************/
	public final ConfigValue<Double> powerUpgrade;
	public final ConfigValue<Double> powerIOUpgrade;

	/************************
	 * Heat Capacity Upgrade
	 ************************/
	public final ConfigValue<Double> heatCapacityUpgrade;

	/************************
	 * Heat Conductivity Upgrade
	 ************************/
	public final ConfigValue<Double> heatConductivityUpgrade;

	/***************************
	 * Output Multiplier Upgrade
	 ***************************/
	public final ConfigValue<Double> outputMultiplierUpgrade;
	public final ConfigValue<Double> outputMultiplierPowerCostUpgrade;

	/********************
	 * Centrifuge Upgrade
	 ********************/
	public final ConfigValue<Integer> maxCentrifugeSpeedUpgrade;
	public final ConfigValue<Double> centrifugeUpgradedPowerIncrease;

	/********
	 * Tools
	 ********/
	public final ConfigValue<Integer> drillBitUses;
	public final ConfigValue<Float> drillSpeedMultiplier;

	public final ConfigValue<Integer> chainsawBladeUses;
	public final ConfigValue<Float> chainsawSpeedMultiplier;

	public final ConfigValue<Double> hardenedDurabilityBoost;
	public final ConfigValue<Boolean> hardenedDurabilityBoostAdditive;
	public final ConfigValue<Integer> hammerUses;
	public final ConfigValue<Double> hammerSwingSpeed;
	public final ConfigValue<Double> hammerDamage;
	public final ConfigValue<Integer> hammerCooldown;
	public final ConfigValue<Integer> wireCutterUses;
	public final LongValue magnetPowerCapacity;
	public final ConfigValue<Integer> magnetRadius;

	/****************
	 * Turbine Blades
	 ****************/
	public final ConfigValue<Integer> turbineBladeDurabilityTicks;
	public final ConfigValue<Double> turbineBladeGenerationBoost;

	/*******
	 * Misc
	 *******/
	public final ConfigValue<Integer> defaultTankCapacity;
	public final ConfigValue<Integer> capsuleCapacity;
	public final ConfigValue<Integer> itemFilterSlots;

	public final ConfigValue<Integer> upgradeOrdinal;

	public StaticPowerTier(ForgeConfigSpec.Builder builder) {
		// Establish field for the tier Id.
		tierId = builder.comment("The unique id of the tier in the format of 'MOD_ID:TIER_NAME'.").translation(StaticPower.MOD_ID + ".config." + "tierId").define("TierId", getTierId().toString());

		// Establish a field for the unlocalized name.
		unlocalizedTierName = builder.comment("The unlocalized name of the tier.").translation(StaticPower.MOD_ID + ".config." + "unlocalizedTierName").define("UnlocalizedTierName",
				getUnlocalizedName());

		builder.push("Digistore");
		digistoreCardCapacity = builder.comment("The number of items that can be contained in a regular digistore card of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "digistoreCardCapacity").define("DigistoreCardCapacity", getDigistoreCardCapacity());
		builder.pop();

		builder.push("Machines");
		defaultMachinePowerCapacity = builder.comment("The base amount of power a machine of this tier can store (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachinePowerCapacity").defineInRange("DefaultMachinePowerCapacity", this.getDefaultMachinePowerCapacity(), 0, Long.MAX_VALUE);

		defaultMachinePowerInput = builder.comment("The base amount of power a machine of this tier can consume from a power providing source (a cable or battery) (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachinePowerInput").defineInRange("DefaultMachinePowerInput", this.getDefaultMachinePowerInput(), 0, Long.MAX_VALUE);

		defaultMachinePowerOutput = builder.comment("The base amount of power that can be extracted or provided by a machine of this tier (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachinePowerOutput").defineInRange("DefaultMachinePowerOutput", this.getDefaultMachinePowerOutput(), 0, Long.MAX_VALUE);

		defaultTankCapacity = builder.comment("The base amount of fluid a machine of this tier can store..").translation(StaticPower.MOD_ID + ".config." + "defaultTankCapacity")
				.define("DefaultTankCapacity", this.getDefaultTankCapacity());

		builder.push("Conveyor");
		conveyorSpeedMultiplier = builder.comment("The speed multitplier applied to conveyors of this tier.").translation(StaticPower.MOD_ID + ".config." + "conveyorSpeedMultiplier")
				.defineInRange("ConveyorSpeedMultiplier", getConveyorSpeedMultiplier(), 0, Double.MAX_VALUE);

		conveyorSupplierStackSize = builder.comment("The maximum stack size suppliers of this tier can consume at a time.").translation(StaticPower.MOD_ID + ".config." + "conveyorSupplierStackSize")
				.defineInRange("ConveyorSupplierStackSize", getConveyorSupplierStackSize(), 0, Integer.MAX_VALUE);

		conveyorExtractorStackSize = builder.comment("The maximum stack size that an extractor of this tier can extract from an adjacent inventory.")
				.translation(StaticPower.MOD_ID + ".config." + "conveyorExtractorStackSize").defineInRange("ConveyorExtractorStackSize", getConveyorExtractorStackSize(), 0, Integer.MAX_VALUE);

		builder.pop();

		builder.push("Battery");
		batteryCapacity = builder.comment("The amount of power that a non-portable battery of this tier can store (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "batteryCapacity").defineInRange("BatteryCapacity", this.getBatteryCapacity(), 0, Long.MAX_VALUE);

		batteryMaxIO = builder.comment("The amount of power that a non-portable battery of this tier can transfer (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "batteryMaxIO").defineInRange("BatteryMaxIO", this.getBatteryMaxIO(), 0, Long.MAX_VALUE);

		builder.pop();

		builder.push("Solar_Panel");
		solarPanelPowerGeneration = builder.comment("The amount of power generated by a solar panel of this tier per tick (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "solarPanelPowerGeneration").defineInRange("SolarPanelPowerGeneration", this.getSolarPanelPowerGeneration(), 0, Long.MAX_VALUE);

		solarPanelPowerStorage = builder.comment("The amount of power a solar panel of this tier can store (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "solarPanelPowerStorage").defineInRange("SolarPanelPowerStorage", this.getSolarPanelPowerGeneration(), 0, Long.MAX_VALUE);
		builder.pop();

		builder.push("Pump");
		pumpRate = builder.comment("The amount of ticks that will elapse between each pump operation. The higher this number, the slower the pump will operate (20 ticks == 1 second).")
				.translation(StaticPower.MOD_ID + ".config." + "pumpRate").define("PumpRate", this.getPumpRate());
		builder.pop();

		builder.push("Turbine Blades");
		turbineBladeDurabilityTicks = builder.comment("The amount of ticks that a turbine blade will last (20 ticks == 1 second).")
				.translation(StaticPower.MOD_ID + ".config." + "turbineBladeDurabilityTicks").define("TurbineBladeDurabilityTicks", this.getTurbineBladeDurabilityTicks());
		turbineBladeGenerationBoost = builder.comment("The multiplier applied to the generation of the turbine these blades are installed in..")
				.translation(StaticPower.MOD_ID + ".config." + "turbineBladeGenerationBoost").define("TurbineBladeGenerationBoost", this.getTurbineBladeGenerationBoost());
		builder.pop();

		builder.push("Heatsink");
		heatSinkCapacity = builder.comment("The amount of heat a heatsink of this tier can store.").translation(StaticPower.MOD_ID + ".config." + "heatSinkCapacity").define("HeatSinkCapacity",
				this.getHeatSinkCapacity());

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

		cableExtractionFilterSlots = builder.comment("The number of filter slots available on an extractor of this tier.").translation(StaticPower.MOD_ID + ".config." + "cableExtractionFilterSlots")
				.define("CableExtractionFilterSlots", this.getCableExtractionFilterSlots());

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
		cablePowerCapacity = builder.comment("The amount of power that a power cable of this tier can store (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "cablePowerCapacity").defineInRange("CablePowerCapacity", this.getCablePowerCapacity(), 0, Long.MAX_VALUE);
		cablePowerDelivery = builder.comment("The amount of power that a power cable of this tier can supply to a single destination (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "cablePowerDelivery").defineInRange("CablePowerDelivery", this.getCablePowerDelivery(), 0, Long.MAX_VALUE);

		cableIndustrialPowerCapacity = builder.comment("The amount of power that an industrial power cable of this tier can store (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialPowerCapacity").defineInRange("CableIndustrialPowerCapacity", this.getCableIndustrialPowerCapacity(), 0, Long.MAX_VALUE);
		cableIndustrialPowerDelivery = builder.comment("The amount of power that an industrial power cable of this tier can supply to a single destination (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialPowerDelivery").defineInRange("CableIndustrialPowerDelivery", this.getCableIndustrialPowerDelivery(), 0, Long.MAX_VALUE);

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
		cableFluidCapacity = builder.comment("The amount of fluid that can be stored in a regular fluid pipe of this tier.").translation(StaticPower.MOD_ID + ".config." + "cableFluidCapacity")
				.define("CableFluidCapacity", this.getCableFluidCapacity());

		cableIndustrialFluidCapacity = builder.comment("The amount of fluid that can be stored in an industrial fluid pipe of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "cableIndustrialFluidCapacity").define("CableIndustrialFluidCapacity", this.getCableIndustrialFluidCapacity());
		builder.pop();

		/********
		 * Heat
		 ********/
		builder.push("Heat");
		heatCableCapacity = builder.comment("The amount of heat that a heat pipe of this tier can store.").translation(StaticPower.MOD_ID + ".config." + "heatCableCapacity")
				.define("HeatCableCapacity", this.getHeatCableCapacity());

		heatCableConductivity = builder.comment("The conductivity multiplier for a heat pipe of this tier. The higher it is, the faster it is able to dissipate heat. This value is PER BLOCK SIDE.")
				.translation(StaticPower.MOD_ID + ".config." + "heatCableConductivity").define("HeatCableConductivity", this.getHeatCableConductivity());
		builder.pop();
		builder.pop();

		/********
		 * Items
		 ********/
		builder.push("Items");
		capsuleCapacity = builder.comment("The amount of fluid that can be stored in a fluid capsule of this tier.").translation(StaticPower.MOD_ID + ".config." + "capsuleCapacity")
				.define("CapsuleCapacity", this.getCapsuleCapacity());

		portableBatteryCapacity = builder.comment("The amount of power that can be stored in a portable battery of this tier (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryCapacity").defineInRange("PortableBatteryCapacity", this.getPortableBatteryCapacity(), 0, Long.MAX_VALUE);

		itemFilterSlots = builder.comment("The number of slots that exist on an item filter of this tier (not the filter attachment, the actual item).")
				.translation(StaticPower.MOD_ID + ".config." + "itemFilterSlots").define("ItemFilterSlots", this.getItemFilterSlots());

		/********
		 * Tools
		 ********/
		builder.push("Tools");
		drillBitUses = builder.comment("The number of blocks that can be mined by a drill bit of this tier.").translation(StaticPower.MOD_ID + ".config." + "drillBitUses").define("DrillBitUses",
				getDrillBitUses());
		drillSpeedMultiplier = builder.comment("The mining speed multiplier of drills of this tier.").translation(StaticPower.MOD_ID + ".config." + "drillSpeedMultiplier")
				.define("DrillSpeedMultiplier", getDrillSpeedMultiplier());

		chainsawBladeUses = builder.comment("The number of blocks that can be mined by a chainsaw blade of this tier.").translation(StaticPower.MOD_ID + ".config." + "chainsawBladeUses")
				.define("ChainsawBladeUses", this.getChainsawBladeUses());
		chainsawSpeedMultiplier = builder.comment("The mining speed multiplier of chainsaws of this tier.").translation(StaticPower.MOD_ID + ".config." + "chainsawSpeedMultiplier")
				.define("ChainsawSpeedMultiplier", getChainsawSpeedMultiplier());

		hardenedDurabilityBoost = builder.comment("The amount of durability gained when the diamond hardened modifier is added.")
				.translation(StaticPower.MOD_ID + ".config." + "diamondHardenedDurabilityBoost").define("DiamondHardenedDurabilityBoost", this.getHardenedDurabilityBoost());
		hardenedDurabilityBoostAdditive = builder.comment("Defines whether the hardened durability boost is additive or multaplicative.")
				.translation(StaticPower.MOD_ID + ".config." + "hardenedDurabilityBoostAdditive").define("HardenedDurabilityBoostAdditive", this.isHardenedDurabilityBoostAdditive());

		hammerUses = builder.comment("The number of blocks/items that can be processed by a hammer of this tier.").translation(StaticPower.MOD_ID + ".config." + "hammerUses").define("HammerUses",
				this.getHammerUses());
		hammerSwingSpeed = builder.comment("How fast a hammer of this tier swings.").translation(StaticPower.MOD_ID + ".config." + "hammerSwingSpeed").define("HammerSwingSpeed",
				this.getHammerSwingSpeed());
		hammerDamage = builder.comment("How much damage a hammer of this tier does.").translation(StaticPower.MOD_ID + ".config." + "hammerDamage").define("HammerDamage", this.getHammerDamage());
		hammerCooldown = builder.comment("How long the cooldown is (in ticks) between each anvil based crafting operation performed by a hammer of this tier..")
				.translation(StaticPower.MOD_ID + ".config." + "hammerCooldown").define("HammerCooldown", this.getHammerCooldown());

		wireCutterUses = builder.comment("The number of items that can be processed by a wire cutter of this tier.").translation(StaticPower.MOD_ID + ".config." + "wireCutterUses")
				.define("WireCutterUses", this.getWireCutterUses());
		magnetPowerCapacity = builder.comment("The amount of power that can be stored in a magnet of this tier (in mSV [1SV = 1000mSV]).")
				.translation(StaticPower.MOD_ID + ".config." + "magnetPowerCapacity").defineInRange("MagnetPowerCapacity", this.getMagnetPowerCapacity(), 0, Long.MAX_VALUE);
		magnetRadius = builder.comment("The number of blocks away from which items will be pulled towards the wielder.").translation(StaticPower.MOD_ID + ".config." + "magnetRadius")
				.define("MagnetRadius", this.getMagnetRadius());
		builder.pop();

		builder.push("Upgrade");
		upgradeOrdinal = builder.comment(
				"The upgrade ordinal of this tier. Higher value will take precedence. For example, if a machine has both Basic and Energized power upgrades installed, the Energized upgrades will be used when calculating power values because it has the higher upgrade ordinal. In the case of a tie, it comes down to which one appears in later in the upgrade slots.")
				.translation(StaticPower.MOD_ID + ".config." + "upgradeOrdinal").define("UpgradeOrdinal", this.getUpgradeOrdinal());

		/************************
		 * Heat Conductivity Upgrade
		 ************************/
		builder.push("Heat");
		heatCapacityUpgrade = builder.comment("The heat capacity for a full upgrade stack of this tier (as a percentage 1.0+).").translation(StaticPower.MOD_ID + ".config." + "heatCapacityUpgrade")
				.define("HeatCapacityUpgrade", this.getHeatCapacityUpgrade());

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
		maxCentrifugeSpeedUpgrade = builder.comment("The new maximum speed set by a centrifuge upgrade of this tier.").translation(StaticPower.MOD_ID + ".config." + "maxCentrifugeSpeedUpgrade")
				.define("MaxCentrifugeSpeedUpgrade", this.getMaxCentrifugeSpeedUpgrade());

		centrifugeUpgradedPowerIncrease = builder.comment("The amount of increased power cost due to a centrifuge upgrade of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "centrifugeUpgradedPowerIncrease").define("CentrifugeUpgradedPowerIncrease", this.getCentrifugeUpgradedPowerIncrease());
		builder.pop();

		/********************
		 * Output Upgrade
		 ********************/
		builder.push("Output_Multiplier");
		outputMultiplierUpgrade = builder.comment("The multiplier applied to the output change of any probability outputs for a full stack of an output multiplier upgrade (as a percentage 1.0+).")
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

	protected long getDefaultMachinePowerCapacity() {
		return 0;
	}

	protected long getDefaultMachinePowerInput() {
		return 0;
	}

	protected long getDefaultMachinePowerOutput() {
		return 0;
	}

	protected double getConveyorSpeedMultiplier() {
		return 1;
	}

	protected int getConveyorSupplierStackSize() {
		return 1;
	}

	protected int getConveyorExtractorStackSize() {
		return 1;
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

	protected float getDrillSpeedMultiplier() {
		return 1.0f;
	}

	protected int getChainsawBladeUses() {
		return 0;
	}

	protected float getChainsawSpeedMultiplier() {
		return 1.0f;
	}

	protected Double getHardenedDurabilityBoost() {
		return 0.0;
	}

	protected Boolean isHardenedDurabilityBoostAdditive() {
		return false;
	}

	protected int getHammerUses() {
		return 0;
	}

	protected double getHammerSwingSpeed() {
		return 0;

	}

	protected double getHammerDamage() {
		return 0;
	}

	protected int getHammerCooldown() {
		return 0;
	}

	protected int getWireCutterUses() {
		return 0;
	}

	protected long getMagnetPowerCapacity() {
		return 0;
	}

	protected int getMagnetRadius() {
		return 0;
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

	protected long getBatteryCapacity() {
		return 0;
	}

	protected long getBatteryMaxIO() {
		return 0;
	}

	protected long getCablePowerCapacity() {
		return 0;
	}

	protected long getCablePowerDelivery() {
		return 0;
	}

	protected long getCableIndustrialPowerCapacity() {
		return 0;
	}

	protected long getCableIndustrialPowerDelivery() {
		return 0;
	}

	protected int getCableFilterSlots() {
		return 0;
	}

	protected long getPortableBatteryCapacity() {
		return 0;
	}

	protected int getDigistoreCardCapacity() {
		return 0;
	}

	protected long getSolarPanelPowerGeneration() {
		return 0;
	}

	protected long getSolarPanelPowerStorage() {
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

	protected double getTurbineBladeGenerationBoost() {
		return 0.0;
	}

	protected int getTurbineBladeDurabilityTicks() {
		return 0;
	}
}
