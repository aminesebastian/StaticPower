package theking530.staticpower.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import theking530.staticcore.utilities.HarvestLevel;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.EnergizedGrass;
import theking530.staticpower.blocks.StaticGrass;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerCutoutBlock;
import theking530.staticpower.blocks.StaticPowerFarmland;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.blocks.StaticPowerRotatePillarBlock;
import theking530.staticpower.blocks.StaticPowerSlimeBlock;
import theking530.staticpower.blocks.crops.BaseSimplePlant;
import theking530.staticpower.blocks.decorative.Lamp;
import theking530.staticpower.blocks.decorative.StaticPowerGlassBlock;
import theking530.staticpower.blocks.tree.StaticPowerSapling;
import theking530.staticpower.blocks.tree.StaticPowerTreeLeaves;
import theking530.staticpower.blocks.tree.StaticPowerTreeLog;
import theking530.staticpower.cables.digistore.BlockDigistoreNetworkWire;
import theking530.staticpower.cables.fluid.BlockFluidCable;
import theking530.staticpower.cables.fluid.BlockIndustrialFluidCable;
import theking530.staticpower.cables.heat.BlockHeatCable;
import theking530.staticpower.cables.item.BlockItemCable;
import theking530.staticpower.cables.power.BlockPowerCable;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.tileentities.digistorenetwork.digistore.BlockDigistore;
import theking530.staticpower.tileentities.digistorenetwork.ioport.BlockDigistoreIOPort;
import theking530.staticpower.tileentities.digistorenetwork.manager.BlockDigistoreManager;
import theking530.staticpower.tileentities.digistorenetwork.patternstorage.BlockPatternStorage;
import theking530.staticpower.tileentities.digistorenetwork.severrack.BlockDigistoreServerRack;
import theking530.staticpower.tileentities.nonpowered.condenser.BlockCondenser;
import theking530.staticpower.tileentities.nonpowered.evaporator.BlockEvaporator;
import theking530.staticpower.tileentities.nonpowered.miner.BlockMiner;
import theking530.staticpower.tileentities.nonpowered.solderingtable.BlockSolderingTable;
import theking530.staticpower.tileentities.nonpowered.tank.BlockTank;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.BlockVacuumChest;
import theking530.staticpower.tileentities.powered.autocrafter.BlockAutoCraftingTable;
import theking530.staticpower.tileentities.powered.autosolderingtable.BlockAutoSolderingTable;
import theking530.staticpower.tileentities.powered.basicfarmer.BlockBasicFarmer;
import theking530.staticpower.tileentities.powered.battery.BlockBattery;
import theking530.staticpower.tileentities.powered.bottler.BlockBottler;
import theking530.staticpower.tileentities.powered.centrifuge.BlockCentrifuge;
import theking530.staticpower.tileentities.powered.chargingstation.BlockChargingStation;
import theking530.staticpower.tileentities.powered.crucible.BlockCrucible;
import theking530.staticpower.tileentities.powered.electricminer.BlockElectricMiner;
import theking530.staticpower.tileentities.powered.fermenter.BlockFermenter;
import theking530.staticpower.tileentities.powered.fluidgenerator.BlockFluidGenerator;
import theking530.staticpower.tileentities.powered.fluidinfuser.BlockFluidInfuser;
import theking530.staticpower.tileentities.powered.former.BlockFormer;
import theking530.staticpower.tileentities.powered.fusionfurnace.BlockFusionFurnace;
import theking530.staticpower.tileentities.powered.heatsink.BlockHeatSink;
import theking530.staticpower.tileentities.powered.lumbermill.BlockLumberMill;
import theking530.staticpower.tileentities.powered.poweredfurnace.BlockPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.BlockPoweredGrinder;
import theking530.staticpower.tileentities.powered.pump.BlockPump;
import theking530.staticpower.tileentities.powered.solarpanels.BlockSolarPanel;
import theking530.staticpower.tileentities.powered.solidgenerator.BlockSolidGenerator;
import theking530.staticpower.tileentities.powered.squeezer.BlockSqueezer;
import theking530.staticpower.tileentities.powered.treefarmer.BlockTreeFarmer;
import theking530.staticpower.tileentities.powered.vulcanizer.BlockVulcanizer;
import theking530.staticpower.trees.rubbertree.RubberTree;

public class ModBlocks {
	// Decorative
	public static Lamp StaticLamp;
	public static Lamp EnergizedLamp;
	public static Lamp LumumLamp;
	public static StaticPowerGlassBlock ObsidianGlass;

	public static StaticPowerFarmland StaticFarmland;
	public static StaticPowerFarmland EnergizedFarmland;
	public static StaticPowerFarmland LumumFarmland;

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

	// Storage Blocks
	public static StaticPowerBlock BlockCopper;
	public static StaticPowerBlock BlockTin;
	public static StaticPowerBlock BlockZinc;
	public static StaticPowerBlock BlockAluminium;
	public static StaticPowerBlock BlockMagnesium;
	public static StaticPowerBlock BlockSilver;
	public static StaticPowerBlock BlockLead;
	public static StaticPowerBlock BlockPlatinum;
	public static StaticPowerBlock BlockRuby;
	public static StaticPowerBlock BlockSapphire;
	public static StaticPowerBlock BlockTungsten;
	public static StaticPowerBlock BlockBrass;
	public static StaticPowerBlock BlockBronze;
	public static StaticPowerBlock BlockRedstoneAlloy;
	public static StaticPowerBlock BlockInertInfusion;
	public static StaticPowerBlock BlockStaticMetal;
	public static StaticPowerBlock BlockEnergizedMetal;
	public static StaticPowerBlock BlockLumumMetal;
	public static StaticPowerSlimeBlock BlockLatex;
	public static StaticPowerSlimeBlock BlockRubber;

	// Machines
	public static StaticPowerCutoutBlock MachineBlockIron;
	public static StaticPowerCutoutBlock MachineBlockBasic;
	public static StaticPowerCutoutBlock MachineBlockAdvanced;
	public static StaticPowerCutoutBlock MachineBlockStatic;
	public static StaticPowerCutoutBlock MachineBlockEnergized;
	public static StaticPowerCutoutBlock MachineBlockLumum;

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
	public static BlockFluidGenerator FluidGenerator;
	public static BlockCrucible Crucible;
	public static BlockSqueezer Squeezer;
	public static BlockBottler Bottler;
	public static BlockSolderingTable SolderingTable;
	public static BlockAutoSolderingTable AutoSolderingTable;
	public static BlockAutoCraftingTable AutoCraftingTable;
	public static BlockFluidInfuser FluidInfuser;
	public static BlockCentrifuge Centrifuge;
	public static BlockFusionFurnace FusionFurnace;
	public static BlockMiner Miner;
	public static BlockElectricMiner ElectricMiner;
	public static BlockEvaporator Evaporator;
	public static BlockCondenser Condenser;
	public static BlockVulcanizer Vulcanizer;

	public static BlockHeatSink AluminiumHeatSink;
	public static BlockHeatSink CopperHeatSink;
	public static BlockHeatSink TinHeatSink;
	public static BlockHeatSink SilverHeatSink;
	public static BlockHeatSink GoldHeatSink;

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
	public static BlockPatternStorage PatternStorage;

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

	public static BlockFluidCable FluidCableBasic;
	public static BlockFluidCable FluidCableAdvanced;
	public static BlockFluidCable FluidCableStatic;
	public static BlockFluidCable FluidCableEnergized;
	public static BlockFluidCable FluidCableLumum;
	public static BlockFluidCable FluidCableCreative;

	public static BlockIndustrialFluidCable IndustrialFluidCableBasic;
	public static BlockIndustrialFluidCable IndustrialFluidCableAdvanced;
	public static BlockIndustrialFluidCable IndustrialFluidCableStatic;
	public static BlockIndustrialFluidCable IndustrialFluidCableEnergized;
	public static BlockIndustrialFluidCable IndustrialFluidCableLumum;
	public static BlockIndustrialFluidCable IndustrialFluidCableCreative;

	public static BlockHeatCable CopperHeatCable;
	public static BlockHeatCable TinHeatCable;
	public static BlockHeatCable SilverHeatCable;
	public static BlockHeatCable GoldHeatCable;
	public static BlockHeatCable AluminiumHeatCable;

	// Batteries
	public static BlockBattery BatteryBasic;
	public static BlockBattery BatteryAdvanced;
	public static BlockBattery BatteryStatic;
	public static BlockBattery BatteryEnergized;
	public static BlockBattery BatteryLumum;
	public static BlockBattery BatteryCreative;

	// Rubber Tree
	public static StaticPowerTreeLog RubberTreeWood;
	public static StaticPowerTreeLog RubberTreeLog;
	public static StaticPowerTreeLog RubberTreeStrippedLog;
	public static StaticPowerTreeLog RubberTreeStrippedWood;
	public static StaticPowerBlock RubberTreePlanks;
	public static StaticPowerTreeLeaves RubberTreeLeaves;
	public static StaticPowerSapling RubberTreeSapling;

	public static void init() {
		// Decorative
		StaticPowerRegistry.preRegisterBlock(StaticLamp = new Lamp("lamp_static"));
		StaticPowerRegistry.preRegisterBlock(EnergizedLamp = new Lamp("lamp_energized"));
		StaticPowerRegistry.preRegisterBlock(LumumLamp = new Lamp("lamp_lumum"));
		StaticPowerRegistry.preRegisterBlock(ObsidianGlass = new StaticPowerGlassBlock("glass_obsidian"));

		StaticPowerRegistry.preRegisterBlock(StaticFarmland = new StaticPowerFarmland("farmland_static"));
		StaticPowerRegistry.preRegisterBlock(EnergizedFarmland = new StaticPowerFarmland("farmland_energized"));
		StaticPowerRegistry.preRegisterBlock(LumumFarmland = new StaticPowerFarmland("farmland_lumum"));

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

		// Metal Blocks
		StaticPowerRegistry.preRegisterBlock(BlockCopper = new StaticPowerBlock("block_copper",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.9f)));
		StaticPowerRegistry.preRegisterBlock(BlockTin = new StaticPowerBlock("block_tin",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.7f)));
		StaticPowerRegistry.preRegisterBlock(BlockZinc = new StaticPowerBlock("block_zinc",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.0f)));
		StaticPowerRegistry.preRegisterBlock(BlockAluminium = new StaticPowerBlock("block_aluminium",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.0f)));
		StaticPowerRegistry.preRegisterBlock(BlockMagnesium = new StaticPowerBlock("block_magnesium",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(2.0f)));
		StaticPowerRegistry.preRegisterBlock(BlockSilver = new StaticPowerBlock("block_silver",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.5f)));
		StaticPowerRegistry.preRegisterBlock(BlockLead = new StaticPowerBlock("block_lead",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.5f)));
		StaticPowerRegistry.preRegisterBlock(BlockPlatinum = new StaticPowerBlock("block_platinum",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(2.0f)));
		StaticPowerRegistry.preRegisterBlock(BlockRuby = new StaticPowerBlock("block_ruby",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.0f)));
		StaticPowerRegistry.preRegisterBlock(BlockSapphire = new StaticPowerBlock("block_sapphire",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.0f)));
		StaticPowerRegistry.preRegisterBlock(BlockTungsten = new StaticPowerBlock("block_tungsten",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.DIAMOND_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(4.0f)));

		StaticPowerRegistry.preRegisterBlock(BlockBrass = new StaticPowerBlock("block_brass",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.7f)));
		StaticPowerRegistry.preRegisterBlock(BlockBronze = new StaticPowerBlock("block_bronze",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.7f)));
		StaticPowerRegistry.preRegisterBlock(BlockLatex = new StaticPowerSlimeBlock("block_latex", Block.Properties.from(Blocks.SLIME_BLOCK)));
		StaticPowerRegistry.preRegisterBlock(BlockRubber = new StaticPowerSlimeBlock("block_rubber", Block.Properties.from(Blocks.SLIME_BLOCK)));

		StaticPowerRegistry.preRegisterBlock(BlockRedstoneAlloy = new StaticPowerBlock("block_redstone_alloy",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.7f)));
		StaticPowerRegistry.preRegisterBlock(BlockInertInfusion = new StaticPowerBlock("block_inert_infusion",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.STONE_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.7f)));

		StaticPowerRegistry.preRegisterBlock(BlockStaticMetal = new StaticPowerBlock("block_static_metal",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(2.5f)));
		StaticPowerRegistry.preRegisterBlock(BlockEnergizedMetal = new StaticPowerBlock("block_energized_metal",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.0f)));
		StaticPowerRegistry.preRegisterBlock(BlockLumumMetal = new StaticPowerBlock("block_lumum_metal",
				Block.Properties.create(Material.IRON).harvestLevel(HarvestLevel.DIAMOND_TOOL.ordinal()).harvestTool(ToolType.PICKAXE).hardnessAndResistance(4.0f)));

		// Machine Blocks
		StaticPowerRegistry
				.preRegisterBlock(MachineBlockIron = new StaticPowerCutoutBlock("machine_block_iron", Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));
		StaticPowerRegistry
				.preRegisterBlock(MachineBlockBasic = new StaticPowerCutoutBlock("machine_block_basic", Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));
		StaticPowerRegistry.preRegisterBlock(
				MachineBlockAdvanced = new StaticPowerCutoutBlock("machine_block_advanced", Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));
		StaticPowerRegistry
				.preRegisterBlock(MachineBlockStatic = new StaticPowerCutoutBlock("machine_block_static", Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));
		StaticPowerRegistry.preRegisterBlock(
				MachineBlockEnergized = new StaticPowerCutoutBlock("machine_block_energized", Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));
		StaticPowerRegistry
				.preRegisterBlock(MachineBlockLumum = new StaticPowerCutoutBlock("machine_block_lumum", Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));

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
		StaticPowerRegistry.preRegisterBlock(FluidGenerator = new BlockFluidGenerator("machine_generator_fluid"));
		StaticPowerRegistry.preRegisterBlock(Crucible = new BlockCrucible("machine_crucible"));
		StaticPowerRegistry.preRegisterBlock(Squeezer = new BlockSqueezer("machine_squeezer"));
		StaticPowerRegistry.preRegisterBlock(Bottler = new BlockBottler("machine_bottler"));
		StaticPowerRegistry.preRegisterBlock(SolderingTable = new BlockSolderingTable("soldering_table"));
		StaticPowerRegistry.preRegisterBlock(AutoSolderingTable = new BlockAutoSolderingTable("machine_industrial_soldering_table"));
		StaticPowerRegistry.preRegisterBlock(AutoCraftingTable = new BlockAutoCraftingTable("machine_industrial_crafting_table"));
		StaticPowerRegistry.preRegisterBlock(FluidInfuser = new BlockFluidInfuser("machine_fluid_infuser"));
		StaticPowerRegistry.preRegisterBlock(Centrifuge = new BlockCentrifuge("machine_centrifuge"));
		StaticPowerRegistry.preRegisterBlock(FusionFurnace = new BlockFusionFurnace("machine_fusion_furnace"));
		StaticPowerRegistry.preRegisterBlock(Miner = new BlockMiner("machine_miner"));
		StaticPowerRegistry.preRegisterBlock(ElectricMiner = new BlockElectricMiner("machine_electric_miner"));
		StaticPowerRegistry.preRegisterBlock(Evaporator = new BlockEvaporator("machine_evaporator"));
		StaticPowerRegistry.preRegisterBlock(Condenser = new BlockCondenser("machine_condenser"));
		StaticPowerRegistry.preRegisterBlock(Vulcanizer = new BlockVulcanizer("machine_vulcanizer"));

		StaticPowerRegistry.preRegisterBlock(AluminiumHeatSink = new BlockHeatSink("heat_sink_aluminium", StaticPowerTiers.ALUMINIUM));
		StaticPowerRegistry.preRegisterBlock(CopperHeatSink = new BlockHeatSink("heat_sink_copper", StaticPowerTiers.COPPER));
		StaticPowerRegistry.preRegisterBlock(TinHeatSink = new BlockHeatSink("heat_sink_tin", StaticPowerTiers.TIN));
		StaticPowerRegistry.preRegisterBlock(SilverHeatSink = new BlockHeatSink("heat_sink_silver", StaticPowerTiers.SILVER));
		StaticPowerRegistry.preRegisterBlock(GoldHeatSink = new BlockHeatSink("heat_sink_gold", StaticPowerTiers.GOLD));

		StaticPowerRegistry.preRegisterBlock(BasicTank = new BlockTank("tank_basic"));
		StaticPowerRegistry.preRegisterBlock(Pump = new BlockPump("pump"));

		StaticPowerRegistry.preRegisterBlock(DigistoreManager = new BlockDigistoreManager("digistore_manager"));
		StaticPowerRegistry.preRegisterBlock(DigistoreIOPort = new BlockDigistoreIOPort("digistore_io_port"));
		StaticPowerRegistry.preRegisterBlock(Digistore = new BlockDigistore("digistore"));
		StaticPowerRegistry.preRegisterBlock(DigistoreServerRack = new BlockDigistoreServerRack("digistore_server_rack"));
		StaticPowerRegistry.preRegisterBlock(PatternStorage = new BlockPatternStorage("digistore_pattern_storage"));

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

		StaticPowerRegistry.preRegisterBlock(FluidCableBasic = new BlockFluidCable("cable_fluid_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterBlock(FluidCableAdvanced = new BlockFluidCable("cable_fluid_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterBlock(FluidCableStatic = new BlockFluidCable("cable_fluid_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterBlock(FluidCableEnergized = new BlockFluidCable("cable_fluid_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterBlock(FluidCableLumum = new BlockFluidCable("cable_fluid_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterBlock(FluidCableCreative = new BlockFluidCable("cable_fluid_creative", StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterBlock(IndustrialFluidCableBasic = new BlockIndustrialFluidCable("cable_industrial_fluid_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterBlock(IndustrialFluidCableAdvanced = new BlockIndustrialFluidCable("cable_industrial_fluid_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterBlock(IndustrialFluidCableStatic = new BlockIndustrialFluidCable("cable_industrial_fluid_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterBlock(IndustrialFluidCableEnergized = new BlockIndustrialFluidCable("cable_industrial_fluid_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterBlock(IndustrialFluidCableLumum = new BlockIndustrialFluidCable("cable_industrial_fluid_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterBlock(IndustrialFluidCableCreative = new BlockIndustrialFluidCable("cable_industrial_fluid_creative", StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterBlock(CopperHeatCable = new BlockHeatCable("cable_heat_copper", StaticPowerTiers.COPPER));
		StaticPowerRegistry.preRegisterBlock(TinHeatCable = new BlockHeatCable("cable_heat_tin", StaticPowerTiers.TIN));
		StaticPowerRegistry.preRegisterBlock(SilverHeatCable = new BlockHeatCable("cable_heat_silver", StaticPowerTiers.SILVER));
		StaticPowerRegistry.preRegisterBlock(GoldHeatCable = new BlockHeatCable("cable_heat_gold", StaticPowerTiers.GOLD));
		StaticPowerRegistry.preRegisterBlock(AluminiumHeatCable = new BlockHeatCable("cable_heat_aluminium", StaticPowerTiers.ALUMINIUM));

		StaticPowerRegistry.preRegisterBlock(BatteryBasic = new BlockBattery("battery_block_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterBlock(BatteryAdvanced = new BlockBattery("battery_block_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterBlock(BatteryStatic = new BlockBattery("battery_block_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterBlock(BatteryEnergized = new BlockBattery("battery_block_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterBlock(BatteryLumum = new BlockBattery("battery_block_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterBlock(BatteryCreative = new BlockBattery("battery_block_creative", StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterBlock(RubberTreeStrippedWood = new StaticPowerTreeLog("rubber_tree_stripped_wood", MaterialColor.WOOD, Block.Properties.from(Blocks.STRIPPED_OAK_WOOD)));
		StaticPowerRegistry.preRegisterBlock(RubberTreeStrippedLog = new StaticPowerTreeLog("rubber_tree_stripped_log", MaterialColor.WOOD, Block.Properties.from(Blocks.STRIPPED_OAK_LOG)));
		StaticPowerRegistry.preRegisterBlock(RubberTreeWood = new StaticPowerTreeLog("rubber_tree_wood", MaterialColor.WOOD, RubberTreeStrippedWood, Block.Properties.from(Blocks.OAK_LOG),
				() -> StaticPowerConfig.minRubberWoodBarkPerStrip, () -> StaticPowerConfig.maxRubberWoodBarkPerStrip, () -> ModItems.RubberWoodBark));
		StaticPowerRegistry.preRegisterBlock(RubberTreeLog = new StaticPowerTreeLog("rubber_tree_log", MaterialColor.WOOD, RubberTreeStrippedLog, Block.Properties.from(Blocks.OAK_LOG),
				() -> StaticPowerConfig.minRubberWoodBarkPerStrip, () -> StaticPowerConfig.maxRubberWoodBarkPerStrip, () -> ModItems.RubberWoodBark));
		StaticPowerRegistry.preRegisterBlock(RubberTreePlanks = new StaticPowerBlock("rubber_tree_planks", Block.Properties.from(Blocks.OAK_PLANKS)));
		StaticPowerRegistry.preRegisterBlock(RubberTreeLeaves = new StaticPowerTreeLeaves("rubber_tree_leaves", Block.Properties.from(Blocks.OAK_LEAVES)));
		StaticPowerRegistry.preRegisterBlock(RubberTreeSapling = new StaticPowerSapling("rubber_tree_sapling", () -> new RubberTree(), Block.Properties.from(Blocks.OAK_SAPLING)));
	}
}
