package theking530.staticpower.initialization;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.Registry;
import theking530.staticpower.blocks.AdvancedEarth;
import theking530.staticpower.blocks.EnergizedGrass;
import theking530.staticpower.blocks.StaticGrass;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.blocks.StaticPowerRotatePillarBlock;
import theking530.staticpower.blocks.crops.BaseSimplePlant;
import theking530.staticpower.blocks.decorative.Lamp;
import theking530.staticpower.blocks.decorative.StaticPowerGlassBlock;
import theking530.staticpower.machines.chargingstation.BlockChargingStation;
import theking530.staticpower.tileentity.solarpanels.BlockSolarPanel;
import theking530.staticpower.tileentity.vacuumchest.BlockVacuumChest;
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
	public static StaticPowerOre OreNickel;
	public static StaticPowerOre OreAluminium;
	public static StaticPowerOre OreRuby;
	public static StaticPowerOre OreSapphire;

	// Machines
	public static BlockVacuumChest VacuumChest;
	public static BlockChargingStation ChargingStation;
	public static BlockSolarPanel SolarPanelBasic;

	public static void init() {
		// Decorative
		Registry.preRegisterBlock(StaticLamp = new Lamp("lamp_static"));
		Registry.preRegisterBlock(EnergizedLamp = new Lamp("lamp_energized"));
		Registry.preRegisterBlock(LumumLamp = new Lamp("lamp_lumum"));
		Registry.preRegisterBlock(ObsidianGlass = new StaticPowerGlassBlock("glass_obsidian"));
		Registry.preRegisterBlock(AdvancedEarth = new AdvancedEarth("advanced_earth"));
		Registry.preRegisterBlock(StaticGrass = new StaticGrass("grass_static"));
		Registry.preRegisterBlock(EnergizedGrass = new EnergizedGrass("grass_energized"));

		// Plants
		Registry.preRegisterBlock(StaticPlant = new BaseSimplePlant("plant_static"));
		Registry.preRegisterBlock(EnergizedPlant = new BaseSimplePlant("plant_energized"));
		Registry.preRegisterBlock(LumumPlant = new BaseSimplePlant("plant_lumum"));

		// Wood
		Registry.preRegisterBlock(StaticWood = new StaticPowerRotatePillarBlock("wood_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(EnergizedWood = new StaticPowerRotatePillarBlock("wood_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(LumumWood = new StaticPowerRotatePillarBlock("wood_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));

		// Planks
		Registry.preRegisterBlock(StaticPlanks = new StaticPowerBlock("planks_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(EnergizedPlanks = new StaticPowerBlock("planks_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(LumumPlanks = new StaticPowerBlock("planks_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));

		// Ore
		Registry.preRegisterBlock(OreCopper = new StaticPowerOre("ore_copper", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.9f));
		Registry.preRegisterBlock(OreTin = new StaticPowerOre("ore_tin", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.7f));
		Registry.preRegisterBlock(OreZinc = new StaticPowerOre("ore_zinc", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.0f));
		Registry.preRegisterBlock(OreAluminium = new StaticPowerOre("ore_aluminium", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 1.0f));
		Registry.preRegisterBlock(OreMagnesium = new StaticPowerOre("ore_magnesium", ToolType.PICKAXE, EHarvestLevel.STONE_TOOL, 2.0f));
		Registry.preRegisterBlock(OreSilver = new StaticPowerOre("ore_silver", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 1.5f));
		Registry.preRegisterBlock(OreLead = new StaticPowerOre("ore_lead", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 1.5f));
		Registry.preRegisterBlock(OrePlatinum = new StaticPowerOre("ore_platinum", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 2.0f));
		Registry.preRegisterBlock(OreRuby = new StaticPowerOre("ore_ruby", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 3.0f));
		Registry.preRegisterBlock(OreSapphire = new StaticPowerOre("ore_sapphire", ToolType.PICKAXE, EHarvestLevel.IRON_TOOL, 3.0f));
		Registry.preRegisterBlock(OreTungsten = new StaticPowerOre("ore_tungsten", ToolType.PICKAXE, EHarvestLevel.DIAMOND_TOOL, 4.0f));

		// Machines
		Registry.preRegisterBlock(VacuumChest = new BlockVacuumChest("chest_vacuum"));
		Registry.preRegisterBlock(ChargingStation = new BlockChargingStation("machine_charging_station"));
		Registry.preRegisterBlock(SolarPanelBasic = new BlockSolarPanel("solar_panel_basic", Tier.BASIC));
	}
}
