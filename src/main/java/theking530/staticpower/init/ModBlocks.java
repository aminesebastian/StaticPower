package theking530.staticpower.init;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.AdvancedEarth;
import theking530.staticpower.blocks.EnergizedGrass;
import theking530.staticpower.blocks.StaticGrass;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.blocks.StaticPowerRotatePillarBlock;
import theking530.staticpower.blocks.crops.BaseSimplePlant;
import theking530.staticpower.blocks.decorative.Lamp;
import theking530.staticpower.blocks.decorative.StaticPowerGlassBlock;
import theking530.staticpower.cables.digistore.BlockDigistoreNetworkWire;
import theking530.staticpower.cables.fluid.BlockFluidCable;
import theking530.staticpower.cables.fluid.BlockIndustrialFluidCable;
import theking530.staticpower.cables.item.BlockItemCable;
import theking530.staticpower.cables.power.BlockPowerCable;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.BlockDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport.BlockDigistoreIOPort;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.BlockDigistoreManager;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack.BlockDigistoreServerRack;
import theking530.staticpower.tileentities.nonpowered.solderingtable.BlockSolderingTable;
import theking530.staticpower.tileentities.nonpowered.tank.BlockTank;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.BlockVacuumChest;
import theking530.staticpower.tileentities.powered.autocrafter.BlockAutoCraftingTable;
import theking530.staticpower.tileentities.powered.autosolderingtable.BlockAutoSolderingTable;
import theking530.staticpower.tileentities.powered.basicfarmer.BlockBasicFarmer;
import theking530.staticpower.tileentities.powered.battery.BlockBattery;
import theking530.staticpower.tileentities.powered.bottler.BlockBottler;
import theking530.staticpower.tileentities.powered.chargingstation.BlockChargingStation;
import theking530.staticpower.tileentities.powered.crucible.BlockCrucible;
import theking530.staticpower.tileentities.powered.fermenter.BlockFermenter;
import theking530.staticpower.tileentities.powered.former.BlockFormer;
import theking530.staticpower.tileentities.powered.lumbermill.BlockLumberMill;
import theking530.staticpower.tileentities.powered.poweredfurnace.BlockPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.BlockPoweredGrinder;
import theking530.staticpower.tileentities.powered.pump.BlockPump;
import theking530.staticpower.tileentities.powered.solarpanels.BlockSolarPanel;
import theking530.staticpower.tileentities.powered.solidgenerator.BlockSolidGenerator;
import theking530.staticpower.tileentities.powered.squeezer.BlockSqueezer;
import theking530.staticpower.tileentities.powered.treefarmer.BlockTreeFarmer;
import theking530.staticpower.utilities.HarvestLevel;

public class ModBlocks {
	// Decorative
	public static Lamp StaticLamp;
	public static Lamp EnergizedLamp;
	public static Lamp LumumLamp;
	public static StaticPowerGlassBlock ObsidianGlass;
	public static AdvancedEarth AdvancedEarth;
	public static StaticGrass StaticGrass;
	public static EnergizedGrass EnergizedGrass;

	// Plants
	public static BaseSimplePlant StaticPlant;
	public static BaseSimplePlant EnergizedPlant;
	public static BaseSimplePlant LumumPlant;

	// Wood
	public static StaticPowerRotatePillarBlock StaticLog;
	public static StaticPowerRotatePillarBlock EnergizedLog;
	public static StaticPowerRotatePillarBlock LumumLog;

	// Planks
	public static StaticPowerBlock StaticPlanks;
	public static StaticPowerBlock EnergizedPlanks;
	public static StaticPowerBlock LumumPlanks;

	// Ore
	public static StaticPowerOre OreCopper;
	public static StaticPowerOre OreTin;
	public static StaticPowerOre OreZinc;
	public static StaticPowerOre OreSilver;
	public static StaticPowerOre OreLead;
	public static StaticPowerOre OreTungsten;
	public static StaticPowerOre OreMagnesium;
	public static StaticPowerOre OrePlatinum;
	public static StaticPowerOre OreAluminium;
	public static StaticPowerOre OreRuby;
	public static StaticPowerOre OreSapphire;

	// Machines
	public static BlockVacuumChest VacuumChest;
	public static BlockChargingStation ChargingStation;
	public static BlockPoweredFurnace PoweredFurnace;
	public static BlockPoweredGrinder PoweredGrinder;
	public static BlockLumberMill LumberMill;
	public static BlockBasicFarmer BasicFarmer;
	public static BlockTank BasicTank;
	public static BlockPump Pump;
	public static BlockTreeFarmer TreeFarmer;
	public static BlockFermenter Fermenter;
	public static BlockFormer Former;
	public static BlockSolidGenerator SolidGenerator;
	public static BlockCrucible Crucible;
	public static BlockSqueezer Squeezer;
	public static BlockBottler Bottler;
	public static BlockSolderingTable SolderingTable;
	public static BlockAutoSolderingTable AutoSolderingTable;
	public static BlockAutoCraftingTable AutoCraftingTable;
	
	public static BlockSolarPanel SolarPanelBasic;
	public static BlockSolarPanel SolarPanelAdvanced;
	public static BlockSolarPanel SolarPanelStatic;
	public static BlockSolarPanel SolarPanelEnergized;
	public static BlockSolarPanel SolarPanelLumum;
	public static BlockSolarPanel SolarPanelCreative;

	public static BlockDigistoreNetworkWire DigistoreWire;
	public static BlockDigistoreManager DigistoreManager;
	public static BlockDigistoreIOPort DigistoreIOPort;
	public static BlockDigistore Digistore;
	public static BlockDigistoreServerRack DigistoreServerRack;

	// Cables
	public static BlockItemCable ItemCableBasic;
	public static BlockItemCable ItemCableAdvanced;
	public static BlockItemCable ItemCableStatic;
	public static BlockItemCable ItemCableEnergized;
	public static BlockItemCable ItemCableLumum;
	public static BlockItemCable ItemCableCreative;

	public static BlockPowerCable PowerCableBasic;
	public static BlockPowerCable PowerCableAdvanced;
	public static BlockPowerCable PowerCableStatic;
	public static BlockPowerCable PowerCableEnergized;
	public static BlockPowerCable PowerCableLumum;
	public static BlockPowerCable PowerCableCreative;

	public static BlockFluidCable FluidCable;
	public static BlockIndustrialFluidCable IndustrialFluidCable;

	// Batteries
	public static BlockBattery BatteryBasic;
	public static BlockBattery BatteryAdvanced;
	public static BlockBattery BatteryStatic;
	public static BlockBattery BatteryEnergized;
	public static BlockBattery BatteryLumum;
	public static BlockBattery BatteryCreative;

	public static void init() {
		// Decorative
		StaticPowerRegistry.preRegisterBlock(StaticLamp = new Lamp("lamp_static"));
		StaticPowerRegistry.preRegisterBlock(EnergizedLamp = new Lamp("lamp_energized"));
		StaticPowerRegistry.preRegisterBlock(LumumLamp = new Lamp("lamp_lumum"));
		StaticPowerRegistry.preRegisterBlock(ObsidianGlass = new StaticPowerGlassBlock("glass_obsidian"));
		StaticPowerRegistry.preRegisterBlock(AdvancedEarth = new AdvancedEarth("advanced_earth"));
		StaticPowerRegistry.preRegisterBlock(StaticGrass = new StaticGrass("grass_static"));
		StaticPowerRegistry.preRegisterBlock(EnergizedGrass = new EnergizedGrass("grass_energized"));

		// Plants
		StaticPowerRegistry.preRegisterBlock(StaticPlant = new BaseSimplePlant("plant_static"));
		StaticPowerRegistry.preRegisterBlock(EnergizedPlant = new BaseSimplePlant("plant_energized"));
		StaticPowerRegistry.preRegisterBlock(LumumPlant = new BaseSimplePlant("plant_lumum"));

		// Wood
		StaticPowerRegistry.preRegisterBlock(StaticLog = new StaticPowerRotatePillarBlock("log_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry.preRegisterBlock(EnergizedLog = new StaticPowerRotatePillarBlock("log_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry.preRegisterBlock(LumumLog = new StaticPowerRotatePillarBlock("log_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));

		// Planks
		StaticPowerRegistry.preRegisterBlock(StaticPlanks = new StaticPowerBlock("planks_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry.preRegisterBlock(EnergizedPlanks = new StaticPowerBlock("planks_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry.preRegisterBlock(LumumPlanks = new StaticPowerBlock("planks_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));

		// Ore
		StaticPowerRegistry.preRegisterBlock(OreCopper = new StaticPowerOre("ore_copper", ToolType.PICKAXE, HarvestLevel.STONE_TOOL, 1.9f));
		StaticPowerRegistry.preRegisterBlock(OreTin = new StaticPowerOre("ore_tin", ToolType.PICKAXE, HarvestLevel.STONE_TOOL, 1.7f));
		StaticPowerRegistry.preRegisterBlock(OreZinc = new StaticPowerOre("ore_zinc", ToolType.PICKAXE, HarvestLevel.STONE_TOOL, 1.0f));
		StaticPowerRegistry.preRegisterBlock(OreAluminium = new StaticPowerOre("ore_aluminium", ToolType.PICKAXE, HarvestLevel.STONE_TOOL, 1.0f));
		StaticPowerRegistry.preRegisterBlock(OreMagnesium = new StaticPowerOre("ore_magnesium", ToolType.PICKAXE, HarvestLevel.STONE_TOOL, 2.0f));
		StaticPowerRegistry.preRegisterBlock(OreSilver = new StaticPowerOre("ore_silver", ToolType.PICKAXE, HarvestLevel.IRON_TOOL, 1.5f));
		StaticPowerRegistry.preRegisterBlock(OreLead = new StaticPowerOre("ore_lead", ToolType.PICKAXE, HarvestLevel.IRON_TOOL, 1.5f));
		StaticPowerRegistry.preRegisterBlock(OrePlatinum = new StaticPowerOre("ore_platinum", ToolType.PICKAXE, HarvestLevel.IRON_TOOL, 2.0f));
		StaticPowerRegistry.preRegisterBlock(OreRuby = new StaticPowerOre("ore_ruby", ToolType.PICKAXE, HarvestLevel.IRON_TOOL, 3.0f));
		StaticPowerRegistry.preRegisterBlock(OreSapphire = new StaticPowerOre("ore_sapphire", ToolType.PICKAXE, HarvestLevel.IRON_TOOL, 3.0f));
		StaticPowerRegistry.preRegisterBlock(OreTungsten = new StaticPowerOre("ore_tungsten", ToolType.PICKAXE, HarvestLevel.DIAMOND_TOOL, 4.0f));

		// Machines
		StaticPowerRegistry.preRegisterBlock(VacuumChest = new BlockVacuumChest("chest_vacuum"));
		StaticPowerRegistry.preRegisterBlock(SolarPanelBasic = new BlockSolarPanel("solar_panel_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterBlock(SolarPanelAdvanced = new BlockSolarPanel("solar_panel_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterBlock(SolarPanelStatic = new BlockSolarPanel("solar_panel_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterBlock(SolarPanelEnergized = new BlockSolarPanel("solar_panel_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterBlock(SolarPanelLumum = new BlockSolarPanel("solar_panel_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterBlock(SolarPanelCreative = new BlockSolarPanel("solar_panel_creative", StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterBlock(ChargingStation = new BlockChargingStation("machine_charging_station"));
		StaticPowerRegistry.preRegisterBlock(PoweredFurnace = new BlockPoweredFurnace("machine_powered_furnace"));
		StaticPowerRegistry.preRegisterBlock(PoweredGrinder = new BlockPoweredGrinder("machine_powered_grinder"));
		StaticPowerRegistry.preRegisterBlock(LumberMill = new BlockLumberMill("machine_lumber_mill"));
		StaticPowerRegistry.preRegisterBlock(BasicFarmer = new BlockBasicFarmer("machine_basic_farmer"));
		StaticPowerRegistry.preRegisterBlock(TreeFarmer = new BlockTreeFarmer("machine_tree_farmer"));
		StaticPowerRegistry.preRegisterBlock(Fermenter = new BlockFermenter("machine_fermenter"));
		StaticPowerRegistry.preRegisterBlock(Former = new BlockFormer("machine_former"));
		StaticPowerRegistry.preRegisterBlock(SolidGenerator = new BlockSolidGenerator("machine_generator_solid"));
		StaticPowerRegistry.preRegisterBlock(Crucible = new BlockCrucible("machine_crucible"));
		StaticPowerRegistry.preRegisterBlock(Squeezer = new BlockSqueezer("machine_squeezer"));
		StaticPowerRegistry.preRegisterBlock(Bottler = new BlockBottler("machine_bottler"));
		StaticPowerRegistry.preRegisterBlock(SolderingTable = new BlockSolderingTable("soldering_table"));
		StaticPowerRegistry.preRegisterBlock(AutoSolderingTable = new BlockAutoSolderingTable("machine_industrial_soldering_table"));
		StaticPowerRegistry.preRegisterBlock(AutoCraftingTable = new BlockAutoCraftingTable("machine_industrial_crafting_table"));


		StaticPowerRegistry.preRegisterBlock(BasicTank = new BlockTank("tank_basic"));
		StaticPowerRegistry.preRegisterBlock(Pump = new BlockPump("pump"));

		StaticPowerRegistry.preRegisterBlock(DigistoreManager = new BlockDigistoreManager("digistore_manager"));
		StaticPowerRegistry.preRegisterBlock(DigistoreIOPort = new BlockDigistoreIOPort("digistore_io_port"));
		StaticPowerRegistry.preRegisterBlock(Digistore = new BlockDigistore("digistore"));
		StaticPowerRegistry.preRegisterBlock(DigistoreServerRack = new BlockDigistoreServerRack("digistore_server_rack"));

		// Cables
		StaticPowerRegistry.preRegisterBlock(DigistoreWire = new BlockDigistoreNetworkWire("cable_digistore"));
		StaticPowerRegistry.preRegisterBlock(PowerCableBasic = new BlockPowerCable("cable_power_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterBlock(PowerCableAdvanced = new BlockPowerCable("cable_power_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterBlock(PowerCableStatic = new BlockPowerCable("cable_power_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterBlock(PowerCableEnergized = new BlockPowerCable("cable_power_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterBlock(PowerCableLumum = new BlockPowerCable("cable_power_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterBlock(PowerCableCreative = new BlockPowerCable("cable_power_creative", StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterBlock(ItemCableBasic = new BlockItemCable("cable_item_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterBlock(ItemCableAdvanced = new BlockItemCable("cable_item_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterBlock(ItemCableStatic = new BlockItemCable("cable_item_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterBlock(ItemCableEnergized = new BlockItemCable("cable_item_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterBlock(ItemCableLumum = new BlockItemCable("cable_item_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterBlock(ItemCableCreative = new BlockItemCable("cable_item_creative", StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterBlock(FluidCable = new BlockFluidCable("cable_fluid"));
		StaticPowerRegistry.preRegisterBlock(IndustrialFluidCable = new BlockIndustrialFluidCable("cable_industrial_fluid"));

		StaticPowerRegistry.preRegisterBlock(BatteryBasic = new BlockBattery("battery_block_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterBlock(BatteryAdvanced = new BlockBattery("battery_block_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterBlock(BatteryStatic = new BlockBattery("battery_block_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterBlock(BatteryEnergized = new BlockBattery("battery_block_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterBlock(BatteryLumum = new BlockBattery("battery_block_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterBlock(BatteryCreative = new BlockBattery("battery_block_creative", StaticPowerTiers.CREATIVE));
	}
}