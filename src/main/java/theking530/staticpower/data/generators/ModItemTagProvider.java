package theking530.staticpower.data.generators;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModItems;
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

		copy(Tags.Blocks.ORES, Tags.Items.ORES);
		copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
		copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
		copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);

		tag(Tags.Items.INGOTS).add(ModItems.IngotAluminum.get(), ModItems.IngotBrass.get(), ModItems.IngotBronze.get(), ModItems.IngotEnergized.get(),
				ModItems.IngotInertInfusion.get(), ModItems.IngotLead.get(), ModItems.IngotLumum.get(), ModItems.IngotMagnesium.get(), ModItems.IngotPlatinum.get(),
				ModItems.IngotRedstoneAlloy.get(), ModItems.IngotSilver.get(), ModItems.IngotStatic.get(), ModItems.IngotTin.get(), ModItems.IngotTungsten.get(),
				ModItems.IngotZinc.get(), ModItems.IngotUranium.get(), ModItems.IngotTinHeated.get(), ModItems.IngotZincHeated.get(), ModItems.IngotSilverHeated.get(),
				ModItems.IngotLeadHeated.get(), ModItems.IngotTungstenHeated.get(), ModItems.IngotMagnesiumHeated.get(), ModItems.IngotPlatinumHeated.get(),
				ModItems.IngotAluminumHeated.get(), ModItems.IngotStaticHeated.get(), ModItems.IngotEnergizedHeated.get(), ModItems.IngotLumumHeated.get(),
				ModItems.IngotInertInfusionHeated.get(), ModItems.IngotRedstoneAlloyHeated.get(), ModItems.IngotBrassHeated.get(), ModItems.IngotBronzeHeated.get(),
				ModItems.IngotIronHeated.get(), ModItems.IngotCopperHeated.get(), ModItems.IngotGoldHeated.get());

		tag(Tags.Items.GEMS).add(ModItems.GemRuby.get(), ModItems.GemSapphire.get());

		tag(Tags.Items.NUGGETS).add(ModItems.NuggetAluminum.get(), ModItems.NuggetBrass.get(), ModItems.NuggetBronze.get(), ModItems.NuggetCopper.get(),
				ModItems.NuggetEnergized.get(), ModItems.NuggetInertInfusion.get(), ModItems.NuggetLead.get(), ModItems.NuggetLumum.get(), ModItems.NuggetMagnesium.get(),
				ModItems.NuggetPlatinum.get(), ModItems.NuggetRedstoneAlloy.get(), ModItems.NuggetSilver.get(), ModItems.NuggetStatic.get(), ModItems.NuggetTin.get(),
				ModItems.NuggetTungsten.get(), ModItems.NuggetZinc.get());

		tag(ModItemTags.PLATES).add(ModItems.PlateAluminum.get(), ModItems.PlateBrass.get(), ModItems.PlateBronze.get(), ModItems.PlateCopper.get(), ModItems.PlateEnergized.get(),
				ModItems.PlateGold.get(), ModItems.PlateInertInfusion.get(), ModItems.PlateIron.get(), ModItems.PlateLead.get(), ModItems.PlateLumum.get(),
				ModItems.PlateMagnesium.get(), ModItems.PlatePlatinum.get(), ModItems.PlateRedstoneAlloy.get(), ModItems.PlateSilver.get(), ModItems.PlateStatic.get(),
				ModItems.PlateTin.get(), ModItems.PlateTungsten.get(), ModItems.PlateZinc.get(), ModItems.RubberSheet.get());

		tag(ModItemTags.GEARS).add(ModItems.GearBrass.get(), ModItems.GearBronze.get(), ModItems.GearCopper.get(), ModItems.GearEnergized.get(), ModItems.GearGold.get(),
				ModItems.GearInertInfusion.get(), ModItems.GearIron.get(), ModItems.GearLumum.get(), ModItems.GearStatic.get(), ModItems.GearTin.get());

		tag(Tags.Items.DUSTS).add(ModItems.DustAluminum.get(), ModItems.DustBrass.get(), ModItems.DustBronze.get(), ModItems.DustCharcoal.get(), ModItems.DustCoal.get(),
				ModItems.DustCopper.get(), ModItems.DustDiamond.get(), ModItems.DustEmerald.get(), ModItems.DustEnergized.get(), ModItems.DustGold.get(),
				ModItems.DustInertInfusion.get(), ModItems.DustIron.get(), ModItems.DustLead.get(), ModItems.DustLumum.get(), ModItems.DustMagnesium.get(),
				ModItems.DustObsidian.get(), ModItems.DustPlatinum.get(), ModItems.DustRedstoneAlloy.get(), ModItems.DustRuby.get(), ModItems.DustSaltpeter.get(),
				ModItems.DustSapphire.get(), ModItems.DustSilver.get(), ModItems.DustStatic.get(), ModItems.DustSulfur.get(), ModItems.DustTin.get(), ModItems.DustTungsten.get(),
				ModItems.DustZinc.get(), ModItems.DustUrnaium.get(), ModItems.DustCoalSmall.get(), ModItems.DustCharcoalSmall.get());

		tag(Tags.Items.RAW_MATERIALS).add(ModItems.RawAluminum.get(), ModItems.RawEnergized.get(), ModItems.RawLead.get(), ModItems.RawLumum.get(), ModItems.RawMagnesium.get(),
				ModItems.RawPlatinum.get(), ModItems.RawRustyIron.get(), ModItems.RawSilver.get(), ModItems.RawStatic.get(), ModItems.RawTin.get(), ModItems.RawTungsten.get(),
				ModItems.RawZinc.get(), ModItems.RawUranium.get());

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

		tag(ModItemTags.LIGHTBULB).add(ModItems.LightBulbWhite.get()).add(ModItems.LightBulbLightGray.get()).add(ModItems.LightBulbGray.get()).add(ModItems.LightBulbBlack.get())
				.add(ModItems.LightBulbBrown.get()).add(ModItems.LightBulbRed.get()).add(ModItems.LightBulbOrange.get()).add(ModItems.LightBulbYellow.get())
				.add(ModItems.LightBulbLime.get()).add(ModItems.LightBulbGreen.get()).add(ModItems.LightBulbCyan.get()).add(ModItems.LightBulbLightBlue.get())
				.add(ModItems.LightBulbBlue.get()).add(ModItems.LightBulbPurple.get()).add(ModItems.LightBulbMagenta.get()).add(ModItems.LightBulbPink.get());

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

		tag(ModItemTags.HEATED_INGOTS).add(ModItems.IngotTinHeated.get(), ModItems.IngotZincHeated.get(), ModItems.IngotSilverHeated.get(), ModItems.IngotLeadHeated.get(),
				ModItems.IngotTungstenHeated.get(), ModItems.IngotMagnesiumHeated.get(), ModItems.IngotPlatinumHeated.get(), ModItems.IngotAluminumHeated.get(),
				ModItems.IngotStaticHeated.get(), ModItems.IngotEnergizedHeated.get(), ModItems.IngotLumumHeated.get(), ModItems.IngotInertInfusionHeated.get(),
				ModItems.IngotRedstoneAlloyHeated.get(), ModItems.IngotBrassHeated.get(), ModItems.IngotBronzeHeated.get(), ModItems.IngotIronHeated.get(),
				ModItems.IngotCopperHeated.get(), ModItems.IngotGoldHeated.get());

		tag(ModItemTags.WIRE_COILS).add(ModItems.WireCoilCopper.get(), ModItems.WireCoilBrass.get(), ModItems.WireCoilStatic.get(), ModItems.WireCoilEnergized.get(),
				ModItems.WireCoilLumum.get(), ModItems.WireCoilInsulatedCopper.get(), ModItems.WireCoilInsulatedBrass.get(), ModItems.WireCoilInsulatedStatic.get(),
				ModItems.WireCoilInsulatedEnergized.get(), ModItems.WireCoilInsulatedLumum.get(), ModItems.WireCoilDigistore.get());

		tag(ModItemTags.STATIC_PLATES).add(ModItems.PlateStatic.get());
		tag(ModItemTags.ENERGIZED_PLATES).add(ModItems.PlateEnergized.get());
		tag(ModItemTags.LUMUM_PLATES).add(ModItems.PlateLumum.get());
		tag(ModItemTags.INERT_INFUSION_PLATES).add(ModItems.PlateInertInfusion.get());
		tag(ModItemTags.REDSTONE_ALLOY_PLATES).add(ModItems.PlateRedstoneAlloy.get());
	}
}
