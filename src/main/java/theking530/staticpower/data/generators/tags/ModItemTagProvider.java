package theking530.staticpower.data.generators.tags;

import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.MaterialBundle;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;

public class ModItemTagProvider extends ItemTagsProvider {

	public ModItemTagProvider(DataGenerator generator, ModBlockTagProvider blockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTags, StaticPower.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		copy(ModBlockTags.CABLES, ModItemTags.CABLES);
		copy(ModBlockTags.PUMPS, ModItemTags.PUMPS);
		copy(ModBlockTags.MACHINES, ModItemTags.MACHINES);
		copy(ModBlockTags.BATTERY_BLOCKS, ModItemTags.BATTERY_BLOCKS);
		copy(ModBlockTags.HEATSINKS, ModItemTags.HEATSINKS);
		copy(ModBlockTags.MACHINE_BLOCKS, ModItemTags.MACHINE_BLOCKS);
		copy(ModBlockTags.SOLAR_PANELS, ModItemTags.SOLAR_PANELS);
		copy(ModBlockTags.TANKS, ModItemTags.TANKS);
		copy(ModBlockTags.TRANSFORMERS, ModItemTags.TRANSFORMERS);
		copy(ModBlockTags.POWER_MANAGEMENT, ModItemTags.POWER_MANAGEMENT);
		copy(ModBlockTags.CIRCUIT_BREAKERS, ModItemTags.CIRCUIT_BREAKERS);
		copy(ModBlockTags.RESISTORS, ModItemTags.RESISTORS);
		copy(ModBlockTags.PUMP_TUBE, ModItemTags.PUMP_TUBE);
		copy(ModBlockTags.TILLABLE, ModItemTags.TILLABLE);
		copy(ModBlockTags.REFINERY_BLOCK, ModItemTags.REFINERY_BLOCK);
		copy(ModBlockTags.FARMLAND, ModItemTags.FARMLAND);
		copy(ModBlockTags.CAULDRONS, ModItemTags.CAULDRONS);
		copy(ModBlockTags.CHESTS, ModItemTags.CHESTS);
		copy(ModBlockTags.CONVEYORS, ModItemTags.CONVEYORS);

		copy(BlockTags.PLANKS, ItemTags.PLANKS);
		copy(BlockTags.LOGS, ItemTags.LOGS);
		copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
		copy(BlockTags.LEAVES, ItemTags.LEAVES);
		copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		copy(BlockTags.WOOL, ItemTags.WOOL);
		copy(ModBlockTags.createForgeTag("rubber_wood_logs"), ModItemTags.createForgeTag("rubber_wood_logs"));

		copy(Tags.Blocks.ORES, Tags.Items.ORES);
		copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
		copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
		copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);

		tag(Tags.Items.INGOTS)
				.add(toArray(
						ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateIngot()).map((bundle) -> bundle.getSmeltedMaterial().get())))
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateHeatedIngotMaterial())
						.map((bundle) -> bundle.getHeatedSmeltedMaterial().get())));

		tag(Tags.Items.GEMS).add(ModMaterials.SAPPHIRE.getRawMaterial().get(), ModMaterials.RUBY.getRawMaterial().get());

		tag(Tags.Items.NUGGETS).add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateNugget()).map((bundle) -> bundle.getNugget().get())));

		tag(ModItemTags.PLATES).add(ModItems.RubberSheet.get())
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGeneratePlate()).map((bundle) -> bundle.getPlate().get())));

		tag(ModItemTags.GEARS).add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateGear()).map((bundle) -> bundle.getGear().get())));

		tag(Tags.Items.DUSTS)
				.add(ModItems.DustCharcoal.get(), ModItems.DustCoal.get(), ModItems.DustSaltpeter.get(), ModItems.DustSulfur.get(), ModItems.DustCoalSmall.get(),
						ModItems.DustCharcoalSmall.get(), ModItems.DustWood.get())
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateDust()).map((bundle) -> bundle.getDust().get())));

		tag(Tags.Items.RAW_MATERIALS).add(ModItems.RawRustyIron.get()).add(toArray(ModMaterials.MATERIALS.values().stream()
				.filter((bundle) -> bundle.shouldGenerateRawMaterial() && bundle.getRawMaterialPrefix().equals("raw")).map((bundle) -> bundle.getRawMaterial().get())));

		tag(ModItemTags.MATERIALS).addTag(Tags.Items.INGOTS).addTag(Tags.Items.DUSTS).addTag(Tags.Items.RODS).addTag(Tags.Items.GEMS).addTag(Tags.Items.RAW_MATERIALS)
				.addTag(ModItemTags.GEARS).addTag(ModItemTags.PLATES).addTag(ModItemTags.HEATED_INGOTS).add(ModItems.RubberWoodBark.get()).add(ModItems.LatexChunk.get())
				.add(ModItems.RubberBar.get());

		tag(ModItemTags.BACKPACKS).add(ModItems.Backpack.get(), ModItems.BuildersBackPack.get(), ModItems.DiggersBackPack.get(), ModItems.EngineersBackpack.get(),
				ModItems.FarmersBackpack.get(), ModItems.LumberjacksBackPack.get(), ModItems.MinersBackpack.get(), ModItems.ToolsBackpack.get());

		tag(ModItemTags.COVER_SAW).add(ModItems.IronCoverSaw.get(), ModItems.RubyCoverSaw.get(), ModItems.SapphireCoverSaw.get(), ModItems.DiamondCoverSaw.get(),
				ModItems.TungstenCoverSaw.get());
		tag(ModItemTags.SOLDERING_IRON).add(ModItems.SolderingIron.get(), ModItems.ElectringSolderingIron.get());
		tag(ModItemTags.MAGNET).add(ModItems.BasicMagnet.get(), ModItems.AdvancedMagnet.get(), ModItems.StaticMagnet.get(), ModItems.EnergizedMagnet.get(),
				ModItems.LumumMagnet.get());

		tag(ModItemTags.MINING_DRILL).add(ModItems.BasicMiningDrill.get(), ModItems.AdvancedMiningDrill.get(), ModItems.StaticMiningDrill.get(),
				ModItems.EnergizedMiningDrill.get(), ModItems.LumumMiningDrill.get());
		tag(ModItemTags.MINING_DRILL_BIT).add(ModItems.IronDrillBit.get(), ModItems.BronzeDrillBit.get(), ModItems.AdvancedDrillBit.get(), ModItems.StaticDrillBit.get(),
				ModItems.EnergizedDrillBit.get(), ModItems.LumumDrillBit.get(), ModItems.TungstenDrillBit.get(), ModItems.CreativeDrillBit.get());

		tag(ModItemTags.BLADE).add(ModItems.IronBlade.get(), ModItems.BronzeBlade.get(), ModItems.AdvancedBlade.get(), ModItems.StaticBlade.get(), ModItems.EnergizedBlade.get(),
				ModItems.LumumBlade.get(), ModItems.TungstenBlade.get(), ModItems.CreativeBlade.get());

		tag(ModItemTags.CHAINSAW).add(ModItems.BasicChainsaw.get(), ModItems.AdvancedChainsaw.get(), ModItems.StaticChainsaw.get(), ModItems.EnergizedChainsaw.get(),
				ModItems.LumumChainsaw.get());
		tag(ModItemTags.CHAINSAW_BLADE).add(ModItems.IronChainsawBlade.get(), ModItems.BronzeChainsawBlade.get(), ModItems.AdvancedChainsawBlade.get(),
				ModItems.StaticChainsawBlade.get(), ModItems.EnergizedChainsawBlade.get(), ModItems.LumumChainsawBlade.get(), ModItems.TungstenChainsawBlade.get(),
				ModItems.CreativeChainsawBlade.get());

		tag(ModItemTags.HAMMER).add(ModItems.IronMetalHammer.get(), ModItems.BronzeMetalHammer.get(), ModItems.CopperMetalHammer.get(), ModItems.TinMetalHammer.get(),
				ModItems.ZincMetalHammer.get(), ModItems.TungstenMetalHammer.get(), ModItems.CreativeMetalHammer.get());
		tag(ModItemTags.WIRE_CUTTER).add(ModItems.BronzeWireCutters.get(), ModItems.IronWireCutters.get(), ModItems.ZincWireCutters.get(), ModItems.TungstenWireCutters.get(),
				ModItems.CreativeWireCutters.get());
		tag(ModItemTags.WRENCH).add(ModItems.Wrench.get(), ModItems.StaticWrench.get(), ModItems.EnergizedWrench.get(), ModItems.LumumWrench.get());
		tag(ModItemTags.PORTABLE_BATTERY).add(ModItems.BasicPortableBattery.get(), ModItems.AdvancedPortableBattery.get(), ModItems.StaticPortableBattery.get(),
				ModItems.EnergizedPortableBattery.get(), ModItems.LumumPortableBattery.get(), ModItems.CreativePortableBattery.get());
		tag(ModItemTags.PORTABLE_BATTERY_PACK).add(ModItems.BasicBatteryPack.get(), ModItems.AdvancedBatteryPack.get(), ModItems.StaticBatteryPack.get(),
				ModItems.EnergizedBatteryPack.get(), ModItems.LumumBatteryPack.get(), ModItems.CreativeBatteryPack.get());
		tag(ModItemTags.FLUID_CAPSULES).add(ModItems.BasicFluidCapsule.get(), ModItems.AdvancedFluidCapsule.get(), ModItems.StaticFluidCapsule.get(),
				ModItems.EnergizedFluidCapsule.get(), ModItems.LumumFluidCapsule.get(), ModItems.CreativeFluidCapsule.get());

		tag(ModItemTags.FARMING_AXE).addTag(Tags.Items.TOOLS_AXES);
		tag(ModItemTags.FARMING_HOE).addTag(Tags.Items.TOOLS_HOES);
		tag(ModItemTags.FARMING_SEEDS).addTag(Tags.Items.SEEDS).add(Items.CARROT).add(Items.POTATO).add(ModItems.StaticSeeds.get()).add(ModItems.EnergizedSeeds.get())
				.add(ModItems.LumumSeeds.get());

		tag(ModItemTags.RESEARCH).add(ModItems.ResearchTier1.get()).add(ModItems.ResearchTier2.get()).add(ModItems.ResearchTier3.get()).add(ModItems.ResearchTier4.get())
				.add(ModItems.ResearchTier5.get()).add(ModItems.ResearchTier6.get()).add(ModItems.ResearchTier7.get());

		tag(ModItemTags.LIGHTBULB).add(toArray(ModItems.Lightbulbs.values().stream().map((bulb) -> bulb.get())));

		tag(ModItemTags.TOOLS).addTag(ModItemTags.HAMMER).addTag(ModItemTags.MAGNET).addTag(ModItemTags.WIRE_CUTTER).addTag(ModItemTags.MINING_DRILL).addTag(ModItemTags.CHAINSAW)
				.addTag(ModItemTags.PORTABLE_BATTERY).addTag(ModItemTags.PORTABLE_BATTERY_PACK).addTag(ModItemTags.SOLDERING_IRON).addTag(ModItemTags.WRENCH)
				.addTag(ModItemTags.COVER_SAW).addTag(ModItemTags.FLUID_CAPSULES)
				.add(ModItems.Thermometer.get(), ModItems.CableNetworkAnalyzer.get(), ModItems.Multimeter.get(), ModItems.DigistoreWirelessTerminal.get());

		tag(ModItemTags.DIGGER_BACKPACK).addTag(ItemTags.DIRT).addTag(Tags.Items.NETHERRACK).addTag(Tags.Items.OBSIDIAN).addTag(Tags.Items.SAND).addTag(Tags.Items.SANDSTONE)
				.addTag(Tags.Items.COBBLESTONE).addTag(Tags.Items.END_STONES).addTag(Tags.Items.ORE_BEARING_GROUND_DEEPSLATE).addTag(Tags.Items.ORE_BEARING_GROUND_NETHERRACK)
				.addTag(Tags.Items.ORE_BEARING_GROUND_STONE);

		tag(ModItemTags.LUMBERJACK_BACKPACK).addTag(ItemTags.LOGS).addTag(ItemTags.PLANKS).addTag(ItemTags.SAPLINGS).addTag(ItemTags.LEAVES).addTag(Tags.Items.RODS_WOODEN);

		tag(ModItemTags.HUNTER_BACKPACK).addTag(Tags.Items.EGGS).addTag(Tags.Items.LEATHER).addTag(Tags.Items.FEATHERS).addTag(ItemTags.WOOL).add(Items.BEEF).add(Items.CHICKEN)
				.add(Items.PORKCHOP).add(ModItems.RawSmutton.get()).add(ModItems.RawEeef.get()).add(Items.ROTTEN_FLESH);

		tag(ModItemTags.BUILDER_BACKPACK).addTag(ItemTags.STONE_BRICKS).addTag(ItemTags.WALLS).addTag(ItemTags.FENCES).addTag(ItemTags.STAIRS).addTag(Tags.Items.STONE)
				.addTag(Tags.Items.GLASS).addTag(Tags.Items.GLASS_PANES).addTag(Tags.Items.FENCE_GATES);

		tag(ModItemTags.MINER_BACKPACK).addTag(Tags.Items.ORES).addTag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).addTag(Tags.Items.ORES_IN_GROUND_NETHERRACK)
				.addTag(Tags.Items.ORES_IN_GROUND_STONE).addTag(Tags.Items.RAW_MATERIALS).addTag(Tags.Items.GEMS).addTag(ItemTags.COALS).addTag(Tags.Items.DUSTS_GLOWSTONE)
				.addTag(Tags.Items.DUSTS_REDSTONE).addTag(Tags.Items.GEMS_PRISMARINE).addTag(Tags.Items.DUSTS_PRISMARINE);

		tag(ModItemTags.FARMER_BACKPACK).addTag(Tags.Items.CROPS).addTag(Tags.Items.SEEDS);

		tag(ModItemTags.ENGINEER_BACKPACK).addTag(ModItemTags.CABLES).addTag(ModItemTags.PUMPS).addTag(ModItemTags.MACHINES).addTag(ModItemTags.HEATSINKS)
				.addTag(ModItemTags.MACHINE_BLOCKS).addTag(ModItemTags.SOLAR_PANELS).addTag(ModItemTags.TANKS).addTag(ModItemTags.TRANSFORMERS).addTag(ModItemTags.POWER_MANAGEMENT)
				.addTag(ModItemTags.RESISTORS).addTag(ModItemTags.CIRCUIT_BREAKERS);

		tag(ModItemTags.TOOL_BACKPACK).addTag(Tags.Items.TOOLS_PICKAXES).addTag(Tags.Items.TOOLS_SHOVELS).addTag(Tags.Items.TOOLS_AXES).addTag(Tags.Items.TOOLS_HOES)
				.addTag(ModItemTags.TOOLS);

		tag(ModItemTags.HEATED_INGOTS).add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateHeatedIngotMaterial())
				.map((bundle) -> bundle.getHeatedSmeltedMaterial().get())));

		tag(ModItemTags.WIRE_COILS).add(ModItems.WireCoilDigistore.get())
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateWireCoil()).map((bundle) -> bundle.getWireCoil().get())))
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.shouldGenerateInsulatedWireCoil())
						.map((bundle) -> bundle.getInsulatedWireCoil().get())));

		tag(ModItemTags.createForgeTag("gems/ruby")).add(ModMaterials.RUBY.getRawMaterial().get());
		tag(ModItemTags.createForgeTag("gems/sapphire")).add(ModMaterials.SAPPHIRE.getRawMaterial().get());

		tag(ModItemTags.RUBBER).add(ModItems.RubberBar.get());
		tag(ModItemTags.RUBBER_SHEET).add(ModItems.RubberSheet.get());

		tag(ModItemTags.createForgeTag("leather")).add(ModItems.Eather.get());
		tag(ModItemTags.createForgeTag("seeds")).add(ModItems.StaticSeeds.get(), ModItems.EnergizedSeeds.get(), ModItems.LumumSeeds.get());
		tag(ModItemTags.createForgeTag("silicon")).add(ModItems.Silicon.get());

		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.hasOres()) {
				copy(bundle.getOreTag(), bundle.getOreItemTag());
			}
			if (bundle.shouldGenerateRawStorageBlock()) {
				copy(bundle.getRawStorageBlockTag(), bundle.getRawStorageBlockItemTag());
			}
			if (bundle.shouldGenerateStorageBlock()) {
				copy(bundle.getStorageBlockTag(), bundle.getStorageBlockItemTag());
			}
			if (bundle.shouldGenerateDust()) {
				tag(bundle.getDustTag()).add(bundle.getDust().get());
			}
			if (bundle.shouldGeneratePlate()) {
				tag(bundle.getPlateTag()).add(bundle.getPlate().get());
			}
			if (bundle.shouldGenerateRod()) {
				tag(bundle.getRodTag()).add(bundle.getRod().get());
			}
			if (bundle.shouldGenerateGear()) {
				tag(bundle.getGearTag()).add(bundle.getGear().get());
			}
			if (bundle.shouldGenerateIngot() && bundle.getSmeltedMaterialPrefix().equals("ingot")) {
				tag(bundle.getIngotTag()).add(bundle.getSmeltedMaterial().get());
			}
			if (bundle.shouldGenerateRawMaterial()) {
				tag(bundle.getRawMaterialTag()).add(bundle.getRawMaterial().get());
			}
			if (bundle.shouldGenerateChunks()) {
				tag(bundle.getChunkTag()).add(bundle.getChunks().get());
			}
			if (bundle.shouldGenerateNugget()) {
				tag(bundle.getNuggetTag()).add(bundle.getNugget().get());
			}
			if (bundle.shouldGenerateWire()) {
				tag(bundle.getWireTag()).add(bundle.getWire().get());
			}
			if (bundle.shouldGenerateInsulatedWire()) {
				tag(bundle.getInsulatedWireTag()).add(bundle.getInsulatedWire().get());
			}
			if (bundle.shouldGenerateWireCoil()) {
				tag(bundle.getWireCoilTag()).add(bundle.getWireCoil().get());
			}
			if (bundle.shouldGenerateInsulatedWireCoil()) {
				tag(bundle.getInsulatedWireCoilTag()).add(bundle.getInsulatedWireCoil().get());
			}
			if (bundle.shouldGeneratePlate() && bundle.shouldGenerateIngot() && bundle.getSmeltedMaterialPrefix().equals("ingot")) {
				tag(ModItemTags.create("ingot_or_plate/" + bundle.getName())).addTag(bundle.getIngotTag()).addTag(bundle.getPlateTag());
			}
		}

		tag(ModItemTags.create("ingot_or_plate/iron")).addTag(ModItemTags.createForgeTag("ingots/iron")).addTag(ModItemTags.createForgeTag("plates/iron"));
		tag(ModItemTags.create("ingot_or_plate/copper")).addTag(ModItemTags.createForgeTag("ingots/copper")).addTag(ModItemTags.createForgeTag("plates/copper"));
		tag(ModItemTags.create("ingot_or_plate/gold")).addTag(ModItemTags.createForgeTag("ingots/gold")).addTag(ModItemTags.createForgeTag("plates/gold"));
	}

	public static Item[] toArray(Stream<? extends Item> stream) {
		return stream.toList().toArray(Item[]::new);
	}
}
