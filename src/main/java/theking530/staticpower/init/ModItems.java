package theking530.staticpower.init;

import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;
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
import theking530.staticpower.cables.attachments.drain.DrainAttachment;
import theking530.staticpower.cables.attachments.extractor.ExtractorAttachment;
import theking530.staticpower.cables.attachments.filter.FilterAttachment;
import theking530.staticpower.cables.attachments.retirever.RetrieverAttachment;
import theking530.staticpower.cables.attachments.sprinkler.SprinklerAttachment;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.BatteryPack;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.items.DigistorePatternCard;
import theking530.staticpower.items.DigistoreStackedCard;
import theking530.staticpower.items.GearBox;
import theking530.staticpower.items.HeatedIngot;
import theking530.staticpower.items.JuiceBottleItem;
import theking530.staticpower.items.MilkBottleItem;
import theking530.staticpower.items.PortableBattery;
import theking530.staticpower.items.ResearchItem;
import theking530.staticpower.items.StaticPowerBurnableItem;
import theking530.staticpower.items.StaticPowerFood;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.items.crops.DepletedCrop;
import theking530.staticpower.items.crops.StaticPlantCrop;
import theking530.staticpower.items.crops.StaticPlantSeeds;
import theking530.staticpower.items.fluidcapsule.FluidCapsule;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.tools.CableNetworkAnalyzer;
import theking530.staticpower.items.tools.CoverSaw;
import theking530.staticpower.items.tools.DigistoreWirelessTerminal;
import theking530.staticpower.items.tools.ElectricSolderingIron;
import theking530.staticpower.items.tools.Hammer;
import theking530.staticpower.items.tools.Magnet;
import theking530.staticpower.items.tools.SolderingIron;
import theking530.staticpower.items.tools.StaticWrench;
import theking530.staticpower.items.tools.TurbineBlades;
import theking530.staticpower.items.tools.WireCutters;
import theking530.staticpower.items.tools.chainsaw.Chainsaw;
import theking530.staticpower.items.tools.chainsaw.ChainsawBlade;
import theking530.staticpower.items.tools.miningdrill.DrillBit;
import theking530.staticpower.items.tools.miningdrill.MiningDrill;
import theking530.staticpower.items.tools.sword.Blade;

public class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StaticPower.MOD_ID);
	public static final RegistryObject<StaticPowerItem> DistilleryGrain = ITEMS.register("distillery_grain", () -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> BedFrame = ITEMS.register("bed_frame", () -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> WheatFlour;
	public static final RegistryObject<StaticPowerItem> PotatoFlour;
	public static final RegistryObject<StaticPowerItem> PotatoBread;
	public static final RegistryObject<StaticPowerItem> ApplePie;
	public static final RegistryObject<StaticPowerItem> StaticPie;
	public static final RegistryObject<StaticPowerItem> EnergizedPie;
	public static final RegistryObject<StaticPowerItem> LumumPie;
	public static final RegistryObject<StaticPowerItem> RawSmutton;
	public static final RegistryObject<StaticPowerItem> CookedSmutton;
	public static final RegistryObject<StaticPowerItem> RawEeef;
	public static final RegistryObject<StaticPowerItem> CookedEeef;
	public static final RegistryObject<StaticPowerItem> Eather;

	public static final RegistryObject<StaticPowerItem> MoldBlank;
	public static final RegistryObject<StaticPowerItem> MoldPlate;
	public static final RegistryObject<StaticPowerItem> MoldWire;
	public static final RegistryObject<StaticPowerItem> MoldGear;
	public static final RegistryObject<StaticPowerItem> MoldBlade;
	public static final RegistryObject<StaticPowerItem> MoldIngot;
	public static final RegistryObject<StaticPowerItem> MoldNugget;
	public static final RegistryObject<StaticPowerItem> MoldBlock;
	public static final RegistryObject<StaticPowerItem> MoldDrillBit;
	public static final RegistryObject<StaticPowerItem> MoldRod;

	public static final RegistryObject<StaticPowerItem> RawRustyIron;
	public static final RegistryObject<StaticPowerItem> RawTin;
	public static final RegistryObject<StaticPowerItem> RawZinc;
	public static final RegistryObject<StaticPowerItem> RawSilver;
	public static final RegistryObject<StaticPowerItem> RawLead;
	public static final RegistryObject<StaticPowerItem> RawTungsten;
	public static final RegistryObject<StaticPowerItem> RawMagnesium;
	public static final RegistryObject<StaticPowerItem> RawPlatinum;
	public static final RegistryObject<StaticPowerItem> RawAluminum;
	public static final RegistryObject<StaticPowerItem> RawStatic;
	public static final RegistryObject<StaticPowerItem> RawEnergized;
	public static final RegistryObject<StaticPowerItem> RawLumum;

	public static final RegistryObject<StaticPowerItem> IngotTin;
	public static final RegistryObject<StaticPowerItem> IngotZinc;
	public static final RegistryObject<StaticPowerItem> IngotSilver;
	public static final RegistryObject<StaticPowerItem> IngotLead;
	public static final RegistryObject<StaticPowerItem> IngotTungsten;
	public static final RegistryObject<StaticPowerItem> IngotMagnesium;
	public static final RegistryObject<StaticPowerItem> IngotPlatinum;
	public static final RegistryObject<StaticPowerItem> IngotAluminum;
	public static final RegistryObject<StaticPowerItem> IngotStatic;
	public static final RegistryObject<StaticPowerItem> IngotEnergized;
	public static final RegistryObject<StaticPowerItem> IngotLumum;
	public static final RegistryObject<StaticPowerItem> IngotInertInfusion;
	public static final RegistryObject<StaticPowerItem> IngotRedstoneAlloy;
	public static final RegistryObject<StaticPowerItem> IngotBrass;
	public static final RegistryObject<StaticPowerItem> IngotBronze;

	public static final RegistryObject<StaticPowerItem> IngotTinHeated;
	public static final RegistryObject<StaticPowerItem> IngotZincHeated;
	public static final RegistryObject<StaticPowerItem> IngotSilverHeated;
	public static final RegistryObject<StaticPowerItem> IngotLeadHeated;
	public static final RegistryObject<StaticPowerItem> IngotTungstenHeated;
	public static final RegistryObject<StaticPowerItem> IngotMagnesiumHeated;
	public static final RegistryObject<StaticPowerItem> IngotPlatinumHeated;
	public static final RegistryObject<StaticPowerItem> IngotAluminumHeated;
	public static final RegistryObject<StaticPowerItem> IngotStaticHeated;
	public static final RegistryObject<StaticPowerItem> IngotEnergizedHeated;
	public static final RegistryObject<StaticPowerItem> IngotLumumHeated;
	public static final RegistryObject<StaticPowerItem> IngotInertInfusionHeated;
	public static final RegistryObject<StaticPowerItem> IngotRedstoneAlloyHeated;
	public static final RegistryObject<StaticPowerItem> IngotBrassHeated;
	public static final RegistryObject<StaticPowerItem> IngotBronzeHeated;

	public static final RegistryObject<StaticPowerItem> IngotIronHeated;
	public static final RegistryObject<StaticPowerItem> IngotCopperHeated;
	public static final RegistryObject<StaticPowerItem> IngotGoldHeated;

	public static final RegistryObject<StaticPowerItem> NuggetCopper;
	public static final RegistryObject<StaticPowerItem> NuggetTin;
	public static final RegistryObject<StaticPowerItem> NuggetZinc;
	public static final RegistryObject<StaticPowerItem> NuggetSilver;
	public static final RegistryObject<StaticPowerItem> NuggetLead;
	public static final RegistryObject<StaticPowerItem> NuggetTungsten;
	public static final RegistryObject<StaticPowerItem> NuggetMagnesium;
	public static final RegistryObject<StaticPowerItem> NuggetPlatinum;
	public static final RegistryObject<StaticPowerItem> NuggetAluminum;
	public static final RegistryObject<StaticPowerItem> NuggetStatic;
	public static final RegistryObject<StaticPowerItem> NuggetEnergized;
	public static final RegistryObject<StaticPowerItem> NuggetLumum;
	public static final RegistryObject<StaticPowerItem> NuggetInertInfusion;
	public static final RegistryObject<StaticPowerItem> NuggetRedstoneAlloy;
	public static final RegistryObject<StaticPowerItem> NuggetBrass;
	public static final RegistryObject<StaticPowerItem> NuggetBronze;

	public static final RegistryObject<StaticPowerItem> PlateIron;
	public static final RegistryObject<StaticPowerItem> PlateGold;
	public static final RegistryObject<StaticPowerItem> PlateCopper;
	public static final RegistryObject<StaticPowerItem> PlateTin;
	public static final RegistryObject<StaticPowerItem> PlateZinc;
	public static final RegistryObject<StaticPowerItem> PlateSilver;
	public static final RegistryObject<StaticPowerItem> PlateLead;
	public static final RegistryObject<StaticPowerItem> PlateTungsten;
	public static final RegistryObject<StaticPowerItem> PlateMagnesium;
	public static final RegistryObject<StaticPowerItem> PlatePlatinum;
	public static final RegistryObject<StaticPowerItem> PlateAluminum;
	public static final RegistryObject<StaticPowerItem> PlateStatic;
	public static final RegistryObject<StaticPowerItem> PlateEnergized;
	public static final RegistryObject<StaticPowerItem> PlateLumum;
	public static final RegistryObject<StaticPowerItem> PlateInertInfusion;
	public static final RegistryObject<StaticPowerItem> PlateRedstoneAlloy;
	public static final RegistryObject<StaticPowerItem> PlateBrass;
	public static final RegistryObject<StaticPowerItem> PlateBronze;
	public static final RegistryObject<StaticPowerItem> RustyIronScrap;

	public static RegistryObject<StaticPowerItem> GearCopper;
	public static RegistryObject<StaticPowerItem> GearTin;
	public static RegistryObject<StaticPowerItem> GearZinc;
	public static RegistryObject<StaticPowerItem> GearSilver;
	public static RegistryObject<StaticPowerItem> GearLead;
	public static RegistryObject<StaticPowerItem> GearTungsten;
	public static RegistryObject<StaticPowerItem> GearMagnesium;
	public static RegistryObject<StaticPowerItem> GearPlatinum;
	public static RegistryObject<StaticPowerItem> GearAluminum;
	public static RegistryObject<StaticPowerItem> GearStatic;
	public static RegistryObject<StaticPowerItem> GearEnergized;
	public static RegistryObject<StaticPowerItem> GearLumum;
	public static RegistryObject<StaticPowerItem> GearInertInfusion;
	public static RegistryObject<StaticPowerItem> GearRedstoneAlloy;
	public static RegistryObject<StaticPowerItem> GearIron;
	public static RegistryObject<StaticPowerItem> GearGold;
	public static RegistryObject<StaticPowerItem> GearBrass;
	public static RegistryObject<StaticPowerItem> GearBronze;

	public static RegistryObject<StaticPowerItem> GearBoxCopper;
	public static RegistryObject<StaticPowerItem> GearBoxTin;
	public static RegistryObject<StaticPowerItem> GearBoxZinc;
	public static RegistryObject<StaticPowerItem> GearBoxSilver;
	public static RegistryObject<StaticPowerItem> GearBoxLead;
	public static RegistryObject<StaticPowerItem> GearBoxTungsten;
	public static RegistryObject<StaticPowerItem> GearBoxMagnesium;
	public static RegistryObject<StaticPowerItem> GearBoxPlatinum;
	public static RegistryObject<StaticPowerItem> GearBoxAluminum;
	public static RegistryObject<StaticPowerItem> GearBoxStatic;
	public static RegistryObject<StaticPowerItem> GearBoxEnergized;
	public static RegistryObject<StaticPowerItem> GearBoxLumum;
	public static RegistryObject<StaticPowerItem> GearBoxInertInfusion;
	public static RegistryObject<StaticPowerItem> GearBoxRedstoneAlloy;
	public static RegistryObject<StaticPowerItem> GearBoxIron;
	public static RegistryObject<StaticPowerItem> GearBoxGold;
	public static RegistryObject<StaticPowerItem> GearBoxBrass;
	public static RegistryObject<StaticPowerItem> GearBoxBronze;

	public static RegistryObject<StaticPowerItem> DustCopper;
	public static RegistryObject<StaticPowerItem> DustTin;
	public static RegistryObject<StaticPowerItem> DustZinc;
	public static RegistryObject<StaticPowerItem> DustSilver;
	public static RegistryObject<StaticPowerItem> DustLead;
	public static RegistryObject<StaticPowerItem> DustTungsten;
	public static RegistryObject<StaticPowerItem> DustMagnesium;
	public static RegistryObject<StaticPowerItem> DustPlatinum;
	public static RegistryObject<StaticPowerItem> DustAluminum;
	public static RegistryObject<StaticPowerItem> DustStatic;
	public static RegistryObject<StaticPowerItem> DustEnergized;
	public static RegistryObject<StaticPowerItem> DustLumum;
	public static RegistryObject<StaticPowerItem> DustInertInfusion;
	public static RegistryObject<StaticPowerItem> DustRedstoneAlloy;
	public static RegistryObject<StaticPowerItem> DustBrass;
	public static RegistryObject<StaticPowerItem> DustBronze;

	public static RegistryObject<StaticPowerItem> DustCoal;
	public static RegistryObject<StaticPowerItem> DustObsidian;
	public static RegistryObject<StaticPowerItem> DustGold;
	public static RegistryObject<StaticPowerItem> DustIron;
	public static RegistryObject<StaticPowerItem> DustRuby;
	public static RegistryObject<StaticPowerItem> DustSapphire;
	public static RegistryObject<StaticPowerItem> DustEmerald;
	public static RegistryObject<StaticPowerItem> DustDiamond;
	public static RegistryObject<StaticPowerItem> DustSulfur;
	public static RegistryObject<StaticPowerItem> DustSaltpeter;
	public static RegistryObject<StaticPowerItem> DustCharcoal;

	public static RegistryObject<StaticPowerItem> ChunksCopper;
	public static RegistryObject<StaticPowerItem> ChunksTin;
	public static RegistryObject<StaticPowerItem> ChunksZinc;
	public static RegistryObject<StaticPowerItem> ChunksSilver;
	public static RegistryObject<StaticPowerItem> ChunksLead;
	public static RegistryObject<StaticPowerItem> ChunksTungsten;
	public static RegistryObject<StaticPowerItem> ChunksMagnesium;
	public static RegistryObject<StaticPowerItem> ChunksPlatinum;
	public static RegistryObject<StaticPowerItem> ChunksAluminum;
	public static RegistryObject<StaticPowerItem> ChunksCoal;
	public static RegistryObject<StaticPowerItem> ChunksGold;
	public static RegistryObject<StaticPowerItem> ChunksIron;
	public static RegistryObject<StaticPowerItem> ChunksRuby;
	public static RegistryObject<StaticPowerItem> ChunksSapphire;
	public static RegistryObject<StaticPowerItem> ChunksEmerald;
	public static RegistryObject<StaticPowerItem> ChunksDiamond;
	public static RegistryObject<StaticPowerItem> ChunksLapis;
	public static RegistryObject<StaticPowerItem> ChunksRedstone;
	public static RegistryObject<StaticPowerItem> ChunksQuartz;

	public static RegistryObject<StaticPowerItem> RodCopper;
	public static RegistryObject<StaticPowerItem> RodTin;
	public static RegistryObject<StaticPowerItem> RodZinc;
	public static RegistryObject<StaticPowerItem> RodSilver;
	public static RegistryObject<StaticPowerItem> RodLead;
	public static RegistryObject<StaticPowerItem> RodTungsten;
	public static RegistryObject<StaticPowerItem> RodMagnesium;
	public static RegistryObject<StaticPowerItem> RodPlatinum;
	public static RegistryObject<StaticPowerItem> RodAluminum;
	public static RegistryObject<StaticPowerItem> RodStatic;
	public static RegistryObject<StaticPowerItem> RodEnergized;
	public static RegistryObject<StaticPowerItem> RodLumum;
	public static RegistryObject<StaticPowerItem> RodInertInfusion;
	public static RegistryObject<StaticPowerItem> RodRedstoneAlloy;
	public static RegistryObject<StaticPowerItem> RodBrass;
	public static RegistryObject<StaticPowerItem> RodBronze;
	public static RegistryObject<StaticPowerItem> RodCoal;
	public static RegistryObject<StaticPowerItem> RodGold;
	public static RegistryObject<StaticPowerItem> RodIron;
	public static RegistryObject<StaticPowerItem> RodRuby;
	public static RegistryObject<StaticPowerItem> RodSapphire;
	public static RegistryObject<StaticPowerItem> RodEmerald;
	public static RegistryObject<StaticPowerItem> RodDiamond;
	public static RegistryObject<StaticPowerItem> RodQuartz;
	public static RegistryObject<StaticPowerItem> RodLapis;
	public static RegistryObject<StaticPowerItem> RodLatex;

	public static RegistryObject<StaticPowerItem> GemRuby;
	public static RegistryObject<StaticPowerItem> GemSapphire;

	public static RegistryObject<StaticPowerItem> RawSilicon;
	public static RegistryObject<StaticPowerItem> Silicon;
	public static RegistryObject<StaticPowerItem> StaticDopedSilicon;
	public static RegistryObject<StaticPowerItem> EnergizedDopedSilicon;
	public static RegistryObject<StaticPowerItem> LumumDopedSilicon;
	public static RegistryObject<StaticPowerItem> CrystalStatic;
	public static RegistryObject<StaticPowerItem> CrystalEnergized;
	public static RegistryObject<StaticPowerItem> CrystalLumum;
	public static RegistryObject<StaticPowerItem> DustWood;
	public static RegistryObject<StaticPowerItem> LatexChunk;
	public static RegistryObject<StaticPowerItem> RubberWoodBark;
	public static RegistryObject<StaticPowerItem> PortableSmeltingCore;
	public static RegistryObject<StaticPowerItem> Slag;

	public static RegistryObject<StaticPowerItem> BasicCard;
	public static RegistryObject<StaticPowerItem> AdvancedCard;
	public static RegistryObject<StaticPowerItem> StaticCard;
	public static RegistryObject<StaticPowerItem> EnergizedCard;
	public static RegistryObject<StaticPowerItem> LumumCard;

	public static TurbineBlades WoodTurbineBlades;
	public static TurbineBlades BasicTurbineBlades;
	public static TurbineBlades AdvancedTurbineBlades;
	public static TurbineBlades StaticTurbineBlades;
	public static TurbineBlades EnergizedTurbineBlades;
	public static TurbineBlades LumumTurbineBlades;
	public static TurbineBlades CreativeTurbineBlades;

	public static RegistryObject<StaticPowerItem> BasicProcessor;
	public static RegistryObject<StaticPowerItem> AdvancedProcessor;
	public static RegistryObject<StaticPowerItem> StaticProcessor;
	public static RegistryObject<StaticPowerItem> EnergizedProcessor;
	public static RegistryObject<StaticPowerItem> LumumProcessor;

	public static RegistryObject<StaticPowerItem> RubberBar;
	public static RegistryObject<StaticPowerItem> RubberSheet;
	public static RegistryObject<StaticPowerItem> IOPort;
	public static RegistryObject<StaticPowerItem> WireCopper;
	public static RegistryObject<StaticPowerItem> WireTin;
	public static RegistryObject<StaticPowerItem> WireSilver;
	public static RegistryObject<StaticPowerItem> WireGold;
	public static RegistryObject<StaticPowerItem> WirePlatinum;
	public static RegistryObject<StaticPowerItem> WireAluminum;
	public static RegistryObject<StaticPowerItem> CoilCopper;
	public static RegistryObject<StaticPowerItem> CoilSilver;
	public static RegistryObject<StaticPowerItem> CoilGold;
	public static RegistryObject<StaticPowerItem> CoilPlatinum;
	public static RegistryObject<StaticPowerItem> CoilAluminum;
	public static RegistryObject<StaticPowerItem> CoilTin;

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

	public static RegistryObject<StaticPowerItem> MemoryChip;
	public static RegistryObject<StaticPowerItem> LogicGatePowerSync;
	public static RegistryObject<StaticPowerItem> InvertedLogicGatePowerSync;
	public static RegistryObject<StaticPowerItem> Servo;
	public static RegistryObject<StaticPowerItem> Diode;
	public static RegistryObject<StaticPowerItem> Transistor;
	public static RegistryObject<StaticPowerItem> InternalClock;
	public static RegistryObject<StaticPowerItem> Motor;
	public static RegistryObject<StaticPowerItem> Plug;
	public static RegistryObject<StaticPowerItem> DigistoreCore;
	public static RegistryObject<StaticPowerItem> GrinderComponent;

	public static FluidCapsule IronFluidCapsule;
	public static FluidCapsule BasicFluidCapsule;
	public static FluidCapsule AdvancedFluidCapsule;
	public static FluidCapsule StaticFluidCapsule;
	public static FluidCapsule EnergizedFluidCapsule;
	public static FluidCapsule LumumFluidCapsule;
	public static FluidCapsule CreativeFluidCapsule;

	public static RegistryObject<StaticPowerItem> BasicUpgradePlate;
	public static RegistryObject<StaticPowerItem> AdvancedUpgradePlate;
	public static RegistryObject<StaticPowerItem> StaticUpgradePlate;
	public static RegistryObject<StaticPowerItem> EnergizedUpgradePlate;
	public static RegistryObject<StaticPowerItem> LumumUpgradePlate;

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

	public static Hammer IronMetalHammer;
	public static Hammer CopperMetalHammer;
	public static Hammer TinMetalHammer;
	public static Hammer ZincMetalHammer;
	public static Hammer BronzeMetalHammer;
	public static Hammer TungstenMetalHammer;
	public static Hammer CreativeMetalHammer;

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

	public static RegistryObject<StaticPowerItem> WeakMagnet;
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

	public static SprinklerAttachment SprinklerAttachment;
	public static DrainAttachment DrainAttachment;

	public static DigistorePatternCard PatternCard;
	public static DigistoreWirelessTerminal DigistoreWirelessTerminal;

	public static DigistoreCard BasicDigistoreCard;
	public static DigistoreCard AdvancedDigistoreCard;
	public static DigistoreCard StaticDigistoreCard;
	public static DigistoreCard EnergizedDigistoreCard;
	public static DigistoreCard LumumDigistoreCard;
	public static DigistoreCard CreativeDigistoreCard;

	public static DigistoreStackedCard BasicStackedDigistoreCard;
	public static DigistoreStackedCard AdvancedStackedDigistoreCard;
	public static DigistoreStackedCard StaticStackedDigistoreCard;
	public static DigistoreStackedCard EnergizedStackedDigistoreCard;
	public static DigistoreStackedCard LumumStackedDigistoreCard;
	public static DigistoreStackedCard CreativeStackedDigistoreCard;

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

	public static RegistryObject<StaticPowerItem> ResearchTier1;
	public static RegistryObject<StaticPowerItem> ResearchTier2;
	public static RegistryObject<StaticPowerItem> ResearchTier3;
	public static RegistryObject<StaticPowerItem> ResearchTier4;
	public static RegistryObject<StaticPowerItem> ResearchTier5;
	public static RegistryObject<StaticPowerItem> ResearchTier6;
	public static RegistryObject<StaticPowerItem> ResearchTier7;

	public static void init(IEventBus eventBus) {
		ITEMS.register(eventBus);
		// Misc.
		StaticPowerRegistry.preRegisterItem(DistilleryGrain = new StaticPowerItem("distillery_grain"));
		StaticPowerRegistry.preRegisterItem(BedFrame = new StaticPowerItem("bed_frame"));

		// Ingots
		StaticPowerRegistry.preRegisterItem(IngotTin = new StaticPowerItem("ingot_tin"));
		StaticPowerRegistry.preRegisterItem(IngotZinc = new StaticPowerItem("ingot_zinc"));
		StaticPowerRegistry.preRegisterItem(IngotSilver = new StaticPowerItem("ingot_silver"));
		StaticPowerRegistry.preRegisterItem(IngotLead = new StaticPowerItem("ingot_lead"));
		StaticPowerRegistry.preRegisterItem(IngotMagnesium = new StaticPowerItem("ingot_magnesium"));
		StaticPowerRegistry.preRegisterItem(IngotTungsten = new StaticPowerItem("ingot_tungsten"));
		StaticPowerRegistry.preRegisterItem(IngotPlatinum = new StaticPowerItem("ingot_platinum"));
		StaticPowerRegistry.preRegisterItem(IngotBrass = new StaticPowerItem("ingot_brass"));
		StaticPowerRegistry.preRegisterItem(IngotBronze = new StaticPowerItem("ingot_bronze"));
		StaticPowerRegistry.preRegisterItem(IngotAluminum = new StaticPowerItem("ingot_aluminum"));
		StaticPowerRegistry.preRegisterItem(IngotStatic = new StaticPowerItem("ingot_static"));
		StaticPowerRegistry.preRegisterItem(IngotEnergized = new StaticPowerItem("ingot_energized"));
		StaticPowerRegistry.preRegisterItem(IngotLumum = new StaticPowerItem("ingot_lumum"));
		StaticPowerRegistry.preRegisterItem(IngotInertInfusion = new StaticPowerItem("ingot_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(IngotRedstoneAlloy = new StaticPowerItem("ingot_redstone_alloy"));

		// Heated Ingots
		StaticPowerRegistry.preRegisterItem(IngotTinHeated = new HeatedIngot("ingot_tin_heated", () -> ModItems.IngotTin));
		StaticPowerRegistry.preRegisterItem(IngotZincHeated = new HeatedIngot("ingot_zinc_heated", () -> ModItems.IngotZinc));
		StaticPowerRegistry.preRegisterItem(IngotSilverHeated = new HeatedIngot("ingot_silver_heated", () -> ModItems.IngotSilver));
		StaticPowerRegistry.preRegisterItem(IngotLeadHeated = new HeatedIngot("ingot_lead_heated", () -> ModItems.IngotLead));
		StaticPowerRegistry.preRegisterItem(IngotMagnesiumHeated = new HeatedIngot("ingot_magnesium_heated", () -> ModItems.IngotMagnesium));
		StaticPowerRegistry.preRegisterItem(IngotTungstenHeated = new HeatedIngot("ingot_tungsten_heated", () -> ModItems.IngotTungsten));
		StaticPowerRegistry.preRegisterItem(IngotPlatinumHeated = new HeatedIngot("ingot_platinum_heated", () -> ModItems.IngotPlatinum));
		StaticPowerRegistry.preRegisterItem(IngotBrassHeated = new HeatedIngot("ingot_brass_heated", () -> ModItems.IngotBrass));
		StaticPowerRegistry.preRegisterItem(IngotBronzeHeated = new HeatedIngot("ingot_bronze_heated", () -> ModItems.IngotBronze));
		StaticPowerRegistry.preRegisterItem(IngotAluminumHeated = new HeatedIngot("ingot_aluminum_heated", () -> ModItems.IngotAluminum));
		StaticPowerRegistry.preRegisterItem(IngotStaticHeated = new HeatedIngot("ingot_static_heated", () -> ModItems.IngotStatic));
		StaticPowerRegistry.preRegisterItem(IngotEnergizedHeated = new HeatedIngot("ingot_energized_heated", () -> ModItems.IngotEnergized));
		StaticPowerRegistry.preRegisterItem(IngotLumumHeated = new HeatedIngot("ingot_lumum_heated", () -> ModItems.IngotLumum));
		StaticPowerRegistry.preRegisterItem(IngotInertInfusionHeated = new HeatedIngot("ingot_inert_infusion_heated", () -> ModItems.IngotInertInfusion));
		StaticPowerRegistry.preRegisterItem(IngotRedstoneAlloyHeated = new HeatedIngot("ingot_redstone_alloy_heated", () -> ModItems.IngotRedstoneAlloy));

		StaticPowerRegistry.preRegisterItem(IngotIronHeated = new HeatedIngot("ingot_iron_heated", () -> Items.IRON_INGOT));
		StaticPowerRegistry.preRegisterItem(IngotCopperHeated = new HeatedIngot("ingot_copper_heated", () -> Items.COPPER_INGOT));
		StaticPowerRegistry.preRegisterItem(IngotGoldHeated = new HeatedIngot("ingot_gold_heated", () -> Items.GOLD_INGOT));

		// Raw Ores
		StaticPowerRegistry.preRegisterItem(RawRustyIron = new StaticPowerItem("raw_rusty_iron"));
		StaticPowerRegistry.preRegisterItem(RawTin = new StaticPowerItem("raw_tin"));
		StaticPowerRegistry.preRegisterItem(RawZinc = new StaticPowerItem("raw_zinc"));
		StaticPowerRegistry.preRegisterItem(RawSilver = new StaticPowerItem("raw_silver"));
		StaticPowerRegistry.preRegisterItem(RawLead = new StaticPowerItem("raw_lead"));
		StaticPowerRegistry.preRegisterItem(RawMagnesium = new StaticPowerItem("raw_magnesium"));
		StaticPowerRegistry.preRegisterItem(RawTungsten = new StaticPowerItem("raw_tungsten"));
		StaticPowerRegistry.preRegisterItem(RawPlatinum = new StaticPowerItem("raw_platinum"));
		StaticPowerRegistry.preRegisterItem(RawAluminum = new StaticPowerItem("raw_aluminum"));
		StaticPowerRegistry.preRegisterItem(RawStatic = new StaticPowerItem("raw_static"));
		StaticPowerRegistry.preRegisterItem(RawEnergized = new StaticPowerItem("raw_energized"));
		StaticPowerRegistry.preRegisterItem(RawLumum = new StaticPowerItem("raw_lumum"));

		// Nuggets
		StaticPowerRegistry.preRegisterItem(NuggetCopper = new StaticPowerItem("nugget_copper"));
		StaticPowerRegistry.preRegisterItem(NuggetTin = new StaticPowerItem("nugget_tin"));
		StaticPowerRegistry.preRegisterItem(NuggetZinc = new StaticPowerItem("nugget_zinc"));
		StaticPowerRegistry.preRegisterItem(NuggetSilver = new StaticPowerItem("nugget_silver"));
		StaticPowerRegistry.preRegisterItem(NuggetLead = new StaticPowerItem("nugget_lead"));
		StaticPowerRegistry.preRegisterItem(NuggetMagnesium = new StaticPowerItem("nugget_magnesium"));
		StaticPowerRegistry.preRegisterItem(NuggetTungsten = new StaticPowerItem("nugget_tungsten"));
		StaticPowerRegistry.preRegisterItem(NuggetPlatinum = new StaticPowerItem("nugget_platinum"));
		StaticPowerRegistry.preRegisterItem(NuggetAluminum = new StaticPowerItem("nugget_aluminum"));
		StaticPowerRegistry.preRegisterItem(NuggetStatic = new StaticPowerItem("nugget_static"));
		StaticPowerRegistry.preRegisterItem(NuggetEnergized = new StaticPowerItem("nugget_energized"));
		StaticPowerRegistry.preRegisterItem(NuggetLumum = new StaticPowerItem("nugget_lumum"));
		StaticPowerRegistry.preRegisterItem(NuggetInertInfusion = new StaticPowerItem("nugget_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(NuggetRedstoneAlloy = new StaticPowerItem("nugget_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(NuggetBrass = new StaticPowerItem("nugget_brass"));
		StaticPowerRegistry.preRegisterItem(NuggetBronze = new StaticPowerItem("nugget_bronze"));

		// Food
		StaticPowerRegistry.preRegisterItem(WheatFlour = new StaticPowerItem("flour_wheat"));
		StaticPowerRegistry.preRegisterItem(PotatoFlour = new StaticPowerItem("flour_potato"));
		StaticPowerRegistry.preRegisterItem(PotatoBread = new StaticPowerFood("bread_potato", Foods.BREAD));
		StaticPowerRegistry.preRegisterItem(ApplePie = new StaticPowerFood("pie_apple", Foods.PUMPKIN_PIE));
		StaticPowerRegistry.preRegisterItem(StaticPie = new StaticPowerFood("pie_static", ModFoods.STATIC_PIE));
		StaticPowerRegistry.preRegisterItem(EnergizedPie = new StaticPowerFood("pie_energized", ModFoods.ENERGIZED_PIE));
		StaticPowerRegistry.preRegisterItem(LumumPie = new StaticPowerFood("pie_lumum", ModFoods.LUMUM_PIE));
		StaticPowerRegistry.preRegisterItem(RawSmutton = new StaticPowerFood("smutton_raw", ModFoods.SMUTTON));
		StaticPowerRegistry.preRegisterItem(CookedSmutton = new StaticPowerFood("smutton_cooked", ModFoods.COOKED_SMUTTON));
		StaticPowerRegistry.preRegisterItem(RawEeef = new StaticPowerFood("eeef_raw", ModFoods.EEEF));
		StaticPowerRegistry.preRegisterItem(CookedEeef = new StaticPowerFood("eeef_cooked", ModFoods.COOKED_EEEF));
		StaticPowerRegistry.preRegisterItem(Eather = new StaticPowerItem("eather"));

		// Molds
		StaticPowerRegistry.preRegisterItem(MoldBlank = new StaticPowerItem("mold_blank"));
		StaticPowerRegistry.preRegisterItem(MoldPlate = new StaticPowerItem("mold_plate"));
		StaticPowerRegistry.preRegisterItem(MoldWire = new StaticPowerItem("mold_wire"));
		StaticPowerRegistry.preRegisterItem(MoldGear = new StaticPowerItem("mold_gear"));
		StaticPowerRegistry.preRegisterItem(MoldBlade = new StaticPowerItem("mold_blade"));
		StaticPowerRegistry.preRegisterItem(MoldIngot = new StaticPowerItem("mold_ingot"));
		StaticPowerRegistry.preRegisterItem(MoldNugget = new StaticPowerItem("mold_nugget"));
		StaticPowerRegistry.preRegisterItem(MoldBlock = new StaticPowerItem("mold_block"));
		StaticPowerRegistry.preRegisterItem(MoldDrillBit = new StaticPowerItem("mold_drill_bit"));
		StaticPowerRegistry.preRegisterItem(MoldRod = new StaticPowerItem("mold_rod"));

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

		// Turbine Blades
		StaticPowerRegistry.preRegisterItem(WoodTurbineBlades = new TurbineBlades("turbine_blades_wood", StaticPowerTiers.WOOD, StaticPowerAdditionalModels.TURBINE_BLADES_WOOD));
		StaticPowerRegistry
				.preRegisterItem(BasicTurbineBlades = new TurbineBlades("turbine_blades_basic", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.TURBINE_BLADES_BASIC));
		StaticPowerRegistry.preRegisterItem(
				AdvancedTurbineBlades = new TurbineBlades("turbine_blades_advanced", StaticPowerTiers.ADVANCED, StaticPowerAdditionalModels.TURBINE_BLADES_ADVANCED));
		StaticPowerRegistry
				.preRegisterItem(StaticTurbineBlades = new TurbineBlades("turbine_blades_static", StaticPowerTiers.STATIC, StaticPowerAdditionalModels.TURBINE_BLADES_STATIC));
		StaticPowerRegistry.preRegisterItem(
				EnergizedTurbineBlades = new TurbineBlades("turbine_blades_energized", StaticPowerTiers.ENERGIZED, StaticPowerAdditionalModels.TURBINE_BLADES_ENERGIZED));
		StaticPowerRegistry
				.preRegisterItem(LumumTurbineBlades = new TurbineBlades("turbine_blades_lumum", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.TURBINE_BLADES_LUMUM));
		StaticPowerRegistry.preRegisterItem(
				CreativeTurbineBlades = new TurbineBlades("turbine_blades_creative", StaticPowerTiers.CREATIVE, StaticPowerAdditionalModels.TURBINE_BLADES_CREATIVE));

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
		StaticPowerRegistry.preRegisterItem(IronMetalHammer = new Hammer("hammer_iron", StaticPowerTiers.IRON, Items.IRON_INGOT));
		StaticPowerRegistry.preRegisterItem(ZincMetalHammer = new Hammer("hammer_zinc", StaticPowerTiers.ZINC, ModItems.IngotZinc));
		StaticPowerRegistry.preRegisterItem(CopperMetalHammer = new Hammer("hammer_copper", StaticPowerTiers.COPPER, Items.COPPER_INGOT));
		StaticPowerRegistry.preRegisterItem(TinMetalHammer = new Hammer("hammer_tin", StaticPowerTiers.TIN, ModItems.IngotTin));
		StaticPowerRegistry.preRegisterItem(BronzeMetalHammer = new Hammer("hammer_bronze", StaticPowerTiers.BRONZE, ModItems.IngotBronze));
		StaticPowerRegistry.preRegisterItem(TungstenMetalHammer = new Hammer("hammer_tungsten", StaticPowerTiers.TUNGSTEN, ModItems.IngotTungsten));
		StaticPowerRegistry.preRegisterItem(CreativeMetalHammer = new Hammer("hammer_creative", StaticPowerTiers.CREATIVE, Items.AIR));

		StaticPowerRegistry.preRegisterItem(IronWireCutters = new WireCutters("wire_cutters_iron", StaticPowerTiers.IRON, Items.IRON_INGOT));
		StaticPowerRegistry.preRegisterItem(ZincWireCutters = new WireCutters("wire_cutters_zinc", StaticPowerTiers.ZINC, ModItems.IngotZinc));
		StaticPowerRegistry.preRegisterItem(BronzeWireCutters = new WireCutters("wire_cutters_bronze", StaticPowerTiers.BRONZE, ModItems.IngotBronze));
		StaticPowerRegistry.preRegisterItem(TungstenWireCutters = new WireCutters("wire_cutters_tungsten", StaticPowerTiers.TUNGSTEN, ModItems.IngotTungsten));
		StaticPowerRegistry.preRegisterItem(CreativeWireCutters = new WireCutters("wire_cutters_creative", StaticPowerTiers.CREATIVE, Items.AIR));

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
		StaticPowerRegistry.preRegisterItem(ElectringSolderingIron = new ElectricSolderingIron("soldering_iron_electric", 1000 * CapabilityStaticVolt.mSV_TO_SV));

		StaticPowerRegistry.preRegisterItem(Wrench = new StaticWrench("wrench"));
		StaticPowerRegistry.preRegisterItem(StaticWrench = new StaticWrench("wrench_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedWrench = new StaticWrench("wrench_energized"));
		StaticPowerRegistry.preRegisterItem(LumumWrench = new StaticWrench("wrench_lumum"));

		StaticPowerRegistry.preRegisterItem(WeakMagnet = new StaticPowerItem("magnet_weak"));
		StaticPowerRegistry.preRegisterItem(BasicMagnet = new Magnet("magnet_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(AdvancedMagnet = new Magnet("magnet_advanced", StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(StaticMagnet = new Magnet("magnet_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedMagnet = new Magnet("magnet_energized", StaticPowerTiers.ENERGIZED));
		;
		StaticPowerRegistry.preRegisterItem(LumumMagnet = new Magnet("magnet_lumum", StaticPowerTiers.LUMUM));

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
		StaticPowerRegistry.preRegisterItem(PortableSmeltingCore = new StaticPowerItem("portable_smelting_core"));
		StaticPowerRegistry.preRegisterItem(Slag = new StaticPowerItem("slag"));

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
		StaticPowerRegistry.preRegisterItem(WireAluminum = new StaticPowerItem("wire_aluminum"));
		StaticPowerRegistry.preRegisterItem(CoilCopper = new StaticPowerItem("coil_copper"));
		StaticPowerRegistry.preRegisterItem(CoilSilver = new StaticPowerItem("coil_silver"));
		StaticPowerRegistry.preRegisterItem(CoilGold = new StaticPowerItem("coil_gold"));
		StaticPowerRegistry.preRegisterItem(CoilPlatinum = new StaticPowerItem("coil_platinum"));
		StaticPowerRegistry.preRegisterItem(CoilAluminum = new StaticPowerItem("coil_aluminum"));
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
		StaticPowerRegistry.preRegisterItem(PlateAluminum = new StaticPowerItem("plate_aluminum"));
		StaticPowerRegistry.preRegisterItem(PlateStatic = new StaticPowerItem("plate_static"));
		StaticPowerRegistry.preRegisterItem(PlateEnergized = new StaticPowerItem("plate_energized"));
		StaticPowerRegistry.preRegisterItem(PlateLumum = new StaticPowerItem("plate_lumum"));
		StaticPowerRegistry.preRegisterItem(PlateInertInfusion = new StaticPowerItem("plate_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(PlateRedstoneAlloy = new StaticPowerItem("plate_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(PlateBrass = new StaticPowerItem("plate_brass"));
		StaticPowerRegistry.preRegisterItem(PlateBronze = new StaticPowerItem("plate_bronze"));
		StaticPowerRegistry.preRegisterItem(RustyIronScrap = new StaticPowerItem("rusty_iron_scrap"));

		// Gears
		StaticPowerRegistry.preRegisterItem(GearCopper = new StaticPowerItem("gear_copper"));
		StaticPowerRegistry.preRegisterItem(GearTin = new StaticPowerItem("gear_tin"));
		StaticPowerRegistry.preRegisterItem(GearZinc = new StaticPowerItem("gear_zinc"));
		StaticPowerRegistry.preRegisterItem(GearSilver = new StaticPowerItem("gear_silver"));
		StaticPowerRegistry.preRegisterItem(GearLead = new StaticPowerItem("gear_lead"));
		StaticPowerRegistry.preRegisterItem(GearTungsten = new StaticPowerItem("gear_tungsten"));
		StaticPowerRegistry.preRegisterItem(GearMagnesium = new StaticPowerItem("gear_magnesium"));
		StaticPowerRegistry.preRegisterItem(GearPlatinum = new StaticPowerItem("gear_platinum"));
		StaticPowerRegistry.preRegisterItem(GearAluminum = new StaticPowerItem("gear_aluminum"));
		StaticPowerRegistry.preRegisterItem(GearStatic = new StaticPowerItem("gear_static"));
		StaticPowerRegistry.preRegisterItem(GearEnergized = new StaticPowerItem("gear_energized"));
		StaticPowerRegistry.preRegisterItem(GearLumum = new StaticPowerItem("gear_lumum"));
		StaticPowerRegistry.preRegisterItem(GearInertInfusion = new StaticPowerItem("gear_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(GearRedstoneAlloy = new StaticPowerItem("gear_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(GearIron = new StaticPowerItem("gear_iron"));
		StaticPowerRegistry.preRegisterItem(GearGold = new StaticPowerItem("gear_gold"));
		StaticPowerRegistry.preRegisterItem(GearBrass = new StaticPowerItem("gear_brass"));
		StaticPowerRegistry.preRegisterItem(GearBronze = new StaticPowerItem("gear_bronze"));

		// Gear Boxes
		StaticPowerRegistry.preRegisterItem(GearBoxCopper = new GearBox("gear_box_copper", GearCopper));
		StaticPowerRegistry.preRegisterItem(GearBoxTin = new GearBox("gear_box_tin", GearTin));
		StaticPowerRegistry.preRegisterItem(GearBoxZinc = new GearBox("gear_box_zinc", GearZinc));
		StaticPowerRegistry.preRegisterItem(GearBoxSilver = new GearBox("gear_box_silver", GearSilver));
		StaticPowerRegistry.preRegisterItem(GearBoxLead = new GearBox("gear_box_lead", GearLead));
		StaticPowerRegistry.preRegisterItem(GearBoxTungsten = new GearBox("gear_box_tungsten", GearTungsten));
		StaticPowerRegistry.preRegisterItem(GearBoxMagnesium = new GearBox("gear_box_magnesium", GearMagnesium));
		StaticPowerRegistry.preRegisterItem(GearBoxPlatinum = new GearBox("gear_box_platinum", GearPlatinum));
		StaticPowerRegistry.preRegisterItem(GearBoxAluminum = new GearBox("gear_box_aluminum", GearAluminum));
		StaticPowerRegistry.preRegisterItem(GearBoxStatic = new GearBox("gear_box_static", GearStatic));
		StaticPowerRegistry.preRegisterItem(GearBoxEnergized = new GearBox("gear_box_energized", GearEnergized));
		StaticPowerRegistry.preRegisterItem(GearBoxLumum = new GearBox("gear_box_lumum", GearLumum));
		StaticPowerRegistry.preRegisterItem(GearBoxInertInfusion = new GearBox("gear_box_inert_infusion", GearInertInfusion));
		StaticPowerRegistry.preRegisterItem(GearBoxRedstoneAlloy = new GearBox("gear_box_redstone_alloy", GearRedstoneAlloy));
		StaticPowerRegistry.preRegisterItem(GearBoxIron = new GearBox("gear_box_iron", GearIron));
		StaticPowerRegistry.preRegisterItem(GearBoxGold = new GearBox("gear_box_gold", GearGold));
		StaticPowerRegistry.preRegisterItem(GearBoxBrass = new GearBox("gear_box_brass", GearBrass));
		StaticPowerRegistry.preRegisterItem(GearBoxBronze = new GearBox("gear_box_bronze", GearBronze));

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
		StaticPowerRegistry.preRegisterItem(DustAluminum = new StaticPowerItem("dust_aluminum"));
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

		// Rods
		StaticPowerRegistry.preRegisterItem(RodCopper = new StaticPowerItem("rod_copper"));
		StaticPowerRegistry.preRegisterItem(RodTin = new StaticPowerItem("rod_tin"));
		StaticPowerRegistry.preRegisterItem(RodZinc = new StaticPowerItem("rod_zinc"));
		StaticPowerRegistry.preRegisterItem(RodSilver = new StaticPowerItem("rod_silver"));
		StaticPowerRegistry.preRegisterItem(RodLead = new StaticPowerItem("rod_lead"));
		StaticPowerRegistry.preRegisterItem(RodTungsten = new StaticPowerItem("rod_tungsten"));
		StaticPowerRegistry.preRegisterItem(RodMagnesium = new StaticPowerItem("rod_magnesium"));
		StaticPowerRegistry.preRegisterItem(RodPlatinum = new StaticPowerItem("rod_platinum"));
		StaticPowerRegistry.preRegisterItem(RodAluminum = new StaticPowerItem("rod_aluminum"));
		StaticPowerRegistry.preRegisterItem(RodStatic = new StaticPowerItem("rod_static"));
		StaticPowerRegistry.preRegisterItem(RodEnergized = new StaticPowerItem("rod_energized"));
		StaticPowerRegistry.preRegisterItem(RodLumum = new StaticPowerItem("rod_lumum"));
		StaticPowerRegistry.preRegisterItem(RodInertInfusion = new StaticPowerItem("rod_inert_infusion"));
		StaticPowerRegistry.preRegisterItem(RodRedstoneAlloy = new StaticPowerItem("rod_redstone_alloy"));
		StaticPowerRegistry.preRegisterItem(RodCoal = new StaticPowerItem("rod_coal"));
		StaticPowerRegistry.preRegisterItem(RodGold = new StaticPowerItem("rod_gold"));
		StaticPowerRegistry.preRegisterItem(RodIron = new StaticPowerItem("rod_iron"));
		StaticPowerRegistry.preRegisterItem(RodRuby = new StaticPowerItem("rod_ruby"));
		StaticPowerRegistry.preRegisterItem(RodSapphire = new StaticPowerItem("rod_sapphire"));
		StaticPowerRegistry.preRegisterItem(RodEmerald = new StaticPowerItem("rod_emerald"));
		StaticPowerRegistry.preRegisterItem(RodDiamond = new StaticPowerItem("rod_diamond"));
		StaticPowerRegistry.preRegisterItem(RodQuartz = new StaticPowerItem("rod_quartz"));
		StaticPowerRegistry.preRegisterItem(RodBrass = new StaticPowerItem("rod_brass"));
		StaticPowerRegistry.preRegisterItem(RodBronze = new StaticPowerItem("rod_bronze"));
		StaticPowerRegistry.preRegisterItem(RodLapis = new StaticPowerItem("rod_lapis"));
		StaticPowerRegistry.preRegisterItem(RodLatex = new StaticPowerItem("rod_latex"));

		// Chunks
		StaticPowerRegistry.preRegisterItem(ChunksCopper = new StaticPowerItem("chunks_copper"));
		StaticPowerRegistry.preRegisterItem(ChunksTin = new StaticPowerItem("chunks_tin"));
		StaticPowerRegistry.preRegisterItem(ChunksZinc = new StaticPowerItem("chunks_zinc"));
		StaticPowerRegistry.preRegisterItem(ChunksSilver = new StaticPowerItem("chunks_silver"));
		StaticPowerRegistry.preRegisterItem(ChunksLead = new StaticPowerItem("chunks_lead"));
		StaticPowerRegistry.preRegisterItem(ChunksTungsten = new StaticPowerItem("chunks_tungsten"));
		StaticPowerRegistry.preRegisterItem(ChunksMagnesium = new StaticPowerItem("chunks_magnesium"));
		StaticPowerRegistry.preRegisterItem(ChunksPlatinum = new StaticPowerItem("chunks_platinum"));
		StaticPowerRegistry.preRegisterItem(ChunksAluminum = new StaticPowerItem("chunks_aluminum"));
		StaticPowerRegistry.preRegisterItem(ChunksCoal = new StaticPowerItem("chunks_coal"));
		StaticPowerRegistry.preRegisterItem(ChunksGold = new StaticPowerItem("chunks_gold"));
		StaticPowerRegistry.preRegisterItem(ChunksIron = new StaticPowerItem("chunks_iron"));
		StaticPowerRegistry.preRegisterItem(ChunksRuby = new StaticPowerItem("chunks_ruby"));
		StaticPowerRegistry.preRegisterItem(ChunksSapphire = new StaticPowerItem("chunks_sapphire"));
		StaticPowerRegistry.preRegisterItem(ChunksEmerald = new StaticPowerItem("chunks_emerald"));
		StaticPowerRegistry.preRegisterItem(ChunksDiamond = new StaticPowerItem("chunks_diamond"));
		StaticPowerRegistry.preRegisterItem(ChunksLapis = new StaticPowerItem("chunks_lapis"));
		StaticPowerRegistry.preRegisterItem(ChunksRedstone = new StaticPowerItem("chunks_redstone"));
		StaticPowerRegistry.preRegisterItem(ChunksQuartz = new StaticPowerItem("chunks_quartz"));

		// Upgrade Plates
		StaticPowerRegistry.preRegisterItem(BasicUpgradePlate = new StaticPowerItem("upgrade_plate_basic"));
		StaticPowerRegistry.preRegisterItem(AdvancedUpgradePlate = new StaticPowerItem("upgrade_plate_advanced"));
		StaticPowerRegistry.preRegisterItem(StaticUpgradePlate = new StaticPowerItem("upgrade_plate_static"));
		StaticPowerRegistry.preRegisterItem(EnergizedUpgradePlate = new StaticPowerItem("upgrade_plate_energized"));
		StaticPowerRegistry.preRegisterItem(LumumUpgradePlate = new StaticPowerItem("upgrade_plate_lumum"));

		// Drill Bits
		StaticPowerRegistry.preRegisterItem(IronDrillBit = new DrillBit("drill_bit_iron", Tiers.IRON, StaticPowerTiers.IRON));
		StaticPowerRegistry.preRegisterItem(BronzeDrillBit = new DrillBit("drill_bit_bronze", Tiers.IRON, StaticPowerTiers.BRONZE));
		StaticPowerRegistry.preRegisterItem(AdvancedDrillBit = new DrillBit("drill_bit_advanced", Tiers.IRON, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(TungstenDrillBit = new DrillBit("drill_bit_tungsten", Tiers.NETHERITE, StaticPowerTiers.TUNGSTEN));
		StaticPowerRegistry.preRegisterItem(StaticDrillBit = new DrillBit("drill_bit_static", Tiers.DIAMOND, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedDrillBit = new DrillBit("drill_bit_energized", Tiers.DIAMOND, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumDrillBit = new DrillBit("drill_bit_lumum", Tiers.NETHERITE, StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeDrillBit = new DrillBit("drill_bit_creative", Tiers.NETHERITE, StaticPowerTiers.CREATIVE));

		// Blades
		StaticPowerRegistry.preRegisterItem(IronBlade = new Blade("blade_iron", Tiers.IRON, StaticPowerTiers.IRON));
		StaticPowerRegistry.preRegisterItem(BronzeBlade = new Blade("blade_bronze", Tiers.IRON, StaticPowerTiers.BRONZE));
		StaticPowerRegistry.preRegisterItem(AdvancedBlade = new Blade("blade_advanced", Tiers.IRON, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(TungstenBlade = new Blade("blade_tungsten", Tiers.NETHERITE, StaticPowerTiers.TUNGSTEN));
		StaticPowerRegistry.preRegisterItem(StaticBlade = new Blade("blade_static", Tiers.DIAMOND, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedBlade = new Blade("blade_energized", Tiers.DIAMOND, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumBlade = new Blade("blade_lumum", Tiers.NETHERITE, StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeBlade = new Blade("blade_creative", Tiers.NETHERITE, StaticPowerTiers.CREATIVE));

		// Chainsaw Blades
		StaticPowerRegistry.preRegisterItem(IronChainsawBlade = new ChainsawBlade("chainsaw_blade_iron", Tiers.IRON, StaticPowerTiers.IRON));
		StaticPowerRegistry.preRegisterItem(BronzeChainsawBlade = new ChainsawBlade("chainsaw_blade_bronze", Tiers.IRON, StaticPowerTiers.BRONZE));
		StaticPowerRegistry.preRegisterItem(AdvancedChainsawBlade = new ChainsawBlade("chainsaw_blade_advanced", Tiers.IRON, StaticPowerTiers.ADVANCED));
		StaticPowerRegistry.preRegisterItem(TungstenChainsawBlade = new ChainsawBlade("chainsaw_blade_tungsten", Tiers.NETHERITE, StaticPowerTiers.TUNGSTEN));
		StaticPowerRegistry.preRegisterItem(StaticChainsawBlade = new ChainsawBlade("chainsaw_blade_static", Tiers.DIAMOND, StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedChainsawBlade = new ChainsawBlade("chainsaw_blade_energized", Tiers.DIAMOND, StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumChainsawBlade = new ChainsawBlade("chainsaw_blade_lumum", Tiers.NETHERITE, StaticPowerTiers.LUMUM));
		StaticPowerRegistry.preRegisterItem(CreativeChainsawBlade = new ChainsawBlade("chainsaw_blade_creative", Tiers.NETHERITE, StaticPowerTiers.CREATIVE));

		StaticPowerRegistry.preRegisterItem(BasicExtractorAttachment = new ExtractorAttachment("cable_attachment_basic_extractor", StaticPowerTiers.BASIC,
				StaticPowerAdditionalModels.CABLE_BASIC_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(AdvancedExtractorAttachment = new ExtractorAttachment("cable_attachment_advanced_extractor", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.CABLE_ADVANCED_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(StaticExtractorAttachment = new ExtractorAttachment("cable_attachment_static_extractor", StaticPowerTiers.STATIC,
				StaticPowerAdditionalModels.CABLE_STATIC_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(EnergizedExtractorAttachment = new ExtractorAttachment("cable_attachment_energized_extractor", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.CABLE_ENERGIZED_EXTRACTOR_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(LumumExtractorAttachment = new ExtractorAttachment("cable_attachment_lumum_extractor", StaticPowerTiers.LUMUM,
				StaticPowerAdditionalModels.CABLE_LUMUM_EXTRACTOR_ATTACHMENT));

		StaticPowerRegistry.preRegisterItem(
				BasicFilterAttachment = new FilterAttachment("cable_attachment_basic_filter", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.CABLE_BASIC_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(AdvancedFilterAttachment = new FilterAttachment("cable_attachment_advanced_filter", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.CABLE_ADVANCED_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(StaticFilterAttachment = new FilterAttachment("cable_attachment_static_filter", StaticPowerTiers.STATIC,
				StaticPowerAdditionalModels.CABLE_STATIC_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(EnergizedFilterAttachment = new FilterAttachment("cable_attachment_energized_filter", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.CABLE_ENERGIZED_FILTER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(
				LumumFilterAttachment = new FilterAttachment("cable_attachment_lumum_filter", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.CABLE_LUMUM_FILTER_ATTACHMENT));

		StaticPowerRegistry.preRegisterItem(BasicRetrieverAttachment = new RetrieverAttachment("cable_attachment_basic_retriever", StaticPowerTiers.BASIC,
				StaticPowerAdditionalModels.CABLE_BASIC_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(AdvancedRetrieverAttachment = new RetrieverAttachment("cable_attachment_advanced_retriever", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.CABLE_ADVANCED_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(StaticRetrieverAttachment = new RetrieverAttachment("cable_attachment_static_retriever", StaticPowerTiers.STATIC,
				StaticPowerAdditionalModels.CABLE_STATIC_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(EnergizedRetrieverAttachment = new RetrieverAttachment("cable_attachment_energized_retriever", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.CABLE_ENERGIZED_RETRIEVER_ATTACHMENT));
		StaticPowerRegistry.preRegisterItem(LumumRetrieverAttachment = new RetrieverAttachment("cable_attachment_lumum_retriever", StaticPowerTiers.LUMUM,
				StaticPowerAdditionalModels.CABLE_LUMUM_RETRIEVER_ATTACHMENT));

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
		StaticPowerRegistry.preRegisterItem(DigistoreWirelessTerminal = new DigistoreWirelessTerminal("digistore_wireless_terminal"));

		StaticPowerRegistry
				.preRegisterItem(SprinklerAttachment = new SprinklerAttachment("cable_attachment_sprinkler", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.SPRINKLER));
		StaticPowerRegistry.preRegisterItem(DrainAttachment = new DrainAttachment("cable_attachment_drain", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.DRAIN));

		// Digistore Misc.
		StaticPowerRegistry.preRegisterItem(PatternCard = new DigistorePatternCard("digistore_pattern_card"));

		// Digistore Cards
		StaticPowerRegistry
				.preRegisterItem(BasicDigistoreCard = new DigistoreCard("digistore_card_basic", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.BASIC_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(
				AdvancedDigistoreCard = new DigistoreCard("digistore_card_advanced", StaticPowerTiers.ADVANCED, StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_CARD));
		StaticPowerRegistry
				.preRegisterItem(StaticDigistoreCard = new DigistoreCard("digistore_card_static", StaticPowerTiers.STATIC, StaticPowerAdditionalModels.STATIC_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(
				EnergizedDigistoreCard = new DigistoreCard("digistore_card_energized", StaticPowerTiers.ENERGIZED, StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_CARD));
		StaticPowerRegistry
				.preRegisterItem(LumumDigistoreCard = new DigistoreCard("digistore_card_lumum", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.LUMUM_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(
				CreativeDigistoreCard = new DigistoreCard("digistore_card_creative", StaticPowerTiers.CREATIVE, StaticPowerAdditionalModels.CREATIVE_DIGISTORE_CARD, true));

		StaticPowerRegistry.preRegisterItem(
				BasicStackedDigistoreCard = new DigistoreStackedCard("digistore_card_stacked_basic", StaticPowerTiers.BASIC, StaticPowerAdditionalModels.BASIC_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(AdvancedStackedDigistoreCard = new DigistoreStackedCard("digistore_card_stacked_advanced", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(
				StaticStackedDigistoreCard = new DigistoreStackedCard("digistore_card_stacked_static", StaticPowerTiers.STATIC, StaticPowerAdditionalModels.STATIC_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(EnergizedStackedDigistoreCard = new DigistoreStackedCard("digistore_card_stacked_energized", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(
				LumumStackedDigistoreCard = new DigistoreStackedCard("digistore_card_stacked_lumum", StaticPowerTiers.LUMUM, StaticPowerAdditionalModels.LUMUM_DIGISTORE_CARD));
		StaticPowerRegistry.preRegisterItem(CreativeStackedDigistoreCard = new DigistoreStackedCard("digistore_card_stacked_creative", StaticPowerTiers.CREATIVE,
				StaticPowerAdditionalModels.CREATIVE_DIGISTORE_CARD, true));

		StaticPowerRegistry.preRegisterItem(BasicSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_basic", StaticPowerTiers.BASIC,
				StaticPowerAdditionalModels.BASIC_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(AdvancedSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_advanced", StaticPowerTiers.ADVANCED,
				StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(StaticSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_static", StaticPowerTiers.STATIC,
				StaticPowerAdditionalModels.STATIC_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(EnergizedSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_energized", StaticPowerTiers.ENERGIZED,
				StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_SINGULAR_CARD));
		StaticPowerRegistry.preRegisterItem(LumumSingularDigistoreCard = new DigistoreMonoCard("digistore_card_singular_lumum", StaticPowerTiers.LUMUM,
				StaticPowerAdditionalModels.LUMUM_DIGISTORE_SINGULAR_CARD));
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

		// Research
		StaticPowerRegistry.preRegisterItem(ResearchTier1 = new ResearchItem("research_tier_1", MinecraftColor.RED.getColor(), 1));
		StaticPowerRegistry.preRegisterItem(ResearchTier2 = new ResearchItem("research_tier_2", MinecraftColor.WHITE.getColor(), 2));
		StaticPowerRegistry.preRegisterItem(ResearchTier3 = new ResearchItem("research_tier_3", MinecraftColor.YELLOW.getColor(), 3));
		StaticPowerRegistry.preRegisterItem(ResearchTier4 = new ResearchItem("research_tier_4", MinecraftColor.LIME.getColor(), 4));
		StaticPowerRegistry.preRegisterItem(ResearchTier5 = new ResearchItem("research_tier_5", MinecraftColor.CYAN.getColor(), 5));
		StaticPowerRegistry.preRegisterItem(ResearchTier6 = new ResearchItem("research_tier_6", MinecraftColor.MAGENTA.getColor(), 6));
		StaticPowerRegistry.preRegisterItem(ResearchTier7 = new ResearchItem("research_tier_7", MinecraftColor.BLACK.getColor().copy().lighten(0.1f, 0.1f, 0.1f, 0.0f), 7));
	}
}
