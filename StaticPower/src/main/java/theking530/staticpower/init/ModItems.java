package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.cablenetwork.attachment.CableCover;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
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
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.init.tags.ModItemTags;
import theking530.staticpower.items.BatteryPack;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.items.DigistorePatternCard;
import theking530.staticpower.items.DigistoreStackedCard;
import theking530.staticpower.items.JuiceBottleItem;
import theking530.staticpower.items.MilkBottleItem;
import theking530.staticpower.items.PortableBattery;
import theking530.staticpower.items.StaticPowerFood;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.items.backpack.Backpack;
import theking530.staticpower.items.crops.DepletedFruit;
import theking530.staticpower.items.crops.StaticCropFruit;
import theking530.staticpower.items.crops.StaticPlantSeeds;
import theking530.staticpower.items.fluidcapsule.FluidCapsule;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.tools.CableNetworkAnalyzer;
import theking530.staticpower.items.tools.CoverSaw;
import theking530.staticpower.items.tools.DigistoreWirelessTerminal;
import theking530.staticpower.items.tools.ElectricSolderingIron;
import theking530.staticpower.items.tools.Hammer;
import theking530.staticpower.items.tools.Magnet;
import theking530.staticpower.items.tools.Multimeter;
import theking530.staticpower.items.tools.SolderingIron;
import theking530.staticpower.items.tools.StaticWrench;
import theking530.staticpower.items.tools.Themometer;
import theking530.staticpower.items.tools.TurbineBlades;
import theking530.staticpower.items.tools.WireCutters;
import theking530.staticpower.items.tools.chainsaw.Chainsaw;
import theking530.staticpower.items.tools.chainsaw.ChainsawBlade;
import theking530.staticpower.items.tools.miningdrill.DrillBit;
import theking530.staticpower.items.tools.miningdrill.MiningDrill;
import theking530.staticpower.items.tools.sword.Blade;
import theking530.staticpower.items.upgrades.AcceleratorUpgrade;
import theking530.staticpower.items.upgrades.BaseCentrifugeUpgrade;
import theking530.staticpower.items.upgrades.BaseHeatCapacityUpgrade;
import theking530.staticpower.items.upgrades.BaseHeatUpgrade;
import theking530.staticpower.items.upgrades.BaseOutputMultiplierUpgrade;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;
import theking530.staticpower.items.upgrades.BaseTransformerUpgrade;
import theking530.staticpower.items.upgrades.CraftingUpgrade;
import theking530.staticpower.items.upgrades.ExperienceVacuumUpgrade;
import theking530.staticpower.items.upgrades.StackUpgrade;
import theking530.staticpower.items.upgrades.TeleportUpgrade;
import theking530.staticpower.items.upgrades.VoidUpgrade;
import theking530.staticpower.items.wirecoils.DigistoreWireCoil;

public class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			StaticPower.MOD_ID);

	public static final RegistryObject<StaticPowerItem> BedFrame = ITEMS.register("bed_frame",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> DistilleryGrain = ITEMS.register("distillery_grain",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> RustyIronScrap = ITEMS.register("rusty_iron_scrap",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> ApplePie = ITEMS.register("pie_apple",
			() -> new StaticPowerFood(Foods.PUMPKIN_PIE));
	public static final RegistryObject<StaticPowerItem> StaticPie = ITEMS.register("pie_static",
			() -> new StaticPowerFood(ModFoods.STATIC_PIE));
	public static final RegistryObject<StaticPowerItem> EnergizedPie = ITEMS.register("pie_energized",
			() -> new StaticPowerFood(ModFoods.ENERGIZED_PIE));
	public static final RegistryObject<StaticPowerItem> LumumPie = ITEMS.register("pie_lumum",
			() -> new StaticPowerFood(ModFoods.LUMUM_PIE));
	public static final RegistryObject<StaticPowerItem> CookedEeef = ITEMS.register("eeef_cooked",
			() -> new StaticPowerFood(ModFoods.COOKED_EEEF));
	public static final RegistryObject<StaticPowerItem> CookedSmutton = ITEMS.register("smutton_cooked",
			() -> new StaticPowerFood(ModFoods.COOKED_SMUTTON));
	public static final RegistryObject<StaticPowerItem> Eather = ITEMS.register("eather", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> PotatoBread = ITEMS.register("bread_potato",
			() -> new StaticPowerFood(Foods.BREAD));
	public static final RegistryObject<StaticPowerItem> PotatoFlour = ITEMS.register("flour_potato",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> RawEeef = ITEMS.register("eeef_raw",
			() -> new StaticPowerFood(ModFoods.EEEF));
	public static final RegistryObject<StaticPowerItem> RawSmutton = ITEMS.register("smutton_raw",
			() -> new StaticPowerFood(ModFoods.SMUTTON));
	public static final RegistryObject<StaticPowerItem> WheatFlour = ITEMS.register("flour_wheat",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> MoldBlade = ITEMS.register("mold_blade",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldBlank = ITEMS.register("mold_blank",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldBlock = ITEMS.register("mold_block",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldDrillBit = ITEMS.register("mold_drill_bit",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldGear = ITEMS.register("mold_gear",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldIngot = ITEMS.register("mold_ingot",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldNugget = ITEMS.register("mold_nugget",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldPlate = ITEMS.register("mold_plate",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldRod = ITEMS.register("mold_rod",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MoldWire = ITEMS.register("mold_wire",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> RawRustyIron = ITEMS.register("raw_rusty_iron",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> DustObsidian = ITEMS.register("dust_obsidian",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> DustSaltpeter = ITEMS.register("dust_saltpeter",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> DustSulfur = ITEMS.register("dust_sulfur",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> DustCoalSmall = ITEMS.register("dust_coal_small",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> DustCharcoalSmall = ITEMS.register("dust_charcoal_small",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> RawSilicon = ITEMS.register("raw_silicon",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> Silicon = ITEMS.register("silicon",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> StaticDopedSilicon = ITEMS.register("silicon_doped_static",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> EnergizedDopedSilicon = ITEMS
			.register("silicon_doped_energized", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> LumumDopedSilicon = ITEMS.register("silicon_doped_lumum",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> CrystalStatic = ITEMS.register("crystal_static",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> CrystalEnergized = ITEMS.register("crystal_energized",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> CrystalLumum = ITEMS.register("crystal_lumum",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> DustWood = ITEMS.register("dust_wood",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> LatexChunk = ITEMS.register("latex_chunk",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> RubberWoodBark = ITEMS.register("rubber_wood_bark",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> PortableSmeltingCore = ITEMS.register("portable_smelting_core",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> Slag = ITEMS.register("slag", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> MemoryChip = ITEMS.register("memory_chip",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> LogicGatePowerSync = ITEMS.register("logic_gate_power_sync",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> InvertedLogicGatePowerSync = ITEMS
			.register("inverted_logic_gate_power_sync", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> Servo = ITEMS.register("servo", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> Diode = ITEMS.register("diode", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> Transistor = ITEMS.register("transistor",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> InternalClock = ITEMS.register("internal_clock",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> IOPort = ITEMS.register("io_port", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> Motor = ITEMS.register("motor", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> Plug = ITEMS.register("plug", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> DigistoreCore = ITEMS.register("digistore_core",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> BasicCard = ITEMS.register("card_basic",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> AdvancedCard = ITEMS.register("card_advanced",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> StaticCard = ITEMS.register("card_static",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> EnergizedCard = ITEMS.register("card_energized",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> LumumCard = ITEMS.register("card_lumum",
			() -> new StaticPowerItem());

	public static final Map<MinecraftColor, RegistryObject<StaticPowerItem>> Lightbulbs = new HashMap<>();
	static {
		for (MinecraftColor color : MinecraftColor.values()) {
			Lightbulbs.put(color, ITEMS.register("light_bulb_" + color.getName(), () -> new StaticPowerItem()));
		}
	}

	public static final RegistryObject<TurbineBlades> WoodTurbineBlades = ITEMS.register("turbine_blades_wood",
			() -> new TurbineBlades(StaticCoreTiers.WOOD, StaticPowerAdditionalModels.TURBINE_BLADES_WOOD));
	public static final RegistryObject<TurbineBlades> BasicTurbineBlades = ITEMS.register("turbine_blades_basic",
			() -> new TurbineBlades(StaticCoreTiers.BASIC, StaticPowerAdditionalModels.TURBINE_BLADES_BASIC));
	public static final RegistryObject<TurbineBlades> AdvancedTurbineBlades = ITEMS.register("turbine_blades_advanced",
			() -> new TurbineBlades(StaticCoreTiers.ADVANCED, StaticPowerAdditionalModels.TURBINE_BLADES_ADVANCED));
	public static final RegistryObject<TurbineBlades> StaticTurbineBlades = ITEMS.register("turbine_blades_static",
			() -> new TurbineBlades(StaticCoreTiers.STATIC, StaticPowerAdditionalModels.TURBINE_BLADES_STATIC));
	public static final RegistryObject<TurbineBlades> EnergizedTurbineBlades = ITEMS.register(
			"turbine_blades_energized",
			() -> new TurbineBlades(StaticCoreTiers.ENERGIZED, StaticPowerAdditionalModels.TURBINE_BLADES_ENERGIZED));
	public static final RegistryObject<TurbineBlades> LumumTurbineBlades = ITEMS.register("turbine_blades_lumum",
			() -> new TurbineBlades(StaticCoreTiers.LUMUM, StaticPowerAdditionalModels.TURBINE_BLADES_LUMUM));
	public static final RegistryObject<TurbineBlades> CreativeTurbineBlades = ITEMS.register("turbine_blades_creative",
			() -> new TurbineBlades(StaticCoreTiers.CREATIVE, StaticPowerAdditionalModels.TURBINE_BLADES_CREATIVE));

	public static final RegistryObject<StaticPowerItem> BasicProcessor = ITEMS.register("processor_basic",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> AdvancedProcessor = ITEMS.register("processor_advanced",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> StaticProcessor = ITEMS.register("processor_static",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> EnergizedProcessor = ITEMS.register("processor_energized",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> LumumProcessor = ITEMS.register("processor_lumum",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> RubberBar = ITEMS.register("rubber_bar",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> RubberSheet = ITEMS.register("rubber_sheet",
			() -> new StaticPowerItem());

	public static final RegistryObject<StaticPowerItem> WireCoilDigistore = ITEMS.register("wire_coil_digistore",
			() -> new DigistoreWireCoil(new SDColor(0.205f, 0.347f, 0.617f), 0.02f, false, StaticCoreTiers.ADVANCED,
					ModCableModules.Digistore));

	public static final RegistryObject<DrillBit> IronDrillBit = ITEMS.register("drill_bit_iron",
			() -> new DrillBit(Tiers.IRON, StaticCoreTiers.IRON));
	public static final RegistryObject<DrillBit> BronzeDrillBit = ITEMS.register("drill_bit_bronze",
			() -> new DrillBit(Tiers.IRON, StaticCoreTiers.BRONZE));
	public static final RegistryObject<DrillBit> AdvancedDrillBit = ITEMS.register("drill_bit_advanced",
			() -> new DrillBit(Tiers.IRON, StaticCoreTiers.ADVANCED));
	public static final RegistryObject<DrillBit> TungstenDrillBit = ITEMS.register("drill_bit_tungsten",
			() -> new DrillBit(Tiers.NETHERITE, StaticCoreTiers.TUNGSTEN));
	public static final RegistryObject<DrillBit> StaticDrillBit = ITEMS.register("drill_bit_static",
			() -> new DrillBit(Tiers.DIAMOND, StaticCoreTiers.STATIC));
	public static final RegistryObject<DrillBit> EnergizedDrillBit = ITEMS.register("drill_bit_energized",
			() -> new DrillBit(Tiers.DIAMOND, StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<DrillBit> LumumDrillBit = ITEMS.register("drill_bit_lumum",
			() -> new DrillBit(Tiers.NETHERITE, StaticCoreTiers.LUMUM));
	public static final RegistryObject<DrillBit> CreativeDrillBit = ITEMS.register("drill_bit_creative",
			() -> new DrillBit(Tiers.NETHERITE, StaticCoreTiers.CREATIVE));

	public static final RegistryObject<Blade> IronBlade = ITEMS.register("blade_iron",
			() -> new Blade(Tiers.IRON, StaticCoreTiers.IRON));
	public static final RegistryObject<Blade> BronzeBlade = ITEMS.register("blade_bronze",
			() -> new Blade(Tiers.IRON, StaticCoreTiers.BRONZE));
	public static final RegistryObject<Blade> AdvancedBlade = ITEMS.register("blade_advanced",
			() -> new Blade(Tiers.IRON, StaticCoreTiers.ADVANCED));
	public static final RegistryObject<Blade> TungstenBlade = ITEMS.register("blade_tungsten",
			() -> new Blade(Tiers.NETHERITE, StaticCoreTiers.TUNGSTEN));
	public static final RegistryObject<Blade> StaticBlade = ITEMS.register("blade_static",
			() -> new Blade(Tiers.DIAMOND, StaticCoreTiers.STATIC));
	public static final RegistryObject<Blade> EnergizedBlade = ITEMS.register("blade_energized",
			() -> new Blade(Tiers.DIAMOND, StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<Blade> LumumBlade = ITEMS.register("blade_lumum",
			() -> new Blade(Tiers.NETHERITE, StaticCoreTiers.LUMUM));
	public static final RegistryObject<Blade> CreativeBlade = ITEMS.register("blade_creative",
			() -> new Blade(Tiers.NETHERITE, StaticCoreTiers.CREATIVE));

	public static final RegistryObject<ChainsawBlade> IronChainsawBlade = ITEMS.register("chainsaw_blade_iron",
			() -> new ChainsawBlade(Tiers.IRON, StaticCoreTiers.IRON));
	public static final RegistryObject<ChainsawBlade> BronzeChainsawBlade = ITEMS.register("chainsaw_blade_bronze",
			() -> new ChainsawBlade(Tiers.IRON, StaticCoreTiers.BRONZE));
	public static final RegistryObject<ChainsawBlade> AdvancedChainsawBlade = ITEMS.register("chainsaw_blade_advanced",
			() -> new ChainsawBlade(Tiers.IRON, StaticCoreTiers.ADVANCED));
	public static final RegistryObject<ChainsawBlade> TungstenChainsawBlade = ITEMS.register("chainsaw_blade_tungsten",
			() -> new ChainsawBlade(Tiers.NETHERITE, StaticCoreTiers.TUNGSTEN));
	public static final RegistryObject<ChainsawBlade> StaticChainsawBlade = ITEMS.register("chainsaw_blade_static",
			() -> new ChainsawBlade(Tiers.DIAMOND, StaticCoreTiers.STATIC));
	public static final RegistryObject<ChainsawBlade> EnergizedChainsawBlade = ITEMS
			.register("chainsaw_blade_energized", () -> new ChainsawBlade(Tiers.DIAMOND, StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<ChainsawBlade> LumumChainsawBlade = ITEMS.register("chainsaw_blade_lumum",
			() -> new ChainsawBlade(Tiers.NETHERITE, StaticCoreTiers.LUMUM));
	public static final RegistryObject<ChainsawBlade> CreativeChainsawBlade = ITEMS.register("chainsaw_blade_creative",
			() -> new ChainsawBlade(Tiers.NETHERITE, StaticCoreTiers.CREATIVE));

	public static final RegistryObject<FluidCapsule> IronFluidCapsule = ITEMS.register("fluid_capsule_iron",
			() -> new FluidCapsule(StaticCoreTiers.IRON));
	public static final RegistryObject<FluidCapsule> BasicFluidCapsule = ITEMS.register("fluid_capsule_basic",
			() -> new FluidCapsule(StaticCoreTiers.BASIC));
	public static final RegistryObject<FluidCapsule> AdvancedFluidCapsule = ITEMS.register("fluid_capsule_advanced",
			() -> new FluidCapsule(StaticCoreTiers.ADVANCED));
	public static final RegistryObject<FluidCapsule> StaticFluidCapsule = ITEMS.register("fluid_capsule_static",
			() -> new FluidCapsule(StaticCoreTiers.STATIC));
	public static final RegistryObject<FluidCapsule> EnergizedFluidCapsule = ITEMS.register("fluid_capsule_energized",
			() -> new FluidCapsule(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<FluidCapsule> LumumFluidCapsule = ITEMS.register("fluid_capsule_lumum",
			() -> new FluidCapsule(StaticCoreTiers.LUMUM));
	public static final RegistryObject<FluidCapsule> CreativeFluidCapsule = ITEMS.register("fluid_capsule_creative",
			() -> new FluidCapsule(StaticCoreTiers.IRON));

	public static final RegistryObject<StaticPowerItem> BasicUpgradePlate = ITEMS.register("upgrade_plate_basic",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> AdvancedUpgradePlate = ITEMS.register("upgrade_plate_advanced",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> StaticUpgradePlate = ITEMS.register("upgrade_plate_static",
			() -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> EnergizedUpgradePlate = ITEMS
			.register("upgrade_plate_energized", () -> new StaticPowerItem());
	public static final RegistryObject<StaticPowerItem> LumumUpgradePlate = ITEMS.register("upgrade_plate_lumum",
			() -> new StaticPowerItem());

	public static final RegistryObject<PortableBattery> BasicPortableBattery = ITEMS.register("portable_battery_basic",
			() -> new PortableBattery(StaticCoreTiers.BASIC));
	public static final RegistryObject<PortableBattery> AdvancedPortableBattery = ITEMS
			.register("portable_battery_advanced", () -> new PortableBattery(StaticCoreTiers.ADVANCED));
	public static final RegistryObject<PortableBattery> StaticPortableBattery = ITEMS
			.register("portable_battery_static", () -> new PortableBattery(StaticCoreTiers.STATIC));
	public static final RegistryObject<PortableBattery> EnergizedPortableBattery = ITEMS
			.register("portable_battery_energized", () -> new PortableBattery(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<PortableBattery> LumumPortableBattery = ITEMS.register("portable_battery_lumum",
			() -> new PortableBattery(StaticCoreTiers.LUMUM));
	public static final RegistryObject<PortableBattery> CreativePortableBattery = ITEMS
			.register("portable_battery_creative", () -> new PortableBattery(StaticCoreTiers.CREATIVE));

	public static final RegistryObject<Backpack> Backpack = ITEMS.register("backpack", () -> new Backpack(12));

	public static final RegistryObject<Backpack> BuildersBackPack = ITEMS.register("backpack_builder",
			() -> new Backpack(12, ModItemTags.BUILDER_BACKPACK));
	public static final RegistryObject<Backpack> DiggersBackPack = ITEMS.register("backpack_digger",
			() -> new Backpack(12, ModItemTags.DIGGER_BACKPACK));
	public static final RegistryObject<Backpack> HuntersPack = ITEMS.register("backpack_hunter",
			() -> new Backpack(12, ModItemTags.HUNTER_BACKPACK));
	public static final RegistryObject<Backpack> LumberjacksBackPack = ITEMS.register("backpack_lumberjack",
			() -> new Backpack(12, ModItemTags.LUMBERJACK_BACKPACK));
	public static final RegistryObject<Backpack> MinersBackpack = ITEMS.register("backpack_miner",
			() -> new Backpack(12, ModItemTags.MINER_BACKPACK));
	public static final RegistryObject<Backpack> FarmersBackpack = ITEMS.register("backpack_farmer",
			() -> new Backpack(12, ModItemTags.FARMER_BACKPACK));
	public static final RegistryObject<Backpack> EngineersBackpack = ITEMS.register("backpack_engineer",
			() -> new Backpack(12, ModItemTags.ENGINEER_BACKPACK));
	public static final RegistryObject<Backpack> ToolsBackpack = ITEMS.register("backpack_tool",
			() -> new Backpack(12, ModItemTags.TOOL_BACKPACK));

	public static final RegistryObject<BatteryPack> BasicBatteryPack = ITEMS.register("battery_pack_basic",
			() -> new BatteryPack(StaticCoreTiers.BASIC));
	public static final RegistryObject<BatteryPack> AdvancedBatteryPack = ITEMS.register("battery_pack_advanced",
			() -> new BatteryPack(StaticCoreTiers.ADVANCED));
	public static final RegistryObject<BatteryPack> StaticBatteryPack = ITEMS.register("battery_pack_static",
			() -> new BatteryPack(StaticCoreTiers.STATIC));
	public static final RegistryObject<BatteryPack> EnergizedBatteryPack = ITEMS.register("battery_pack_energized",
			() -> new BatteryPack(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BatteryPack> LumumBatteryPack = ITEMS.register("battery_pack_lumum",
			() -> new BatteryPack(StaticCoreTiers.LUMUM));
	public static final RegistryObject<BatteryPack> CreativeBatteryPack = ITEMS.register("battery_pack_creative",
			() -> new BatteryPack(StaticCoreTiers.CREATIVE));

	public static final RegistryObject<StaticPlantSeeds> StaticSeeds = ITEMS.register("seed_static",
			() -> new StaticPlantSeeds(ModBlocks.StaticCrop.get()));
	public static final RegistryObject<StaticPlantSeeds> EnergizedSeeds = ITEMS.register("seed_energized",
			() -> new StaticPlantSeeds(ModBlocks.EnergizedCrop.get()));
	public static final RegistryObject<StaticPlantSeeds> LumumSeeds = ITEMS.register("seed_lumum",
			() -> new StaticPlantSeeds(ModBlocks.LumumCrop.get()));

	public static final RegistryObject<StaticCropFruit> StaticFruit = ITEMS.register("fruit_static",
			() -> new StaticCropFruit(ModFoods.STATIC_FRUIT));
	public static final RegistryObject<StaticCropFruit> EnergizedFruit = ITEMS.register("fruit_energized",
			() -> new StaticCropFruit(ModFoods.ENERGIZED_FRUIT));
	public static final RegistryObject<StaticCropFruit> LumumFruit = ITEMS.register("fruit_lumum",
			() -> new StaticCropFruit(ModFoods.LUMUM_FRUIT));
	public static final RegistryObject<DepletedFruit> DepletedFruit = ITEMS.register("fruit_depleted",
			() -> new DepletedFruit());

	public static final RegistryObject<Hammer> IronMetalHammer = ITEMS.register("hammer_iron",
			() -> new Hammer(StaticCoreTiers.IRON, () -> Items.IRON_INGOT));
	public static final RegistryObject<Hammer> ZincMetalHammer = ITEMS.register("hammer_zinc",
			() -> new Hammer(StaticCoreTiers.ZINC, () -> ModMaterials.ZINC.get(MaterialTypes.INGOT).get()));
	public static final RegistryObject<Hammer> CopperMetalHammer = ITEMS.register("hammer_copper",
			() -> new Hammer(StaticCoreTiers.COPPER, () -> Items.COPPER_INGOT));
	public static final RegistryObject<Hammer> TinMetalHammer = ITEMS.register("hammer_tin",
			() -> new Hammer(StaticCoreTiers.TIN, ModMaterials.TIN.get(MaterialTypes.INGOT).getSupplier()));
	public static final RegistryObject<Hammer> BronzeMetalHammer = ITEMS.register("hammer_bronze",
			() -> new Hammer(StaticCoreTiers.BRONZE, () -> ModMaterials.BRONZE.get(MaterialTypes.INGOT).get()));
	public static final RegistryObject<Hammer> TungstenMetalHammer = ITEMS.register("hammer_tungsten",
			() -> new Hammer(StaticCoreTiers.TUNGSTEN, () -> ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).get()));
	public static final RegistryObject<Hammer> CreativeMetalHammer = ITEMS.register("hammer_creative",
			() -> new Hammer(StaticCoreTiers.CREATIVE, () -> Items.AIR));

	public static final RegistryObject<WireCutters> IronWireCutters = ITEMS.register("wire_cutters_iron",
			() -> new WireCutters(StaticCoreTiers.IRON, () -> Items.IRON_INGOT));
	public static final RegistryObject<WireCutters> ZincWireCutters = ITEMS.register("wire_cutters_zinc",
			() -> new WireCutters(StaticCoreTiers.ZINC, () -> ModMaterials.ZINC.get(MaterialTypes.INGOT).get()));
	public static final RegistryObject<WireCutters> BronzeWireCutters = ITEMS.register("wire_cutters_bronze",
			() -> new WireCutters(StaticCoreTiers.BRONZE, () -> ModMaterials.BRONZE.get(MaterialTypes.INGOT).get()));
	public static final RegistryObject<WireCutters> TungstenWireCutters = ITEMS.register("wire_cutters_tungsten",
			() -> new WireCutters(StaticCoreTiers.TUNGSTEN,
					() -> ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).get()));
	public static final RegistryObject<WireCutters> CreativeWireCutters = ITEMS.register("wire_cutters_creative",
			() -> new WireCutters(StaticCoreTiers.CREATIVE, () -> Items.AIR));

	public static final RegistryObject<SolderingIron> SolderingIron = ITEMS.register("soldering_iron",
			() -> new SolderingIron(100));
	public static final RegistryObject<ElectricSolderingIron> ElectringSolderingIron = ITEMS
			.register("soldering_iron_electric", () -> new ElectricSolderingIron());

	public static final RegistryObject<MiningDrill> BasicMiningDrill = ITEMS.register("mining_drill_basic",
			() -> new MiningDrill(5.0f, 5.0f, StaticCoreTiers.BASIC));
	public static final RegistryObject<MiningDrill> AdvancedMiningDrill = ITEMS.register("mining_drill_advanced",
			() -> new MiningDrill(5.0f, 5.0f, StaticCoreTiers.ADVANCED));
	public static final RegistryObject<MiningDrill> StaticMiningDrill = ITEMS.register("mining_drill_static",
			() -> new MiningDrill(5.0f, 5.0f, StaticCoreTiers.STATIC));
	public static final RegistryObject<MiningDrill> EnergizedMiningDrill = ITEMS.register("mining_drill_energized",
			() -> new MiningDrill(5.0f, 5.0f, StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<MiningDrill> LumumMiningDrill = ITEMS.register("mining_drill_lumum",
			() -> new MiningDrill(5.0f, 5.0f, StaticCoreTiers.LUMUM));

	public static final RegistryObject<Chainsaw> BasicChainsaw = ITEMS.register("chainsaw_basic",
			() -> new Chainsaw(5.0f, 5.0f, StaticCoreTiers.BASIC));
	public static final RegistryObject<Chainsaw> AdvancedChainsaw = ITEMS.register("chainsaw_advanced",
			() -> new Chainsaw(5.0f, 5.0f, StaticCoreTiers.ADVANCED));
	public static final RegistryObject<Chainsaw> StaticChainsaw = ITEMS.register("chainsaw_static",
			() -> new Chainsaw(5.0f, 5.0f, StaticCoreTiers.STATIC));
	public static final RegistryObject<Chainsaw> EnergizedChainsaw = ITEMS.register("chainsaw_energized",
			() -> new Chainsaw(5.0f, 5.0f, StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<Chainsaw> LumumChainsaw = ITEMS.register("chainsaw_lumum",
			() -> new Chainsaw(5.0f, 5.0f, StaticCoreTiers.LUMUM));

	public static final RegistryObject<StaticWrench> Wrench = ITEMS.register("wrench", () -> new StaticWrench());
	public static final RegistryObject<StaticWrench> StaticWrench = ITEMS.register("wrench_static",
			() -> new StaticWrench());
	public static final RegistryObject<StaticWrench> EnergizedWrench = ITEMS.register("wrench_energized",
			() -> new StaticWrench());
	public static final RegistryObject<StaticWrench> LumumWrench = ITEMS.register("wrench_lumum",
			() -> new StaticWrench());

	public static final RegistryObject<Themometer> Thermometer = ITEMS.register("thermometer", () -> new Themometer());

	public static final RegistryObject<StaticPowerItem> WeakMagnet = ITEMS.register("magnet_weak",
			() -> new StaticPowerItem());
	public static final RegistryObject<Magnet> BasicMagnet = ITEMS.register("magnet_basic",
			() -> new Magnet(StaticCoreTiers.BASIC));
	public static final RegistryObject<Magnet> AdvancedMagnet = ITEMS.register("magnet_advanced",
			() -> new Magnet(StaticCoreTiers.ADVANCED));
	public static final RegistryObject<Magnet> StaticMagnet = ITEMS.register("magnet_static",
			() -> new Magnet(StaticCoreTiers.STATIC));
	public static final RegistryObject<Magnet> EnergizedMagnet = ITEMS.register("magnet_energized",
			() -> new Magnet(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<Magnet> LumumMagnet = ITEMS.register("magnet_lumum",
			() -> new Magnet(StaticCoreTiers.LUMUM));

	public static final RegistryObject<CableNetworkAnalyzer> CableNetworkAnalyzer = ITEMS
			.register("cable_network_analyzer", () -> new CableNetworkAnalyzer());
	public static final RegistryObject<Multimeter> Multimeter = ITEMS.register("multimeter", () -> new Multimeter());

	public static final RegistryObject<CoverSaw> IronCoverSaw = ITEMS.register("saw_iron", () -> new CoverSaw(100));
	public static final RegistryObject<CoverSaw> TungstenCoverSaw = ITEMS.register("saw_tungsten",
			() -> new CoverSaw(2500));
	public static final RegistryObject<CoverSaw> DiamondCoverSaw = ITEMS.register("saw_diamond",
			() -> new CoverSaw(250));
	public static final RegistryObject<CoverSaw> RubyCoverSaw = ITEMS.register("saw_ruby", () -> new CoverSaw(500));
	public static final RegistryObject<CoverSaw> SapphireCoverSaw = ITEMS.register("saw_sapphire",
			() -> new CoverSaw(1000));

	public static final RegistryObject<ItemFilter> BasicFilter = ITEMS.register("filter_item_basic",
			() -> new ItemFilter(StaticCoreTiers.BASIC));
	public static final RegistryObject<ItemFilter> AdvancedFilter = ITEMS.register("filter_item_advanced",
			() -> new ItemFilter(StaticCoreTiers.ADVANCED));
	public static final RegistryObject<ItemFilter> StaticFilter = ITEMS.register("filter_item_static",
			() -> new ItemFilter(StaticCoreTiers.STATIC));
	public static final RegistryObject<ItemFilter> EnergizedFilter = ITEMS.register("filter_item_energized",
			() -> new ItemFilter(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<ItemFilter> LumumFilter = ITEMS.register("filter_item_lumum",
			() -> new ItemFilter(StaticCoreTiers.LUMUM));

	public static final RegistryObject<ExtractorAttachment> BasicExtractorAttachment = ITEMS
			.register("cable_attachment_basic_extractor", () -> new ExtractorAttachment(StaticCoreTiers.BASIC,
					StaticPowerAdditionalModels.CABLE_BASIC_EXTRACTOR_ATTACHMENT));
	public static final RegistryObject<ExtractorAttachment> AdvancedExtractorAttachment = ITEMS
			.register("cable_attachment_advanced_extractor", () -> new ExtractorAttachment(StaticCoreTiers.ADVANCED,
					StaticPowerAdditionalModels.CABLE_ADVANCED_EXTRACTOR_ATTACHMENT));
	public static final RegistryObject<ExtractorAttachment> StaticExtractorAttachment = ITEMS
			.register("cable_attachment_static_extractor", () -> new ExtractorAttachment(StaticCoreTiers.STATIC,
					StaticPowerAdditionalModels.CABLE_STATIC_EXTRACTOR_ATTACHMENT));
	public static final RegistryObject<ExtractorAttachment> EnergizedExtractorAttachment = ITEMS
			.register("cable_attachment_energized_extractor", () -> new ExtractorAttachment(StaticCoreTiers.ENERGIZED,
					StaticPowerAdditionalModels.CABLE_ENERGIZED_EXTRACTOR_ATTACHMENT));
	public static final RegistryObject<ExtractorAttachment> LumumExtractorAttachment = ITEMS
			.register("cable_attachment_lumum_extractor", () -> new ExtractorAttachment(StaticCoreTiers.LUMUM,
					StaticPowerAdditionalModels.CABLE_LUMUM_EXTRACTOR_ATTACHMENT));

	public static final RegistryObject<FilterAttachment> BasicFilterAttachment = ITEMS
			.register("cable_attachment_basic_filter", () -> new FilterAttachment(StaticCoreTiers.BASIC,
					StaticPowerAdditionalModels.CABLE_BASIC_FILTER_ATTACHMENT));
	public static final RegistryObject<FilterAttachment> AdvancedFilterAttachment = ITEMS
			.register("cable_attachment_advanced_filter", () -> new FilterAttachment(StaticCoreTiers.ADVANCED,
					StaticPowerAdditionalModels.CABLE_ADVANCED_FILTER_ATTACHMENT));
	public static final RegistryObject<FilterAttachment> StaticFilterAttachment = ITEMS
			.register("cable_attachment_static_filter", () -> new FilterAttachment(StaticCoreTiers.STATIC,
					StaticPowerAdditionalModels.CABLE_STATIC_FILTER_ATTACHMENT));
	public static final RegistryObject<FilterAttachment> EnergizedFilterAttachment = ITEMS
			.register("cable_attachment_energized_filter", () -> new FilterAttachment(StaticCoreTiers.ENERGIZED,
					StaticPowerAdditionalModels.CABLE_ENERGIZED_FILTER_ATTACHMENT));
	public static final RegistryObject<FilterAttachment> LumumFilterAttachment = ITEMS
			.register("cable_attachment_lumum_filter", () -> new FilterAttachment(StaticCoreTiers.LUMUM,
					StaticPowerAdditionalModels.CABLE_LUMUM_FILTER_ATTACHMENT));

	public static final RegistryObject<RetrieverAttachment> BasicRetrieverAttachment = ITEMS
			.register("cable_attachment_basic_retriever", () -> new RetrieverAttachment(StaticCoreTiers.BASIC,
					StaticPowerAdditionalModels.CABLE_BASIC_RETRIEVER_ATTACHMENT));
	public static final RegistryObject<RetrieverAttachment> AdvancedRetrieverAttachment = ITEMS
			.register("cable_attachment_advanced_retriever", () -> new RetrieverAttachment(StaticCoreTiers.ADVANCED,
					StaticPowerAdditionalModels.CABLE_ADVANCED_RETRIEVER_ATTACHMENT));
	public static final RegistryObject<RetrieverAttachment> StaticRetrieverAttachment = ITEMS
			.register("cable_attachment_static_retriever", () -> new RetrieverAttachment(StaticCoreTiers.STATIC,
					StaticPowerAdditionalModels.CABLE_STATIC_RETRIEVER_ATTACHMENT));
	public static final RegistryObject<RetrieverAttachment> EnergizedRetrieverAttachment = ITEMS
			.register("cable_attachment_energized_retriever", () -> new RetrieverAttachment(StaticCoreTiers.ENERGIZED,
					StaticPowerAdditionalModels.CABLE_ENERGIZED_RETRIEVER_ATTACHMENT));
	public static final RegistryObject<RetrieverAttachment> LumumRetrieverAttachment = ITEMS
			.register("cable_attachment_lumum_retriever", () -> new RetrieverAttachment(StaticCoreTiers.LUMUM,
					StaticPowerAdditionalModels.CABLE_LUMUM_RETRIEVER_ATTACHMENT));

	public static final RegistryObject<DigistoreExporterAttachment> ExporterAttachment = ITEMS
			.register("cable_attachment_digistore_exporter", () -> new DigistoreExporterAttachment());
	public static final RegistryObject<DigistoreImporterAttachment> ImporterAttachment = ITEMS
			.register("cable_attachment_digistore_importer", () -> new DigistoreImporterAttachment());
	public static final RegistryObject<DigistoreIOBusAttachment> IOBusAttachment = ITEMS
			.register("cable_attachment_digistore_io_bus", () -> new DigistoreIOBusAttachment());
	public static final RegistryObject<DigistoreRegulatorAttachment> RegulatorAttachment = ITEMS
			.register("cable_attachment_digistore_regulator", () -> new DigistoreRegulatorAttachment());
	public static final RegistryObject<DigistoreTerminal> DigistoreTerminalAttachment = ITEMS
			.register("cable_attachment_digistore_terminal", () -> new DigistoreTerminal());
	public static final RegistryObject<DigistoreCraftingTerminal> DigistoreCraftingTerminalAttachment = ITEMS
			.register("cable_attachment_digistore_crafting_terminal", () -> new DigistoreCraftingTerminal());
	public static final RegistryObject<DigistorePatternEncoder> DigistorePatternEncoderAttachment = ITEMS
			.register("cable_attachment_digistore_pattern_encoder", () -> new DigistorePatternEncoder());
	public static final RegistryObject<DigistoreCraftingInterfaceAttachment> DigistoreCraftingInterfaceAttachment = ITEMS
			.register("cable_attachment_digistore_crafting_interface",
					() -> new DigistoreCraftingInterfaceAttachment());
	public static final RegistryObject<DigistoreScreen> DigistoreScreenAttachment = ITEMS
			.register("cable_attachment_digistore_screen", () -> new DigistoreScreen());
	public static final RegistryObject<DigistoreLight> DigistoreLightAttachment = ITEMS
			.register("cable_attachment_digistore_light", () -> new DigistoreLight());

	public static final RegistryObject<SprinklerAttachment> SprinklerAttachment = ITEMS.register(
			"cable_attachment_sprinkler",
			() -> new SprinklerAttachment(StaticCoreTiers.BASIC, StaticPowerAdditionalModels.SPRINKLER));
	public static final RegistryObject<DrainAttachment> DrainAttachment = ITEMS.register("cable_attachment_drain",
			() -> new DrainAttachment(StaticCoreTiers.BASIC, StaticPowerAdditionalModels.DRAIN));

	public static final RegistryObject<DigistorePatternCard> PatternCard = ITEMS.register("digistore_pattern_card",
			() -> new DigistorePatternCard());
	public static final RegistryObject<DigistoreWirelessTerminal> DigistoreWirelessTerminal = ITEMS
			.register("digistore_wireless_terminal", () -> new DigistoreWirelessTerminal());

	public static final RegistryObject<CableCover> CableCover = ITEMS.register("cable_cover", () -> new CableCover());

	public static final RegistryObject<MilkBottleItem> MilkBottle = ITEMS.register("bottle_milk",
			() -> new MilkBottleItem(40));
	public static final RegistryObject<JuiceBottleItem> AppleJuiceBottle = ITEMS.register("bottle_juice_apple",
			() -> new JuiceBottleItem(40, 6, 8.0f));
	public static final RegistryObject<JuiceBottleItem> CarrotJuiceBottle = ITEMS.register("bottle_juice_carrot",
			() -> new JuiceBottleItem(40, 4, 6.0f));
	public static final RegistryObject<JuiceBottleItem> PumpkinJuiceBottle = ITEMS.register("bottle_juice_pumpkin",
			() -> new JuiceBottleItem(40, 6, 8.0f));
	public static final RegistryObject<JuiceBottleItem> MelonJuiceBottle = ITEMS.register("bottle_juice_melon",
			() -> new JuiceBottleItem(40, 6, 8.0f));
	public static final RegistryObject<JuiceBottleItem> BeetJuiceBottle = ITEMS.register("bottle_juice_beetroot",
			() -> new JuiceBottleItem(40, 4, 6.0f));
	public static final RegistryObject<JuiceBottleItem> BerryJuiceBottle = ITEMS.register("bottle_juice_berry",
			() -> new JuiceBottleItem(40, 8, 10.0f));

	public static final RegistryObject<DigistoreCard> BasicDigistoreCard = ITEMS.register("digistore_card_basic",
			() -> new DigistoreCard(StaticCoreTiers.BASIC, StaticPowerAdditionalModels.BASIC_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreCard> AdvancedDigistoreCard = ITEMS.register("digistore_card_advanced",
			() -> new DigistoreCard(StaticCoreTiers.ADVANCED, StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreCard> StaticDigistoreCard = ITEMS.register("digistore_card_static",
			() -> new DigistoreCard(StaticCoreTiers.STATIC, StaticPowerAdditionalModels.STATIC_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreCard> EnergizedDigistoreCard = ITEMS.register(
			"digistore_card_energized",
			() -> new DigistoreCard(StaticCoreTiers.ENERGIZED, StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreCard> LumumDigistoreCard = ITEMS.register("digistore_card_lumum",
			() -> new DigistoreCard(StaticCoreTiers.LUMUM, StaticPowerAdditionalModels.LUMUM_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreCard> CreativeDigistoreCard = ITEMS.register("digistore_card_creative",
			() -> new DigistoreCard(StaticCoreTiers.CREATIVE, StaticPowerAdditionalModels.CREATIVE_DIGISTORE_CARD,
					true));

	public static final RegistryObject<DigistoreStackedCard> BasicStackedDigistoreCard = ITEMS.register(
			"digistore_card_stacked_basic",
			() -> new DigistoreStackedCard(StaticCoreTiers.BASIC, StaticPowerAdditionalModels.BASIC_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreStackedCard> AdvancedStackedDigistoreCard = ITEMS
			.register("digistore_card_stacked_advanced", () -> new DigistoreStackedCard(StaticCoreTiers.ADVANCED,
					StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreStackedCard> StaticStackedDigistoreCard = ITEMS.register(
			"digistore_card_stacked_static",
			() -> new DigistoreStackedCard(StaticCoreTiers.STATIC, StaticPowerAdditionalModels.STATIC_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreStackedCard> EnergizedStackedDigistoreCard = ITEMS
			.register("digistore_card_stacked_energized", () -> new DigistoreStackedCard(StaticCoreTiers.ENERGIZED,
					StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreStackedCard> LumumStackedDigistoreCard = ITEMS.register(
			"digistore_card_stacked_lumum",
			() -> new DigistoreStackedCard(StaticCoreTiers.LUMUM, StaticPowerAdditionalModels.LUMUM_DIGISTORE_CARD));
	public static final RegistryObject<DigistoreStackedCard> CreativeStackedDigistoreCard = ITEMS
			.register("digistore_card_stacked_creative", () -> new DigistoreStackedCard(StaticCoreTiers.CREATIVE,
					StaticPowerAdditionalModels.CREATIVE_DIGISTORE_CARD, true));

	public static final RegistryObject<DigistoreMonoCard> BasicSingularDigistoreCard = ITEMS
			.register("digistore_card_singular_basic", () -> new DigistoreMonoCard(StaticCoreTiers.BASIC,
					StaticPowerAdditionalModels.BASIC_DIGISTORE_SINGULAR_CARD));
	public static final RegistryObject<DigistoreMonoCard> AdvancedSingularDigistoreCard = ITEMS
			.register("digistore_card_singular_advanced", () -> new DigistoreMonoCard(StaticCoreTiers.ADVANCED,
					StaticPowerAdditionalModels.ADVANCVED_DIGISTORE_SINGULAR_CARD));
	public static final RegistryObject<DigistoreMonoCard> StaticSingularDigistoreCard = ITEMS
			.register("digistore_card_singular_static", () -> new DigistoreMonoCard(StaticCoreTiers.STATIC,
					StaticPowerAdditionalModels.STATIC_DIGISTORE_SINGULAR_CARD));
	public static final RegistryObject<DigistoreMonoCard> EnergizedSingularDigistoreCard = ITEMS
			.register("digistore_card_singular_energized", () -> new DigistoreMonoCard(StaticCoreTiers.ENERGIZED,
					StaticPowerAdditionalModels.ENERGIZED_DIGISTORE_SINGULAR_CARD));
	public static final RegistryObject<DigistoreMonoCard> LumumSingularDigistoreCard = ITEMS
			.register("digistore_card_singular_lumum", () -> new DigistoreMonoCard(StaticCoreTiers.LUMUM,
					StaticPowerAdditionalModels.LUMUM_DIGISTORE_SINGULAR_CARD));
	public static final RegistryObject<DigistoreMonoCard> CreativeSingularDigistoreCard = ITEMS
			.register("digistore_card_singular_creative", () -> new DigistoreMonoCard(StaticCoreTiers.CREATIVE,
					StaticPowerAdditionalModels.CREATIVE_DIGISTORE_SINGULAR_CARD, true));

	// Upgrades
	public static final RegistryObject<TeleportUpgrade> TeleportUpgrade = ITEMS.register("upgrade_teleport",
			() -> new TeleportUpgrade());
	public static final RegistryObject<ExperienceVacuumUpgrade> ExperienceVacuumUpgrade = ITEMS
			.register("upgrade_experience_vacuum", () -> new ExperienceVacuumUpgrade());
	public static final RegistryObject<VoidUpgrade> VoidUpgrade = ITEMS.register("upgrade_void",
			() -> new VoidUpgrade());
	public static final RegistryObject<StackUpgrade> StackUpgrade = ITEMS.register("upgrade_stack",
			() -> new StackUpgrade());
	public static final RegistryObject<AcceleratorUpgrade> AcceleratorUpgrade = ITEMS.register("upgrade_accelerator",
			() -> new AcceleratorUpgrade());
	public static final RegistryObject<CraftingUpgrade> CraftingUpgrade = ITEMS.register("upgrade_crafting",
			() -> new CraftingUpgrade());

	public static final RegistryObject<BaseTransformerUpgrade> TransformerUpgradeMV = ITEMS
			.register("upgrade_transformer_advanced", () -> new BaseTransformerUpgrade(StaticCoreTiers.ADVANCED));
	public static final RegistryObject<BaseTransformerUpgrade> TransformerUpgradeHV = ITEMS
			.register("upgrade_transformer_static", () -> new BaseTransformerUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseTransformerUpgrade> TransformerUpgradeVHV = ITEMS
			.register("upgrade_transformer_energized", () -> new BaseTransformerUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseTransformerUpgrade> TransformerUpgradeEV = ITEMS
			.register("upgrade_transformer_lumum", () -> new BaseTransformerUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BasePowerUpgrade> BasicPowerUpgrade = ITEMS.register("upgrade_power_basic",
			() -> new BasePowerUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BasePowerUpgrade> StaticPowerUpgrade = ITEMS.register("upgrade_power_static",
			() -> new BasePowerUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BasePowerUpgrade> EnergizedPowerUpgrade = ITEMS
			.register("upgrade_power_energized", () -> new BasePowerUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BasePowerUpgrade> LumumPowerUpgrade = ITEMS.register("upgrade_power_lumum",
			() -> new BasePowerUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BaseOutputMultiplierUpgrade> BasicOutputMultiplierUpgrade = ITEMS
			.register("upgrade_output_multiplier_basic", () -> new BaseOutputMultiplierUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BaseOutputMultiplierUpgrade> StaticOutputMultiplierUpgrade = ITEMS.register(
			"upgrade_output_multiplier_static", () -> new BaseOutputMultiplierUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseOutputMultiplierUpgrade> EnergizedOutputMultiplierUpgrade = ITEMS.register(
			"upgrade_output_multiplier_energized", () -> new BaseOutputMultiplierUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseOutputMultiplierUpgrade> LumumOutputMultiplierUpgrade = ITEMS
			.register("upgrade_output_multiplier_lumum", () -> new BaseOutputMultiplierUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BaseSpeedUpgrade> BasicSpeedUpgrade = ITEMS.register("upgrade_speed_basic",
			() -> new BaseSpeedUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BaseSpeedUpgrade> StaticSpeedUpgrade = ITEMS.register("upgrade_speed_static",
			() -> new BaseSpeedUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseSpeedUpgrade> EnergizedSpeedUpgrade = ITEMS
			.register("upgrade_speed_energized", () -> new BaseSpeedUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseSpeedUpgrade> LumumSpeedUpgrade = ITEMS.register("upgrade_speed_lumum",
			() -> new BaseSpeedUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BaseRangeUpgrade> BasicRangeUpgrade = ITEMS.register("upgrade_range_basic",
			() -> new BaseRangeUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BaseRangeUpgrade> StaticRangeUpgrade = ITEMS.register("upgrade_range_static",
			() -> new BaseRangeUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseRangeUpgrade> EnergizedRangeUpgrade = ITEMS
			.register("upgrade_range_energized", () -> new BaseRangeUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseRangeUpgrade> LumumRangeUpgrade = ITEMS.register("upgrade_range_lumum",
			() -> new BaseRangeUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BaseTankUpgrade> BasicTankUpgrade = ITEMS.register("upgrade_tank_basic",
			() -> new BaseTankUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BaseTankUpgrade> StaticTankUpgrade = ITEMS.register("upgrade_tank_static",
			() -> new BaseTankUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseTankUpgrade> EnergizedTankUpgrade = ITEMS.register("upgrade_tank_energized",
			() -> new BaseTankUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseTankUpgrade> LumumTankUpgrade = ITEMS.register("upgrade_tank_lumum",
			() -> new BaseTankUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BaseCentrifugeUpgrade> BasicCentrifugeUpgrade = ITEMS
			.register("upgrade_centrifuge_basic", () -> new BaseCentrifugeUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BaseCentrifugeUpgrade> StaticCentrifugeUpgrade = ITEMS
			.register("upgrade_centrifuge_static", () -> new BaseCentrifugeUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseCentrifugeUpgrade> EnergizedCentrifugeUpgrade = ITEMS
			.register("upgrade_centrifuge_energized", () -> new BaseCentrifugeUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseCentrifugeUpgrade> LumumCentrifugeUpgrade = ITEMS
			.register("upgrade_centrifuge_lumum", () -> new BaseCentrifugeUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BaseHeatCapacityUpgrade> BasicHeatCapacityUpgrade = ITEMS
			.register("upgrade_heat_capacity_basic", () -> new BaseHeatCapacityUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BaseHeatCapacityUpgrade> StaticHeatCapacityUpgrade = ITEMS
			.register("upgrade_heat_capacity_static", () -> new BaseHeatCapacityUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseHeatCapacityUpgrade> EnergizedHeatCapacityUpgrade = ITEMS
			.register("upgrade_heat_capacity_energized", () -> new BaseHeatCapacityUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseHeatCapacityUpgrade> LumumHeatCapacityUpgrade = ITEMS
			.register("upgrade_heat_capacity_lumum", () -> new BaseHeatCapacityUpgrade(StaticCoreTiers.LUMUM));

	public static final RegistryObject<BaseHeatUpgrade> BasicHeatUpgrade = ITEMS.register("upgrade_heat_basic",
			() -> new BaseHeatUpgrade(StaticCoreTiers.BASIC));
	public static final RegistryObject<BaseHeatUpgrade> StaticHeatUpgrade = ITEMS.register("upgrade_heat_static",
			() -> new BaseHeatUpgrade(StaticCoreTiers.STATIC));
	public static final RegistryObject<BaseHeatUpgrade> EnergizedHeatUpgrade = ITEMS.register("upgrade_heat_energized",
			() -> new BaseHeatUpgrade(StaticCoreTiers.ENERGIZED));
	public static final RegistryObject<BaseHeatUpgrade> LumumHeatUpgrade = ITEMS.register("upgrade_heat_lumum",
			() -> new BaseHeatUpgrade(StaticCoreTiers.LUMUM));;

	public static void init(IEventBus eventBus) {
		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			bundle.generateItems(ITEMS);
		}
		ITEMS.register(eventBus);
	}

	public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> block) {
		return ITEMS.register(name, block);
	}
}
