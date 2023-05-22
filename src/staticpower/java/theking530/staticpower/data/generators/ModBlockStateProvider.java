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
import theking530.staticcore.fluid.StaticPowerFluidBundle;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.power.circuit_breaker.BlockCircuitBreaker;
import theking530.staticpower.blocks.StaticPowerBlockProperties;
import theking530.staticpower.blocks.StaticPowerBlockProperties.TowerPiece;
import theking530.staticpower.blocks.crops.BaseSimplePlant;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.data.Tiers;
import theking530.staticpower.data.Tiers.RedstoneCableTier;
import theking530.staticpower.data.Tiers.ResistorTier;
import theking530.staticpower.data.Tiers.TierPair;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModMaterials;

public class ModBlockStateProvider extends BlockStateProvider {
	public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, StaticPower.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		// Dummy Blocks
		simpleBlockWithCustomTexture(ModBlocks.RandomItemGenerator.get(), "placeholder_texture");
		simpleBlockWithCustomTexture(ModBlocks.RandomOreGenerator.get(), "placeholder_texture");
		simpleBlockWithCustomTexture(ModBlocks.ResearchCheater.get(), "placeholder_texture");

		makeCrop(ModBlocks.StaticCrop.get(), "crops/static");
		makeCrop(ModBlocks.EnergizedCrop.get(), "crops/energized");
		makeCrop(ModBlocks.LumumCrop.get(), "crops/lumum");

		cauldron(ModBlocks.CleanCauldron.get(), "clean_cauldron");
		cauldron(ModBlocks.RustyCauldron.get(), "rusty_cauldron");

		simpleBlockWithCustomTexture(ModBlocks.StaticLamp.get(), "decorative/lamp_static");
		simpleBlockWithCustomTexture(ModBlocks.EnergizedLamp.get(), "decorative/lamp_energized");
		simpleBlockWithCustomTexture(ModBlocks.LumumLamp.get(), "decorative/lamp_lumum");
		simpleBlockWithCustomTexture(ModBlocks.ObsidianGlass.get(), "decorative/glass_obsidian");
		simpleBlockWithCustomTexture(ModBlocks.SmeepWool.get(), "decorative/smeep_wool");

		rotatedPillarBlockWithCustomTexture(ModBlocks.StaticLog.get(), "decorative/log_static");
		rotatedPillarBlockWithCustomTexture(ModBlocks.EnergizedLog.get(), "decorative/log_energized");
		rotatedPillarBlockWithCustomTexture(ModBlocks.LumumLog.get(), "decorative/log_lumum");
		rotatedPillarBlockWithCustomTexture(ModBlocks.BurntLog.get(), "decorative/log_burnt");

		farmlandWithCustomTexture(ModBlocks.StaticFarmland.get(), "decorative/farmland_static");
		farmlandWithCustomTexture(ModBlocks.EnergizedFarmland.get(), "decorative/farmland_energized");
		farmlandWithCustomTexture(ModBlocks.LumumFarmland.get(), "decorative/farmland_lumum");

		simpleColumnWithCustomTexture(ModBlocks.StaticGrass.get(), "decorative/grass_static");
		simpleColumnWithCustomTexture(ModBlocks.EnergizedGrass.get(), "decorative/grass_energized");

		simpleBlockWithCustomTexture(ModBlocks.StaticPlanks.get(), "decorative/planks_static");
		simpleBlockWithCustomTexture(ModBlocks.EnergizedPlanks.get(), "decorative/planks_energized");
		simpleBlockWithCustomTexture(ModBlocks.LumumPlanks.get(), "decorative/planks_lumum");

		simpleBlockWithCustomTexture(ModBlocks.OreRustyIron.get(), "ore/ore_rusty_iron");
		simpleBlockWithCustomTexture(ModBlocks.BlockLatex.get(), "storageblocks/block_latex");
		simpleBlockWithCustomTexture(ModBlocks.BlockRubber.get(), "storageblocks/block_rubber");

		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.hasGeneratedMaterial(MaterialTypes.OVERWORLD_ORE)) {
				simpleBlockWithCustomTexture(bundle.get(MaterialTypes.OVERWORLD_ORE).get(),
						"ore/ore_" + bundle.getName());
			}
			if (bundle.hasGeneratedMaterial(MaterialTypes.NETHER_ORE)) {
				simpleBlockWithCustomTexture(bundle.get(MaterialTypes.NETHER_ORE).get(),
						"ore/ore_nether_" + bundle.getName());
			}
			if (bundle.hasGeneratedMaterial(MaterialTypes.DEEPSLATE_ORE)) {
				simpleBlockWithCustomTexture(bundle.get(MaterialTypes.DEEPSLATE_ORE).get(),
						"ore/ore_deepslate_" + bundle.getName());
			}
			if (bundle.hasGeneratedMaterial(MaterialTypes.RAW_STOARGE_BLOCK)) {
				simpleBlockWithCustomTexture(bundle.get(MaterialTypes.RAW_STOARGE_BLOCK).get(),
						"storageblocks/block_raw_" + bundle.getName());
			}
			if (bundle.hasGeneratedMaterial(MaterialTypes.STORAGE_BLOCK)) {
				simpleBlockWithCustomTexture(bundle.get(MaterialTypes.STORAGE_BLOCK).get(),
						"storageblocks/block_" + bundle.getName());
			}
			if (bundle.hasGeneratedMaterial(MaterialTypes.CUT_STORAGE_BLOCK)) {
				simpleBlockWithCustomTexture(bundle.get(MaterialTypes.CUT_STORAGE_BLOCK).get(),
						"storageblocks/block_cut_" + bundle.getName());
			}
		}

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

		orientableWithCustomModel(ModBlocks.VacuumChest.get(), "chest_vacuum");
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
		machine(ModBlocks.Carpenter.get(), "static", "machines/carpenter");
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

		solarPanel(ModBlocks.SolarPanelBasic.get(), "basic");
		solarPanel(ModBlocks.SolarPanelAdvanced.get(), "advanced");
		solarPanel(ModBlocks.SolarPanelStatic.get(), "static");
		solarPanel(ModBlocks.SolarPanelEnergized.get(), "energized");
		solarPanel(ModBlocks.SolarPanelLumum.get(), "lumum");
		solarPanel(ModBlocks.SolarPanelCreative.get(), "creative");

		for (ResistorTier tier : Tiers.getResistorTiers()) {
			resistor(ModBlocks.Resistors.get(tier.value()).get(), tier.firstStripe(), tier.secondStripe(),
					tier.thirdStripe());
		}
		for (int value : Tiers.getCircuitBrakerTiers()) {
			circuitBreaker(ModBlocks.CircuitBreakers.get(value).get(), String.valueOf(value));
		}

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

		basicCustomModelOnOff(ModBlocks.AlloyFurnace.get(), "alloy_furnace");
		basicCustomModelOnOff(ModBlocks.DirectDropper.get(), "direct_dropper");
		basicCustomModelOnOff(ModBlocks.AutomaticPlacer.get(), "automatic_placer");
		pumpTube(ModBlocks.PumpTube.get());

		simpleBlockWithCustomTexture(ModBlocks.RubberTreeLeaves.get(), "trees/rubber_tree_leaves");
		rotatedPillarBlockWithCustomTexture(ModBlocks.RubberTreeLog.get(), "trees/rubber_tree_log");
		rotatedPillarBlockWithCustomTexture(ModBlocks.RubberTreeStrippedLog.get(), "trees/rubber_tree_stripped_log");
		simpleBlockWithCustomTexture(ModBlocks.RubberTreeWood.get(), "trees/rubber_tree_log");
		simpleBlockWithCustomTexture(ModBlocks.RubberTreeStrippedWood.get(), "trees/rubber_tree_stripped_log");
		simpleBlockWithCustomTexture(ModBlocks.RubberTreePlanks.get(), "trees/rubber_tree_planks");
		sapling(ModBlocks.RubberTreeSapling.get(), "rubber_tree_sapling");

		digistore(ModBlocks.DigistoreManager.get(), "digistore/manager_front", "digistore/manager_side", true, true);
		digistore(ModBlocks.DigistoreIOPort.get(), "digistore/io_port", "digistore/io_port", true, true);
		digistoreBlock(ModBlocks.Digistore.get(), "digistore/digistore_front");
		orientableWithCustomModel(ModBlocks.DigistoreServerRack.get(), "digistore_server_rack");
		digistore(ModBlocks.DigistorePatternStorage.get(), "digistore/pattern_storage", "digistore/pattern_storage",
				true, true);

		orientableWithCustomModel(ModBlocks.Inverter.get(), "inverter");
		orientableWithCustomModel(ModBlocks.Rectifier.get(), "rectifier");
		orientableWithCustomModel(ModBlocks.SolderingTable.get(), "soldering_table");
		orientableWithCustomModel(ModBlocks.Turbine.get(), "machine_turbine");
		orientableWithCustomModel(ModBlocks.HydroponicPod.get(), "machine_hydroponic_pod");
		basicCustomModel(ModBlocks.ExperienceHopper.get(), "experience_hopper");
		orientableWithCustomModel(ModBlocks.PowerMonitor.get(), "power_monitor");

		refineryTower(ModBlocks.RefineryTower.get());
		lightSocket(ModBlocks.LightSocket.get());

		for (TierPair tier : Tiers.getConveyorTiers()) {
			conveyorStraight(ModBlocks.ConveyorsStraight.get(tier.location()).get(), tier.name());
			conveyorRamp(ModBlocks.ConveyorsRampUp.get(tier.location()).get(), tier.name(), true);
			conveyorRamp(ModBlocks.ConveyorsRampDown.get(tier.location()).get(), tier.name(), false);
			converyorSupplier(ModBlocks.ConveyorsSupplier.get(tier.location()).get(), tier.name());
			conveyorExtractor(ModBlocks.ConveyorsExtractor.get(tier.location()).get(), tier.name());
			conveyorHopper(ModBlocks.ConveyorsHopper.get(tier.location()).get(), tier.name(), false);
			conveyorHopper(ModBlocks.ConveyorsFilteredHopper.get(tier.location()).get(), tier.name(), true);
		}

		cable1Thickness(ModBlocks.BasicRedstoneCableNaked.get(), "redstone/cable_basic_redstone_naked", null);
		cable5Thickness(ModBlocks.BundledRedstoneCable.get(), "redstone/cable_bundled_redstone", null);

		cable3Thickness(ModBlocks.DigistoreCable.get(), "cable_digistore", "attachments/cable_digistore_attachment");
		cable5Thickness(ModBlocks.ScaffoldCable.get(), "cable_scaffold", "attachments/cable_scaffold_attachment");

		for (TierPair tier : Tiers.getCableTiers()) {
			cable5Thickness(ModBlocks.PowerCables.get(tier.location()).get(), "cable_power_" + tier.name(),
					"attachments/cable_" + tier.name() + "_power_attachment");
			cable5Thickness(ModBlocks.InsulatedPowerCables.get(tier.location()).get(),
					"cable_power_" + tier.name() + "_insulated",
					"attachments/cable_" + tier.name() + "_power_attachment");
			cable7Thickness(ModBlocks.IndustrialPowerCables.get(tier.location()).get(),
					"cable_industrial_power_" + tier.name(), "cable_industrial_power_" + tier.name());

			cable5Thickness(ModBlocks.ItemCables.get(tier.location()).get(), "cable_item_" + tier.name(),
					"attachments/cable_" + tier.name() + "_item_attachment");

			cable5Thickness(ModBlocks.FluidCables.get(tier.location()).get(), "cable_fluid_" + tier.name(),
					"attachments/cable_" + tier.name() + "_fluid_attachment");
			cableBlock(ModBlocks.CapillaryFluidCables.get(tier.location()).get(), "cable_2_core", "cable_2_extension",
					"cable_2_straight", "cable_5_attachment", "cable_capillary_fluid_" + tier.name(),
					"attachments/cable_" + tier.name() + "_fluid_attachment");
			cable7Thickness(ModBlocks.IndustrialFluidCables.get(tier.location()).get(),
					"cable_industrial_fluid_" + tier.name(), "cable_industrial_fluid_" + tier.name());
		}

		for (TierPair tier : Tiers.getHeat()) {
			cable5Thickness(ModBlocks.HeatCables.get(tier.location()).get(), "cable_" + tier.name() + "_heat",
					"attachments/cable_" + tier.name() + "_attachment");
		}

		for (RedstoneCableTier tier : Tiers.getRedstone()) {
			cable2Thickness(ModBlocks.RedstoneCables.get(tier.location()).get(),
					"redstone/cable_basic_redstone_" + tier.color().getName(), null);
		}

		/**********
		 * Fluids *
		 **********/
		for (StaticPowerFluidBundle fluid : ModFluids.FLUID_BUNDLES) {
			if (!ModFluids.CONCRETE.containsValue(fluid) && !ModFluids.DYES.containsValue(fluid)) {
				simpleBlockWithCustomTexture(fluid.getBlock().get(),
						"fluids/" + fluid.getSource().getKey().location().getPath() + "_still");
			}
		}
		for (StaticPowerFluidBundle fluid : ModFluids.CONCRETE.values()) {
			simpleBlockWithCustomTexture(fluid.getBlock().get(), "fluids/concrete_still");
		}
		for (StaticPowerFluidBundle fluid : ModFluids.DYES.values()) {
			simpleBlockWithCustomTexture(fluid.getBlock().get(), "fluids/dye_still");
		}
	}

	public void rotatedPillarBlockWithCustomTexture(Block block, String texture) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture);
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture + "_top");

		ModelFile vertical = models().cubeColumn(name(block), side, top);
		ModelFile horizontal = models().cubeColumnHorizontal(name(block) + "_horizontal", side, top);

		getVariantBuilder(block).partialState().with(RotatedPillarBlock.AXIS, Axis.Y).modelForState()
				.modelFile(vertical).addModel().partialState().with(RotatedPillarBlock.AXIS, Axis.Z).modelForState()
				.modelFile(horizontal).rotationX(90).addModel().partialState().with(RotatedPillarBlock.AXIS, Axis.X)
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
		ResourceLocation dirt = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture + "_side");
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture + "_top");
		ModelFile model = models().withExistingParent(name(block), "block/template_farmland").texture("dirt", dirt)
				.texture("top", top).texture("particle", top);
		simpleBlock(block, model);
	}

	public void machine(Block block, String textureSet, String machine) {
		machine(block, textureSet, machine, true);
	}

	public void machineWithoutOnTexture(Block block, String textureSet, String machine) {
		machine(block, textureSet, machine, false);
	}

	public void machine(Block block, String textureSet, String machine, boolean differentModelForOnState) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_side");
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation front = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_front");
		ResourceLocation ioFrame = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_io_frame");
		ResourceLocation frontOverlayOff = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + machine);
		ResourceLocation frontOverlayOn = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/" + machine + (differentModelForOnState ? "_on" : ""));

		ModelFile offModel = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/machine_base"))
				.texture("side", side).texture("top", top).texture("front", front).texture("io_frame", ioFrame)
				.texture("front_overlay", frontOverlayOff).texture("particle", side);

		ModelFile onModel = models()
				.withExistingParent(name(block) + "_on",
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/machine_base"))
				.texture("side", side).texture("top", top).texture("front", front).texture("io_frame", ioFrame)
				.texture("front_overlay", frontOverlayOn).texture("particle", side);

		getVariantBuilder(block).forAllStates(state -> {
			ModelFile modelToUse = differentModelForOnState
					? state.getValue(StaticPowerMachineBlock.IS_ON) ? onModel : offModel
					: offModel;
			return ConfiguredModel.builder().modelFile(modelToUse)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void batteryBlock(Block block, String textureSet) {
		ResourceLocation base = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation battery = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/batteries/battery_block_" + textureSet);
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/battery_block"))
				.texture("base", base).texture("base", base).texture("battery", battery).texture("particle", battery);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void tankBlock(Block block, String textureSet) {
		ResourceLocation tank = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/tanks/tank_" + textureSet);
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/tank_block"))
				.texture("tank", tank).texture("particle", tank);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void pumpBlock(Block block, String textureSet) {
		ResourceLocation pump = new ResourceLocation(StaticPower.MOD_ID, "blocks/machines/pumps/pump_" + textureSet);
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/pump_block"))
				.texture("texture", pump).texture("particle", pump);

		getVariantBuilder(block).forAllStates(state -> {
			ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
			Direction facing = state.getValue(BlockStateProperties.FACING);
			applySixSidedRotationFromnNorthBearing(builder, facing);
			return builder.build();
		});
	}

	public void chest(Block block, String textureSet) {
		ResourceLocation chest = new ResourceLocation(StaticPower.MOD_ID, "entity/chest/" + textureSet + "_chest");
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/base_chest"))
				.texture("texture", chest).texture("particle", chest);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void machineBlock(Block block, String textureSet) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_side");
		ResourceLocation top = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation front = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_front");
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/machine_block"))
				.texture("side", side).texture("top", top).texture("front", front).texture("particle", side);
		simpleBlock(block, model);
	}

	public void onOffBlock(Block block, String modelName) {
		getVariantBuilder(block).forAllStates(state -> {
			ModelFile offModel = models().withExistingParent(name(block),
					new ResourceLocation(StaticPower.MOD_ID, "block/base_models/" + modelName));
			ModelFile onModel = models().withExistingParent(name(block) + "_on",
					new ResourceLocation(StaticPower.MOD_ID, "block/base_models/" + modelName + "_on"));
			return ConfiguredModel.builder()
					.modelFile(state.getValue(StaticPowerMachineBlock.IS_ON) ? onModel : offModel).build();
		});
	}

	public void refineryBlock(Block block, String overlayName) {
		ResourceLocation sideOverlay = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/refinery/" + overlayName);
		ModelFile offModel = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/refinery_block"))
				.texture("side_overlay", sideOverlay);
		ModelFile onModel = models()
				.withExistingParent(name(block) + "_on",
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/refinery_block"))
				.texture("side_overlay", sideOverlay);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(StaticPowerMachineBlock.IS_ON) ? onModel : offModel).build();
		});
	}

	public void solarPanel(Block block, String panel) {
		ResourceLocation panelTexture = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/solar_panel/" + panel);
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/solar_panel"))
				.texture("panel", panelTexture).texture("particle", panelTexture);
		simpleBlock(block, model);
	}

	public void resistor(Block block, String band1, String band2, String band3) {
		ResourceLocation band1Texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/resistor/" + band1);
		ResourceLocation band2Texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/resistor/" + band2);
		ResourceLocation band3Texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/power/resistor/" + band3);
		ModelFile model = models()
				.withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/resistor"))
				.texture("band_1", band1Texture).texture("band_2", band2Texture).texture("band_3", band3Texture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void circuitBreaker(Block block, String label) {
		ResourceLocation labelTexture = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/power/circuit_breaker/" + label);
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/circuit_breaker"))
				.texture("label", labelTexture);
		ModelFile modelTripped = models()
				.withExistingParent(name(block) + "_tripped",
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/circuit_breaker_tripped"))
				.texture("label", labelTexture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(BlockCircuitBreaker.TRIPPED) ? modelTripped : model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void transformer(Block block, String textureSet) {
		ResourceLocation texture = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/power/transformers/transformer_" + textureSet);
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/transformer"))
				.texture("texture", texture).texture("particle", texture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void wireTerminal(Block block, String texture) {
		ResourceLocation textureLocation = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/cables/terminals/" + texture);
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/wire_terminal"))
				.texture("texture", textureLocation).texture("particle", textureLocation);

		getVariantBuilder(block).forAllStates(state -> {
			ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
			Direction facing = state.getValue(BlockStateProperties.FACING);
			applySixSidedRotationFromUpBearing(builder, facing);
			return builder.build();
		});
	}

	public void lightSocket(Block block) {
		ModelFile model = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/light_socket"));

		getVariantBuilder(block).forAllStates(state -> {
			ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
			Direction facing = state.getValue(BlockStateProperties.FACING);
			applySixSidedRotationFromUpBearing(builder, facing);
			return builder.build();
		});
	}

	public void makeCrop(BaseSimplePlant block, String baseTexture) {
		getVariantBuilder(block).forAllStates(state -> {
			int age = state.getValue(block.getAgeProperty());
			ResourceLocation texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + baseTexture + "_" + age);
			ModelFile model = models().withExistingParent(name(block), "block/crop").texture("crop", texture)
					.texture("particle", texture);

			return ConfiguredModel.builder().modelFile(model).build();
		});
	}

	public void sapling(Block block, String name) {
		ResourceLocation panelTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/trees/" + name);
		simpleBlock(block, models().cross(name(block), panelTexture));
	}

	public void orientableWithCustomTexture(Block block, String side, String front, String top) {
		ResourceLocation sideTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + side);
		ResourceLocation frontTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + front);
		ResourceLocation topTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + top);

		ModelFile model = models().orientable(name(block), sideTexture, frontTexture, topTexture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void basicCustomModel(Block block, String modelName) {
		ModelFile model = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/" + modelName));
		simpleBlock(block, model);
	}

	public void basicCustomModelWithSixSidedFacing(Block block, String path, Direction bearing) {
		ModelFile model = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/" + path));

		getVariantBuilder(block).forAllStates(state -> {
			ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
			Direction facing = state.getValue(BlockStateProperties.FACING);
			if (bearing == Direction.UP) {
				applySixSidedRotationFromUpBearing(builder, facing);
			} else if (bearing == Direction.NORTH) {
				this.applySixSidedRotationFromnNorthBearing(builder, facing);
			} else {
				throw new RuntimeException("Bearing: " + bearing + " not supported!");
			}

			return builder.build();
		});
	}

	public void pumpTube(Block block) {
		ModelFile bottom = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/pump_tube/bottom"));
		ModelFile full = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/pump_tube/full"));
		ModelFile middle = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/pump_tube/middle"));
		ModelFile top = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/pump_tube/top"));

		getVariantBuilder(block).forAllStates(state -> {
			ModelFile file = null;
			TowerPiece piece = state.getValue(StaticPowerBlockProperties.TOWER_POSITION);
			switch (piece) {
			case BOTTOM:
				file = bottom;
				break;
			case FULL:
				file = full;
				break;
			case MIDDLE:
				file = middle;
				break;
			case TOP:
				file = top;
				break;
			}

			ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(file);
			Direction facing = state.getValue(BlockStateProperties.FACING);
			applySixSidedRotationFromUpBearing(builder, facing);
			return builder.build();
		});
	}

	public void basicCustomModelOnOff(Block block, String modelName) {
		ModelFile model = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/" + modelName));
		ModelFile modelOn = models()
				.getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/" + modelName + "_on"));
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(state.getValue(StaticPowerMachineBlock.IS_ON) ? modelOn : model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void conveyorStraight(Block block, String textureSet) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_side");
		ResourceLocation bottom = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_top");
		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/conveyor_straight"))
				.texture("base_side", side).texture("base_bottom", bottom);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void conveyorHopper(Block block, String textureSet, boolean filtered) {
		ResourceLocation base = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_front");
		ResourceLocation filter = new ResourceLocation(StaticPower.MOD_ID, "blocks/empty_texture");
		if (filtered) {
			filter = new ResourceLocation("block/iron_trapdoor");
		}

		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/conveyor_hopper"))
				.texture("base", base).texture("filter", filter);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void converyorSupplier(Block block, String textureSet) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_front");
		ResourceLocation sideWithBolts = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_side");
		ResourceLocation bottom = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation ioFrame = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_io_frame");

		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/conveyor_supplier"))
				.texture("side", side).texture("side_with_bolts", sideWithBolts).texture("bottom", bottom)
				.texture("io_frame", ioFrame);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void conveyorExtractor(Block block, String textureSet) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_front");
		ResourceLocation sideWithBolts = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_side");
		ResourceLocation bottom = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_top");
		ResourceLocation ioFrame = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_io_frame");

		ModelFile model = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/conveyor_extractor"))
				.texture("side", side).texture("side_with_bolts", sideWithBolts).texture("bottom", bottom)
				.texture("io_frame", ioFrame);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void conveyorRamp(Block block, String textureSet, boolean slopeUp) {
		ResourceLocation side = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_front");
		ResourceLocation bottom = new ResourceLocation(StaticPower.MOD_ID,
				"blocks/machines/sides/" + textureSet + "_machine_top");
		String modelPath = slopeUp ? "block/base_models/conveyor_slope_up" : "block/base_models/conveyor_slope_down";
		ModelFile model = models().withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, modelPath))
				.texture("side_texture", side).texture("bottom_texture", bottom).texture("particle", bottom);
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model).rotationY(
					((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + (!slopeUp ? 180 : 0))
							% 360)
					.build();
		});
	}

	public void orientableWithCustomModel(Block block, String modelPath) {
		ModelFile model = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/" + modelPath));
		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder().modelFile(model)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void digistoreBlock(Block block, String front) {
		ResourceLocation frontTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + front);
		ResourceLocation frontOnTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + front + "_on");

		ModelFile modelOff = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/digistore_base"))
				.texture("front_overlay", frontTexture)
				.texture("side_overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/empty_texture"));
		ModelFile modelOn = models()
				.withExistingParent(name(block) + "_on",
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/digistore_base"))
				.texture("front_overlay", frontOnTexture)
				.texture("side_overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/empty_texture"));

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(StaticPowerMachineBlock.IS_ON) ? modelOn : modelOff)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void digistore(Block block, String front, String side, boolean hasFrontOn, boolean hasSideOn) {
		ResourceLocation frontTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + front);
		ResourceLocation sideTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + side);

		ResourceLocation frontOnTexture = frontTexture;
		ResourceLocation sideOnTexture = sideTexture;

		if (hasFrontOn) {
			frontOnTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + front + "_on");
		}

		if (hasSideOn) {
			sideOnTexture = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + side + "_on");
		}

		ModelFile modelOff = models()
				.withExistingParent(name(block),
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/digistore_base"))
				.texture("front_overlay", frontTexture).texture("side_overlay", sideTexture);
		ModelFile modelOn = models()
				.withExistingParent(name(block) + "_on",
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/digistore_base"))
				.texture("front_overlay", frontOnTexture).texture("side_overlay", sideOnTexture);

		getVariantBuilder(block).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(StaticPowerMachineBlock.IS_ON) ? modelOn : modelOff)
					.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
					.build();
		});
	}

	public void cauldron(Block block, String texture) {
		ResourceLocation textureLocation = new ResourceLocation(StaticPower.MOD_ID, "blocks/" + texture);
		ModelFile model = models()
				.withExistingParent(name(block), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/cauldron"))
				.texture("texture", textureLocation).texture("particle", textureLocation);
		simpleBlock(block, model);
	}

	public void refineryTower(Block block) {
		ModelFile bottom = models()
				.getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/refinery_tower/bottom"));
		ModelFile full = models()
				.getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/refinery_tower/full"));
		ModelFile middle = models()
				.getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/refinery_tower/middle"));
		ModelFile top = models().getExistingFile(new ResourceLocation(StaticPower.MOD_ID, "block/refinery_tower/top"));

		getVariantBuilder(block).forAllStates(state -> {
			ModelFile file = null;
			TowerPiece piece = state.getValue(StaticPowerBlockProperties.TOWER_POSITION);
			switch (piece) {
			case BOTTOM:
				file = bottom;
				break;
			case FULL:
				file = full;
				break;
			case MIDDLE:
				file = middle;
				break;
			case TOP:
				file = top;
				break;
			}
			return ConfiguredModel.builder().modelFile(file).build();
		});
	}

	public void cable7Thickness(Block block, String texturePath, String attachmentTexturePath) {
		cableBlock(block, "cable_7_core", "cable_7_extension", "cable_7_straight", "cable_7_attachment", texturePath,
				attachmentTexturePath);
	}

	public void cable5Thickness(Block block, String texturePath, String attachmentTexturePath) {
		cableBlock(block, "cable_5_core", "cable_5_extension", "cable_5_straight", "cable_5_attachment", texturePath,
				attachmentTexturePath);
	}

	public void cable3Thickness(Block block, String texturePath, String attachmentTexturePath) {
		cableBlock(block, "cable_3_core", "cable_3_extension", "cable_3_straight", "cable_3_attachment", texturePath,
				attachmentTexturePath);
	}

	public void cable2Thickness(Block block, String texturePath, String attachmentTexturePath) {
		cableBlock(block, "cable_2_core", "cable_2_extension", "cable_2_straight", "cable_2_attachment", texturePath,
				attachmentTexturePath);
	}

	public void cable1Thickness(Block block, String texturePath, String attachmentTexturePath) {
		cableBlock(block, "cable_1_core", "cable_1_extension", "cable_1_straight", "cable_1_attachment", texturePath,
				attachmentTexturePath);
	}

	public void cableBlock(Block block, String coreModel, String extensionModel, String straightModel,
			String attachmentModel, String texturePath, String attachmentTexturePath) {
		ResourceLocation texture = new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/" + texturePath);

		ModelFile core = models()
				.withExistingParent("block/" + name(block) + "_core",
						new ResourceLocation(StaticPower.MOD_ID, "block/base_models/cables/" + coreModel))
				.texture("cable_texture", texture).texture("particle", texture);
		models().withExistingParent("block/" + name(block) + "_extension",
				new ResourceLocation(StaticPower.MOD_ID, "block/base_models/cables/" + extensionModel))
				.texture("cable_texture", texture).texture("particle", texture);
		models().withExistingParent("block/" + name(block) + "_straight",
				new ResourceLocation(StaticPower.MOD_ID, "block/base_models/cables/" + straightModel))
				.texture("cable_texture", texture).texture("particle", texture);

		if (attachmentTexturePath != null) {
			ResourceLocation attachmentTexture = new ResourceLocation(StaticPower.MOD_ID,
					"blocks/cables/" + attachmentTexturePath);
			models().withExistingParent("block/" + name(block) + "_attachment",
					new ResourceLocation(StaticPower.MOD_ID, "block/base_models/cables/" + attachmentModel))
					.texture("cable_texture", attachmentTexture).texture("particle", attachmentTexture);
		}

		// Ignore the blockstates here.
		simpleBlock(block, core);
	}

	private String name(Block block) {
		return key(block).getPath();
	}

	private ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	private void applySixSidedRotationFromnNorthBearing(ConfiguredModel.Builder<?> builder, Direction facing) {
		switch (facing) {
		case DOWN:
			builder.rotationX(-90);
			break;
		case UP:
			builder.rotationX(90);
			break;
		case EAST:
			builder.rotationY(-90);
			break;
		case SOUTH:
			builder.rotationX(180);
			break;
		case WEST:
			builder.rotationY(90);
			break;
		default:
			break;
		}
	}

	private void applySixSidedRotationFromUpBearing(ConfiguredModel.Builder<?> builder, Direction facing) {
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
	}
}
