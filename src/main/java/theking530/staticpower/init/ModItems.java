package theking530.staticpower.init;

import net.minecraft.item.ItemTier;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.cables.attachments.digistore.DigistoreLight;
import theking530.staticpower.cables.attachments.digistore.DigistoreScreen;
import theking530.staticpower.cables.attachments.digistore.craftinginterface.DigistoreCraftingInterfaceAttachment;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.exporter.DigistoreExporterAttachment;
import theking530.staticpower.cables.attachments.digistore.importer.DigistoreImporterAttachment;
import theking530.staticpower.cables.attachments.digistore.iobus.DigistoreIOBusAttachment;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder;
import theking530.staticpower.cables.attachments.digistore.regulator.DigistoreRegulatorAttachment;
import theking530.staticpower.cables.attachments.digistore.terminal.DigistoreTerminal;
import theking530.staticpower.cables.attachments.extractor.ExtractorAttachment;
import theking530.staticpower.cables.attachments.filter.FilterAttachment;
import theking530.staticpower.cables.attachments.retirever.RetrieverAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.BatteryPack;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.items.DigistorePatternCard;
import theking530.staticpower.items.JuiceBottleItem;
import theking530.staticpower.items.MilkBottleItem;
import theking530.staticpower.items.PortableBattery;
import theking530.staticpower.items.StaticPowerBurnableItem;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.items.crops.DepletedCrop;
import theking530.staticpower.items.crops.StaticPlantCrop;
import theking530.staticpower.items.crops.StaticPlantSeeds;
import theking530.staticpower.items.fluidcapsule.FluidCapsule;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.tools.CableNetworkAnalyzer;
import theking530.staticpower.items.tools.CoverSaw;
import theking530.staticpower.items.tools.ElectricSolderingIron;
import theking530.staticpower.items.tools.Magnet;
import theking530.staticpower.items.tools.MetalHammer;
import theking530.staticpower.items.tools.SolderingIron;
import theking530.staticpower.items.tools.StaticWrench;
import theking530.staticpower.items.tools.WireCutters;
import theking530.staticpower.items.tools.chainsaw.Chainsaw;
import theking530.staticpower.items.tools.chainsaw.ChainsawBlade;
import theking530.staticpower.items.tools.miningdrill.DrillBit;
import theking530.staticpower.items.tools.miningdrill.MiningDrill;
import theking530.staticpower.items.tools.sword.Blade;

public class ModItems {
	public static StaticPowerItem DistilleryGrain;

	public static StaticPowerItem WheatFlour;
	public static StaticPowerItem PotatoFlour;
	public static StaticPowerItem PotatoBread;
	public static StaticPowerItem ApplePie;
	public static StaticPowerItem StaticPie;
	public static StaticPowerItem EnergizedPie;
	public static StaticPowerItem LumumPie;

	public static StaticPowerItem MoldBlank;
	public static StaticPowerItem MoldPlate;
	public static StaticPowerItem MoldWire;
	public static StaticPowerItem MoldGear;
	public static StaticPowerItem MoldBlade;

	public static StaticPowerItem IngotCopper;
	public static StaticPowerItem IngotTin;
	public static StaticPowerItem IngotZinc;
	public static StaticPowerItem IngotSilver;
	public static StaticPowerItem IngotLead;
	public static StaticPowerItem IngotTungsten;
	public static StaticPowerItem IngotMagnesium;
	public static StaticPowerItem IngotPlatinum;
	public static StaticPowerItem IngotAluminium;
	public static StaticPowerItem IngotStatic;
	public static StaticPowerItem IngotEnergized;
	public static StaticPowerItem IngotLumum;
	public static StaticPowerItem IngotInertInfusion;
	public static StaticPowerItem IngotRedstoneAlloy;
	public static StaticPowerItem IngotBrass;
	public static StaticPowerItem IngotBronze;

	public static StaticPowerItem NuggetCopper;
	public static StaticPowerItem NuggetTin;
	public static StaticPowerItem NuggetZinc;
	public static StaticPowerItem NuggetSilver;
	public static StaticPowerItem NuggetLead;
	public static StaticPowerItem NuggetTungsten;
	public static StaticPowerItem NuggetMagnesium;
	public static StaticPowerItem NuggetPlatinum;
	public static StaticPowerItem NuggetAluminium;
	public static StaticPowerItem NuggetStatic;
	public static StaticPowerItem NuggetEnergized;
	public static StaticPowerItem NuggetLumum;
	public static StaticPowerItem NuggetInertInfusion;
	public static StaticPowerItem NuggetRedstoneAlloy;
	public static StaticPowerItem NuggetBrass;
	public static StaticPowerItem NuggetBronze;

	public static StaticPowerItem PlateIron;
	public static StaticPowerItem PlateGold;
	public static StaticPowerItem PlateCopper;
	public static StaticPowerItem PlateTin;
	public static StaticPowerItem PlateZinc;
	public static StaticPowerItem PlateSilver;
	public static StaticPowerItem PlateLead;
	public static StaticPowerItem PlateTungsten;
	public static StaticPowerItem PlateMagnesium;
	public static StaticPowerItem PlatePlatinum;
	public static StaticPowerItem PlateAluminium;
	public static StaticPowerItem PlateStatic;
	public static StaticPowerItem PlateEnergized;
	public static StaticPowerItem PlateLumum;
	public static StaticPowerItem PlateInertInfusion;
	public static StaticPowerItem PlateRedstoneAlloy;
	public static StaticPowerItem PlateBrass;
	public static StaticPowerItem PlateBronze;

	public static StaticPowerItem GearCopper;
	public static StaticPowerItem GearTin;
	public static StaticPowerItem GearZinc;
	public static StaticPowerItem GearSilver;
	public static StaticPowerItem GearLead;
	public static StaticPowerItem GearTungsten;
	public static StaticPowerItem GearMagnesium;
	public static StaticPowerItem GearPlatinum;
	public static StaticPowerItem GearAluminium;
	public static StaticPowerItem GearStatic;
	public static StaticPowerItem GearEnergized;
	public static StaticPowerItem GearLumum;
	public static StaticPowerItem GearInertInfusion;
	public static StaticPowerItem GearRedstoneAlloy;
	public static StaticPowerItem GearIron;
	public static StaticPowerItem GearGold;
	public static StaticPowerItem GearBrass;
	public static StaticPowerItem GearBronze;

	public static StaticPowerItem DustCopper;
	public static StaticPowerItem DustTin;
	public static StaticPowerItem DustZinc;
	public static StaticPowerItem DustSilver;
	public static StaticPowerItem DustLead;
	public static StaticPowerItem DustTungsten;
	public static StaticPowerItem DustMagnesium;
	public static StaticPowerItem DustPlatinum;
	public static StaticPowerItem DustAluminium;
	public static StaticPowerItem DustStatic;
	public static StaticPowerItem DustEnergized;
	public static StaticPowerItem DustLumum;
	public static StaticPowerItem DustInertInfusion;
	public static StaticPowerItem DustRedstoneAlloy;
	public static StaticPowerItem DustBrass;
	public static StaticPowerItem DustBronze;

	public static StaticPowerItem DustCoal;
	public static StaticPowerItem DustObsidian;
	public static StaticPowerItem DustGold;
	public static StaticPowerItem DustIron;
	public static StaticPowerItem DustRuby;
	public static StaticPowerItem DustSapphire;
	public static StaticPowerItem DustEmerald;
	public static StaticPowerItem DustDiamond;
	public static StaticPowerItem DustSulfur;
	public static StaticPowerItem DustSaltpeter;
	public static StaticPowerItem DustCharcoal;

	public static StaticPowerItem GemRuby;
	public static StaticPowerItem GemSapphire;

	public static StaticPowerItem RawSilicon;
	public static StaticPowerItem Silicon;
	public static StaticPowerItem StaticDopedSilicon;
	public static StaticPowerItem EnergizedDopedSilicon;
	public static StaticPowerItem LumumDopedSilicon;
	public static StaticPowerItem CrystalStatic;
	public static StaticPowerItem CrystalEnergized;
	public static StaticPowerItem CrystalLumum;
	public static StaticPowerItem DustWood;
	public static StaticPowerItem LatexChunk;
	public static StaticPowerItem RubberWoodBark;
	public static StaticPowerItem PortableSmeltingCore;

	public static StaticPowerItem BasicCard;
	public static StaticPowerItem AdvancedCard;
	public static StaticPowerItem StaticCard;
	public static StaticPowerItem EnergizedCard;
	public static StaticPowerItem LumumCard;

	public static StaticPowerItem BasicProcessor;
	public static StaticPowerItem AdvancedProcessor;
	public static StaticPowerItem StaticProcessor;
	public static StaticPowerItem EnergizedProcessor;
	public static StaticPowerItem LumumProcessor;

	public static StaticPowerItem RubberBar;
	public static StaticPowerItem RubberSheet;
	public static StaticPowerItem IOPort;
	public static StaticPowerItem WireCopper;
	public static StaticPowerItem WireTin;
	public static StaticPowerItem WireSilver;
	public static StaticPowerItem WireGold;
	public static StaticPowerItem WirePlatinum;
	public static StaticPowerItem WireAluminium;
	public static StaticPowerItem CoilCopper;
	public static StaticPowerItem CoilSilver;
	public static StaticPowerItem CoilGold;
	public static StaticPowerItem CoilPlatinum;
	public static StaticPowerItem CoilAluminium;
	public static StaticPowerItem CoilTin;

	public static DrillBit IronDrillBit;
	public static DrillBit BronzeDrillBit;
	public static DrillBit AdvancedDrillBit;
	public static DrillBit TungstenDrillBit;
	public static DrillBit StaticDrillBit;
	public static DrillBit EnergizedDrillBit;
	public static DrillBit LumumDrillBit;
	public static DrillBit CreativeDrillBit;

	public static Blade IronBlade;
	public static Blade BronzeBlade;
	public static Blade AdvancedBlade;
	public static Blade TungstenBlade;
	public static Blade StaticBlade;
	public static Blade EnergizedBlade;
	public static Blade LumumBlade;
	public static Blade CreativeBlade;

	public static ChainsawBlade IronChainsawBlade;
	public static ChainsawBlade BronzeChainsawBlade;
	public static ChainsawBlade AdvancedChainsawBlade;
	public static ChainsawBlade TungstenChainsawBlade;
	public static ChainsawBlade StaticChainsawBlade;
	public static ChainsawBlade EnergizedChainsawBlade;
	public static ChainsawBlade LumumChainsawBlade;
	public static ChainsawBlade CreativeChainsawBlade;

	public static StaticPowerItem MemoryChip;
	public static StaticPowerItem LogicGatePowerSync;
	public static StaticPowerItem InvertedLogicGatePowerSync;
	public static StaticPowerItem Servo;
	public static StaticPowerItem Diode;
	public static StaticPowerItem Transistor;
	public static StaticPowerItem InternalClock;
	public static StaticPowerItem Motor;
	public static StaticPowerItem Plug;
	public static StaticPowerItem DigistoreCore;
	public static StaticPowerItem GrinderComponent;

	public static FluidCapsule IronFluidCapsule;
	public static FluidCapsule BasicFluidCapsule;
	public static FluidCapsule AdvancedFluidCapsule;
	public static FluidCapsule StaticFluidCapsule;
	public static FluidCapsule EnergizedFluidCapsule;
	public static FluidCapsule LumumFluidCapsule;
	public static FluidCapsule CreativeFluidCapsule;

	public static StaticPowerItem BasicUpgradePlate;
	public static StaticPowerItem AdvancedUpgradePlate;
	public static StaticPowerItem StaticUpgradePlate;
	public static StaticPowerItem EnergizedUpgradePlate;
	public static StaticPowerItem LumumUpgradePlate;

	public static PortableBattery BasicPortableBattery;
	public static PortableBattery AdvancedPortableBattery;
	public static PortableBattery StaticPortableBattery;
	public static PortableBattery EnergizedPortableBattery;
	public static PortableBattery LumumPortableBattery;
	public static PortableBattery CreativePortableBattery;

	public static BatteryPack BasicBatteryPack;
	public static BatteryPack AdvancedBatteryPack;
	public static BatteryPack StaticBatteryPack;
	public static BatteryPack EnergizedBatteryPack;
	public static BatteryPack LumumBatteryPack;
	public static BatteryPack CreativeBatteryPack;

	public static StaticPlantSeeds StaticSeeds;
	public static StaticPlantSeeds EnergizedSeeds;
	public static StaticPlantSeeds LumumSeeds;

	public static StaticPlantCrop StaticCrop;
	public static StaticPlantCrop EnergizedCrop;
	public static StaticPlantCrop LumumCrop;
	public static DepletedCrop DepletedCrop;

	public static MetalHammer IronMetalHammer;
	public static MetalHammer ZincMetalHammer;
	public static MetalHammer BronzeMetalHammer;
	public static MetalHammer TungstenMetalHammer;
	public static MetalHammer CreativeMetalHammer;

	public static WireCutters IronWireCutters;
	public static WireCutters ZincWireCutters;
	public static WireCutters BronzeWireCutters;
	public static WireCutters TungstenWireCutters;
	public static WireCutters CreativeWireCutters;

	public static SolderingIron SolderingIron;
	public static ElectricSolderingIron ElectringSolderingIron;

	public static MiningDrill BasicMiningDrill;
	public static MiningDrill AdvancedMiningDrill;
	public static MiningDrill StaticMiningDrill;
	public static MiningDrill EnergizedMiningDrill;
	public static MiningDrill LumumMiningDrill;

	public static Chainsaw BasicChainsaw;
	public static Chainsaw AdvancedChainsaw;
	public static Chainsaw StaticChainsaw;
	public static Chainsaw EnergizedChainsaw;
	public static Chainsaw LumumChainsaw;

	public static StaticWrench Wrench;
	public static StaticWrench StaticWrench;
	public static StaticWrench EnergizedWrench;
	public static StaticWrench LumumWrench;

	public static Magnet BasicMagnet;
	public static Magnet AdvancedMagnet;
	public static Magnet StaticMagnet;
	public static Magnet EnergizedMagnet;
	public static Magnet LumumMagnet;

	public static CableNetworkAnalyzer CableNetworkAnalyzer;
	public static CoverSaw IronCoverSaw;
	public static CoverSaw TungstenCoverSaw;
	public static CoverSaw DiamondCoverSaw;
	public static CoverSaw RubyCoverSaw;
	public static CoverSaw SapphireCoverSaw;

	public static ItemFilter BasicFilter;
	public static ItemFilter AdvancedFilter;
	public static ItemFilter StaticFilter;
	public static ItemFilter EnergizedFilter;
	public static ItemFilter LumumFilter;

	public static ExtractorAttachment BasicExtractorAttachment;
	public static ExtractorAttachment AdvancedExtractorAttachment;
	public static ExtractorAttachment StaticExtractorAttachment;
	public static ExtractorAttachment EnergizedExtractorAttachment;
	public static ExtractorAttachment LumumExtractorAttachment;

	public static FilterAttachment BasicFilterAttachment;
	public static FilterAttachment AdvancedFilterAttachment;
	public static FilterAttachment StaticFilterAttachment;
	public static FilterAttachment EnergizedFilterAttachment;
	public static FilterAttachment LumumFilterAttachment;

	public static RetrieverAttachment BasicRetrieverAttachment;
	public static RetrieverAttachment AdvancedRetrieverAttachment;
	public static RetrieverAttachment StaticRetrieverAttachment;
	public static RetrieverAttachment EnergizedRetrieverAttachment;
	public static RetrieverAttachment LumumRetrieverAttachment;

	public static DigistoreExporterAttachment ExporterAttachment;
	public static DigistoreImporterAttachment ImporterAttachment;
	public static DigistoreIOBusAttachment IOBusAttachment;
	public static DigistoreRegulatorAttachment RegulatorAttachment;
	public static DigistoreTerminal DigistoreTerminalAttachment;
	public static DigistoreCraftingTerminal DigistoreCraftingTerminalAttachment;
	public static DigistorePatternEncoder DigistorePatternEncoderAttachment;
	public static DigistoreCraftingInterfaceAttachment DigistoreCraftingInterfaceAttachment;
	public static DigistoreScreen DigistoreScreenAttachment;
	public static DigistoreLight DigistoreLightAttachment;

	public static DigistorePatternCard PatternCard;

	public static DigistoreCard BasicDigistoreCard;
	public static DigistoreCard AdvancedDigistoreCard;
	public static DigistoreCard StaticDigistoreCard;
	public static DigistoreCard EnergizedDigistoreCard;
	public static DigistoreCard LumumDigistoreCard;
	public static DigistoreCard CreativeDigistoreCard;

	public static DigistoreMonoCard BasicSingularDigistoreCard;
	public static DigistoreMonoCard AdvancedSingularDigistoreCard;
	public static DigistoreMonoCard StaticSingularDigistoreCard;
	public static DigistoreMonoCard EnergizedSingularDigistoreCard;
	public static DigistoreMonoCard LumumSingularDigistoreCard;
	public static DigistoreMonoCard CreativeSingularDigistoreCard;

	public static CableCover CableCover;

	public static MilkBottleItem MilkBottle;
	public static JuiceBottleItem AppleJuiceBottle;
	public static JuiceBottleItem CarrotJuiceBottle;
	public static JuiceBottleItem PumpkinJuiceBottle;
	public static JuiceBottleItem MelonJuiceBottle;
	public static JuiceBottleItem BeetJuiceBottle;
	public static JuiceBottleItem BerryJuiceBottle;

	public static void init() {
		// Misc.
		StaticPowerRegistry.preRegisterItem(DistilleryGrain = new StaticPowerItem("distillery_grain"));

		// Food
		StaticPowerRegistry.preRegisterItem(WheatFlour = new StaticPowerItem("flour_wheat"));
		StaticPowerRegistry.preRegisterItem(PotatoFlour = new StaticPowerItem("flour_potato"));
		StaticPowerRegistry.preRegisterItem(PotatoBread = new StaticPowerItem("bread_potato"));
		StaticPowerRegistry.preRegisterItem(ApplePie = new StaticPowerItem("pie_apple"));
		StaticPowerRegistry.preRegisterItem(StaticPie = new StaticPowerItem("pie_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedPie = new StaticPowerItem("pie_energized"));
		StaticPowerRegistry.preRegisterItem(LumumPie = new StaticPowerItem("pie_lumum"));

		// Molds
		StaticPowerRegistry.preRegisterItem(MoldBlank = new StaticPowerItem("mold_blank"));
		StaticPowerRegistry.preRegisterItem(MoldPlate = new StaticPowerItem("mold_plate"));
		StaticPowerRegistry.preRegisterItem(MoldWire = new StaticPowerItem("mold_wire"));
		StaticPowerRegistry.preRegisterItem(MoldGear = new StaticPowerItem("mold_gear"));
		StaticPowerRegistry.preRegisterItem(MoldBlade = new StaticPowerItem("mold_blade"));

		// Plants
		StaticPowerRegistry.preRegisterItem(StaticSeeds = new StaticPlantSeeds("seed_static", ModBlocks.StaticPlant));
		StaticPowerRegistry.preRegisterItem(EnergizedSeeds = new StaticPlantSeeds("seed_energized", ModBlocks.EnergizedPlant));
		StaticPowerRegistry.preRegisterItem(LumumSeeds = new StaticPlantSeeds("seed_lumum", ModBlocks.LumumPlant));

		StaticPowerRegistry.preRegisterItem(StaticCrop = new StaticPlantCrop("crop_static", ModFoods.STATIC_CROP));
		StaticPowerRegistry.preRegisterItem(EnergizedCrop = new StaticPlantCrop("crop_energized", ModFoods.ENERGIZED_CROP));
		StaticPowerRegistry.preRegisterItem(LumumCrop = new StaticPlantCrop("crop_lumum", ModFoods.LUMUM_CROP));
		StaticPowerRegistry.preRegisterItem(DepletedCrop = new DepletedCrop("crop_depleted"));

		// Cards
		StaticPowerRegistry.preRegisterItem(BasicProcessor = new StaticPowerItem("processor_basic"));
		StaticPowerRegistry.preRegisterItem(AdvancedProcessor = new StaticPowerItem("processor_advanced"));
		StaticPowerRegistry.preRegisterItem(StaticProcessor = new StaticPowerItem("processor_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedProcessor = new StaticPowerItem("processor_energized"));
		StaticPowerRegistry.preRegisterItem(LumumProcessor = new StaticPowerItem("processor_lumum"));

		// Processors
		StaticPowerRegistry.preRegisterItem(BasicCard = new StaticPowerItem("card_basic"));
		StaticPowerRegistry.preRegisterItem(AdvancedCard = new StaticPowerItem("card_advanced"));
		StaticPowerRegistry.preRegisterItem(StaticCard = new StaticPowerItem("card_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedCard = new StaticPowerItem("card_energized"));
		StaticPowerRegistry.preRegisterItem(LumumCard = new StaticPowerItem("card_lumum"));

		// Batteries
		StaticPowerRegistry.preRegisterItem(BasicPortableBattery = new PortableBattery("portable_battery_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(AdvancedPortableBattery = new PortableBattery("portable_battery_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(StaticPortableBattery = new PortableBattery("portable_battery_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedPortableBattery = new PortableBattery("portable_battery_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumPortableBattery = new PortableBattery("portable_battery_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativePortableBattery = new PortableBattery("portable_battery_creative", StaticPowerTiers.CREATIVE));

		// Battery packs.
		StaticPowerRegistry.preRegisterItem(BasicBatteryPack = new BatteryPack("battery_pack_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(AdvancedBatteryPack = new BatteryPack("battery_pack_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(StaticBatteryPack = new BatteryPack("battery_pack_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedBatteryPack = new BatteryPack("battery_pack_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumBatteryPack = new BatteryPack("battery_pack_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeBatteryPack = new BatteryPack("battery_pack_creative", StaticPowerTiers.CREATIVE));

		// Tools
		StaticPowerRegistry.preRegisterItem(IronMetalHammer = new MetalHammer("metal_hammer_iron", 100));
		StaticPowerRegistry.preRegisterItem(ZincMetalHammer = new MetalHammer("metal_hammer_zinc", 500));
		StaticPowerRegistry.preRegisterItem(BronzeMetalHammer = new MetalHammer("metal_hammer_bronze", 1000));
		StaticPowerRegistry.preRegisterItem(TungstenMetalHammer = new MetalHammer("metal_hammer_tungsten", 5000));
		StaticPowerRegistry.preRegisterItem(CreativeMetalHammer = new MetalHammer("metal_hammer_creative", Integer.MAX_VALUE));

		StaticPowerRegistry.preRegisterItem(IronWireCutters = new WireCutters("wire_cutters_iron", 100));
		StaticPowerRegistry.preRegisterItem(ZincWireCutters = new WireCutters("wire_cutters_zinc", 500));
		StaticPowerRegistry.preRegisterItem(BronzeWireCutters = new WireCutters("wire_cutters_bronze", 1000));
		StaticPowerRegistry.preRegisterItem(TungstenWireCutters = new WireCutters("wire_cutters_tungsten", 5000));
		StaticPowerRegistry.preRegisterItem(CreativeWireCutters = new WireCutters("wire_cutters_creative", Integer.MAX_VALUE));

		StaticPowerRegistry.preRegisterItem(BasicMiningDrill = new MiningDrill("mining_drill_basic", 5.0f, 5.0f, StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(AdvancedMiningDrill = new MiningDrill("mining_drill_advanced", 5.0f, 5.0f, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(StaticMiningDrill = new MiningDrill("mining_drill_static", 5.0f, 5.0f, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedMiningDrill = new MiningDrill("mining_drill_energized", 5.0f, 5.0f, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumMiningDrill = new MiningDrill("mining_drill_lumum", 5.0f, 5.0f, StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(BasicChainsaw = new Chainsaw("chainsaw_basic", 5.0f, 5.0f, StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(AdvancedChainsaw = new Chainsaw("chainsaw_advanced", 5.0f, 5.0f, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(StaticChainsaw = new Chainsaw("chainsaw_static", 5.0f, 5.0f, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedChainsaw = new Chainsaw("chainsaw_energized", 5.0f, 5.0f, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumChainsaw = new Chainsaw("chainsaw_lumum", 5.0f, 5.0f, StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(SolderingIron = new SolderingIron("soldering_iron", 100));
		StaticPowerRegistry.preRegisterItem(ElectringSolderingIron = new ElectricSolderingIron("soldering_iron_electric", 1000));

		StaticPowerRegistry.preRegisterItem(Wrench = new StaticWrench("wrench"));
		StaticPowerRegistry.preRegisterItem(StaticWrench = new StaticWrench("wrench_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedWrench = new StaticWrench("wrench_energized"));
		StaticPowerRegistry.preRegisterItem(LumumWrench = new StaticWrench("wrench_lumum"));

		StaticPowerRegistry.preRegisterItem(BasicMagnet = new Magnet("magnet_basic", 1000, 2));
		StaticPowerRegistry.preRegisterItem(AdvancedMagnet = new Magnet("magnet_advanced", 2000, 3));
		StaticPowerRegistry.preRegisterItem(StaticMagnet = new Magnet("magnet_static", 5000, 4));
		StaticPowerRegistry.preRegisterItem(EnergizedMagnet = new Magnet("magnet_energized", 10000, 6));
		StaticPowerRegistry.preRegisterItem(LumumMagnet = new Magnet("magnet_lumum", 20000, 8));

		StaticPowerRegistry.preRegisterItem(IronCoverSaw = new CoverSaw("saw_iron", 100));
		StaticPowerRegistry.preRegisterItem(TungstenCoverSaw = new CoverSaw("saw_tungsten", 2500));
		StaticPowerRegistry.preRegisterItem(DiamondCoverSaw = new CoverSaw("saw_diamond", 250));
		StaticPowerRegistry.preRegisterItem(RubyCoverSaw = new CoverSaw("saw_ruby", 500));
		StaticPowerRegistry.preRegisterItem(SapphireCoverSaw = new CoverSaw("saw_sapphire", 1000));

		StaticPowerRegistry.preRegisterItem(CableNetworkAnalyzer = new CableNetworkAnalyzer("cable_network_analyzer"));

		// Filters
		StaticPowerRegistry.preRegisterItem(BasicFilter = new ItemFilter("filter_item_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(AdvancedFilter = new ItemFilter("filter_item_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(StaticFilter = new ItemFilter("filter_item_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedFilter = new ItemFilter("filter_item_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumFilter = new ItemFilter("filter_item_lumum", StaticPowerTiers.LUMUM));

		// Components
		StaticPowerRegistry.preRegisterItem(MemoryChip = new StaticPowerItem("memory_chip"));
		StaticPowerRegistry.preRegisterItem(LogicGatePowerSync = new StaticPowerItem("logic_gate_power_sync"));
		StaticPowerRegistry.preRegisterItem(InvertedLogicGatePowerSync = new StaticPowerItem("inverted_logic_gate_power_sync"));
		StaticPowerRegistry.preRegisterItem(Servo = new StaticPowerItem("servo"));
		StaticPowerRegistry.preRegisterItem(Diode = new StaticPowerItem("diode"));
		StaticPowerRegistry.preRegisterItem(Transistor = new StaticPowerItem("transistor"));
		StaticPowerRegistry.preRegisterItem(InternalClock = new StaticPowerItem("internal_clock"));
		StaticPowerRegistry.preRegisterItem(IOPort = new StaticPowerItem("io_port"));
		StaticPowerRegistry.preRegisterItem(Motor = new StaticPowerItem("motor"));
		StaticPowerRegistry.preRegisterItem(Plug = new StaticPowerItem("plug"));
		StaticPowerRegistry.preRegisterItem(DigistoreCore = new StaticPowerItem("digistore_core"));
		StaticPowerRegistry.preRegisterItem(GrinderComponent = new StaticPowerItem("grinder_component"));
		StaticPowerRegistry.preRegisterItem(PortableSmeltingCore = new StaticPowerItem("portable_smelting_core"));

		StaticPowerRegistry.preRegisterItem(IronFluidCapsule = new FluidCapsule("fluid_capsule_iron", StaticPowerTiers.IRON));
		StaticPowerRegistry.preRegisterItem(BasicFluidCapsule = new FluidCapsule("fluid_capsule_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(AdvancedFluidCapsule = new FluidCapsule("fluid_capsule_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(StaticFluidCapsule = new FluidCapsule("fluid_capsule_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedFluidCapsule = new FluidCapsule("fluid_capsule_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumFluidCapsule = new FluidCapsule("fluid_capsule_lumum", StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeFluidCapsule = new FluidCapsule("fluid_capsule_creative", StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterItem(WireCopper = new StaticPowerItem("wire_copper"));
		StaticPowerRegistry.preRegisterItem(WireTin = new StaticPowerItem("wire_tin"));
		StaticPowerRegistry.preRegisterItem(WireSilver = new StaticPowerItem("wire_silver"));
		StaticPowerRegistry.preRegisterItem(WireGold = new StaticPowerItem("wire_gold"));
		StaticPowerRegistry.preRegisterItem(WirePlatinum = new StaticPowerItem("wire_platinum"));
		StaticPowerRegistry.preRegisterItem(WireAluminium = new StaticPowerItem("wire_aluminium"));
		StaticPowerRegistry.preRegisterItem(CoilCopper = new StaticPowerItem("coil_copper"));
		StaticPowerRegistry.preRegisterItem(CoilSilver = new StaticPowerItem("coil_silver"));
		StaticPowerRegistry.preRegisterItem(CoilGold = new StaticPowerItem("coil_gold"));
		StaticPowerRegistry.preRegisterItem(CoilPlatinum = new StaticPowerItem("coil_platinum"));
		StaticPowerRegistry.preRegisterItem(CoilAluminium = new StaticPowerItem("coil_aluminium"));
		StaticPowerRegistry.preRegisterItem(CoilTin = new StaticPowerItem("coil_tin"));

		// Materials
		StaticPowerRegistry.preRegisterItem(GemRuby = new StaticPowerItem("gem_ruby"));
		StaticPowerRegistry.preRegisterItem(GemSapphire = new StaticPowerItem("gem_sapphire"));
		StaticPowerRegistry.preRegisterItem(RubberBar = new StaticPowerItem("rubber_bar"));
		StaticPowerRegistry.preRegisterItem(RubberSheet = new StaticPowerItem("rubber_sheet"));
		StaticPowerRegistry.preRegisterItem(RawSilicon = new StaticPowerItem("raw_silicon"));
		StaticPowerRegistry.preRegisterItem(Silicon = new StaticPowerItem("silicon"));
		StaticPowerRegistry.preRegisterItem(StaticDopedSilicon = new StaticPowerItem("silicon_doped_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedDopedSilicon = new StaticPowerItem("silicon_doped_energized"));
		StaticPowerRegistry.preRegisterItem(LumumDopedSilicon = new StaticPowerItem("silicon_doped_lumum"));
		StaticPowerRegistry.preRegisterItem(CrystalStatic = new StaticPowerItem("crystal_static"));
		StaticPowerRegistry.preRegisterItem(CrystalEnergized = new StaticPowerItem("crystal_energized"));
		StaticPowerRegistry.preRegisterItem(CrystalLumum = new StaticPowerItem("crystal_lumum"));
		StaticPowerRegistry.preRegisterItem(LatexChunk = new StaticPowerItem("latex_chunk"));
		StaticPowerRegistry.preRegisterItem(RubberWoodBark = new StaticPowerItem("rubber_wood_bark"));

		// Plates
		StaticPowerRegistry.preRegisterItem(PlateIron = new StaticPowerItem("plate_iron"));
		StaticPowerRegistry.preRegisterItem(PlateGold = new StaticPowerItem("plate_gold"));
		StaticPowerRegistry.preRegisterItem(PlateCopper = new StaticPowerItem("plate_copper"));
		StaticPowerRegistry.preRegisterItem(PlateTin = new StaticPowerItem("plate_tin"));
		StaticPowerRegistry.preRegisterItem(PlateZinc = new StaticPowerItem("plate_zinc"));
		StaticPowerRegistry.preRegisterItem(PlateSilver = new StaticPowerItem("plate_silver"));
		StaticPowerRegistry.preRegisterItem(PlateLead = new StaticPowerItem("plate_lead"));
		StaticPowerRegistry.preRegisterItem(PlateTungsten = new StaticPowerItem("plate_tungsten"));
		StaticPowerRegistry.preRegisterItem(PlateMagnesium = new StaticPowerItem("plate_magnesium"));
		StaticPowerRegistry.preRegisterItem(PlatePlatinum = new StaticPowerItem("plate_platinum"));
		StaticPowerRegistry.preRegisterItem(PlateAluminium = new StaticPowerItem("plate_aluminium"));
		StaticPowerRegistry.preRegisterItem(PlateStatic = new StaticPowerItem("plate_static"));
		StaticPowerRegistry.preRegisterItem(PlateEnergized = new StaticPowerItem("plate_energized"));
		StaticPowerRegistry.preRegisterItem(PlateLumum = new StaticPowerItem("plate_lumum"));
		StaticPowerRegistry.preRegisterItem(PlateInertInfusion = new StaticPowerItem("plate_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(PlateRedstoneAlloy = new StaticPowerItem("plate_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(PlateBrass = new StaticPowerItem("plate_brass"));
		StaticPowerRegistry.preRegisterItem(PlateBronze = new StaticPowerItem("plate_bronze"));

		// Gears
		StaticPowerRegistry.preRegisterItem(GearCopper = new StaticPowerItem("gear_copper"));
		StaticPowerRegistry.preRegisterItem(GearTin = new StaticPowerItem("gear_tin"));
		StaticPowerRegistry.preRegisterItem(GearZinc = new StaticPowerItem("gear_zinc"));
		StaticPowerRegistry.preRegisterItem(GearSilver = new StaticPowerItem("gear_silver"));
		StaticPowerRegistry.preRegisterItem(GearLead = new StaticPowerItem("gear_lead"));
		StaticPowerRegistry.preRegisterItem(GearTungsten = new StaticPowerItem("gear_tungsten"));
		StaticPowerRegistry.preRegisterItem(GearMagnesium = new StaticPowerItem("gear_magnesium"));
		StaticPowerRegistry.preRegisterItem(GearPlatinum = new StaticPowerItem("gear_platinum"));
		StaticPowerRegistry.preRegisterItem(GearAluminium = new StaticPowerItem("gear_aluminium"));
		StaticPowerRegistry.preRegisterItem(GearStatic = new StaticPowerItem("gear_static"));
		StaticPowerRegistry.preRegisterItem(GearEnergized = new StaticPowerItem("gear_energized"));
		StaticPowerRegistry.preRegisterItem(GearLumum = new StaticPowerItem("gear_lumum"));
		StaticPowerRegistry.preRegisterItem(GearInertInfusion = new StaticPowerItem("gear_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(GearRedstoneAlloy = new StaticPowerItem("gear_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(GearIron = new StaticPowerItem("gear_iron"));
		StaticPowerRegistry.preRegisterItem(GearGold = new StaticPowerItem("gear_gold"));
		StaticPowerRegistry.preRegisterItem(GearBrass = new StaticPowerItem("gear_brass"));
		StaticPowerRegistry.preRegisterItem(GearBronze = new StaticPowerItem("gear_bronze"));

		// Dusts
		StaticPowerRegistry.preRegisterItem(DustWood = new StaticPowerBurnableItem("dust_wood", 250));
		StaticPowerRegistry.preRegisterItem(DustCopper = new StaticPowerItem("dust_copper"));
		StaticPowerRegistry.preRegisterItem(DustTin = new StaticPowerItem("dust_tin"));
		StaticPowerRegistry.preRegisterItem(DustZinc = new StaticPowerItem("dust_zinc"));
		StaticPowerRegistry.preRegisterItem(DustSilver = new StaticPowerItem("dust_silver"));
		StaticPowerRegistry.preRegisterItem(DustLead = new StaticPowerItem("dust_lead"));
		StaticPowerRegistry.preRegisterItem(DustTungsten = new StaticPowerItem("dust_tungsten"));
		StaticPowerRegistry.preRegisterItem(DustMagnesium = new StaticPowerItem("dust_magnesium"));
		StaticPowerRegistry.preRegisterItem(DustPlatinum = new StaticPowerItem("dust_platinum"));
		StaticPowerRegistry.preRegisterItem(DustAluminium = new StaticPowerItem("dust_aluminium"));
		StaticPowerRegistry.preRegisterItem(DustStatic = new StaticPowerItem("dust_static"));
		StaticPowerRegistry.preRegisterItem(DustEnergized = new StaticPowerItem("dust_energized"));
		StaticPowerRegistry.preRegisterItem(DustLumum = new StaticPowerItem("dust_lumum"));
		StaticPowerRegistry.preRegisterItem(DustInertInfusion = new StaticPowerItem("dust_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(DustRedstoneAlloy = new StaticPowerItem("dust_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(DustCoal = new StaticPowerItem("dust_coal"));
		StaticPowerRegistry.preRegisterItem(DustObsidian = new StaticPowerItem("dust_obsidian"));
		StaticPowerRegistry.preRegisterItem(DustGold = new StaticPowerItem("dust_gold"));
		StaticPowerRegistry.preRegisterItem(DustIron = new StaticPowerItem("dust_iron"));
		StaticPowerRegistry.preRegisterItem(DustRuby = new StaticPowerItem("dust_ruby"));
		StaticPowerRegistry.preRegisterItem(DustSapphire = new StaticPowerItem("dust_sapphire"));
		StaticPowerRegistry.preRegisterItem(DustEmerald = new StaticPowerItem("dust_emerald"));
		StaticPowerRegistry.preRegisterItem(DustDiamond = new StaticPowerItem("dust_diamond"));
		StaticPowerRegistry.preRegisterItem(DustSulfur = new StaticPowerItem("dust_sulfur"));
		StaticPowerRegistry.preRegisterItem(DustSaltpeter = new StaticPowerItem("dust_saltpeter"));
		StaticPowerRegistry.preRegisterItem(DustCharcoal = new StaticPowerItem("dust_charcoal"));
		StaticPowerRegistry.preRegisterItem(DustBrass = new StaticPowerItem("dust_brass"));
		StaticPowerRegistry.preRegisterItem(DustBronze = new StaticPowerItem("dust_bronze"));

		// Upgrade Plates
		StaticPowerRegistry.preRegisterItem(BasicUpgradePlate = new StaticPowerItem("upgrade_plate_basic"));
		StaticPowerRegistry.preRegisterItem(AdvancedUpgradePlate = new StaticPowerItem("upgrade_plate_advanced"));
		StaticPowerRegistry.preRegisterItem(StaticUpgradePlate = new StaticPowerItem("upgrade_plate_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedUpgradePlate = new StaticPowerItem("upgrade_plate_energized"));
		StaticPowerRegistry.preRegisterItem(LumumUpgradePlate = new StaticPowerItem("upgrade_plate_lumum"));

		// Drill Bits
		StaticPowerRegistry.preRegisterItem(IronDrillBit = new DrillBit("drill_bit_iron", ItemTier.IRON, StaticPowerTiers.IRON));
		StaticPowerRegistry.preRegisterItem(BronzeDrillBit = new DrillBit("drill_bit_bronze", ItemTier.IRON, StaticPowerTiers.BRONZE));
		StaticPowerRegistry.preRegisterItem(AdvancedDrillBit = new DrillBit("drill_bit_advanced", ItemTier.IRON, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(TungstenDrillBit = new DrillBit("drill_bit_tungsten", ItemTier.NETHERITE, StaticPowerTiers.TUNGSTEN));
		StaticPowerRegistry.preRegisterItem(StaticDrillBit = new DrillBit("drill_bit_static", ItemTier.DIAMOND, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedDrillBit = new DrillBit("drill_bit_energized", ItemTier.DIAMOND, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumDrillBit = new DrillBit("drill_bit_lumum", ItemTier.NETHERITE, StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeDrillBit = new DrillBit("drill_bit_creative", ItemTier.NETHERITE, StaticPowerTiers.CREATIVE));

		// Blades
		StaticPowerRegistry.preRegisterItem(IronBlade = new Blade("blade_iron", ItemTier.IRON, StaticPowerTiers.IRON));
		StaticPowerRegistry.preRegisterItem(BronzeBlade = new Blade("blade_bronze", ItemTier.IRON, StaticPowerTiers.BRONZE));
		StaticPowerRegistry.preRegisterItem(AdvancedBlade = new Blade("blade_advanced", ItemTier.IRON, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(TungstenBlade = new Blade("blade_tungsten", ItemTier.NETHERITE, StaticPowerTiers.TUNGSTEN));
		StaticPowerRegistry.preRegisterItem(StaticBlade = new Blade("blade_static", ItemTier.DIAMOND, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedBlade = new Blade("blade_energized", ItemTier.DIAMOND, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumBlade = new Blade("blade_lumum", ItemTier.NETHERITE, StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeBlade = new Blade("blade_creative", ItemTier.NETHERITE, StaticPowerTiers.CREATIVE));

		// Chainsaw Blades
		StaticPowerRegistry.preRegisterItem(IronChainsawBlade = new ChainsawBlade("chainsaw_blade_iron", ItemTier.IRON, StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(BronzeChainsawBlade = new ChainsawBlade("chainsaw_blade_bronze", ItemTier.IRON, StaticPowerTiers.BRONZE));
		StaticPowerRegistry.preRegisterItem(AdvancedChainsawBlade = new ChainsawBlade("chainsaw_blade_advanced", ItemTier.IRON, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(TungstenChainsawBlade = new ChainsawBlade("chainsaw_blade_tungsten", ItemTier.NETHERITE, StaticPowerTiers.TUNGSTEN));
		StaticPowerRegistry.preRegisterItem(StaticChainsawBlade = new ChainsawBlade("chainsaw_blade_static", ItemTier.DIAMOND, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedChainsawBlade = new ChainsawBlade("chainsaw_blade_energized", ItemTier.DIAMOND, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumChainsawBlade = new ChainsawBlade("chainsaw_blade_lumum", ItemTier.NETHERITE, StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeChainsawBlade = new ChainsawBlade("chainsaw_blade_creative", ItemTier.NETHERITE, StaticPowerTiers.CREATIVE));

		// Ingots
		StaticPowerRegistry.preRegisterItem(IngotCopper = new StaticPowerItem("ingot_copper"));
		StaticPowerRegistry.preRegisterItem(IngotTin = new StaticPowerItem("ingot_tin"));
		StaticPowerRegistry.preRegisterItem(IngotZinc = new StaticPowerItem("ingot_zinc"));
		StaticPowerRegistry.preRegisterItem(IngotSilver = new StaticPowerItem("ingot_silver"));
		StaticPowerRegistry.preRegisterItem(IngotLead = new StaticPowerItem("ingot_lead"));
		StaticPowerRegistry.preRegisterItem(IngotMagnesium = new StaticPowerItem("ingot_magnesium"));
		StaticPowerRegistry.preRegisterItem(IngotTungsten = new StaticPowerItem("ingot_tungsten"));
		StaticPowerRegistry.preRegisterItem(IngotPlatinum = new StaticPowerItem("ingot_platinum"));
		StaticPowerRegistry.preRegisterItem(IngotBrass = new StaticPowerItem("ingot_brass"));
		StaticPowerRegistry.preRegisterItem(IngotBronze = new StaticPowerItem("ingot_bronze"));
		StaticPowerRegistry.preRegisterItem(IngotAluminium = new StaticPowerItem("ingot_aluminium"));
		StaticPowerRegistry.preRegisterItem(IngotStatic = new StaticPowerItem("ingot_static"));
		StaticPowerRegistry.preRegisterItem(IngotEnergized = new StaticPowerItem("ingot_energized"));
		StaticPowerRegistry.preRegisterItem(IngotLumum = new StaticPowerItem("ingot_lumum"));
		StaticPowerRegistry.preRegisterItem(IngotInertInfusion = new StaticPowerItem("ingot_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(IngotRedstoneAlloy = new StaticPowerItem("ingot_redstone_alloy"));

		// Nuggets
		StaticPowerRegistry.preRegisterItem(NuggetCopper = new StaticPowerItem("nugget_copper"));
		StaticPowerRegistry.preRegisterItem(NuggetTin = new StaticPowerItem("nugget_tin"));
		StaticPowerRegistry.preRegisterItem(NuggetZinc = new StaticPowerItem("nugget_zinc"));
		StaticPowerRegistry.preRegisterItem(NuggetSilver = new StaticPowerItem("nugget_silver"));
		StaticPowerRegistry.preRegisterItem(NuggetLead = new StaticPowerItem("nugget_lead"));
		StaticPowerRegistry.preRegisterItem(NuggetMagnesium = new StaticPowerItem("nugget_magnesium"));
		StaticPowerRegistry.preRegisterItem(NuggetTungsten = new StaticPowerItem("nugget_tungsten"));
		StaticPowerRegistry.preRegisterItem(NuggetPlatinum = new StaticPowerItem("nugget_platinum"));
		StaticPowerRegistry.preRegisterItem(NuggetAluminium = new StaticPowerItem("nugget_aluminium"));
		StaticPowerRegistry.preRegisterItem(NuggetStatic = new StaticPowerItem("nugget_static"));
		StaticPowerRegistry.preRegisterItem(NuggetEnergized = new StaticPowerItem("nugget_energized"));
		StaticPowerRegistry.preRegisterItem(NuggetLumum = new StaticPowerItem("nugget_lumum"));
		StaticPowerRegistry.preRegisterItem(NuggetInertInfusion = new StaticPowerItem("nugget_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(NuggetRedstoneAlloy = new StaticPowerItem("nugget_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(NuggetBrass = new StaticPowerItem("nugget_brass"));
		StaticPowerRegistry.preRegisterItem(NuggetBronze = new StaticPowerItem("nugget_bronze"));

		StaticPowerRegistry.preRegisterItem(
				BasicExtractorAttachment = new ExtractorAttachment("cable_attachment_basic_extractor", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.CABLE_BASIC_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(AdvancedExtractorAttachment = new ExtractorAttachment("cable_attachment_advanced_extractor", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.CABLE_ADVANCED_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(StaticExtractorAttachment = new ExtractorAttachment("cable_attachment_static_extractor", StaticPowerTiers.STATIC,
				StaticPowerAdditionalModels.CABLE_STATIC_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(EnergizedExtractorAttachment = new ExtractorAttachment("cable_attachment_energized_extractor", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.CABLE_ENERGIZED_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(
				LumumExtractorAttachment = new ExtractorAttachment("cable_attachment_lumum_extractor", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.CABLE_LUMUM_EXTRACTOR_ATTACHMENT));

		StaticPowerRegistry.preRegisterItem(
				BasicFilterAttachment = new FilterAttachment("cable_attachment_basic_filter", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.CABLE_BASIC_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(
				AdvancedFilterAttachment = new FilterAttachment("cable_attachment_advanced_filter", StaticPowerTiers.ADVANCED, StaticPowerAdditionalModels.CABLE_ADVANCED_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(
				StaticFilterAttachment = new FilterAttachment("cable_attachment_static_filter", StaticPowerTiers.STATIC, StaticPowerAdditionalModels.CABLE_STATIC_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(EnergizedFilterAttachment = new FilterAttachment("cable_attachment_energized_filter", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.CABLE_ENERGIZED_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(
				LumumFilterAttachment = new FilterAttachment("cable_attachment_lumum_filter", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.CABLE_LUMUM_FILTER_ATTACHMENT));

		StaticPowerRegistry.preRegisterItem(
				BasicRetrieverAttachment = new RetrieverAttachment("cable_attachment_basic_retriever", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.CABLE_BASIC_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(AdvancedRetrieverAttachment = new RetrieverAttachment("cable_attachment_advanced_retriever", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.CABLE_ADVANCED_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(StaticRetrieverAttachment = new RetrieverAttachment("cable_attachment_static_retriever", StaticPowerTiers.STATIC,
				StaticPowerAdditionalModels.CABLE_STATIC_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(EnergizedRetrieverAttachment = new RetrieverAttachment("cable_attachment_energized_retriever", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.CABLE_ENERGIZED_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(
				LumumRetrieverAttachment = new RetrieverAttachment("cable_attachment_lumum_retriever", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.CABLE_LUMUM_RETRIEVER_ATTACHMENT));

		StaticPowerRegistry.preRegisterItem(ExporterAttachment = new DigistoreExporterAttachment("cable_attachment_digistore_exporter"));
		StaticPowerRegistry.preRegisterItem(ImporterAttachment = new DigistoreImporterAttachment("cable_attachment_digistore_importer"));
		StaticPowerRegistry.preRegisterItem(IOBusAttachment = new DigistoreIOBusAttachment("cable_attachment_digistore_io_bus"));
		StaticPowerRegistry.preRegisterItem(RegulatorAttachment = new DigistoreRegulatorAttachment("cable_attachment_digistore_regulator"));
		StaticPowerRegistry.preRegisterItem(DigistoreTerminalAttachment = new DigistoreTerminal("cable_attachment_digistore_terminal"));
		StaticPowerRegistry.preRegisterItem(DigistoreCraftingTerminalAttachment = new DigistoreCraftingTerminal("cable_attachment_digistore_crafting_terminal"));
		StaticPowerRegistry.preRegisterItem(DigistorePatternEncoderAttachment = new DigistorePatternEncoder("cable_attachment_digistore_pattern_encoder"));
		StaticPowerRegistry.preRegisterItem(DigistoreCraftingInterfaceAttachment = new DigistoreCraftingInterfaceAttachment("cable_attachment_digistore_crafting_interface"));
		StaticPowerRegistry.preRegisterItem(DigistoreScreenAttachment = new DigistoreScreen("cable_attachment_digistore_screen"));
		StaticPowerRegistry.preRegisterItem(DigistoreLightAttachment = new DigistoreLight("cable_attachment_digistore_light"));

		// Digistore Misc.
		StaticPowerRegistry.preRegisterItem(PatternCard = new DigistorePatternCard("digistore_pattern_card"));

		// Digistore Cards
		StaticPowerRegistry.preRegisterItem(BasicDigistoreCard = new DigistoreCard("digistore_card_basic", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.BASIC_DIGISTORE_CARD));
		StaticPowerRegistry
				.preRegisterItem(AdvancedDigistoreCard = new DigistoreCard("digistore_card_advanced", StaticPowerTiers.ADVANCED, StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(StaticDigistoreCard = new DigistoreCard("digistore_card_static", StaticPowerTiers.STATIC, StaticPowerAdditionalModels.STATIC_DIGISTORE_CARD));
		StaticPowerRegistry
				.preRegisterItem(EnergizedDigistoreCard = new DigistoreCard("digistore_card_energized", StaticPowerTiers.ENERGIZED, StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(LumumDigistoreCard = new DigistoreCard("digistore_card_lumum", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.LUMUM_DIGISTORE_CARD));
		StaticPowerRegistry
				.preRegisterItem(CreativeDigistoreCard = new DigistoreCard("digistore_card_creative", StaticPowerTiers.CREATIVE, StaticPowerAdditionalModels.CREATIVE_DIGISTORE_CARD, true));

		StaticPowerRegistry.preRegisterItem(
				BasicSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_basic", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.BASIC_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(AdvancedSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_advanced", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(
				StaticSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_static", StaticPowerTiers.STATIC, StaticPowerAdditionalModels.STATIC_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(EnergizedSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_energized", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(
				LumumSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_lumum", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.LUMUM_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(CreativeSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_creative", StaticPowerTiers.CREATIVE,
				StaticPowerAdditionalModels.CREATIVE_DIGISTORE_SINGULAR_CARD, true));

		StaticPowerRegistry.preRegisterItem(CableCover = new CableCover("cable_cover"));

		StaticPowerRegistry.preRegisterItem(MilkBottle = new MilkBottleItem("bottle_milk", 40));
		StaticPowerRegistry.preRegisterItem(AppleJuiceBottle = new JuiceBottleItem("bottle_juice_apple", 40, 6, 8.0f));
		StaticPowerRegistry.preRegisterItem(CarrotJuiceBottle = new JuiceBottleItem("bottle_juice_carrot", 40, 4, 6.0f));
		StaticPowerRegistry.preRegisterItem(PumpkinJuiceBottle = new JuiceBottleItem("bottle_juice_pumpkin", 40, 6, 8.0f));
		StaticPowerRegistry.preRegisterItem(MelonJuiceBottle = new JuiceBottleItem("bottle_juice_melon", 40, 6, 8.0f));
		StaticPowerRegistry.preRegisterItem(BeetJuiceBottle = new JuiceBottleItem("bottle_juice_beetroot", 40, 4, 6.0f));
		StaticPowerRegistry.preRegisterItem(BerryJuiceBottle = new JuiceBottleItem("bottle_juice_berry", 40, 8, 10.0f));
	}
}
