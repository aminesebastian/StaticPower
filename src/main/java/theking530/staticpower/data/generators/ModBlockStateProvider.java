package theking530.staticpower.data.generators;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.power.circuit_breaker.BlockCircuitBreaker;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {

	public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, StaticPower.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlockWithCustomTexture(ModBlocks.StaticLamp.get(), "decorative/lamp_static");
		simpleBlockWithCustomTexture(ModBlocks.EnergizedLamp.get(), "decorative/lamp_energized");
		simpleBlockWithCustomTexture(ModBlocks.LumumLamp.get(), "decorative/lamp_lumum");
		simpleBlockWithCustomTexture(ModBlocks.ObsidianGlass.get(), "decorative/glass_obsidian");
		simpleBlockWithCustomTexture(ModBlocks.SmeepWool.get(), "decorative/smeep_wool");

		rotatedPillarBlockWithCustomTexture(ModBlocks.StaticLog.get(), "decorative/log_static");
		rotatedPillarBlockWithCustomTexture(ModBlocks.EnergizedLog.get(), "decorative/log_energized");
		rotatedPillarBlockWithCustomTexture(ModBlocks.LumumLog.get(), "decorative/log_lumum");

		farmlandWithCustomTexture(ModBlocks.StaticFarmland.get(), "decorative/farmland_static");
		farmlandWithCustomTexture(ModBlocks.EnergizedFarmland.get(), "decorative/farmland_energized");
		farmlandWithCustomTexture(ModBlocks.LumumFarmland.get(), "decorative/farmland_lumum");

		simpleColumnWithCustomTexture(ModBlocks.StaticGrass.get(), "decorative/grass_static");
		simpleColumnWithCustomTexture(ModBlocks.EnergizedGrass.get(), "decorative/grass_energized");

		simpleBlockWithCustomTexture(ModBlocks.StaticPlanks.get(), "decorative/planks_static");
		simpleBlockWithCustomTexture(ModBlocks.EnergizedPlanks.get(), "decorative/planks_energized");
		simpleBlockWithCustomTexture(ModBlocks.LumumPlanks.get(), "decorative/planks_lumum");

		simpleBlockWithCustomTexture(ModBlocks.OreTin.get(), "ore/ore_tin");
		simpleBlockWithCustomTexture(ModBlocks.OreZinc.get(), "ore/ore_zinc");
		simpleBlockWithCustomTexture(ModBlocks.OreSilver.get(), "ore/ore_silver");
		simpleBlockWithCustomTexture(ModBlocks.OreLead.get(), "ore/ore_lead");
		simpleBlockWithCustomTexture(ModBlocks.OreTungsten.get(), "ore/ore_tungsten");
		simpleBlockWithCustomTexture(ModBlocks.OreMagnesium.get(), "ore/ore_magnesium");
		simpleBlockWithCustomTexture(ModBlocks.OrePlatinum.get(), "ore/ore_platinum");
		simpleBlockWithCustomTexture(ModBlocks.OreAluminum.get(), "ore/ore_aluminum");
		simpleBlockWithCustomTexture(ModBlocks.OreRuby.get(), "ore/ore_ruby");
		simpleBlockWithCustomTexture(ModBlocks.OreSapphire.get(), "ore/ore_sapphire");
		simpleBlockWithCustomTexture(ModBlocks.OreRustyIron.get(), "ore/ore_rusty_iron");
		simpleBlockWithCustomTexture(ModBlocks.OreUranium.get(), "ore/ore_uranium");

		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateTin.get(), "ore/ore_deepslate_tin");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateZinc.get(), "ore/ore_deepslate_zinc");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateSilver.get(), "ore/ore_deepslate_silver");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateLead.get(), "ore/ore_deepslate_lead");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateTungsten.get(), "ore/ore_deepslate_tungsten");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateMagnesium.get(), "ore/ore_deepslate_magnesium");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslatePlatinum.get(), "ore/ore_deepslate_platinum");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateAluminum.get(), "ore/ore_deepslate_aluminum");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateRuby.get(), "ore/ore_deepslate_ruby");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateSapphire.get(), "ore/ore_deepslate_sapphire");
		simpleBlockWithCustomTexture(ModBlocks.OreDeepslateUranium.get(), "ore/ore_deepslate_uranium");

		simpleBlockWithCustomTexture(ModBlocks.OreNetherSilver.get(), "ore/ore_nether_silver");
		simpleBlockWithCustomTexture(ModBlocks.OreNetherPlatinum.get(), "ore/ore_nether_platinum");
		simpleBlockWithCustomTexture(ModBlocks.OreNetherTungsten.get(), "ore/ore_nether_tungsten");

		simpleBlockWithCustomTexture(ModBlocks.BlockRawTin.get(), "storageblocks/block_raw_tin");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawZinc.get(), "storageblocks/block_raw_zinc");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawAluminum.get(), "storageblocks/block_raw_aluminum");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawMagnesium.get(), "storageblocks/block_raw_magnesium");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawSilver.get(), "storageblocks/block_raw_silver");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawLead.get(), "storageblocks/block_raw_lead");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawPlatinum.get(), "storageblocks/block_raw_platinum");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawTungsten.get(), "storageblocks/block_raw_tungsten");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawStaticMetal.get(), "storageblocks/block_raw_static_metal");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawEnergizedMetal.get(), "storageblocks/block_raw_energized_metal");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawLumumMetal.get(), "storageblocks/block_raw_lumum_metal");
		simpleBlockWithCustomTexture(ModBlocks.BlockRawUranium.get(), "storageblocks/block_raw_uranium");

		simpleBlockWithCustomTexture(ModBlocks.BlockTin.get(), "storageblocks/block_tin");
		simpleBlockWithCustomTexture(ModBlocks.BlockZinc.get(), "storageblocks/block_zinc");
		simpleBlockWithCustomTexture(ModBlocks.BlockAluminum.get(), "storageblocks/block_aluminum");
		simpleBlockWithCustomTexture(ModBlocks.BlockMagnesium.get(), "storageblocks/block_magnesium");
		simpleBlockWithCustomTexture(ModBlocks.BlockSilver.get(), "storageblocks/block_silver");
		simpleBlockWithCustomTexture(ModBlocks.BlockBrass.get(), "storageblocks/block_brass");
		simpleBlockWithCustomTexture(ModBlocks.BlockBronze.get(), "storageblocks/block_bronze");
		simpleBlockWithCustomTexture(ModBlocks.BlockLead.get(), "storageblocks/block_lead");
		simpleBlockWithCustomTexture(ModBlocks.BlockPlatinum.get(), "storageblocks/block_platinum");
		simpleBlockWithCustomTexture(ModBlocks.BlockRuby.get(), "storageblocks/block_ruby");
		simpleBlockWithCustomTexture(ModBlocks.BlockSapphire.get(), "storageblocks/block_sapphire");
		simpleBlockWithCustomTexture(ModBlocks.BlockTungsten.get(), "storageblocks/block_tungsten");
		simpleBlockWithCustomTexture(ModBlocks.BlockRedstoneAlloy.get(), "storageblocks/block_redstone_alloy");
		simpleBlockWithCustomTexture(ModBlocks.BlockInertInfusion.get(), "storageblocks/block_inert_infusion");
		simpleBlockWithCustomTexture(ModBlocks.BlockStaticMetal.get(), "storageblocks/block_static_metal");
		simpleBlockWithCustomTexture(ModBlocks.BlockEnergizedMetal.get(), "storageblocks/block_energized_metal");
		simpleBlockWithCustomTexture(ModBlocks.BlockLumumMetal.get(), "storageblocks/block_lumum_metal");
		simpleBlockWithCustomTexture(ModBlocks.BlockUranium.get(), "storageblocks/block_uranium");
		simpleBlockWithCustomTexture(ModBlocks.BlockLatex.get(), "storageblocks/block_latex");
		simpleBlockWithCustomTexture(ModBlocks.BlockRubber.get(), "storageblocks/block_rubber");

		simpleBlockWithCustomTexture(ModBlocks.BlockCutTin.get(), "storageblocks/block_cut_tin");
		simpleBlockWithCustomTexture(ModBlocks.BlockCutZinc.get(), "storageblocks/block_cut_zinc");
		simpleBlockWithCustomTexture(ModBlocks.BlockCutAluminum.get(), "storageblocks/block_cut_aluminum");
		simpleBlockWithCustomTexture(ModBlocks.BlockCutMagnesium.get(), "storageblocks/block_cut_magnesium");
		simpleBlockWithCustomTexture(ModBlocks.BlockCutSilver.get(), "storageblocks/block_cut_silver");
		simpleBlockWithCustomTexture(ModBlocks.BlockCutBrass.get(), "storageblocks/block_cut_brass");
		simpleBlockWithCustomTexture(ModBlocks.BlockCutBronze.get(), "storageblocks/block_cut_bronze");

		machineBlock(ModBlocks.MachineBlockAluminum.get(), "aluminum");
		machineBlock(ModBlocks.MachineBlockIndustrial.get(), "industrial");
		machineBlock(ModBlocks.MachineBlockBasic.get(), "basic");
		machineBlock(ModBlocks.MachineBlockAdvanced.get(), "advanced");
		machineBlock(ModBlocks.MachineBlockStatic.get(), "static");
		machineBlock(ModBlocks.MachineBlockEnergized.get(), "energized");
		machineBlock(ModBlocks.MachineBlockLumum.get(), "lumum");

		tankBlock(ModBlocks.IronTank.get(), "iron");
		tankBlock(ModBlocks.BasicTank.get(), "basic");
		tankBlock(ModBlocks.AdvancedTank.get(), "advanced");
		tankBlock(ModBlocks.StaticTank.get(), "static");
		tankBlock(ModBlocks.EnergizedTank.get(), "energized");
		tankBlock(ModBlocks.LumumTank.get(), "lumum");
		tankBlock(ModBlocks.CreativeTank.get(), "creative");

		pumpBlock(ModBlocks.BasicPump.get(), "basic");
		pumpBlock(ModBlocks.AdvancedPump.get(), "advanced");
		pumpBlock(ModBlocks.StaticPump.get(), "static");
		pumpBlock(ModBlocks.EnergizedPump.get(), "energized");
		pumpBlock(ModBlocks.LumumPump.get(), "lumum");
		pumpBlock(ModBlocks.CreativePump.get(), "creative");

		chest(ModBlocks.BasicChest.get(), "basic");
		chest(ModBlocks.AdvancedChest.get(), "advanced");
		chest(ModBlocks.StaticChest.get(), "static");
		chest(ModBlocks.EnergizedChest.get(), "energized");
		chest(ModBlocks.LumumChest.get(), "lumum");

		batteryBlock(ModBlocks.BatteryBasic.get(), "basic");
		batteryBlock(ModBlocks.BatteryAdvanced.get(), "advanced");
		batteryBlock(ModBlocks.BatteryStatic.get(), "static");
		batteryBlock(ModBlocks.BatteryEnergized.get(), "energized");
		batteryBlock(ModBlocks.BatteryLumum.get(), "lumum");
		batteryBlock(ModBlocks.BatteryCreative.get(), "creative");

		machine(ModBlocks.ChargingStation.get(), "energized", "machines/charging_station");
		machine(ModBlocks.PoweredFurnace.get(), "basic", "machines/powered_furnace");
		machine(ModBlocks.PoweredGrinder.get(), "basic", "machines/powered_grinder");
		machine(ModBlocks.LumberMill.get(), "basic", "machines/lumber_mill");
		machine(ModBlocks.BasicFarmer.get(), "advanced", "machines/basic_farmer");
		machine(ModBlocks.TreeFarmer.get(), "static", "machines/tree_farmer");
		machine(ModBlocks.Fermenter.get(), "advanced", "machines/fermenter");
		machine(ModBlocks.Former.get(), "basic", "machines/former");
		machine(ModBlocks.SolidGenerator.get(), "aluminum", "machines/generator_solid");
		machine(ModBlocks.FluidGenerator.get(), "basic", "machines/generator_fluid");
		machine(ModBlocks.Crucible.get(), "energized", "machines/crucible");
		machine(ModBlocks.Squeezer.get(), "basic", "machines/squeezer");
		machine(ModBlocks.Bottler.get(), "basic", "machines/bottler");

		machine(ModBlocks.AutoSolderingTable.get(), "advanced", "machines/industrial_soldering_table");
		machine(ModBlocks.AutoCraftingTable.get(), "advanced", "machines/industrial_crafting_table");
		machine(ModBlocks.FluidInfuser.get(), "basic", "machines/fluid_infuser");
		machine(ModBlocks.Centrifuge.get(), "advanced", "machines/centrifuge");
		machine(ModBlocks.FusionFurnace.get(), "energized", "machines/fusion_furnace");
		machine(ModBlocks.Miner.get(), "advanced", "machines/miner");
		machine(ModBlocks.ElectricMiner.get(), "energized", "machines/electric_miner");
		machine(ModBlocks.Evaporator.get(), "static", "machines/evaporator");
		machine(ModBlocks.Condenser.get(), "static", "machines/condenser");
		machine(ModBlocks.Vulcanizer.get(), "basic", "machines/vulcanizer");
		machine(ModBlocks.AutoSmith.get(), "advanced", "machines/auto_smith");
		machine(ModBlocks.Lathe.get(), "static", "machines/lathe");
		machine(ModBlocks.Mixer.get(), "advanced", "machines/mixer");
		machine(ModBlocks.Caster.get(), "energized", "machines/caster");
		machine(ModBlocks.Tumbler.get(), "energized", "machines/tumbler");
		machine(ModBlocks.Packager.get(), "basic", "machines/packager");
		machine(ModBlocks.Enchanter.get(), "lumum", "machines/enchanter");
		machine(ModBlocks.HydroponicFarmer.get(), "static", "machines/hydroponic_farmer");
		machine(ModBlocks.Laboratory.get(), "aluminum", "machines/laboratory");

		machine(ModBlocks.RefineryController.get(), "industrial", "machines/refinery/controller");
		refineryBlock(ModBlocks.RefineryPowerTap.get(), "power_tap");
		refineryBlock(ModBlocks.RefineryFluidInput.get(), "fluid_input");
		refineryBlock(ModBlocks.RefineryFluidOutput.get(), "fluid_output");
		refineryBlock(ModBlocks.RefineryItemInput.get(), "item_input");
		refineryBlock(ModBlocks.RefineryHeatVent.get(), "heat_vent");
		refineryBlock(ModBlocks.RefineryBoiler.get(), "boiler");

		simpleColumnWithCustomTexture(ModBlocks.AluminumHeatSink.get(), "heat_sink_aluminum");
		simpleColumnWithCustomTexture(ModBlocks.CopperHeatSink.get(), "heat_sink_copper");
		simpleColumnWithCustomTexture(ModBlocks.GoldHeatSink.get(), "heat_sink_gold");

		solarPanel(ModBlocks.SolarPanelBasic.get(), "basic");
		solarPanel(ModBlocks.SolarPanelAdvanced.get(), "advanced");
		solarPanel(ModBlocks.SolarPanelStatic.get(), "static");
		solarPanel(ModBlocks.SolarPanelEnergized.get(), "energized");
		solarPanel(ModBlocks.SolarPanelLumum.get(), "lumum");
		solarPanel(ModBlocks.SolarPanelCreative.get(), "creative");

		resistor(ModBlocks.Resistor1W.get(), "0", "1", "0");
		resistor(ModBlocks.Resistor5W.get(), "0", "5", "0");
		resistor(ModBlocks.Resistor10W.get(), "1", "0", "0");
		resistor(ModBlocks.Resistor25W.get(), "2", "5", "0");
		resistor(ModBlocks.Resistor50W.get(), "5", "0", "0");
		resistor(ModBlocks.Resistor100W.get(), "1", "0", "1");
		resistor(ModBlocks.Resistor250W.get(), "2", "5", "1");
		resistor(ModBlocks.Resistor500W.get(), "5", "0", "1");
		resistor(ModBlocks.Resistor1KW.get(), "1", "0", "3");

		circuitBreaker(ModBlocks.CircuitBreaker2A.get(), "2");
		circuitBreaker(ModBlocks.CircuitBreaker5A.get(), "5");
		circuitBreaker(ModBlocks.CircuitBreaker10A.get(), "10");
		circuitBreaker(ModBlocks.CircuitBreaker20A.get(), "20");
		circuitBreaker(ModBlocks.CircuitBreaker50A.get(), "50");
		circuitBreaker(ModBlocks.CircuitBreaker100A.get(), "100");

		transformer(ModBlocks.TransformerBasic.get(), "basic");
		transformer(ModBlocks.TransformerAdvanced.get(), "advanced");
		transformer(ModBlocks.TransformerStatic.get(), "static");
		transformer(ModBlocks.TransformerEnergized.get(), "energized");
		transformer(ModBlocks.TransformerLumum.get(), "lumum");

		wireTerminal(ModBlocks.WireConnectorLV.get(), "lv_terminal");
		wireTerminal(ModBlocks.WireConnectorMV.get(), "mv_terminal");
		wireTerminal(ModBlocks.WireConnectorHV.get(), "hv_terminal");
		wireTerminal(ModBlocks.WireConnectorEV.get(), "ev_terminal");
		wireTerminal(ModBlocks.WireConnectorBV.get(), "bv_terminal");
		wireTerminal(ModBlocks.WireConnectorDigistore.get(), "digistore_terminal");

		simpleBlockWithCustomTexture(ModBlocks.RubberTreeLeaves.get(), "trees/rubber_tree_leaves");
		rotatedPillarBlockWithCustomTexture(ModBlocks.RubberTreeLog.get(), "trees/rubber_tree_log");
		rotatedPillarBlockWithCustomTexture(ModBlocks.RubberTreeStrippedLog.get(), "trees/rubber_tree_stripped_log");
		simpleBlockWithCustomTexture(ModBlocks.RubberTreeWood.get(), "trees/rubber_tree_log");
		simpleBlockWithCustomTexture(ModBlocks.RubberTreeStrippedWood.get(), "trees/rubber_tree_stripped_log");
		simpleBlockWithCustomTexture(ModBlocks.RubberTreePlanks.get(), "trees/rubber_tree_planks");
		sapling(ModBlocks.RubberTreeSapling.get(), "rubber_tree_sapling");
	}

	public void basicCustomModel(Block block, String modelName) {
		simpleBlock(block, models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/" + modelName)));
	}

	public void rotatedPillarBlockWithCustomTexture(Block block, String texture) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture);
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture + "_top");

		ModelFile vertical = models().cubeColumn(name(block), side, top);
		ModelFile horizontal = models().cubeColumnHorizontal(name(block) + "_horizontal", side, top);

		getVariantBuilder(block).partialState().with(RotatedPillarBlock.AXIS, Axis.Y).modelForState().modelFile(vertical).addModel().partialState()
				.with(RotatedPillarBlock.AXIS, Axis.Z).modelForState().modelFile(horizontal).rotationX(90).addModel().partialState().with(RotatedPillarBlock.AXIS, Axis.X)
				.modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
	}

	public void simpleBlockWithCustomTexture(Block block, String texture) {
		ModelFile model = models().cubeAll(name(block), new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture));
		simpleBlock(block, model);
	}

	public void simpleColumnWithCustomTexture(Block block, String texture) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture);
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture + "_top");
		ModelFile model = models().cubeColumn(name(block), side, top);
		simpleBlock(block, model);
	}

	public void farmlandWithCustomTexture(Block block, String texture) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture + "_side");
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture + "_top");
		ModelFile model = models().withExistingParent(name(block), "block/template_farmland").texture("side", side).texture("top", top);
		simpleBlock(block, model);
	}

	public void machine(Block block, String textureSet, String machine) {
		machine(block, textureSet, machine, true);
	}

	public void machineWithoutOnTexture(Block block, String textureSet, String machine) {
		machine(block, textureSet, machine, false);
	}

	public void machine(Block block, String textureSet, String machine, boolean differentModelForOnState) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_machine_side");
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation front = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_machine_front");
		ResourceLocation ioFrame = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_io_frame");
		ResourceLocation frontOverlayOff = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + machine);
		ResourceLocation frontOverlayOn = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + machine + (differentModelForOnState ? "_on" : ""));

		ModelFile offModel = models().withExistingParent(name(block) + "_off", new ResourceLocation(StaticPower.MOD_ID, "block/base_models/machine_base")).texture("side", side)
				.texture("top", top).texture("front", front).texture("io_frame", ioFrame).texture("front_overlay", frontOverlayOff).texture("particle", side);

		ModelFile onModel = models().withExistingParent(name(block) + "_on", new ResourceLocation(StaticPower.MOD_ID, "block/base_models/machine_base")).texture("side", side)
				.texture("top", top).texture("front", front).texture("io_frame", ioFrame).texture("front_overlay", frontOverlayOn).texture("particle", side);

		getVariantBuilder(block).forAllStates(state -> {
			ModelFile modelToUse = differentModelForOnState ? state.getValue(StaticPowerMachineBlock.IS_ON) ? onModel : offModel : offModel;
			return ConfiguredModel.builder().modelFile(modelToUse).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();
		});
	}

	public void batteryBlock(Block block, String textureSet) {
		ResourceLocation base = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation battery = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/batteries/battery_block_" + textureSet);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/battery_block")).texture("base", base)
				.texture("base", base).texture("battery", battery).texture("particle", battery);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();
		});
	}

	public void tankBlock(Block block, String textureSet) {
		ResourceLocation tank = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/tanks/tank_" + textureSet);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/tank_block")).texture("tank", tank)
				.texture("particle", tank);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();
		});
	}

	public void pumpBlock(Block block, String textureSet) {
		ResourceLocation pump = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/pumps/pump_" + textureSet);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/pump_block")).texture("pump", pump)
				.texture("particle", pump);
		simpleBlock(block, model);
	}

	public void chest(Block block, String textureSet) {
		ResourceLocation chest = new ResourceLocation(StaticPower.MOD_ID, "entity/chest/" + textureSet + "_chest");
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/base_chest")).texture("pump", chest)
				.texture("particle", chest);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();
		});
	}

	public void machineBlock(Block block, String textureSet) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_machine_side");
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation front = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/sides/" + textureSet + "_machine_front");
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/machine_block")).texture("side", side)
				.texture("top", top).texture("front", front).texture("particle", side);
		simpleBlock(block, model);
	}

	public void onOffBlock(Block block, String modelName) {
		getVariantBuilder(block).forAllStates(state -> {
			ModelFile offModel = models().withExistingParent(name(block) + "_off", new ResourceLocation(StaticPower.MOD_ID, "block/base_models/" + modelName));
			ModelFile onModel = models().withExistingParent(name(block) + "_on", new ResourceLocation(StaticPower.MOD_ID, "block/base_models/" + modelName + "_on"));
			return ConfiguredModel.builder().modelFile(state.getValue(StaticPowerMachineBlock.IS_ON) ? onModel : offModel).build();
		});
	}

	public void refineryBlock(Block block, String overlayName) {
		ResourceLocation sideOverlay = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/refinery/" + overlayName);
		ModelFile offModel = models().withExistingParent(name(block) + "_off", new ResourceLocation(StaticPower.MOD_ID, "block/base_models/refinery_block")).texture("side_overlay",
				sideOverlay);
		ModelFile onModel = models().withExistingParent(name(block) + "_on", new ResourceLocation(StaticPower.MOD_ID, "block/base_models/refinery_block")).texture("side_overlay",
				sideOverlay);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(state.getValue(StaticPowerMachineBlock.IS_ON) ? onModel : offModel).build();
		});
	}

	public void solarPanel(Block block, String panel) {
		ResourceLocation panelTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/solar_panel/" + panel);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/solar_panel")).texture("panel", panelTexture)
				.texture("particle", panelTexture);
		simpleBlock(block, model);
	}

	public void resistor(Block block, String band1, String band2, String band3) {
		ResourceLocation band1Texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/resistor/" + band1);
		ResourceLocation band2Texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/resistor/" + band2);
		ResourceLocation band3Texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/resistor/" + band3);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/resistor")).texture("band_1", band1Texture)
				.texture("band_2", band2Texture).texture("band_3", band3Texture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();
		});
	}

	public void circuitBreaker(Block block, String label) {
		ResourceLocation labelTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/circuit_breaker/" + label);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/circuit_breaker")).texture("label", labelTexture);
		ModelFile modelTripped = models().withExistingParent(name(block) + "_tripped", new ResourceLocation(StaticPower.MOD_ID, "block/base_models/circuit_breaker_tripped"))
				.texture("label", labelTexture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(state.getValue(BlockCircuitBreaker.TRIPPED) ? modelTripped : model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();
		});
	}

	public void transformer(Block block, String textureSet) {
		ResourceLocation texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/transformers/transformer_" + textureSet);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/transformer")).texture("texture", texture)
				.texture("particle", texture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model).rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();
		});
	}

	public void wireTerminal(Block block, String texture) {
		ResourceLocation textureLocation = new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/terminals/" + texture);
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/wire_terminal")).texture("texture", textureLocation)
				.texture("particle", textureLocation);

		getVariantBuilder(block).forAllStates(state -> {
			ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
			Direction facing = state.getValue(BlockStateProperties.FACING);
			switch (facing) {
			case DOWN:
				builder.rotationX(180);
				break;
			case EAST:
				builder.rotationX(-90);
				builder.rotationY(-90);
				break;
			case NORTH:
				builder.rotationX(90);
				break;
			case SOUTH:
				builder.rotationX(-90);
				break;
			case WEST:
				builder.rotationX(-90);
				builder.rotationY(90);
				break;
			default:
				break;
			}
			return builder.build();
		});
	}

	public void sapling(Block block, String name) {
		ResourceLocation panelTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/trees/" + name);
		simpleBlock(block, models().cross(name(block), panelTexture));
	}

	private String name(Block block) {
		return key(block).getPath();
	}

	private ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}
}
