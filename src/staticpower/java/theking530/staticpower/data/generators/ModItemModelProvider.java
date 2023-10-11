package theking530.staticpower.data.generators;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.Tiers;
import theking530.staticpower.data.Tiers.RedstoneCableTier;
import theking530.staticpower.data.Tiers.TierPair;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.entities.AbstractEntityBuilder;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;

public class ModItemModelProvider extends ItemModelProvider {
	private final Set<Block> customModelBlockItems;

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, existingFileHelper);
		customModelBlockItems = new HashSet<Block>();

		customModelBlockItems.add(ModBlocks.RefineryTower.get());
		customModelBlockItems.add(ModBlocks.RefineryCondenser.get());
		customModelBlockItems.add(ModBlocks.PumpTube.get());
	}

	@Override
	protected void registerModels() {
		for (AbstractEntityBuilder<?> entity : ModEntities.ENTITIES) {
			if (entity instanceof AbstractSpawnableMobType) {
				spawnEgg(((AbstractSpawnableMobType<?>) entity).getEgg());
			}
		}

		simpleItem(ModItems.DistilleryGrain.get(), "misc/distillery_grain");
		simpleItem(ModItems.RustyIronScrap.get(), "materials/rusty_iron_scrap");

		simpleItem(ModItems.ApplePie.get(), "misc/pie_apple");
		simpleItem(ModItems.StaticPie.get(), "misc/pie_static");
		simpleItem(ModItems.EnergizedPie.get(), "misc/pie_energized");
		simpleItem(ModItems.LumumPie.get(), "misc/pie_lumum");
		simpleItem(ModItems.CookedEeef.get(), "misc/eeef_cooked");
		simpleItem(ModItems.CookedSmutton.get(), "misc/smutton_cooked");
		simpleItem(ModItems.Eather.get(), "misc/eather");
		simpleItem(ModItems.PotatoBread.get(), "misc/bread_potato");
		simpleItem(ModItems.PotatoFlour.get(), "misc/flour_potato");
		simpleItem(ModItems.RawEeef.get(), "misc/eeef_raw");
		simpleItem(ModItems.RawSmutton.get(), "misc/smutton_raw");
		simpleItem(ModItems.WheatFlour.get(), "misc/flour_wheat");

		simpleItem(ModItems.MoldBlade.get(), "molds/mold_blade");
		simpleItem(ModItems.MoldBlank.get(), "molds/mold_blank");
		simpleItem(ModItems.MoldBlock.get(), "molds/mold_block");
		simpleItem(ModItems.MoldDrillBit.get(), "molds/mold_drill_bit");
		simpleItem(ModItems.MoldGear.get(), "molds/mold_gear");
		simpleItem(ModItems.MoldIngot.get(), "molds/mold_ingot");
		simpleItem(ModItems.MoldNugget.get(), "molds/mold_nugget");
		simpleItem(ModItems.MoldPlate.get(), "molds/mold_plate");
		simpleItem(ModItems.MoldRod.get(), "molds/mold_rod");
		simpleItem(ModItems.MoldWire.get(), "molds/mold_wire");

		simpleItem(ModItems.RawRustyIron.get(), "materials/raw_ores/raw_rusty_iron");

		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.hasGeneratedMaterial(MaterialTypes.RAW_MATERIAL)) {
				simpleItem(bundle.get(MaterialTypes.RAW_MATERIAL).get(),
						"materials/raw_ores/" + bundle.get(MaterialTypes.RAW_MATERIAL).getFormattedName(bundle.getName()));
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.INGOT)) {
				simpleItem(bundle.get(MaterialTypes.INGOT).get(), "materials/ingots/" + bundle.get(MaterialTypes.INGOT).getFormattedName(bundle.getName()));
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.HEATED_INGOT)) {
				if (bundle == ModMaterials.IRON) {
					simpleItemVanillaTexture(ModMaterials.IRON.get(MaterialTypes.HEATED_INGOT).get(), "iron_ingot");
				} else if (bundle == ModMaterials.GOLD) {
					simpleItemVanillaTexture(ModMaterials.GOLD.get(MaterialTypes.HEATED_INGOT).get(), "gold_ingot");
				} else if (bundle == ModMaterials.COPPER) {
					simpleItemVanillaTexture(ModMaterials.COPPER.get(MaterialTypes.HEATED_INGOT).get(), "copper_ingot");
				} else {
					simpleItem(bundle.get(MaterialTypes.HEATED_INGOT).get(), "materials/ingots/" + bundle.get(MaterialTypes.INGOT).getFormattedName(bundle.getName()));
				}
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.NUGGET)) {
				simpleItem(bundle.get(MaterialTypes.NUGGET).get(), "materials/nuggets/nugget_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.PLATE)) {
				simpleItem(bundle.get(MaterialTypes.PLATE).get(), "materials/plates/plate_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.GEAR)) {
				simpleItem(bundle.get(MaterialTypes.GEAR).get(), "materials/gears/gear_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.GEAR_BOX)) {
				// This is just a dummy model, the real one is generated at runtime.
				simpleItem(bundle.get(MaterialTypes.GEAR_BOX).get(), "materials/gears/gear_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.DUST)) {
				simpleItem(bundle.get(MaterialTypes.DUST).get(), "materials/dusts/dust_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.CHUNKS)) {
				simpleItem(bundle.get(MaterialTypes.CHUNKS).get(), "materials/chunks/chunks_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.ROD)) {
				simpleItem(bundle.get(MaterialTypes.ROD).get(), "materials/rods/rod_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.WIRE)) {
				simpleItem(bundle.get(MaterialTypes.WIRE).get(), "components/wire_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.INSULATED_WIRE)) {
				simpleItem(bundle.get(MaterialTypes.INSULATED_WIRE).get(), "components/wire_insulated_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.WIRE_COIL)) {
				simpleItem(bundle.get(MaterialTypes.WIRE_COIL).get(), "components/wire_coil_" + bundle.getName());
			}

			if (bundle.hasGeneratedMaterial(MaterialTypes.INSULATED_WIRE_COIL)) {
				simpleItem(bundle.get(MaterialTypes.INSULATED_WIRE_COIL).get(), "components/wire_coil_insulated_" + bundle.getName());
			}
		}

		simpleItem(ModItems.DustObsidian.get(), "materials/dusts/dust_obsidian");
		simpleItem(ModItems.DustSaltpeter.get(), "materials/dusts/dust_saltpeter");
		simpleItem(ModItems.DustSulfur.get(), "materials/dusts/dust_sulfur");
		simpleItem(ModItems.DustWood.get(), "materials/dusts/dust_wood");

		simpleItem(ModItems.DustCoalSmall.get(), "materials/dusts/dust_coal_small");
		simpleItem(ModItems.DustCharcoalSmall.get(), "materials/dusts/dust_charcoal_small");

		simpleItem(ModItems.RawSilicon.get(), "materials/raw_silicon");
		simpleItem(ModItems.Silicon.get(), "materials/silicon");
		simpleItem(ModItems.StaticDopedSilicon.get(), "materials/silicon_doped_static");
		simpleItem(ModItems.EnergizedDopedSilicon.get(), "materials/silicon_doped_energized");
		simpleItem(ModItems.LumumDopedSilicon.get(), "materials/silicon_doped_lumum");
		simpleItem(ModItems.CrystalStatic.get(), "materials/crystal_static");
		simpleItem(ModItems.CrystalEnergized.get(), "materials/crystal_energized");
		simpleItem(ModItems.CrystalLumum.get(), "materials/crystal_lumum");
		simpleItem(ModItems.LatexChunk.get(), "materials/latex_chunk");
		simpleItem(ModItems.RubberWoodBark.get(), "materials/rubber_wood_bark");
		simpleItem(ModItems.Slag.get(), "materials/slag");

		simpleItem(ModItems.PortableSmeltingCore.get(), "components/portable_smelting_core");
		simpleItem(ModItems.MemoryChip.get(), "components/memory_chip");
		simpleItem(ModItems.LogicGatePowerSync.get(), "components/logic_gate_power_sync");
		simpleItem(ModItems.InvertedLogicGatePowerSync.get(), "components/inverted_logic_gate_power_sync");
		simpleItem(ModItems.Servo.get(), "components/servo");
		simpleItem(ModItems.Diode.get(), "components/diode");
		simpleItem(ModItems.Transistor.get(), "components/transistor");
		simpleItem(ModItems.InternalClock.get(), "components/internal_clock");
		simpleItem(ModItems.IOPort.get(), "components/io_port");
		simpleItem(ModItems.Motor.get(), "components/motor");
		simpleItem(ModItems.Plug.get(), "components/plug");
		simpleItem(ModItems.DigistoreCore.get(), "components/digistore_core");

		simpleItem(ModItems.BasicCard.get(), "components/card_basic");
		simpleItem(ModItems.AdvancedCard.get(), "components/card_advanced");
		simpleItem(ModItems.StaticCard.get(), "components/card_static");
		simpleItem(ModItems.EnergizedCard.get(), "components/card_energized");
		simpleItem(ModItems.LumumCard.get(), "components/card_lumum");

		for (MinecraftColor color : MinecraftColor.values()) {
			lightbulb(ModItems.Lightbulbs.get(color).get(), color.getName());
		}

		simpleItem(ModItems.WoodTurbineBlades.get(), "components/turbine_blades_wood");
		simpleItem(ModItems.BasicTurbineBlades.get(), "components/turbine_blades_basic");
		simpleItem(ModItems.AdvancedTurbineBlades.get(), "components/turbine_blades_advanced");
		simpleItem(ModItems.StaticTurbineBlades.get(), "components/turbine_blades_static");
		simpleItem(ModItems.EnergizedTurbineBlades.get(), "components/turbine_blades_energized");
		simpleItem(ModItems.LumumTurbineBlades.get(), "components/turbine_blades_lumum");
		simpleItem(ModItems.CreativeTurbineBlades.get(), "components/turbine_blades_creative");

		simpleItem(ModItems.BasicProcessor.get(), "components/processor_basic");
		simpleItem(ModItems.AdvancedProcessor.get(), "components/processor_advanced");
		simpleItem(ModItems.StaticProcessor.get(), "components/processor_static");
		simpleItem(ModItems.EnergizedProcessor.get(), "components/processor_energized");
		simpleItem(ModItems.LumumProcessor.get(), "components/processor_lumum");

		simpleItem(ModItems.RubberBar.get(), "materials/rubber_bar");
		simpleItem(ModItems.RubberSheet.get(), "materials/rubber_sheet");

		simpleItem(ModItems.WireCoilDigistore.get(), "components/wire_coil_digistore");

		simpleItem(ModItems.IronDrillBit.get(), "components/drill_bits/drill_bit_iron");
		simpleItem(ModItems.BronzeDrillBit.get(), "components/drill_bits/drill_bit_bronze");
		simpleItem(ModItems.AdvancedDrillBit.get(), "components/drill_bits/drill_bit_advanced");
		simpleItem(ModItems.TungstenDrillBit.get(), "components/drill_bits/drill_bit_tungsten");
		simpleItem(ModItems.StaticDrillBit.get(), "components/drill_bits/drill_bit_static");
		simpleItem(ModItems.EnergizedDrillBit.get(), "components/drill_bits/drill_bit_energized");
		simpleItem(ModItems.LumumDrillBit.get(), "components/drill_bits/drill_bit_lumum");
		simpleItem(ModItems.CreativeDrillBit.get(), "components/drill_bits/drill_bit_creative");

		simpleItem(ModItems.IronBlade.get(), "components/blades/blade_iron");
		simpleItem(ModItems.BronzeBlade.get(), "components/blades/blade_bronze");
		simpleItem(ModItems.AdvancedBlade.get(), "components/blades/blade_advanced");
		simpleItem(ModItems.TungstenBlade.get(), "components/blades/blade_tungsten");
		simpleItem(ModItems.StaticBlade.get(), "components/blades/blade_static");
		simpleItem(ModItems.EnergizedBlade.get(), "components/blades/blade_energized");
		simpleItem(ModItems.LumumBlade.get(), "components/blades/blade_lumum");
		simpleItem(ModItems.CreativeBlade.get(), "components/blades/blade_creative");

		simpleItem(ModItems.IronChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_iron");
		simpleItem(ModItems.BronzeChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_bronze");
		simpleItem(ModItems.AdvancedChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_advanced");
		simpleItem(ModItems.TungstenChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_tungsten");
		simpleItem(ModItems.StaticChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_static");
		simpleItem(ModItems.EnergizedChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_energized");
		simpleItem(ModItems.LumumChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_lumum");
		simpleItem(ModItems.CreativeChainsawBlade.get(), "components/chainsaw_blades/chainsaw_blade_creative");

		simpleItem(ModItems.IronFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_iron");
		simpleItem(ModItems.BasicFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_basic");
		simpleItem(ModItems.AdvancedFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_advanced");
		simpleItem(ModItems.StaticFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_static");
		simpleItem(ModItems.EnergizedFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_energized");
		simpleItem(ModItems.LumumFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_lumum");
		simpleItem(ModItems.CreativeFluidCapsule.get(), "tools/fluid_capsules/fluid_capsule_creative");

		simpleItem(ModItems.BasicUpgradePlate.get(), "components/upgrade_plate_basic");
		simpleItem(ModItems.AdvancedUpgradePlate.get(), "components/upgrade_plate_advanced");
		simpleItem(ModItems.StaticUpgradePlate.get(), "components/upgrade_plate_static");
		simpleItem(ModItems.EnergizedUpgradePlate.get(), "components/upgrade_plate_energized");
		simpleItem(ModItems.LumumUpgradePlate.get(), "components/upgrade_plate_lumum");

		simpleItem(ModItems.BasicPortableBattery.get(), "tools/batteries/portable_battery_basic");
		simpleItem(ModItems.AdvancedPortableBattery.get(), "tools/batteries/portable_battery_advanced");
		simpleItem(ModItems.StaticPortableBattery.get(), "tools/batteries/portable_battery_static");
		simpleItem(ModItems.EnergizedPortableBattery.get(), "tools/batteries/portable_battery_energized");
		simpleItem(ModItems.LumumPortableBattery.get(), "tools/batteries/portable_battery_lumum");
		simpleItem(ModItems.CreativePortableBattery.get(), "tools/batteries/portable_battery_creative");

		handheldItem(ModItems.Backpack.get(), "backpacks/generic");
		handheldItem(ModItems.BuildersBackPack.get(), "backpacks/builder");
		handheldItem(ModItems.DiggersBackPack.get(), "backpacks/digger");
		handheldItem(ModItems.HuntersPack.get(), "backpacks/hunter");
		handheldItem(ModItems.LumberjacksBackPack.get(), "backpacks/lumberjack");
		handheldItem(ModItems.MinersBackpack.get(), "backpacks/miner");
		handheldItem(ModItems.FarmersBackpack.get(), "backpacks/farmer");
		handheldItem(ModItems.EngineersBackpack.get(), "backpacks/engineer");
		handheldItem(ModItems.ToolsBackpack.get(), "backpacks/tool");

		handheldItem(name(ModItems.Backpack.get()) + "_open", "backpacks/generic_open");
		handheldItem(name(ModItems.BuildersBackPack.get()) + "_open", "backpacks/builder_open");
		handheldItem(name(ModItems.DiggersBackPack.get()) + "_open", "backpacks/digger_open");
		handheldItem(name(ModItems.HuntersPack.get()) + "_open", "backpacks/hunter_open");
		handheldItem(name(ModItems.LumberjacksBackPack.get()) + "_open", "backpacks/lumberjack_open");
		handheldItem(name(ModItems.MinersBackpack.get()) + "_open", "backpacks/miner_open");
		handheldItem(name(ModItems.FarmersBackpack.get()) + "_open", "backpacks/farmer_open");
		handheldItem(name(ModItems.EngineersBackpack.get()) + "_open", "backpacks/engineer_open");
		handheldItem(name(ModItems.ToolsBackpack.get()) + "_open", "backpacks/tool_open");

		simpleItem(ModItems.Backpack.get(), "backpacks/generic");
		simpleItem(ModItems.BuildersBackPack.get(), "backpacks/builder");
		simpleItem(ModItems.DiggersBackPack.get(), "backpacks/digger");
		simpleItem(ModItems.HuntersPack.get(), "backpacks/hunter");
		simpleItem(ModItems.LumberjacksBackPack.get(), "backpacks/lumberjack");
		simpleItem(ModItems.MinersBackpack.get(), "backpacks/miner");
		simpleItem(ModItems.FarmersBackpack.get(), "backpacks/farmer");
		simpleItem(ModItems.EngineersBackpack.get(), "backpacks/engineer");
		simpleItem(ModItems.ToolsBackpack.get(), "backpacks/tool");

		simpleItem(ModItems.BasicBatteryPack.get(), "tools/batteries/battery_pack_basic");
		simpleItem(ModItems.AdvancedBatteryPack.get(), "tools/batteries/battery_pack_advanced");
		simpleItem(ModItems.StaticBatteryPack.get(), "tools/batteries/battery_pack_static");
		simpleItem(ModItems.EnergizedBatteryPack.get(), "tools/batteries/battery_pack_energized");
		simpleItem(ModItems.LumumBatteryPack.get(), "tools/batteries/battery_pack_lumum");
		simpleItem(ModItems.CreativeBatteryPack.get(), "tools/batteries/battery_pack_creative");

		simpleItem(ModItems.StaticSeeds.get(), "plants/seed_static");
		simpleItem(ModItems.EnergizedSeeds.get(), "plants/seed_energized");
		simpleItem(ModItems.LumumSeeds.get(), "plants/seed_lumum");

		simpleItem(ModItems.StaticFruit.get(), "plants/fruit_static");
		simpleItem(ModItems.EnergizedFruit.get(), "plants/fruit_energized");
		simpleItem(ModItems.LumumFruit.get(), "plants/fruit_lumum");
		simpleItem(ModItems.DepletedFruit.get(), "plants/fruit_depleted");

		handheldItem(ModItems.IronMetalHammer.get(), "tools/hammer_iron");
		handheldItem(ModItems.CopperMetalHammer.get(), "tools/hammer_copper");
		handheldItem(ModItems.TinMetalHammer.get(), "tools/hammer_tin");
		handheldItem(ModItems.ZincMetalHammer.get(), "tools/hammer_zinc");
		handheldItem(ModItems.BronzeMetalHammer.get(), "tools/hammer_bronze");
		handheldItem(ModItems.TungstenMetalHammer.get(), "tools/hammer_tungsten");
		handheldItem(ModItems.CreativeMetalHammer.get(), "tools/hammer_creative");

		handheldItem(ModItems.IronWireCutters.get(), "tools/wire_cutters_iron");
		handheldItem(ModItems.ZincWireCutters.get(), "tools/wire_cutters_zinc");
		handheldItem(ModItems.BronzeWireCutters.get(), "tools/wire_cutters_bronze");
		handheldItem(ModItems.TungstenWireCutters.get(), "tools/wire_cutters_tungsten");
		handheldItem(ModItems.CreativeWireCutters.get(), "tools/wire_cutters_creative");

		handheldItem(ModItems.SolderingIron.get(), "tools/soldering_iron");
		handheldItem(ModItems.ElectringSolderingIron.get(), "tools/soldering_iron_electric");

		handheldItem(ModItems.BasicMiningDrill.get(), "tools/mining_drill_basic");
		handheldItem(ModItems.AdvancedMiningDrill.get(), "tools/mining_drill_advanced");
		handheldItem(ModItems.StaticMiningDrill.get(), "tools/mining_drill_static");
		handheldItem(ModItems.EnergizedMiningDrill.get(), "tools/mining_drill_energized");
		handheldItem(ModItems.LumumMiningDrill.get(), "tools/mining_drill_lumum");

		handheldItem(ModItems.BasicChainsaw.get(), "tools/chainsaw_basic");
		handheldItem(ModItems.AdvancedChainsaw.get(), "tools/chainsaw_advanced");
		handheldItem(ModItems.StaticChainsaw.get(), "tools/chainsaw_static");
		handheldItem(ModItems.EnergizedChainsaw.get(), "tools/chainsaw_energized");
		handheldItem(ModItems.LumumChainsaw.get(), "tools/chainsaw_lumum");

		handheldItem(ModItems.Wrench.get(), "tools/wrench");
		handheldItem(ModItems.StaticWrench.get(), "tools/wrench_static");
		handheldItem(ModItems.EnergizedWrench.get(), "tools/wrench_energized");
		handheldItem(ModItems.LumumWrench.get(), "tools/wrench_lumum");

		handheldItem(ModItems.Thermometer.get(), "tools/thermometer");

		handheldItem(ModItems.WeakMagnet.get(), "tools/magnet_weak");
		handheldItem(ModItems.BasicMagnet.get(), "tools/magnet_basic");
		handheldItem(ModItems.AdvancedMagnet.get(), "tools/magnet_advanced");
		handheldItem(ModItems.StaticMagnet.get(), "tools/magnet_static");
		handheldItem(ModItems.EnergizedMagnet.get(), "tools/magnet_energized");
		handheldItem(ModItems.LumumMagnet.get(), "tools/magnet_lumum");

		handheldItem(ModItems.CableNetworkAnalyzer.get(), "tools/cable_network_analyzer");
		handheldItem(ModItems.Multimeter.get(), "tools/multimeter");

		handheldItem(ModItems.IronCoverSaw.get(), "tools/saw_iron");
		handheldItem(ModItems.TungstenCoverSaw.get(), "tools/saw_tungsten");
		handheldItem(ModItems.DiamondCoverSaw.get(), "tools/saw_diamond");
		handheldItem(ModItems.RubyCoverSaw.get(), "tools/saw_ruby");
		handheldItem(ModItems.SapphireCoverSaw.get(), "tools/saw_sapphire");

		simpleItem(ModItems.BasicFilter.get(), "filters/filter_item_basic");
		simpleItem(ModItems.AdvancedFilter.get(), "filters/filter_item_advanced");
		simpleItem(ModItems.StaticFilter.get(), "filters/filter_item_static");
		simpleItem(ModItems.EnergizedFilter.get(), "filters/filter_item_energized");
		simpleItem(ModItems.LumumFilter.get(), "filters/filter_item_lumum");

		regularSizeAttachment(ModItems.BasicExtractorAttachment.get(), "cable_basic_item_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.AdvancedExtractorAttachment.get(), "cable_advanced_item_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.StaticExtractorAttachment.get(), "cable_static_item_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.EnergizedExtractorAttachment.get(), "cable_energized_item_attachment", "cable_attachment_extract_off");
		regularSizeAttachment(ModItems.LumumExtractorAttachment.get(), "cable_lumum_item_attachment", "cable_attachment_extract_off");

		regularSizeAttachment(ModItems.BasicFilterAttachment.get(), "cable_basic_item_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.AdvancedFilterAttachment.get(), "cable_advanced_item_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.StaticFilterAttachment.get(), "cable_static_item_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.EnergizedFilterAttachment.get(), "cable_energized_item_attachment", "cable_attachment_filter");
		regularSizeAttachment(ModItems.LumumFilterAttachment.get(), "cable_lumum_item_attachment", "cable_attachment_filter");

		regularSizeAttachment(ModItems.BasicRetrieverAttachment.get(), "cable_basic_item_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.AdvancedRetrieverAttachment.get(), "cable_advanced_item_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.StaticRetrieverAttachment.get(), "cable_static_item_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.EnergizedRetrieverAttachment.get(), "cable_energized_item_attachment", "cable_attachment_retriever");
		regularSizeAttachment(ModItems.LumumRetrieverAttachment.get(), "cable_lumum_item_attachment", "cable_attachment_retriever");

		digistoreAttachment(ModItems.ExporterAttachment.get(), "cable_digistore_exporter_attachment");
		digistoreAttachment(ModItems.ImporterAttachment.get(), "cable_digistore_importer_attachment");
		digistoreAttachment(ModItems.IOBusAttachment.get(), "cable_digistore_io_bus_attachment");
		digistoreAttachment(ModItems.RegulatorAttachment.get(), "cable_digistore_regulator_attachment");
		digistoreAttachment(ModItems.DigistoreCraftingInterfaceAttachment.get(), "cable_digistore_crafting_interface_attachment");

		digistoreFullBlockAttachment(ModItems.DigistoreTerminalAttachment.get(), "cable_digistore_base_full_attachment", "digistore_terminal");
		digistoreFullBlockAttachment(ModItems.DigistoreCraftingTerminalAttachment.get(), "cable_digistore_base_full_attachment", "digistore_crafting_terminal");
		digistoreFullBlockAttachment(ModItems.DigistorePatternEncoderAttachment.get(), "cable_digistore_base_full_attachment", "digistore_pattern_encoder");
		digistoreFullBlockAttachment(ModItems.DigistoreScreenAttachment.get(), "cable_digistore_base_full_attachment", "digistore_screen");
		digistoreFullBlockAttachment(ModItems.DigistoreLightAttachment.get(), "cable_digistore_base_full_attachment", "digistore_light");

		fromExistingModel(ModItems.SprinklerAttachment.get(), "item/sprinkler");
		fromExistingModel(ModItems.DrainAttachment.get(), "item/drain");
		fromExistingModel(ModBlocks.RefineryTower.get().asItem(), "block/refinery_tower/full");
		fromExistingModel(ModBlocks.RefineryCondenser.get().asItem(), "block/refinery_condenser/machine_refinery_condenser");
		fromExistingModel(ModBlocks.PumpTube.get().asItem(), "block/pump_tube/full");

		simpleItem(ModItems.PatternCard.get(), "digistore/digistore_pattern_card_encoded");
		namedGeneratedModel("digistore_pattern_card_encoded", "digistore/digistore_pattern_card_encoded");

		simpleItem(ModItems.PatternCard.get(), "digistore/digistore_pattern_card_empty");
		simpleItem(ModItems.DigistoreWirelessTerminal.get(), "tools/digistore_wireless_terminal");

		withExistingParent(name(ModItems.CableCover.get()), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/cable_cover"));

		simpleItem(ModItems.MilkBottle.get(), "bottles/bottle_milk");
		simpleItem(ModItems.AppleJuiceBottle.get(), "bottles/bottle_juice_apple");
		simpleItem(ModItems.CarrotJuiceBottle.get(), "bottles/bottle_juice_carrot");
		simpleItem(ModItems.PumpkinJuiceBottle.get(), "bottles/bottle_juice_pumpkin");
		simpleItem(ModItems.MelonJuiceBottle.get(), "bottles/bottle_juice_melon");
		simpleItem(ModItems.BeetJuiceBottle.get(), "bottles/bottle_juice_beetroot");
		simpleItem(ModItems.BerryJuiceBottle.get(), "bottles/bottle_juice_berry");

		simpleItem(ModItems.BasicDigistoreCard.get(), "digistore/digistore_card_basic");
		simpleItem(ModItems.AdvancedDigistoreCard.get(), "digistore/digistore_card_advanced");
		simpleItem(ModItems.StaticDigistoreCard.get(), "digistore/digistore_card_static");
		simpleItem(ModItems.EnergizedDigistoreCard.get(), "digistore/digistore_card_energized");
		simpleItem(ModItems.LumumDigistoreCard.get(), "digistore/digistore_card_lumum");
		simpleItem(ModItems.CreativeDigistoreCard.get(), "digistore/digistore_card_creative");

		simpleItem(ModItems.BasicStackedDigistoreCard.get(), "digistore/digistore_card_stacked_basic");
		simpleItem(ModItems.AdvancedStackedDigistoreCard.get(), "digistore/digistore_card_stacked_advanced");
		simpleItem(ModItems.StaticStackedDigistoreCard.get(), "digistore/digistore_card_stacked_static");
		simpleItem(ModItems.EnergizedStackedDigistoreCard.get(), "digistore/digistore_card_stacked_energized");
		simpleItem(ModItems.LumumStackedDigistoreCard.get(), "digistore/digistore_card_stacked_lumum");
		simpleItem(ModItems.CreativeStackedDigistoreCard.get(), "digistore/digistore_card_stacked_creative");

		simpleItem(ModItems.BasicSingularDigistoreCard.get(), "digistore/digistore_card_singular_basic");
		simpleItem(ModItems.AdvancedSingularDigistoreCard.get(), "digistore/digistore_card_singular_advanced");
		simpleItem(ModItems.StaticSingularDigistoreCard.get(), "digistore/digistore_card_singular_static");
		simpleItem(ModItems.EnergizedSingularDigistoreCard.get(), "digistore/digistore_card_singular_energized");
		simpleItem(ModItems.LumumSingularDigistoreCard.get(), "digistore/digistore_card_singular_lumum");
		simpleItem(ModItems.CreativeSingularDigistoreCard.get(), "digistore/digistore_card_singular_creative");

		simpleItem(ModItems.TeleportUpgrade.get(), "upgrades/upgrade_teleport");
		simpleItem(ModItems.ExperienceVacuumUpgrade.get(), "upgrades/upgrade_experience_vacuum");
		simpleItem(ModItems.VoidUpgrade.get(), "upgrades/upgrade_void");
		simpleItem(ModItems.StackUpgrade.get(), "upgrades/upgrade_stack");
		simpleItem(ModItems.AcceleratorUpgrade.get(), "upgrades/upgrade_accelerator");
		simpleItem(ModItems.CraftingUpgrade.get(), "upgrades/upgrade_crafting");

		upgrade(ModItems.TransformerUpgradeMV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_advanced");
		upgrade(ModItems.TransformerUpgradeHV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_static");
		upgrade(ModItems.TransformerUpgradeVHV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_energized");
		upgrade(ModItems.TransformerUpgradeEV.get(), "upgrades/upgrade_power_transformer", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_basic");
		upgrade(ModItems.StaticPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_energized");
		upgrade(ModItems.LumumPowerUpgrade.get(), "upgrades/upgrade_power", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_basic");
		upgrade(ModItems.StaticOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_energized");
		upgrade(ModItems.LumumOutputMultiplierUpgrade.get(), "upgrades/upgrade_output_multiplier", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_basic");
		upgrade(ModItems.StaticSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_energized");
		upgrade(ModItems.LumumSpeedUpgrade.get(), "upgrades/upgrade_speed", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_basic");
		upgrade(ModItems.StaticRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_energized");
		upgrade(ModItems.LumumRangeUpgrade.get(), "upgrades/upgrade_range", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_basic");
		upgrade(ModItems.StaticTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_energized");
		upgrade(ModItems.LumumTankUpgrade.get(), "upgrades/upgrade_tank", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_basic");
		upgrade(ModItems.StaticCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_energized");
		upgrade(ModItems.LumumCentrifugeUpgrade.get(), "upgrades/upgrade_centrifuge", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_basic");
		upgrade(ModItems.StaticHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_energized");
		upgrade(ModItems.LumumHeatCapacityUpgrade.get(), "upgrades/upgrade_heat_capacity", "overlay_upgrade_lumum");

		upgrade(ModItems.BasicHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_basic");
		upgrade(ModItems.StaticHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_static");
		upgrade(ModItems.EnergizedHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_energized");
		upgrade(ModItems.LumumHeatUpgrade.get(), "upgrades/upgrade_heat", "overlay_upgrade_lumum");

		cable1Inventory(ModBlocks.BasicRedstoneCableNaked.get(), "redstone/cable_basic_redstone_naked");
		cable5Inventory(ModBlocks.BundledRedstoneCable.get(), "redstone/cable_bundled_redstone");
		cable3Inventory(ModBlocks.DigistoreCable.get(), "cable_digistore");
		cable5Inventory(ModBlocks.ScaffoldCable.get(), "cable_scaffold");

		for (TierPair tier : Tiers.getCableTiers()) {
			cable5Inventory(ModBlocks.PowerCables.get(tier.location()).get(), "cable_power_" + tier.name());
			cable5Inventory(ModBlocks.InsulatedPowerCables.get(tier.location()).get(), "cable_power_" + tier.name() + "_insulated");
			cable7Inventory(ModBlocks.IndustrialPowerCables.get(tier.location()).get(), "cable_industrial_power_" + tier.name());

			cable5Inventory(ModBlocks.ItemCables.get(tier.location()).get(), "cable_item_" + tier.name());

			cable5Inventory(ModBlocks.FluidCables.get(tier.location()).get(), "cable_fluid_" + tier.name());
			cable2InventoryVertical(ModBlocks.CapillaryFluidCables.get(tier.location()).get(), "cable_capillary_fluid_" + tier.name());
			cable7Inventory(ModBlocks.IndustrialFluidCables.get(tier.location()).get(), "cable_industrial_fluid_" + tier.name());
		}

		for (TierPair tier : Tiers.getHeat()) {
			cable5Inventory(ModBlocks.HeatCables.get(tier.location()).get(), "cable_" + tier.name() + "_heat");
		}

		for (RedstoneCableTier tier : Tiers.getRedstone()) {
			cable2Inventory(ModBlocks.RedstoneCables.get(tier.location()).get(), "redstone/cable_basic_redstone_" + tier.color().getName());
		}

		for (RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries()) {
			if (customModelBlockItems.contains(block.get())) {
				continue;
			}
			if (block.get().asItem() == Items.AIR) {
				continue;
			}
			basicItemBlock(block.get());
		}

		simpleItem(ModItems.StaticSeeds.get(), "plants/seed_static");
		simpleItem(ModItems.EnergizedSeeds.get(), "plants/seed_energized");
		simpleItem(ModItems.LumumSeeds.get(), "plants/seed_lumum");
	}

	private ItemModelBuilder lightbulb(Item item, String color) {
		ResourceLocation bulbGlass = new ResourceLocation(StaticPower.MOD_ID, "items/light_bulb/" + color + "_bulb");
		return withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/light_bulb")).texture("glass", bulbGlass);
	}

	private ItemModelBuilder spawnEgg(Item item) {
		return withExistingParent(name(item), new ResourceLocation("item/template_spawn_egg"));
	}

	private ItemModelBuilder namedGeneratedModel(String name, String texturePath) {
		return withExistingParent(name, new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + texturePath));
	}

	private ItemModelBuilder simpleItem(Item item, String texturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + texturePath));
	}

	private ItemModelBuilder handheldItem(String name, String texturePath) {
		return withExistingParent(name, new ResourceLocation("item/handheld")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + texturePath));
	}

	private ItemModelBuilder handheldItem(Item item, String texturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/handheld")).texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + texturePath));
	}

	public ItemModelBuilder basicItemBlock(Block block) {
		ResourceLocation blockModelLocation = new ResourceLocation(StaticPower.MOD_ID, "block/" + block.asItem());
		ItemModelBuilder builder = withExistingParent(name(block.asItem()), blockModelLocation);
		return builder;
	}

	private ItemModelBuilder upgrade(Item item, String upgradeTexturePath, String plateTexturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/generated"))
				.texture("layer0", new ResourceLocation(StaticPower.MOD_ID, "items/" + upgradeTexturePath))
				.texture("layer1", new ResourceLocation(StaticPower.MOD_ID, "items/upgrades/" + plateTexturePath));
	}

	private ItemModelBuilder simpleItemVanillaTexture(Item item, String texturePath) {
		return withExistingParent(name(item), new ResourceLocation("item/generated")).texture("layer0", new ResourceLocation("item/" + texturePath));
	}

	private ItemModelBuilder regularSizeAttachment(Item item, String baseTexturePath, String overlayTexturePath) {
		return withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "block/base_models/cables/cable_5_attachment"))
				.texture("cable_texture", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + baseTexturePath))
				.texture("overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + overlayTexturePath)).guiLight(GuiLight.FRONT);
	}

	private void digistoreAttachment(Item item, String texturePath) {
		withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/digistore_basic_attachment"))
				.texture("texture", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + texturePath)).guiLight(GuiLight.FRONT);
	}

	private void digistoreFullBlockAttachment(Item item, String texturePath, String overlayTexturePath) {
		withExistingParent(name(item) + "_off", new ResourceLocation(StaticPower.MOD_ID, "item/base_models/digistore_large_attachment"))
				.texture("base", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + texturePath))
				.texture("overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + overlayTexturePath)).guiLight(GuiLight.FRONT);

		withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/digistore_large_attachment"))
				.texture("base", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + texturePath))
				.texture("overlay", new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/attachments/" + overlayTexturePath + "_on")).guiLight(GuiLight.FRONT);
	}

	private ItemModelBuilder cable7Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_7_inventory", texturePath);
	}

	private ItemModelBuilder cable5Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_5_inventory", texturePath);
	}

	private ItemModelBuilder cable3Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_3_inventory", texturePath);
	}

	@SuppressWarnings("unused")
	private ItemModelBuilder cable3InventoryVertical(Block block, String texturePath) {
		return cableInventory(block, "cable_3_inventory_vertical", texturePath);
	}

	@SuppressWarnings("unused")
	private ItemModelBuilder cable2Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_2_inventory", texturePath);
	}

	private ItemModelBuilder cable2InventoryVertical(Block block, String texturePath) {
		return cableInventory(block, "cable_2_inventory_vertical", texturePath);
	}

	@SuppressWarnings("unused")
	private ItemModelBuilder cable1Inventory(Block block, String texturePath) {
		return cableInventory(block, "cable_1_inventory", texturePath);
	}

	private ItemModelBuilder cableInventory(Block block, String modelPath, String texturePath) {
		Item item = block.asItem();
		customModelBlockItems.add(block);
		return withExistingParent(name(item), new ResourceLocation(StaticPower.MOD_ID, "item/base_models/" + modelPath)).texture("cable_texture",
				new ResourceLocation(StaticPower.MOD_ID, "blocks/cables/" + texturePath));
	}

	private void fromExistingModel(Item item, String modelPath) {
		ModelFile file = getExistingFile(new ResourceLocation(StaticPower.MOD_ID, modelPath));
		getBuilder(key(item).getPath()).parent(file);
	}

	private String name(Item item) {
		return key(item).getPath();
	}

	private ResourceLocation key(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}
}
