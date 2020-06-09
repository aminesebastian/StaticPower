package theking530.staticpower.initialization;

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
import theking530.staticpower.tileentities.cables.item.BlockItemCable;
import theking530.staticpower.tileentities.cables.power.BlockPowerCable;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.BlockDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport.BlockDigistoreIOPort;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.BlockDigistoreManager;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.networkwire.BlockDigistoreNetworkWire;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.BlockVacuumChest;
import theking530.staticpower.tileentities.powered.chargingstation.BlockChargingStation;
import theking530.staticpower.tileentities.powered.poweredfurnace.BlockPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.BlockPoweredGrinder;
import theking530.staticpower.tileentities.powered.solarpanels.BlockSolarPanel;
import theking530.staticpower.utilities.EHarvestLevel;
import theking530.staticpower.utilities.Tier;

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
	public static StaticPowerRotatePillarBlock StaticWood;
	public static StaticPowerRotatePillarBlock EnergizedWood;
	public static StaticPowerRotatePillarBlock LumumWood;

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
	public static BlockSolarPanel SolarPanelBasic;
	public static BlockPoweredFurnace PoweredFurnace;
	public static BlockPoweredGrinder PoweredGrinder;
	public static BlockDigistoreNetworkWire DigistoreWire;
	public static BlockDigistoreManager DigistoreManager;
	public static BlockDigistoreIOPort DigistoreIOPort;
	public static BlockDigistore Digistore;

	// Cables
	public static BlockItemCable ItemCable;
	public static BlockPowerCable PowerCable;

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
		StaticPowerRegistry.preRegisterBlock(StaticWood = new StaticPowerRotatePillarBlock("wood_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry
				.preRegisterBlock(EnergizedWood = new StaticPowerRotatePillarBlock("wood_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry.preRegisterBlock(LumumWood = new StaticPowerRotatePillarBlock("wood_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));

		// Planks
		StaticPowerRegistry.preRegisterBlock(StaticPlanks = new StaticPowerBlock("planks_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry.preRegisterBlock(EnergizedPlanks = new StaticPowerBlock("planks_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		StaticPowerRegistry.preRegisterBlock(LumumPlanks = new StaticPowerBlock("planks_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));

		// Ore
		StaticPowerRegistry.preRegisterBlock(OreCopper = new StaticPowerOre("ore_copper", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.9f));
		StaticPowerRegistry.preRegisterBlock(OreTin = new StaticPowerOre("ore_tin", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.7f));
		StaticPowerRegistry.preRegisterBlock(OreZinc = new StaticPowerOre("ore_zinc", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.0f));
		StaticPowerRegistry.preRegisterBlock(OreAluminium = new StaticPowerOre("ore_aluminium", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.0f));
		StaticPowerRegistry.preRegisterBlock(OreMagnesium = new StaticPowerOre("ore_magnesium", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 2.0f));
		StaticPowerRegistry.preRegisterBlock(OreSilver = new StaticPowerOre("ore_silver", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 1.5f));
		StaticPowerRegistry.preRegisterBlock(OreLead = new StaticPowerOre("ore_lead", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 1.5f));
		StaticPowerRegistry.preRegisterBlock(OrePlatinum = new StaticPowerOre("ore_platinum", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 2.0f));
		StaticPowerRegistry.preRegisterBlock(OreRuby = new StaticPowerOre("ore_ruby", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 3.0f));
		StaticPowerRegistry.preRegisterBlock(OreSapphire = new StaticPowerOre("ore_sapphire", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 3.0f));
		StaticPowerRegistry.preRegisterBlock(OreTungsten = new StaticPowerOre("ore_tungsten", ToolType.PICKAXE, EHarvestLevel.DIAMOND_TOOL, 4.0f));

		// Machines
		StaticPowerRegistry.preRegisterBlock(VacuumChest = new BlockVacuumChest("chest_vacuum"));
		StaticPowerRegistry.preRegisterBlock(ChargingStation = new BlockChargingStation("machine_charging_station"));
		StaticPowerRegistry.preRegisterBlock(SolarPanelBasic = new BlockSolarPanel("solar_panel_basic", Tier.BASIC));
		StaticPowerRegistry.preRegisterBlock(PoweredFurnace = new BlockPoweredFurnace("machine_powered_furnace"));
		StaticPowerRegistry.preRegisterBlock(PoweredGrinder = new BlockPoweredGrinder("machine_powered_grinder"));

		StaticPowerRegistry.preRegisterBlock(DigistoreWire = new BlockDigistoreNetworkWire("digistore_wire"));
		StaticPowerRegistry.preRegisterBlock(DigistoreManager = new BlockDigistoreManager("digistore_manager"));
		StaticPowerRegistry.preRegisterBlock(DigistoreIOPort = new BlockDigistoreIOPort("digistore_io_port"));
		StaticPowerRegistry.preRegisterBlock(Digistore = new BlockDigistore("digistore"));

		// Cables
		StaticPowerRegistry.preRegisterBlock(PowerCable = new BlockPowerCable("cable_power"));
		StaticPowerRegistry.preRegisterBlock(ItemCable = new BlockItemCable("cable_item"));
	}
}
