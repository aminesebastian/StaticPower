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
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.materials.Material;
import theking530.staticpower.data.materials.Material.MaterialClass;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;

public class ModItemTagProvider extends ItemTagsProvider {

	public ModItemTagProvider(DataGenerator generator, ModBlockTagProvider blockTags,
			@Nullable ExistingFileHelper existingFileHelper) {
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

		copy(MinecraftColor.WHITE.getBlockWoolTag(), MinecraftColor.WHITE.getItemWoolTag());
		copy(MinecraftColor.ORANGE.getBlockWoolTag(), MinecraftColor.ORANGE.getItemWoolTag());
		copy(MinecraftColor.MAGENTA.getBlockWoolTag(), MinecraftColor.MAGENTA.getItemWoolTag());
		copy(MinecraftColor.LIGHT_BLUE.getBlockWoolTag(), MinecraftColor.LIGHT_BLUE.getItemWoolTag());
		copy(MinecraftColor.YELLOW.getBlockWoolTag(), MinecraftColor.YELLOW.getItemWoolTag());
		copy(MinecraftColor.LIME.getBlockWoolTag(), MinecraftColor.LIME.getItemWoolTag());
		copy(MinecraftColor.PINK.getBlockWoolTag(), MinecraftColor.PINK.getItemWoolTag());
		copy(MinecraftColor.GRAY.getBlockWoolTag(), MinecraftColor.GRAY.getItemWoolTag());
		copy(MinecraftColor.LIGHT_GRAY.getBlockWoolTag(), MinecraftColor.LIGHT_GRAY.getItemWoolTag());
		copy(MinecraftColor.CYAN.getBlockWoolTag(), MinecraftColor.CYAN.getItemWoolTag());
		copy(MinecraftColor.PURPLE.getBlockWoolTag(), MinecraftColor.PURPLE.getItemWoolTag());
		copy(MinecraftColor.BLUE.getBlockWoolTag(), MinecraftColor.BLUE.getItemWoolTag());
		copy(MinecraftColor.BROWN.getBlockWoolTag(), MinecraftColor.BROWN.getItemWoolTag());
		copy(MinecraftColor.GREEN.getBlockWoolTag(), MinecraftColor.GREEN.getItemWoolTag());
		copy(MinecraftColor.RED.getBlockWoolTag(), MinecraftColor.RED.getItemWoolTag());
		copy(MinecraftColor.BLACK.getBlockWoolTag(), MinecraftColor.BLACK.getItemWoolTag());

		copy(MinecraftColor.WHITE.getBlockConcreteTag(), MinecraftColor.WHITE.getItemConcreteTag());
		copy(MinecraftColor.ORANGE.getBlockConcreteTag(), MinecraftColor.ORANGE.getItemConcreteTag());
		copy(MinecraftColor.MAGENTA.getBlockConcreteTag(), MinecraftColor.MAGENTA.getItemConcreteTag());
		copy(MinecraftColor.LIGHT_BLUE.getBlockConcreteTag(), MinecraftColor.LIGHT_BLUE.getItemConcreteTag());
		copy(MinecraftColor.YELLOW.getBlockConcreteTag(), MinecraftColor.YELLOW.getItemConcreteTag());
		copy(MinecraftColor.LIME.getBlockConcreteTag(), MinecraftColor.LIME.getItemConcreteTag());
		copy(MinecraftColor.PINK.getBlockConcreteTag(), MinecraftColor.PINK.getItemConcreteTag());
		copy(MinecraftColor.GRAY.getBlockConcreteTag(), MinecraftColor.GRAY.getItemConcreteTag());
		copy(MinecraftColor.LIGHT_GRAY.getBlockConcreteTag(), MinecraftColor.LIGHT_GRAY.getItemConcreteTag());
		copy(MinecraftColor.CYAN.getBlockConcreteTag(), MinecraftColor.CYAN.getItemConcreteTag());
		copy(MinecraftColor.PURPLE.getBlockConcreteTag(), MinecraftColor.PURPLE.getItemConcreteTag());
		copy(MinecraftColor.BLUE.getBlockConcreteTag(), MinecraftColor.BLUE.getItemConcreteTag());
		copy(MinecraftColor.BROWN.getBlockConcreteTag(), MinecraftColor.BROWN.getItemConcreteTag());
		copy(MinecraftColor.GREEN.getBlockConcreteTag(), MinecraftColor.GREEN.getItemConcreteTag());
		copy(MinecraftColor.RED.getBlockConcreteTag(), MinecraftColor.RED.getItemConcreteTag());
		copy(MinecraftColor.BLACK.getBlockConcreteTag(), MinecraftColor.BLACK.getItemConcreteTag());

		copy(ModBlockTags.createForgeTag("rubber_wood_logs"), ModItemTags.createForgeTag("rubber_wood_logs"));

		copy(Tags.Blocks.ORES, Tags.Items.ORES);
		copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
		copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
		copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);

		tag(Tags.Items.INGOTS)
				.add(toArray(
						ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.INGOT))
								.map((bundle) -> bundle.get(MaterialTypes.INGOT).get())))
				.add(toArray(ModMaterials.MATERIALS.values().stream()
						.filter((bundle) -> bundle.has(MaterialTypes.HEATED_INGOT))
						.map((bundle) -> bundle.get(MaterialTypes.HEATED_INGOT).get())));

		tag(Tags.Items.GEMS).add(ModMaterials.SAPPHIRE.get(MaterialTypes.RAW_MATERIAL).get(),
				ModMaterials.RUBY.get(MaterialTypes.RAW_MATERIAL).get());

		tag(ModItemTags.OBSIDIAN_DUST).add(ModItems.DustObsidian.get());
		tag(ModItemTags.WOOD_DUST).add(ModItems.DustWood.get());

		tag(ModItemTags.COALS_DUST).addTag(ModMaterials.COAL.get(MaterialTypes.DUST).getItemTag())
				.addTag(ModMaterials.CHARCOAL.get(MaterialTypes.DUST).getItemTag());

		tag(ModItemTags.BLAST_FURNACE_FUEL).add(Items.CHARCOAL)
				.addTag(ModMaterials.COAL_COKE.get(MaterialTypes.RAW_MATERIAL).getItemTag())
				.addTag(ModMaterials.COAL_COKE.get(MaterialTypes.RAW_STOARGE_BLOCK).getItemTag());

		tag(Tags.Items.NUGGETS).add(
				toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.NUGGET))
						.map((bundle) -> bundle.get(MaterialTypes.NUGGET).get())));

		tag(ModItemTags.PLATES).add(ModItems.RubberSheet.get()).add(
				toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.PLATE))
						.map((bundle) -> bundle.get(MaterialTypes.PLATE).get())));

		tag(ModItemTags.WIRES)
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.WIRE))
						.map((bundle) -> bundle.get(MaterialTypes.WIRE).get())));

		tag(ModItemTags.GEARS)
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.GEAR))
						.map((bundle) -> bundle.get(MaterialTypes.GEAR).get())));

		tag(Tags.Items.DUSTS)
				.add(ModItems.DustSaltpeter.get(), ModItems.DustSulfur.get(), ModItems.DustCoalSmall.get(),
						ModItems.DustCharcoalSmall.get(), ModItems.DustWood.get())
				.add(toArray(ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.DUST))
						.map((bundle) -> bundle.get(MaterialTypes.DUST).get())));

		tag(Tags.Items.RAW_MATERIALS).add(ModItems.RawRustyIron.get())
				.add(toArray(ModMaterials.MATERIALS.values().stream()
						.filter((bundle) -> bundle.has(MaterialTypes.RAW_MATERIAL))
						.map((bundle) -> bundle.get(MaterialTypes.RAW_MATERIAL).get())));

		tag(ModItemTags.MATERIALS).addTag(Tags.Items.INGOTS).addTag(Tags.Items.DUSTS).addTag(Tags.Items.RODS)
				.addTag(Tags.Items.GEMS).addTag(Tags.Items.RAW_MATERIALS).addTag(ModItemTags.GEARS)
				.addTag(ModItemTags.PLATES).addTag(ModItemTags.HEATED_INGOTS).add(ModItems.RubberWoodBark.get())
				.add(ModItems.LatexChunk.get()).add(ModItems.RubberBar.get());

		tag(ModItemTags.BACKPACKS).add(ModItems.Backpack.get(), ModItems.BuildersBackPack.get(),
				ModItems.DiggersBackPack.get(), ModItems.EngineersBackpack.get(), ModItems.FarmersBackpack.get(),
				ModItems.LumberjacksBackPack.get(), ModItems.MinersBackpack.get(), ModItems.ToolsBackpack.get());

		tag(ModItemTags.COVER_SAW).add(ModItems.IronCoverSaw.get(), ModItems.RubyCoverSaw.get(),
				ModItems.SapphireCoverSaw.get(), ModItems.DiamondCoverSaw.get(), ModItems.TungstenCoverSaw.get());
		tag(ModItemTags.SOLDERING_IRON).add(ModItems.SolderingIron.get(), ModItems.ElectringSolderingIron.get());
		tag(ModItemTags.MAGNET).add(ModItems.BasicMagnet.get(), ModItems.AdvancedMagnet.get(),
				ModItems.StaticMagnet.get(), ModItems.EnergizedMagnet.get(), ModItems.LumumMagnet.get());

		tag(ModItemTags.MINING_DRILL).add(ModItems.BasicMiningDrill.get(), ModItems.AdvancedMiningDrill.get(),
				ModItems.StaticMiningDrill.get(), ModItems.EnergizedMiningDrill.get(), ModItems.LumumMiningDrill.get());
		tag(ModItemTags.MINING_DRILL_BIT).add(ModItems.IronDrillBit.get(), ModItems.BronzeDrillBit.get(),
				ModItems.AdvancedDrillBit.get(), ModItems.StaticDrillBit.get(), ModItems.EnergizedDrillBit.get(),
				ModItems.LumumDrillBit.get(), ModItems.TungstenDrillBit.get(), ModItems.CreativeDrillBit.get());

		tag(ModItemTags.BLADE).add(ModItems.IronBlade.get(), ModItems.BronzeBlade.get(), ModItems.AdvancedBlade.get(),
				ModItems.StaticBlade.get(), ModItems.EnergizedBlade.get(), ModItems.LumumBlade.get(),
				ModItems.TungstenBlade.get(), ModItems.CreativeBlade.get());

		tag(ModItemTags.CHAINSAW).add(ModItems.BasicChainsaw.get(), ModItems.AdvancedChainsaw.get(),
				ModItems.StaticChainsaw.get(), ModItems.EnergizedChainsaw.get(), ModItems.LumumChainsaw.get());
		tag(ModItemTags.CHAINSAW_BLADE).add(ModItems.IronChainsawBlade.get(), ModItems.BronzeChainsawBlade.get(),
				ModItems.AdvancedChainsawBlade.get(), ModItems.StaticChainsawBlade.get(),
				ModItems.EnergizedChainsawBlade.get(), ModItems.LumumChainsawBlade.get(),
				ModItems.TungstenChainsawBlade.get(), ModItems.CreativeChainsawBlade.get());

		tag(ModItemTags.HAMMER).add(ModItems.IronMetalHammer.get(), ModItems.BronzeMetalHammer.get(),
				ModItems.CopperMetalHammer.get(), ModItems.TinMetalHammer.get(), ModItems.ZincMetalHammer.get(),
				ModItems.TungstenMetalHammer.get(), ModItems.CreativeMetalHammer.get());
		tag(ModItemTags.WIRE_CUTTER).add(ModItems.BronzeWireCutters.get(), ModItems.IronWireCutters.get(),
				ModItems.ZincWireCutters.get(), ModItems.TungstenWireCutters.get(), ModItems.CreativeWireCutters.get());
		tag(ModItemTags.WRENCH).add(ModItems.Wrench.get(), ModItems.StaticWrench.get(), ModItems.EnergizedWrench.get(),
				ModItems.LumumWrench.get());
		tag(ModItemTags.PORTABLE_BATTERY).add(ModItems.BasicPortableBattery.get(),
				ModItems.AdvancedPortableBattery.get(), ModItems.StaticPortableBattery.get(),
				ModItems.EnergizedPortableBattery.get(), ModItems.LumumPortableBattery.get(),
				ModItems.CreativePortableBattery.get());
		tag(ModItemTags.PORTABLE_BATTERY_PACK).add(ModItems.BasicBatteryPack.get(), ModItems.AdvancedBatteryPack.get(),
				ModItems.StaticBatteryPack.get(), ModItems.EnergizedBatteryPack.get(), ModItems.LumumBatteryPack.get(),
				ModItems.CreativeBatteryPack.get());
		tag(ModItemTags.FLUID_CAPSULES).add(ModItems.BasicFluidCapsule.get(), ModItems.AdvancedFluidCapsule.get(),
				ModItems.StaticFluidCapsule.get(), ModItems.EnergizedFluidCapsule.get(),
				ModItems.LumumFluidCapsule.get(), ModItems.CreativeFluidCapsule.get());

		tag(ModItemTags.FARMING_AXE).addTag(Tags.Items.TOOLS_AXES);
		tag(ModItemTags.FARMING_HOE).addTag(Tags.Items.TOOLS_HOES);
		tag(ModItemTags.FARMING_SEEDS).addTag(Tags.Items.SEEDS).add(Items.CARROT).add(Items.POTATO)
				.add(ModItems.StaticSeeds.get()).add(ModItems.EnergizedSeeds.get()).add(ModItems.LumumSeeds.get());

		tag(ModItemTags.LIGHTBULB).add(toArray(ModItems.Lightbulbs.values().stream().map((bulb) -> bulb.get())));

		tag(ModItemTags.TOOLS).addTag(ModItemTags.HAMMER).addTag(ModItemTags.MAGNET).addTag(ModItemTags.WIRE_CUTTER)
				.addTag(ModItemTags.MINING_DRILL).addTag(ModItemTags.CHAINSAW).addTag(ModItemTags.PORTABLE_BATTERY)
				.addTag(ModItemTags.PORTABLE_BATTERY_PACK).addTag(ModItemTags.SOLDERING_IRON).addTag(ModItemTags.WRENCH)
				.addTag(ModItemTags.COVER_SAW).addTag(ModItemTags.FLUID_CAPSULES).add(ModItems.Thermometer.get(),
						ModItems.CableNetworkAnalyzer.get(), ModItems.Multimeter.get(),
						ModItems.DigistoreWirelessTerminal.get());

		tag(ModItemTags.DIGGER_BACKPACK).addTag(ItemTags.DIRT).addTag(Tags.Items.NETHERRACK).addTag(Tags.Items.OBSIDIAN)
				.addTag(Tags.Items.SAND).addTag(Tags.Items.SANDSTONE).addTag(Tags.Items.COBBLESTONE)
				.addTag(Tags.Items.END_STONES).addTag(Tags.Items.ORE_BEARING_GROUND_DEEPSLATE)
				.addTag(Tags.Items.ORE_BEARING_GROUND_NETHERRACK).addTag(Tags.Items.ORE_BEARING_GROUND_STONE);

		tag(ModItemTags.LUMBERJACK_BACKPACK).addTag(ItemTags.LOGS).addTag(ItemTags.PLANKS).addTag(ItemTags.SAPLINGS)
				.addTag(ItemTags.LEAVES).addTag(Tags.Items.RODS_WOODEN);

		tag(ModItemTags.HUNTER_BACKPACK).addTag(Tags.Items.EGGS).addTag(Tags.Items.LEATHER).addTag(Tags.Items.FEATHERS)
				.addTag(ItemTags.WOOL).add(Items.BEEF).add(Items.CHICKEN).add(Items.PORKCHOP)
				.add(ModItems.RawSmutton.get()).add(ModItems.RawEeef.get()).add(Items.ROTTEN_FLESH);

		tag(ModItemTags.BUILDER_BACKPACK).addTag(ItemTags.STONE_BRICKS).addTag(ItemTags.WALLS).addTag(ItemTags.FENCES)
				.addTag(ItemTags.STAIRS).addTag(Tags.Items.STONE).addTag(Tags.Items.GLASS)
				.addTag(Tags.Items.GLASS_PANES).addTag(Tags.Items.FENCE_GATES);

		tag(ModItemTags.MINER_BACKPACK).addTag(Tags.Items.ORES).addTag(Tags.Items.ORES_IN_GROUND_DEEPSLATE)
				.addTag(Tags.Items.ORES_IN_GROUND_NETHERRACK).addTag(Tags.Items.ORES_IN_GROUND_STONE)
				.addTag(Tags.Items.RAW_MATERIALS).addTag(Tags.Items.GEMS).addTag(ItemTags.COALS)
				.addTag(Tags.Items.DUSTS_GLOWSTONE).addTag(Tags.Items.DUSTS_REDSTONE).addTag(Tags.Items.GEMS_PRISMARINE)
				.addTag(Tags.Items.DUSTS_PRISMARINE);

		tag(ModItemTags.FARMER_BACKPACK).addTag(Tags.Items.CROPS).addTag(Tags.Items.SEEDS);

		tag(ModItemTags.ENGINEER_BACKPACK).addTag(ModItemTags.CABLES).addTag(ModItemTags.PUMPS)
				.addTag(ModItemTags.MACHINES).addTag(ModItemTags.HEATSINKS).addTag(ModItemTags.MACHINE_BLOCKS)
				.addTag(ModItemTags.SOLAR_PANELS).addTag(ModItemTags.TANKS).addTag(ModItemTags.TRANSFORMERS)
				.addTag(ModItemTags.POWER_MANAGEMENT).addTag(ModItemTags.RESISTORS)
				.addTag(ModItemTags.CIRCUIT_BREAKERS);

		tag(ModItemTags.TOOL_BACKPACK).addTag(Tags.Items.TOOLS_PICKAXES).addTag(Tags.Items.TOOLS_SHOVELS)
				.addTag(Tags.Items.TOOLS_AXES).addTag(Tags.Items.TOOLS_HOES).addTag(ModItemTags.TOOLS);

		tag(ModItemTags.HEATED_INGOTS).add(toArray(
				ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.HEATED_INGOT))
						.map((bundle) -> bundle.get(MaterialTypes.HEATED_INGOT).get())));

		tag(ModItemTags.WIRE_COILS).add(ModItems.WireCoilDigistore.get())
				.add(toArray(
						ModMaterials.MATERIALS.values().stream().filter((bundle) -> bundle.has(MaterialTypes.WIRE_COIL))
								.map((bundle) -> bundle.get(MaterialTypes.WIRE_COIL).get())))
				.add(toArray(ModMaterials.MATERIALS.values().stream()
						.filter((bundle) -> bundle.has(MaterialTypes.INSULATED_WIRE_COIL))
						.map((bundle) -> bundle.get(MaterialTypes.INSULATED_WIRE_COIL).get())));

		tag(ModItemTags.RUBBER).add(ModItems.RubberBar.get());
		tag(ModItemTags.RUBBER_SHEET).add(ModItems.RubberSheet.get());

		tag(Tags.Items.LEATHER).add(ModItems.Eather.get());
		tag(Tags.Items.SEEDS).add(ModItems.StaticSeeds.get(), ModItems.EnergizedSeeds.get(), ModItems.LumumSeeds.get());
		tag(ModItemTags.SILICON).add(ModItems.Silicon.get());

		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.hasGeneratedOre()) {
				copy(bundle.oreBlockTag(), bundle.oreItemTag());
			}

			for (Material<?> material : bundle.getMaterials()) {
				if (!material.hasItemTag()) {
					continue;
				}

				if (material.getMaterialClass() == MaterialClass.BLOCK && material.hasBlockTag()) {
					copy(material.getBlockTag(), material.getItemTag());
				}

				if (material.getMaterialClass() == MaterialClass.ITEM) {
					tag(material.getItemTag()).add((Item) material.get());
				}
			}

			if (bundle.plateOrIngotTag() != null) {
				tag(bundle.plateOrIngotTag()).addTag(bundle.get(MaterialTypes.PLATE).getItemTag())
						.addTag(bundle.get(MaterialTypes.INGOT).getItemTag());
			}
		}

		tag(ModItemTags.GLASS_BOTTLES).add(Items.GLASS_BOTTLE);
	}

	public static Item[] toArray(Stream<? extends Item> stream) {
		return stream.toList().toArray(Item[]::new);
	}
}
