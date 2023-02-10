package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.digistorenetwork.digistore.BlockDigistore;
import theking530.staticpower.blockentities.digistorenetwork.ioport.BlockDigistoreIOPort;
import theking530.staticpower.blockentities.digistorenetwork.manager.BlockDigistoreManager;
import theking530.staticpower.blockentities.digistorenetwork.patternstorage.BlockPatternStorage;
import theking530.staticpower.blockentities.digistorenetwork.severrack.BlockDigistoreServerRack;
import theking530.staticpower.blockentities.digistorenetwork.wireterminal.BlockDigistoreWireConnector;
import theking530.staticpower.blockentities.machines.autocrafter.BlockAutoCraftingTable;
import theking530.staticpower.blockentities.machines.autosmith.BlockAutoSmith;
import theking530.staticpower.blockentities.machines.autosolderingtable.BlockAutoSolderingTable;
import theking530.staticpower.blockentities.machines.bottler.BlockBottler;
import theking530.staticpower.blockentities.machines.caster.BlockCaster;
import theking530.staticpower.blockentities.machines.centrifuge.BlockCentrifuge;
import theking530.staticpower.blockentities.machines.chargingstation.BlockChargingStation;
import theking530.staticpower.blockentities.machines.cropfarmer.BlockBasicFarmer;
import theking530.staticpower.blockentities.machines.crucible.BlockCrucible;
import theking530.staticpower.blockentities.machines.enchanter.BlockEnchanter;
import theking530.staticpower.blockentities.machines.fermenter.BlockFermenter;
import theking530.staticpower.blockentities.machines.fluidinfuser.BlockFluidInfuser;
import theking530.staticpower.blockentities.machines.former.BlockFormer;
import theking530.staticpower.blockentities.machines.fusionfurnace.BlockFusionFurnace;
import theking530.staticpower.blockentities.machines.hydroponics.farmer.BlockHydroponicFarmer;
import theking530.staticpower.blockentities.machines.hydroponics.pod.BlockHydroponicPod;
import theking530.staticpower.blockentities.machines.laboratory.BlockLaboratory;
import theking530.staticpower.blockentities.machines.lathe.BlockLathe;
import theking530.staticpower.blockentities.machines.lumbermill.BlockLumberMill;
import theking530.staticpower.blockentities.machines.mixer.BlockMixer;
import theking530.staticpower.blockentities.machines.packager.BlockPackager;
import theking530.staticpower.blockentities.machines.poweredfurnace.BlockPoweredFurnace;
import theking530.staticpower.blockentities.machines.poweredgrinder.BlockPoweredGrinder;
import theking530.staticpower.blockentities.machines.pump.BlockPump;
import theking530.staticpower.blockentities.machines.pump.PumpTube;
import theking530.staticpower.blockentities.machines.refinery.boiler.BlockRefineryBoiler;
import theking530.staticpower.blockentities.machines.refinery.controller.BlockRefineryController;
import theking530.staticpower.blockentities.machines.refinery.fluidio.input.BlockRefineryFluidInput;
import theking530.staticpower.blockentities.machines.refinery.fluidio.output.BlockRefineryFluidOutput;
import theking530.staticpower.blockentities.machines.refinery.heatvent.BlockRefineryHeatVent;
import theking530.staticpower.blockentities.machines.refinery.iteminput.BlockRefineryItemInput;
import theking530.staticpower.blockentities.machines.refinery.powertap.BlockRefineryPowerTap;
import theking530.staticpower.blockentities.machines.refinery.tower.BlockRefineryTower;
import theking530.staticpower.blockentities.machines.squeezer.BlockSqueezer;
import theking530.staticpower.blockentities.machines.treefarmer.BlockTreeFarmer;
import theking530.staticpower.blockentities.machines.tumbler.BlockTumbler;
import theking530.staticpower.blockentities.machines.vulcanizer.BlockVulcanizer;
import theking530.staticpower.blockentities.nonpowered.alloyfurnace.BlockAlloyFurnace;
import theking530.staticpower.blockentities.nonpowered.cauldron.BlockCauldron;
import theking530.staticpower.blockentities.nonpowered.chest.BlockStaticChest;
import theking530.staticpower.blockentities.nonpowered.condenser.BlockCondenser;
import theking530.staticpower.blockentities.nonpowered.conveyors.extractor.BlockConveyorExtractor;
import theking530.staticpower.blockentities.nonpowered.conveyors.hopper.BlockConveyorHopper;
import theking530.staticpower.blockentities.nonpowered.conveyors.rampdown.BlockRampDownConveyor;
import theking530.staticpower.blockentities.nonpowered.conveyors.rampup.BlockRampUpConveyor;
import theking530.staticpower.blockentities.nonpowered.conveyors.straight.BlockStraightConveyor;
import theking530.staticpower.blockentities.nonpowered.conveyors.supplier.BlockConveyorSupplier;
import theking530.staticpower.blockentities.nonpowered.directdropper.BlockDirectDropper;
import theking530.staticpower.blockentities.nonpowered.evaporator.BlockEvaporator;
import theking530.staticpower.blockentities.nonpowered.experiencehopper.BlockExperienceHopper;
import theking530.staticpower.blockentities.nonpowered.miner.BlockMiner;
import theking530.staticpower.blockentities.nonpowered.placer.BlockAutomaticPlacer;
import theking530.staticpower.blockentities.nonpowered.randomitem.BlockRandomItemGenerator;
import theking530.staticpower.blockentities.nonpowered.researchcheater.BlockResearchCheater;
import theking530.staticpower.blockentities.nonpowered.solderingtable.BlockSolderingTable;
import theking530.staticpower.blockentities.nonpowered.tank.BlockTank;
import theking530.staticpower.blockentities.nonpowered.vacuumchest.BlockVacuumChest;
import theking530.staticpower.blockentities.power.battery.BlockBattery;
import theking530.staticpower.blockentities.power.circuit_breaker.BlockCircuitBreaker;
import theking530.staticpower.blockentities.power.electricminer.BlockElectricMiner;
import theking530.staticpower.blockentities.power.fluidgenerator.BlockFluidGenerator;
import theking530.staticpower.blockentities.power.heatsink.BlockHeatSink;
import theking530.staticpower.blockentities.power.inverter.BlockInverter;
import theking530.staticpower.blockentities.power.lightsocket.BlockLightSocket;
import theking530.staticpower.blockentities.power.powermonitor.BlockPowerMonitor;
import theking530.staticpower.blockentities.power.rectifier.BlockRectifier;
import theking530.staticpower.blockentities.power.resistor.BlockResistor;
import theking530.staticpower.blockentities.power.solarpanels.BlockSolarPanel;
import theking530.staticpower.blockentities.power.solidgenerator.BlockSolidGenerator;
import theking530.staticpower.blockentities.power.transformer.BlockTransformer;
import theking530.staticpower.blockentities.power.turbine.BlockTurbine;
import theking530.staticpower.blockentities.power.wireconnector.BlockWireConnector;
import theking530.staticpower.blocks.EnergizedGrass;
import theking530.staticpower.blocks.StaticGrass;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerCutoutBlock;
import theking530.staticpower.blocks.StaticPowerFarmland;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.blocks.StaticPowerRotatePillarBlock;
import theking530.staticpower.blocks.StaticPowerSlimeBlock;
import theking530.staticpower.blocks.crops.BaseSimplePlant;
import theking530.staticpower.blocks.decorative.Lamp;
import theking530.staticpower.blocks.decorative.StaticPowerGlassBlock;
import theking530.staticpower.blocks.tree.StaticPowerSapling;
import theking530.staticpower.blocks.tree.StaticPowerTreeLeaves;
import theking530.staticpower.blocks.tree.StaticPowerTreeLog;
import theking530.staticpower.cables.CableTiers;
import theking530.staticpower.cables.CableTiers.CableTier;
import theking530.staticpower.cables.digistore.BlockDigistoreNetworkWire;
import theking530.staticpower.cables.fluid.BlockCapillaryFluidCable;
import theking530.staticpower.cables.fluid.BlockFluidCable;
import theking530.staticpower.cables.fluid.BlockIndustrialFluidCable;
import theking530.staticpower.cables.heat.BlockHeatCable;
import theking530.staticpower.cables.item.BlockItemCable;
import theking530.staticpower.cables.power.BlockIndustrialPowerCable;
import theking530.staticpower.cables.power.BlockPowerCable;
import theking530.staticpower.cables.redstone.basic.BlockRedstoneCable;
import theking530.staticpower.cables.redstone.bundled.BlockBundledRedstoneCable;
import theking530.staticpower.cables.scaffold.BlockScaffoldCable;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.data.StaticPowerTiers;

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StaticPower.MOD_ID);
	// Decorative
	public static final RegistryObject<Lamp> StaticLamp = registerBlock("lamp_static", () -> new Lamp());
	public static final RegistryObject<Lamp> EnergizedLamp = registerBlock("lamp_energized", () -> new Lamp());
	public static final RegistryObject<Lamp> LumumLamp = registerBlock("lamp_lumum", () -> new Lamp());
	public static final RegistryObject<StaticPowerGlassBlock> ObsidianGlass = registerBlock("glass_obsidian", () -> new StaticPowerGlassBlock());
	public static final RegistryObject<StaticPowerBlock> SmeepWool = registerBlock("smeep_wool", () -> new StaticPowerBlock(Properties.copy(Blocks.LIME_WOOL)));

	public static final RegistryObject<StaticPowerFarmland> StaticFarmland = registerBlock("farmland_static", () -> new StaticPowerFarmland());
	public static final RegistryObject<StaticPowerFarmland> EnergizedFarmland = registerBlock("farmland_energized", () -> new StaticPowerFarmland());
	public static final RegistryObject<StaticPowerFarmland> LumumFarmland = registerBlock("farmland_lumum", () -> new StaticPowerFarmland());

	public static final RegistryObject<StaticGrass> StaticGrass = registerBlock("grass_static", () -> new StaticGrass());
	public static final RegistryObject<EnergizedGrass> EnergizedGrass = registerBlock("grass_energized", () -> new EnergizedGrass());

	// Plants
	public static final RegistryObject<BaseSimplePlant> StaticCrop = registerBlock("crop_static", () -> new BaseSimplePlant(() -> ModItems.StaticSeeds.get()));
	public static final RegistryObject<BaseSimplePlant> EnergizedCrop = registerBlock("crop_energized", () -> new BaseSimplePlant(() -> ModItems.EnergizedSeeds.get()));
	public static final RegistryObject<BaseSimplePlant> LumumCrop = registerBlock("crop_lumum", () -> new BaseSimplePlant(() -> ModItems.LumumSeeds.get()));

	// Wood
	public static final RegistryObject<StaticPowerRotatePillarBlock> StaticLog = registerBlock("log_static",
			() -> new StaticPowerRotatePillarBlock(Properties.copy(Blocks.BIRCH_WOOD)));
	public static final RegistryObject<StaticPowerRotatePillarBlock> EnergizedLog = registerBlock("log_energized",
			() -> new StaticPowerRotatePillarBlock(Properties.copy(Blocks.BIRCH_WOOD)));
	public static final RegistryObject<StaticPowerRotatePillarBlock> LumumLog = registerBlock("log_lumum",
			() -> new StaticPowerRotatePillarBlock(Properties.copy(Blocks.BIRCH_WOOD)));

	// Planks
	public static final RegistryObject<StaticPowerBlock> StaticPlanks = registerBlock("planks_static", () -> new StaticPowerBlock(Properties.copy(Blocks.OAK_PLANKS)));
	public static final RegistryObject<StaticPowerBlock> EnergizedPlanks = registerBlock("planks_energized", () -> new StaticPowerBlock(Properties.copy(Blocks.OAK_PLANKS)));
	public static final RegistryObject<StaticPowerBlock> LumumPlanks = registerBlock("planks_lumum", () -> new StaticPowerBlock(Properties.copy(Blocks.OAK_PLANKS)));

	// Ore
	public static final RegistryObject<StaticPowerOre> OreTin = registerBlock("ore_tin", () -> new StaticPowerOre(Properties.copy(Blocks.COPPER_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreZinc = registerBlock("ore_zinc", () -> new StaticPowerOre(Properties.copy(Blocks.IRON_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreSilver = registerBlock("ore_silver", () -> new StaticPowerOre(Properties.copy(Blocks.GOLD_ORE), 2, 4));
	public static final RegistryObject<StaticPowerOre> OreLead = registerBlock("ore_lead", () -> new StaticPowerOre(Properties.copy(Blocks.GOLD_ORE), 2, 4));
	public static final RegistryObject<StaticPowerOre> OreTungsten = registerBlock("ore_tungsten", () -> new StaticPowerOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreMagnesium = registerBlock("ore_magnesium", () -> new StaticPowerOre(Properties.copy(Blocks.IRON_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OrePlatinum = registerBlock("ore_platinum", () -> new StaticPowerOre(Properties.copy(Blocks.GOLD_ORE), 2, 4));
	public static final RegistryObject<StaticPowerOre> OreAluminum = registerBlock("ore_aluminum", () -> new StaticPowerOre(Properties.copy(Blocks.COPPER_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreRuby = registerBlock("ore_ruby", () -> new StaticPowerOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5));
	public static final RegistryObject<StaticPowerOre> OreSapphire = registerBlock("ore_sapphire", () -> new StaticPowerOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5));
	public static final RegistryObject<StaticPowerOre> OreRustyIron = registerBlock("ore_rusty_iron", () -> new StaticPowerOre(Properties.copy(Blocks.COAL_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreUranium = registerBlock("ore_uranium", () -> new StaticPowerOre(Properties.copy(Blocks.COAL_ORE), 1, 2));

	// Deepslate Ore
	public static final RegistryObject<StaticPowerOre> OreDeepslateTin = registerBlock("ore_deepslate_tin",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreDeepslateZinc = registerBlock("ore_deepslate_zinc",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreDeepslateSilver = registerBlock("ore_deepslate_silver",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4));
	public static final RegistryObject<StaticPowerOre> OreDeepslateLead = registerBlock("ore_deepslate_lead",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4));
	public static final RegistryObject<StaticPowerOre> OreDeepslateTungsten = registerBlock("ore_deepslate_tungsten",
			() -> new StaticPowerOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreDeepslateMagnesium = registerBlock("ore_deepslate_magnesium",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreDeepslatePlatinum = registerBlock("ore_deepslate_platinum",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4));
	public static final RegistryObject<StaticPowerOre> OreDeepslateAluminum = registerBlock("ore_deepslate_aluminum",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2));
	public static final RegistryObject<StaticPowerOre> OreDeepslateRuby = registerBlock("ore_deepslate_ruby",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5));
	public static final RegistryObject<StaticPowerOre> OreDeepslateSapphire = registerBlock("ore_deepslate_sapphire",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5));
	public static final RegistryObject<StaticPowerOre> OreDeepslateUranium = registerBlock("ore_deepslate_uranium",
			() -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4));

	// Nether Ore
	public static final RegistryObject<StaticPowerOre> OreNetherSilver = registerBlock("ore_nether_silver",
			() -> new StaticPowerOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5));
	public static final RegistryObject<StaticPowerOre> OreNetherPlatinum = registerBlock("ore_nether_platinum",
			() -> new StaticPowerOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5));
	public static final RegistryObject<StaticPowerOre> OreNetherTungsten = registerBlock("ore_nether_tungsten",
			() -> new StaticPowerOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5));

	// Storage Blocks
	public static final RegistryObject<StaticPowerBlock> BlockTin = registerBlock("block_tin", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockCutTin = registerBlock("block_cut_tin", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));

	public static final RegistryObject<StaticPowerBlock> BlockZinc = registerBlock("block_zinc", () -> new StaticPowerBlock(Properties.copy(Blocks.IRON_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockCutZinc = registerBlock("block_cut_zinc", () -> new StaticPowerBlock(Properties.copy(Blocks.IRON_BLOCK)));

	public static final RegistryObject<StaticPowerBlock> BlockAluminum = registerBlock("block_aluminum", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockCutAluminum = registerBlock("block_cut_aluminum", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));

	public static final RegistryObject<StaticPowerBlock> BlockMagnesium = registerBlock("block_magnesium", () -> new StaticPowerBlock(Properties.copy(Blocks.IRON_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockCutMagnesium = registerBlock("block_cut_magnesium", () -> new StaticPowerBlock(Properties.copy(Blocks.IRON_BLOCK)));

	public static final RegistryObject<StaticPowerBlock> BlockSilver = registerBlock("block_silver", () -> new StaticPowerBlock(Properties.copy(Blocks.GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockCutSilver = registerBlock("block_cut_silver", () -> new StaticPowerBlock(Properties.copy(Blocks.GOLD_BLOCK)));

	public static final RegistryObject<StaticPowerBlock> BlockBrass = registerBlock("block_brass", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockCutBrass = registerBlock("block_cut_brass", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));

	public static final RegistryObject<StaticPowerBlock> BlockBronze = registerBlock("block_bronze", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockCutBronze = registerBlock("block_cut_bronze", () -> new StaticPowerBlock(Properties.copy(Blocks.COPPER_BLOCK)));

	public static final RegistryObject<StaticPowerBlock> BlockLead = registerBlock("block_lead", () -> new StaticPowerBlock(Properties.copy(Blocks.GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockPlatinum = registerBlock("block_platinum", () -> new StaticPowerBlock(Properties.copy(Blocks.GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRuby = registerBlock("block_ruby", () -> new StaticPowerBlock(Properties.copy(Blocks.DIAMOND_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockSapphire = registerBlock("block_sapphire", () -> new StaticPowerBlock(Properties.copy(Blocks.DIAMOND_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockTungsten = registerBlock("block_tungsten", () -> new StaticPowerBlock(Properties.copy(Blocks.DIAMOND_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRedstoneAlloy = registerBlock("block_redstone_alloy", () -> new StaticPowerBlock(Properties.copy(Blocks.IRON_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockInertInfusion = registerBlock("block_inert_infusion", () -> new StaticPowerBlock(Properties.copy(Blocks.GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockStaticMetal = registerBlock("block_static_metal", () -> new StaticPowerBlock(Properties.copy(Blocks.IRON_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockEnergizedMetal = registerBlock("block_energized_metal",
			() -> new StaticPowerBlock(Properties.copy(Blocks.GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockLumumMetal = registerBlock("block_lumum_metal", () -> new StaticPowerBlock(Properties.copy(Blocks.DIAMOND_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockUranium = registerBlock("block_uranium", () -> new StaticPowerBlock(Properties.copy(Blocks.DIAMOND_BLOCK)));
	public static final RegistryObject<StaticPowerSlimeBlock> BlockLatex = registerBlock("block_latex", () -> new StaticPowerSlimeBlock(Properties.copy(Blocks.SLIME_BLOCK)));
	public static final RegistryObject<StaticPowerSlimeBlock> BlockRubber = registerBlock("block_rubber", () -> new StaticPowerSlimeBlock(Properties.copy(Blocks.SLIME_BLOCK)));

	// Raw Material Blocks
	public static final RegistryObject<StaticPowerBlock> BlockRawTin = registerBlock("block_raw_tin", () -> new StaticPowerBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawZinc = registerBlock("block_raw_zinc", () -> new StaticPowerBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawAluminum = registerBlock("block_raw_aluminum",
			() -> new StaticPowerBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawMagnesium = registerBlock("block_raw_magnesium",
			() -> new StaticPowerBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawSilver = registerBlock("block_raw_silver", () -> new StaticPowerBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawLead = registerBlock("block_raw_lead", () -> new StaticPowerBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawPlatinum = registerBlock("block_raw_platinum", () -> new StaticPowerBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawTungsten = registerBlock("block_raw_tungsten", () -> new StaticPowerBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawStaticMetal = registerBlock("block_raw_static_metal",
			() -> new StaticPowerBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawEnergizedMetal = registerBlock("block_raw_energized_metal",
			() -> new StaticPowerBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawLumumMetal = registerBlock("block_raw_lumum_metal",
			() -> new StaticPowerBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)));
	public static final RegistryObject<StaticPowerBlock> BlockRawUranium = registerBlock("block_raw_uranium", () -> new StaticPowerBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)));

	// Machine blocks.
	public static final RegistryObject<StaticPowerCutoutBlock> MachineBlockAluminum = registerBlock("machine_block_aluminum",
			() -> new StaticPowerCutoutBlock(Properties.copy(Blocks.IRON_DOOR)));
	public static final RegistryObject<StaticPowerCutoutBlock> MachineBlockIndustrial = registerBlock("machine_block_industrial",
			() -> new StaticPowerCutoutBlock(Properties.copy(Blocks.IRON_DOOR)));
	public static final RegistryObject<StaticPowerCutoutBlock> MachineBlockBasic = registerBlock("machine_block_basic",
			() -> new StaticPowerCutoutBlock(Properties.copy(Blocks.IRON_DOOR)));
	public static final RegistryObject<StaticPowerCutoutBlock> MachineBlockAdvanced = registerBlock("machine_block_advanced",
			() -> new StaticPowerCutoutBlock(Properties.copy(Blocks.IRON_DOOR)));
	public static final RegistryObject<StaticPowerCutoutBlock> MachineBlockStatic = registerBlock("machine_block_static",
			() -> new StaticPowerCutoutBlock(Properties.copy(Blocks.IRON_DOOR)));
	public static final RegistryObject<StaticPowerCutoutBlock> MachineBlockEnergized = registerBlock("machine_block_energized",
			() -> new StaticPowerCutoutBlock(Properties.copy(Blocks.IRON_DOOR)));
	public static final RegistryObject<StaticPowerCutoutBlock> MachineBlockLumum = registerBlock("machine_block_lumum",
			() -> new StaticPowerCutoutBlock(Properties.copy(Blocks.IRON_DOOR)));

	public static final RegistryObject<BlockTank> IronTank = registerBlock("tank_iron", () -> new BlockTank(StaticPowerTiers.IRON));
	public static final RegistryObject<BlockTank> BasicTank = registerBlock("tank_basic", () -> new BlockTank(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockTank> AdvancedTank = registerBlock("tank_advanced", () -> new BlockTank(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockTank> StaticTank = registerBlock("tank_static", () -> new BlockTank(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockTank> EnergizedTank = registerBlock("tank_energized", () -> new BlockTank(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockTank> LumumTank = registerBlock("tank_lumum", () -> new BlockTank(StaticPowerTiers.LUMUM));
	public static final RegistryObject<BlockTank> CreativeTank = registerBlock("tank_creative", () -> new BlockTank(StaticPowerTiers.CREATIVE));

	public static final RegistryObject<PumpTube> PumpTube = registerBlock("pump_tube", () -> new PumpTube());
	public static final RegistryObject<BlockPump> BasicPump = registerBlock("pump_basic",
			() -> new BlockPump(StaticPowerTiers.BASIC, StaticPowerAdditionalModels.PUMP_CONNECTOR_BASIC));
	public static final RegistryObject<BlockPump> AdvancedPump = registerBlock("pump_advanced",
			() -> new BlockPump(StaticPowerTiers.ADVANCED, StaticPowerAdditionalModels.PUMP_CONNECTOR_ADVANCED));
	public static final RegistryObject<BlockPump> StaticPump = registerBlock("pump_static",
			() -> new BlockPump(StaticPowerTiers.STATIC, StaticPowerAdditionalModels.PUMP_CONNECTOR_STATIC));
	public static final RegistryObject<BlockPump> EnergizedPump = registerBlock("pump_energized",
			() -> new BlockPump(StaticPowerTiers.ENERGIZED, StaticPowerAdditionalModels.PUMP_CONNECTOR_ENERGIZED));
	public static final RegistryObject<BlockPump> LumumPump = registerBlock("pump_lumum",
			() -> new BlockPump(StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.PUMP_CONNECTOR_LUMUM));
	public static final RegistryObject<BlockPump> CreativePump = registerBlock("pump_creative",
			() -> new BlockPump(StaticPowerTiers.CREATIVE, StaticPowerAdditionalModels.PUMP_CONNECTOR_CREATIVE));

	public static final RegistryObject<BlockVacuumChest> VacuumChest = registerBlock("chest_vacuum", () -> new BlockVacuumChest());
	public static final RegistryObject<BlockStaticChest> BasicChest = registerBlock("chest_basic", () -> new BlockStaticChest(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockStaticChest> AdvancedChest = registerBlock("chest_advanced", () -> new BlockStaticChest(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockStaticChest> StaticChest = registerBlock("chest_static", () -> new BlockStaticChest(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockStaticChest> EnergizedChest = registerBlock("chest_energized", () -> new BlockStaticChest(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockStaticChest> LumumChest = registerBlock("chest_lumum", () -> new BlockStaticChest(StaticPowerTiers.LUMUM));

	public static final RegistryObject<BlockChargingStation> ChargingStation = registerBlock("machine_charging_station", () -> new BlockChargingStation());
	public static final RegistryObject<BlockPoweredFurnace> PoweredFurnace = registerBlock("machine_powered_furnace", () -> new BlockPoweredFurnace());
	public static final RegistryObject<BlockPoweredGrinder> PoweredGrinder = registerBlock("machine_powered_grinder", () -> new BlockPoweredGrinder());
	public static final RegistryObject<BlockLumberMill> LumberMill = registerBlock("machine_lumber_mill", () -> new BlockLumberMill());
	public static final RegistryObject<BlockBasicFarmer> BasicFarmer = registerBlock("machine_basic_farmer", () -> new BlockBasicFarmer());
	public static final RegistryObject<BlockTreeFarmer> TreeFarmer = registerBlock("machine_tree_farmer", () -> new BlockTreeFarmer());
	public static final RegistryObject<BlockFermenter> Fermenter = registerBlock("machine_fermenter", () -> new BlockFermenter());
	public static final RegistryObject<BlockFormer> Former = registerBlock("machine_former", () -> new BlockFormer());
	public static final RegistryObject<BlockSolidGenerator> SolidGenerator = registerBlock("machine_generator_solid", () -> new BlockSolidGenerator());
	public static final RegistryObject<BlockFluidGenerator> FluidGenerator = registerBlock("machine_generator_fluid", () -> new BlockFluidGenerator());
	public static final RegistryObject<BlockCrucible> Crucible = registerBlock("machine_crucible", () -> new BlockCrucible());
	public static final RegistryObject<BlockSqueezer> Squeezer = registerBlock("machine_squeezer", () -> new BlockSqueezer());
	public static final RegistryObject<BlockBottler> Bottler = registerBlock("machine_bottler", () -> new BlockBottler());

	public static final RegistryObject<BlockSolderingTable> SolderingTable = registerBlock("soldering_table", () -> new BlockSolderingTable());

	public static final RegistryObject<BlockAutoSolderingTable> AutoSolderingTable = registerBlock("machine_industrial_soldering_table", () -> new BlockAutoSolderingTable());
	public static final RegistryObject<BlockAutoCraftingTable> AutoCraftingTable = registerBlock("machine_industrial_crafting_table", () -> new BlockAutoCraftingTable());
	public static final RegistryObject<BlockFluidInfuser> FluidInfuser = registerBlock("machine_fluid_infuser", () -> new BlockFluidInfuser());
	public static final RegistryObject<BlockCentrifuge> Centrifuge = registerBlock("machine_centrifuge", () -> new BlockCentrifuge());
	public static final RegistryObject<BlockFusionFurnace> FusionFurnace = registerBlock("machine_fusion_furnace", () -> new BlockFusionFurnace());
	public static final RegistryObject<BlockMiner> Miner = registerBlock("machine_miner", () -> new BlockMiner());
	public static final RegistryObject<BlockElectricMiner> ElectricMiner = registerBlock("machine_electric_miner", () -> new BlockElectricMiner());
	public static final RegistryObject<BlockEvaporator> Evaporator = registerBlock("machine_evaporator", () -> new BlockEvaporator());
	public static final RegistryObject<BlockCondenser> Condenser = registerBlock("machine_condenser", () -> new BlockCondenser());
	public static final RegistryObject<BlockVulcanizer> Vulcanizer = registerBlock("machine_vulcanizer", () -> new BlockVulcanizer());
	public static final RegistryObject<BlockAutoSmith> AutoSmith = registerBlock("machine_auto_smith", () -> new BlockAutoSmith());
	public static final RegistryObject<BlockLathe> Lathe = registerBlock("machine_lathe", () -> new BlockLathe());
	public static final RegistryObject<BlockMixer> Mixer = registerBlock("machine_mixer", () -> new BlockMixer());
	public static final RegistryObject<BlockCaster> Caster = registerBlock("machine_caster", () -> new BlockCaster());
	public static final RegistryObject<BlockTumbler> Tumbler = registerBlock("machine_tumbler", () -> new BlockTumbler());
	public static final RegistryObject<BlockTurbine> Turbine = registerBlock("machine_turbine", () -> new BlockTurbine());
	public static final RegistryObject<BlockPackager> Packager = registerBlock("machine_packager", () -> new BlockPackager());

	public static final RegistryObject<BlockExperienceHopper> ExperienceHopper = registerBlock("experience_hopper", () -> new BlockExperienceHopper());
	public static final RegistryObject<BlockCauldron> RustyCauldron = registerBlock("rusty_cauldron", () -> new BlockCauldron(false));
	public static final RegistryObject<BlockCauldron> CleanCauldron = registerBlock("clean_cauldron", () -> new BlockCauldron(true));
	public static final RegistryObject<BlockDirectDropper> DirectDropper = registerBlock("direct_dropper", () -> new BlockDirectDropper());
	public static final RegistryObject<BlockAutomaticPlacer> AutomaticPlacer = registerBlock("automatic_placer", () -> new BlockAutomaticPlacer());

	public static final RegistryObject<BlockEnchanter> Enchanter = registerBlock("machine_enchanter", () -> new BlockEnchanter());

	public static final RegistryObject<BlockRandomItemGenerator> RandomItemGenerator = registerBlock("random_item_generator", () -> new BlockRandomItemGenerator(null));
	public static final RegistryObject<BlockRandomItemGenerator> RandomOreGenerator = registerBlock("random_ore_generator", () -> new BlockRandomItemGenerator("ore"));

	public static final RegistryObject<BlockHydroponicFarmer> HydroponicFarmer = registerBlock("machine_hydroponic_farmer", () -> new BlockHydroponicFarmer());
	public static final RegistryObject<BlockHydroponicPod> HydroponicPod = registerBlock("machine_hydroponic_pod", () -> new BlockHydroponicPod());

	public static final RegistryObject<BlockAlloyFurnace> AlloyFurnace = registerBlock("alloy_furnace", () -> new BlockAlloyFurnace());

	public static final RegistryObject<BlockRefineryController> RefineryController = registerBlock("machine_refinery_controller", () -> new BlockRefineryController());
	public static final RegistryObject<BlockRefineryPowerTap> RefineryPowerTap = registerBlock("machine_refinery_power_tap", () -> new BlockRefineryPowerTap());
	public static final RegistryObject<BlockRefineryFluidInput> RefineryFluidInput = registerBlock("machine_refinery_fluid_input", () -> new BlockRefineryFluidInput());
	public static final RegistryObject<BlockRefineryFluidOutput> RefineryFluidOutput = registerBlock("machine_refinery_fluid_output", () -> new BlockRefineryFluidOutput());
	public static final RegistryObject<BlockRefineryItemInput> RefineryItemInput = registerBlock("machine_refinery_item_input", () -> new BlockRefineryItemInput());
	public static final RegistryObject<BlockRefineryHeatVent> RefineryHeatVent = registerBlock("machine_refinery_heat_vent", () -> new BlockRefineryHeatVent());
	public static final RegistryObject<BlockRefineryTower> RefineryTower = registerBlock("machine_refinery_tower", () -> new BlockRefineryTower());
	public static final RegistryObject<BlockRefineryBoiler> RefineryBoiler = registerBlock("machine_refinery_boiler", () -> new BlockRefineryBoiler());

	public static final RegistryObject<BlockLaboratory> Laboratory = registerBlock("laboratory", () -> new BlockLaboratory());
	public static final RegistryObject<BlockResearchCheater> ResearchCheater = registerBlock("research_cheater", () -> new BlockResearchCheater());

	public static final RegistryObject<BlockStraightConveyor> StraightConveyorBasic = registerBlock("conveyor_straight_basic",
			() -> new BlockStraightConveyor(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockStraightConveyor> StraightConveyorAdvanced = registerBlock("conveyor_straight_advanced",
			() -> new BlockStraightConveyor(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockStraightConveyor> StraightConveyorStatic = registerBlock("conveyor_straight_static",
			() -> new BlockStraightConveyor(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockStraightConveyor> StraightConveyorEnergized = registerBlock("conveyor_straight_energized",
			() -> new BlockStraightConveyor(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockStraightConveyor> StraightConveyorLumum = registerBlock("conveyor_straight_lumum",
			() -> new BlockStraightConveyor(StaticPowerTiers.LUMUM));

	public static final RegistryObject<BlockRampUpConveyor> RampUpConveyorBasic = registerBlock("conveyor_ramp_up_basic", () -> new BlockRampUpConveyor(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockRampUpConveyor> RampUpConveyorAdvanced = registerBlock("conveyor_ramp_up_advanced",
			() -> new BlockRampUpConveyor(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockRampUpConveyor> RampUpConveyorStatic = registerBlock("conveyor_ramp_up_static", () -> new BlockRampUpConveyor(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockRampUpConveyor> RampUpConveyorEnergized = registerBlock("conveyor_ramp_up_energized",
			() -> new BlockRampUpConveyor(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockRampUpConveyor> RampUpConveyorLumum = registerBlock("conveyor_ramp_up_lumum", () -> new BlockRampUpConveyor(StaticPowerTiers.LUMUM));

	public static final RegistryObject<BlockRampDownConveyor> RampDownConveyorBasic = registerBlock("conveyor_ramp_down_basic",
			() -> new BlockRampDownConveyor(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockRampDownConveyor> RampDownConveyorAdvanced = registerBlock("conveyor_ramp_down_advanced",
			() -> new BlockRampDownConveyor(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockRampDownConveyor> RampDownConveyorStatic = registerBlock("conveyor_ramp_down_static",
			() -> new BlockRampDownConveyor(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockRampDownConveyor> RampDownConveyorEnergized = registerBlock("conveyor_ramp_down_energized",
			() -> new BlockRampDownConveyor(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockRampDownConveyor> RampDownConveyorLumum = registerBlock("conveyor_ramp_down_lumum",
			() -> new BlockRampDownConveyor(StaticPowerTiers.LUMUM));

	public static final RegistryObject<BlockConveyorSupplier> ConveyorSupplierBasic = registerBlock("conveyor_supplier_basic",
			() -> new BlockConveyorSupplier(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockConveyorSupplier> ConveyorSupplierAdvanced = registerBlock("conveyor_supplier_advanced",
			() -> new BlockConveyorSupplier(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockConveyorSupplier> ConveyorSupplierStatic = registerBlock("conveyor_supplier_static",
			() -> new BlockConveyorSupplier(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockConveyorSupplier> ConveyorSupplierEnergized = registerBlock("conveyor_supplier_energized",
			() -> new BlockConveyorSupplier(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockConveyorSupplier> ConveyorSupplierLumum = registerBlock("conveyor_supplier_lumum",
			() -> new BlockConveyorSupplier(StaticPowerTiers.LUMUM));

	public static final RegistryObject<BlockConveyorExtractor> ConveyorExtractorBasic = registerBlock("conveyor_extractor_basic",
			() -> new BlockConveyorExtractor(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockConveyorExtractor> ConveyorExtractorAdvanced = registerBlock("conveyor_extractor_advanced",
			() -> new BlockConveyorExtractor(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockConveyorExtractor> ConveyorExtractorStatic = registerBlock("conveyor_extractor_static",
			() -> new BlockConveyorExtractor(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockConveyorExtractor> ConveyorExtractorEnergized = registerBlock("conveyor_extractor_energized",
			() -> new BlockConveyorExtractor(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockConveyorExtractor> ConveyorExtractorLumum = registerBlock("conveyor_extractor_lumum",
			() -> new BlockConveyorExtractor(StaticPowerTiers.LUMUM));

	public static final RegistryObject<BlockConveyorHopper> ConveyorHopperBasic = registerBlock("conveyor_hopper_basic",
			() -> new BlockConveyorHopper(StaticPowerTiers.BASIC, false));
	public static final RegistryObject<BlockConveyorHopper> ConveyorHopperAdvanced = registerBlock("conveyor_hopper_advanced",
			() -> new BlockConveyorHopper(StaticPowerTiers.ADVANCED, false));
	public static final RegistryObject<BlockConveyorHopper> ConveyorHopperStatic = registerBlock("conveyor_hopper_static",
			() -> new BlockConveyorHopper(StaticPowerTiers.STATIC, false));
	public static final RegistryObject<BlockConveyorHopper> ConveyorHopperEnergized = registerBlock("conveyor_hopper_energized",
			() -> new BlockConveyorHopper(StaticPowerTiers.ENERGIZED, false));
	public static final RegistryObject<BlockConveyorHopper> ConveyorHopperLumum = registerBlock("conveyor_hopper_lumum",
			() -> new BlockConveyorHopper(StaticPowerTiers.LUMUM, false));

	public static final RegistryObject<BlockConveyorHopper> ConveyorFilteredHopperBasic = registerBlock("conveyor_hopper_filtered_basic",
			() -> new BlockConveyorHopper(StaticPowerTiers.BASIC, true));
	public static final RegistryObject<BlockConveyorHopper> ConveyorFilteredHopperAdvanced = registerBlock("conveyor_hopper_filtered_advanced",
			() -> new BlockConveyorHopper(StaticPowerTiers.ADVANCED, true));
	public static final RegistryObject<BlockConveyorHopper> ConveyorFilteredHopperStatic = registerBlock("conveyor_hopper_filtered_static",
			() -> new BlockConveyorHopper(StaticPowerTiers.STATIC, true));
	public static final RegistryObject<BlockConveyorHopper> ConveyorFilteredHopperEnergized = registerBlock("conveyor_hopper_filtered_energized",
			() -> new BlockConveyorHopper(StaticPowerTiers.ENERGIZED, true));
	public static final RegistryObject<BlockConveyorHopper> ConveyorFilteredHopperLumum = registerBlock("conveyor_hopper_filtered_lumum",
			() -> new BlockConveyorHopper(StaticPowerTiers.LUMUM, true));

	public static final RegistryObject<BlockHeatSink> AluminumHeatSink = registerBlock("heat_sink_aluminum", () -> new BlockHeatSink(StaticPowerTiers.ALUMINUM));
	public static final RegistryObject<BlockHeatSink> CopperHeatSink = registerBlock("heat_sink_copper", () -> new BlockHeatSink(StaticPowerTiers.COPPER));
	public static final RegistryObject<BlockHeatSink> GoldHeatSink = registerBlock("heat_sink_gold", () -> new BlockHeatSink(StaticPowerTiers.GOLD));

	public static final RegistryObject<BlockSolarPanel> SolarPanelBasic = registerBlock("solar_panel_basic", () -> new BlockSolarPanel(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockSolarPanel> SolarPanelAdvanced = registerBlock("solar_panel_advanced", () -> new BlockSolarPanel(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockSolarPanel> SolarPanelStatic = registerBlock("solar_panel_static", () -> new BlockSolarPanel(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockSolarPanel> SolarPanelEnergized = registerBlock("solar_panel_energized", () -> new BlockSolarPanel(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockSolarPanel> SolarPanelLumum = registerBlock("solar_panel_lumum", () -> new BlockSolarPanel(StaticPowerTiers.LUMUM));
	public static final RegistryObject<BlockSolarPanel> SolarPanelCreative = registerBlock("solar_panel_creative", () -> new BlockSolarPanel(StaticPowerTiers.CREATIVE));

	public static final RegistryObject<BlockDigistoreManager> DigistoreManager = registerBlock("digistore_manager", () -> new BlockDigistoreManager());
	public static final RegistryObject<BlockDigistoreIOPort> DigistoreIOPort = registerBlock("digistore_io_port", () -> new BlockDigistoreIOPort());
	public static final RegistryObject<BlockDigistore> Digistore = registerBlock("digistore", () -> new BlockDigistore());
	public static final RegistryObject<BlockDigistoreServerRack> DigistoreServerRack = registerBlock("digistore_server_rack", () -> new BlockDigistoreServerRack());
	public static final RegistryObject<BlockPatternStorage> DigistorePatternStorage = registerBlock("digistore_pattern_storage", () -> new BlockPatternStorage());

	// Cables
	public static final RegistryObject<BlockDigistoreNetworkWire> DigistoreWire = registerBlock("cable_digistore", () -> new BlockDigistoreNetworkWire());

	public static final Map<ResourceLocation, RegistryObject<BlockItemCable>> ItemCables = new HashMap<>();
	public static final Map<ResourceLocation, RegistryObject<BlockPowerCable>> PowerCables = new HashMap<>();
	public static final Map<ResourceLocation, RegistryObject<BlockPowerCable>> InsulatedPowerCables = new HashMap<>();
	public static final Map<ResourceLocation, RegistryObject<BlockIndustrialPowerCable>> IndustrialPowerCables = new HashMap<>();
	public static final Map<ResourceLocation, RegistryObject<BlockFluidCable>> FluidCables = new HashMap<>();
	public static final Map<ResourceLocation, RegistryObject<BlockCapillaryFluidCable>> CapillaryFluidCables = new HashMap<>();
	public static final Map<ResourceLocation, RegistryObject<BlockIndustrialFluidCable>> IndustrialFluidCables = new HashMap<>();

	static {
		for (CableTier tier : CableTiers.get()) {
			ItemCables.put(tier.location(), registerBlock("cable_item_" + tier.name(), () -> new BlockItemCable(tier.location())));
			PowerCables.put(tier.location(), registerBlock("cable_power_" + tier.name(), () -> new BlockPowerCable(tier.location(), false)));
			InsulatedPowerCables.put(tier.location(), registerBlock("cable_insulated_power_" + tier.name(), () -> new BlockPowerCable(tier.location(), true)));
			IndustrialPowerCables.put(tier.location(), registerBlock("cable_industrial_power_" + tier.name(), () -> new BlockIndustrialPowerCable(tier.location())));
			FluidCables.put(tier.location(), registerBlock("cable_fluid_" + tier.name(), () -> new BlockFluidCable(tier.location())));
			CapillaryFluidCables.put(tier.location(), registerBlock("cable_capillary_fluid_" + tier.name(), () -> new BlockCapillaryFluidCable(tier.location())));
			IndustrialFluidCables.put(tier.location(), registerBlock("cable_industrial_fluid_" + tier.name(), () -> new BlockIndustrialFluidCable(tier.location())));
		}
	}

	public static final RegistryObject<BlockHeatCable> AluminumHeatCable = registerBlock("cable_heat_aluminum", () -> new BlockHeatCable(StaticPowerTiers.ALUMINUM));
	public static final RegistryObject<BlockHeatCable> CopperHeatCable = registerBlock("cable_heat_copper", () -> new BlockHeatCable(StaticPowerTiers.COPPER));
	public static final RegistryObject<BlockHeatCable> GoldHeatCable = registerBlock("cable_heat_gold", () -> new BlockHeatCable(StaticPowerTiers.GOLD));

	public static final RegistryObject<BlockScaffoldCable> ScaffoldCable = registerBlock("cable_scaffold", () -> new BlockScaffoldCable());

	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableNaked = registerBlock("cable_redstone_basic_naked", () -> new BlockRedstoneCable("naked"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableBlack = registerBlock("cable_redstone_basic_black", () -> new BlockRedstoneCable("black"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableDarkBlue = registerBlock("cable_redstone_basic_dark_blue", () -> new BlockRedstoneCable("blue"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableDarkGreen = registerBlock("cable_redstone_basic_dark_green",
			() -> new BlockRedstoneCable("dark_green"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableDarkAqua = registerBlock("cable_redstone_basic_dark_aqua", () -> new BlockRedstoneCable("dark_aqua"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableDarkRed = registerBlock("cable_redstone_basic_dark_red", () -> new BlockRedstoneCable("dark_red"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableDarkPurple = registerBlock("cable_redstone_basic_dark_purple",
			() -> new BlockRedstoneCable("dark_purple"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableGold = registerBlock("cable_redstone_basic_gold", () -> new BlockRedstoneCable("gold"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableGray = registerBlock("cable_redstone_basic_gray", () -> new BlockRedstoneCable("gray"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableDarkGray = registerBlock("cable_redstone_basic_dark_gray", () -> new BlockRedstoneCable("dark_gray"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableBlue = registerBlock("cable_redstone_basic_blue", () -> new BlockRedstoneCable("blue"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableGreen = registerBlock("cable_redstone_basic_green", () -> new BlockRedstoneCable("green"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableAqua = registerBlock("cable_redstone_basic_aqua", () -> new BlockRedstoneCable("aqua"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableRed = registerBlock("cable_redstone_basic_red", () -> new BlockRedstoneCable("red"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableLightPurple = registerBlock("cable_redstone_basic_light_purple",
			() -> new BlockRedstoneCable("light_purple"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableYellow = registerBlock("cable_redstone_basic_yellow", () -> new BlockRedstoneCable("yellow"));
	public static final RegistryObject<BlockRedstoneCable> BasicRedstoneCableWhite = registerBlock("cable_redstone_basic_white", () -> new BlockRedstoneCable("white"));
	public static final RegistryObject<BlockBundledRedstoneCable> BundledRedstoneCable = registerBlock("cable_bundled_redstone", () -> new BlockBundledRedstoneCable());

	// Batteries
	public static final RegistryObject<BlockBattery> BatteryBasic = registerBlock("battery_block_basic", () -> new BlockBattery(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockBattery> BatteryAdvanced = registerBlock("battery_block_advanced", () -> new BlockBattery(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockBattery> BatteryStatic = registerBlock("battery_block_static", () -> new BlockBattery(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockBattery> BatteryEnergized = registerBlock("battery_block_energized", () -> new BlockBattery(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockBattery> BatteryLumum = registerBlock("battery_block_lumum", () -> new BlockBattery(StaticPowerTiers.LUMUM));
	public static final RegistryObject<BlockBattery> BatteryCreative = registerBlock("battery_block_creative", () -> new BlockBattery(StaticPowerTiers.CREATIVE));

	// Transformers
	public static final RegistryObject<BlockTransformer> TransformerBasic = registerBlock("transformer_basic", () -> new BlockTransformer(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockTransformer> TransformerAdvanced = registerBlock("transformer_advanced", () -> new BlockTransformer(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockTransformer> TransformerStatic = registerBlock("transformer_static", () -> new BlockTransformer(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockTransformer> TransformerEnergized = registerBlock("transformer_energized", () -> new BlockTransformer(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockTransformer> TransformerLumum = registerBlock("transformer_lumum", () -> new BlockTransformer(StaticPowerTiers.LUMUM));

	// Inverters
	public static final RegistryObject<BlockInverter> Inverter = registerBlock("inverter", () -> new BlockInverter(StaticPowerTiers.BASIC));

	// Rectifiers
	public static final RegistryObject<BlockRectifier> Rectifier = registerBlock("rectifier", () -> new BlockRectifier(StaticPowerTiers.BASIC));

	// Circuit Breakers
	public static final RegistryObject<BlockCircuitBreaker> CircuitBreaker2A = registerBlock("circuit_breaker_2", () -> new BlockCircuitBreaker(2));
	public static final RegistryObject<BlockCircuitBreaker> CircuitBreaker5A = registerBlock("circuit_breaker_5", () -> new BlockCircuitBreaker(5));
	public static final RegistryObject<BlockCircuitBreaker> CircuitBreaker10A = registerBlock("circuit_breaker_10", () -> new BlockCircuitBreaker(10));
	public static final RegistryObject<BlockCircuitBreaker> CircuitBreaker20A = registerBlock("circuit_breaker_20", () -> new BlockCircuitBreaker(20));
	public static final RegistryObject<BlockCircuitBreaker> CircuitBreaker50A = registerBlock("circuit_breaker_50", () -> new BlockCircuitBreaker(50));
	public static final RegistryObject<BlockCircuitBreaker> CircuitBreaker100A = registerBlock("circuit_breaker_100", () -> new BlockCircuitBreaker(100));

	// Resistors
	public static final RegistryObject<BlockResistor> Resistor1W = registerBlock("resistor_1", () -> new BlockResistor(1));
	public static final RegistryObject<BlockResistor> Resistor5W = registerBlock("resistor_5", () -> new BlockResistor(5));
	public static final RegistryObject<BlockResistor> Resistor10W = registerBlock("resistor_10", () -> new BlockResistor(10));
	public static final RegistryObject<BlockResistor> Resistor25W = registerBlock("resistor_25", () -> new BlockResistor(25));
	public static final RegistryObject<BlockResistor> Resistor50W = registerBlock("resistor_50", () -> new BlockResistor(50));
	public static final RegistryObject<BlockResistor> Resistor100W = registerBlock("resistor_100", () -> new BlockResistor(100));
	public static final RegistryObject<BlockResistor> Resistor250W = registerBlock("resistor_250", () -> new BlockResistor(250));
	public static final RegistryObject<BlockResistor> Resistor500W = registerBlock("resistor_500", () -> new BlockResistor(500));
	public static final RegistryObject<BlockResistor> Resistor1KW = registerBlock("resistor_1000", () -> new BlockResistor(1000));

	// Wire Terminals
	public static final RegistryObject<BlockWireConnector> WireConnectorLV = registerBlock("wire_terminal_lv", () -> new BlockWireConnector(StaticPowerTiers.BASIC));
	public static final RegistryObject<BlockWireConnector> WireConnectorMV = registerBlock("wire_terminal_mv", () -> new BlockWireConnector(StaticPowerTiers.ADVANCED));
	public static final RegistryObject<BlockWireConnector> WireConnectorHV = registerBlock("wire_terminal_hv", () -> new BlockWireConnector(StaticPowerTiers.STATIC));
	public static final RegistryObject<BlockWireConnector> WireConnectorEV = registerBlock("wire_terminal_ev", () -> new BlockWireConnector(StaticPowerTiers.ENERGIZED));
	public static final RegistryObject<BlockWireConnector> WireConnectorBV = registerBlock("wire_terminal_bv", () -> new BlockWireConnector(StaticPowerTiers.LUMUM));
	public static final RegistryObject<BlockDigistoreWireConnector> WireConnectorDigistore = registerBlock("wire_terminal_digistore", () -> new BlockDigistoreWireConnector());

	// Lights
	public static final RegistryObject<BlockLightSocket> LightSocket = registerBlock("light_socket", () -> new BlockLightSocket());

	// Monitors
	public static final RegistryObject<BlockPowerMonitor> PowerMonitor = registerBlock("power_monitor", () -> new BlockPowerMonitor());

	// Rubber Tree
	public static final RegistryObject<StaticPowerTreeLog> RubberTreeStrippedWood = registerBlock("rubber_tree_stripped_wood",
			() -> new StaticPowerTreeLog(Block.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
	public static final RegistryObject<StaticPowerTreeLog> RubberTreeWood = registerBlock("rubber_tree_wood",
			() -> new StaticPowerTreeLog(() -> RubberTreeStrippedWood.get(), Block.Properties.copy(Blocks.OAK_LOG), () -> StaticPowerConfig.SERVER.minRubberWoodBarkPerStrip.get(),
					() -> StaticPowerConfig.SERVER.maxRubberWoodBarkPerStrip.get(), () -> ModItems.RubberWoodBark.get()));

	public static final RegistryObject<StaticPowerTreeLog> RubberTreeStrippedLog = registerBlock("rubber_tree_stripped_log",
			() -> new StaticPowerTreeLog(Block.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
	public static final RegistryObject<StaticPowerTreeLog> RubberTreeLog = registerBlock("rubber_tree_log",
			() -> new StaticPowerTreeLog(() -> RubberTreeStrippedLog.get(), Block.Properties.copy(Blocks.OAK_LOG), () -> StaticPowerConfig.SERVER.minRubberWoodBarkPerStrip.get(),
					() -> StaticPowerConfig.SERVER.maxRubberWoodBarkPerStrip.get(), () -> ModItems.RubberWoodBark.get()));

	public static final RegistryObject<StaticPowerBlock> RubberTreePlanks = registerBlock("rubber_tree_planks",
			() -> new StaticPowerBlock(Block.Properties.copy(Blocks.OAK_PLANKS)));
	public static final RegistryObject<StaticPowerTreeLeaves> RubberTreeLeaves = registerBlock("rubber_tree_leaves",
			() -> new StaticPowerTreeLeaves(Block.Properties.copy(Blocks.OAK_LEAVES)));
	public static final RegistryObject<StaticPowerSapling> RubberTreeSapling = registerBlock("rubber_tree_sapling",
			() -> new StaticPowerSapling(() -> ModFeatures.RUBBER_TREE.getTreeGrower(), Block.Properties.copy(Blocks.OAK_SAPLING)));

	public static void init(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
		ModItems.ITEMS.register(name, () -> {
			Block lazyBlock = block.get();
			if (lazyBlock instanceof StaticPowerBlock) {
				return ((StaticPowerBlock) lazyBlock).getItemBlock();
			}
			return new StaticPowerItemBlock(lazyBlock);
		});
	}
}
