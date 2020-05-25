package theking530.staticpower.initialization;

import net.minecraft.item.Item;
import theking530.staticpower.Registry;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.items.book.StaticPowerBook;

public class ModItems {
	public static Item StaticPowerBook;
	
	public static Item DistilleryGrain;
	
	public static Item WheatFlour;
	public static Item PotatoFlour;
	public static Item PotatoBread;
	public static Item ApplePie;
	public static Item StaticPie;
	public static Item EnergizedPie;
	public static Item LumumPie;

	public static Item MoldBlank;
	public static Item MoldPlate;
	public static Item MoldWire;
	public static Item MoldGear;

	public static Item IngotCopper;
	public static Item IngotTin;
	public static Item IngotZinc;
	public static Item IngotSilver;
	public static Item IngotLead;
	public static Item IngotTungsten;
	public static Item IngotMagnesium;
	public static Item IngotPlatinum;
	public static Item IngotNickel;
	public static Item IngotAluminium;
	public static Item IngotStatic;
	public static Item IngotEnergized;
	public static Item IngotLumum;
	public static Item IngotInertInfusion;
	public static Item IngotRedstoneAlloy;
	public static Item IngotBrass;
	public static Item IngotBronze;

	public static Item NuggetCopper;
	public static Item NuggetTin;
	public static Item NuggetZinc;
	public static Item NuggetSilver;
	public static Item NuggetLead;
	public static Item NuggetTungsten;
	public static Item NuggetMagnesium;
	public static Item NuggetPlatinum;
	public static Item NuggetNickel;
	public static Item NuggetAluminium;
	public static Item NuggetStatic;
	public static Item NuggetEnergized;
	public static Item NuggetLumum;
	public static Item NuggetInertInfusion;
	public static Item NuggetRedstoneAlloy;
	public static Item NuggetBrass;
	public static Item NuggetBronze;

	public static Item PlateIron;
	public static Item PlateGold;
	public static Item PlateCopper;
	public static Item PlateTin;
	public static Item PlateZinc;
	public static Item PlateSilver;
	public static Item PlateLead;
	public static Item PlateTungsten;
	public static Item PlateMagnesium;
	public static Item PlatePlatinum;
	public static Item PlateNickel;
	public static Item PlateAluminium;
	public static Item PlateStatic;
	public static Item PlateEnergized;
	public static Item PlateLumum;
	public static Item PlateInertInfusion;
	public static Item PlateRedstoneAlloy;
	public static Item PlateBrass;
	public static Item PlateBronze;

	public static Item GearCopper;
	public static Item GearTin;
	public static Item GearZinc;
	public static Item GearSilver;
	public static Item GearLead;
	public static Item GearTungsten;
	public static Item GearMagnesium;
	public static Item GearPlatinum;
	public static Item GearNickel;
	public static Item GearAluminium;
	public static Item GearStatic;
	public static Item GearEnergized;
	public static Item GearLumum;
	public static Item GearInertInfusion;
	public static Item GearRedstoneAlloy;
	public static Item GearIron;
	public static Item GearGold;
	public static Item GearBrass;
	public static Item GearBronze;

	public static Item DustCopper;
	public static Item DustTin;
	public static Item DustZinc;
	public static Item DustSilver;
	public static Item DustLead;
	public static Item DustTungsten;
	public static Item DustMagnesium;
	public static Item DustPlatinum;
	public static Item DustNickel;
	public static Item DustAluminium;
	public static Item DustStatic;
	public static Item DustEnergized;
	public static Item DustLumum;
	public static Item DustInertInfusion;
	public static Item DustRedstoneAlloy;
	public static Item DustBrass;
	public static Item DustBronze;

	public static Item DustCoal;
	public static Item DustObsidian;
	public static Item DustGold;
	public static Item DustIron;
	public static Item DustRuby;
	public static Item DustSapphire;
	public static Item DustSulfur;
	public static Item DustSaltpeter;
	public static Item DustCharcoal;
	public static Item DustStaticInfusion;
	public static Item DustEnergizedInfusion;
	public static Item DustLumumInfusion;

	public static Item DustCobalt;
	public static Item DustArdite;

	public static Item GemRuby;
	public static Item GemSapphire;

	public static Item Silicon;
	public static Item CrystalStatic;
	public static Item CrystalEnergized;
	public static Item CrystalLumum;
	public static Item DustWood;

	public static Item BasicProcessor;
	public static Item StaticProcessor;
	public static Item EnergizedProcessor;
	public static Item LumumProcessor;

	public static Item Rubber;
	public static Item IOPort;
	public static Item WireCopper;
	public static Item WireSilver;
	public static Item WireGold;
	public static Item CoilCopper;
	public static Item CoilSilver;
	public static Item CoilGold;

	public static Item MemoryChip;
	public static Item LogicGatePowerSync;
	public static Item InvertedLogicGatePowerSync;
	public static Item LogicGateServo;
	public static Item Diode;
	public static Item Transistor;
	public static Item InternalClock;

	public static Item BasicUpgradePlate;
	public static Item StaticUpgradePlate;
	public static Item EnergizedUpgradePlate;
	public static Item LumumUpgradePlate;

	public static void init(Registry registry) {
		// Book
		registry.PreRegisterItem(StaticPowerBook = new StaticPowerBook("tutorial_book"));
		
		// Misc.
		registry.PreRegisterItem(DistilleryGrain = new StaticPowerItem("distillery_grain"));

		// Food
		registry.PreRegisterItem(WheatFlour = new StaticPowerItem("flour_wheat"));
		registry.PreRegisterItem(PotatoFlour = new StaticPowerItem("flour_potato"));
		registry.PreRegisterItem(PotatoBread = new StaticPowerItem("bread_potato"));
		registry.PreRegisterItem(ApplePie = new StaticPowerItem("pie_apple"));
		registry.PreRegisterItem(StaticPie = new StaticPowerItem("pie_static"));
		registry.PreRegisterItem(EnergizedPie = new StaticPowerItem("pie_energized"));
		registry.PreRegisterItem(LumumPie = new StaticPowerItem("pie_lumum"));

		// Molds
		registry.PreRegisterItem(MoldBlank = new StaticPowerItem("mold_blank"));
		registry.PreRegisterItem(MoldPlate = new StaticPowerItem("mold_plate"));
		registry.PreRegisterItem(MoldWire = new StaticPowerItem("mold_wire"));
		registry.PreRegisterItem(MoldGear = new StaticPowerItem("mold_gear"));

		// Processors
		registry.PreRegisterItem(BasicProcessor = new StaticPowerItem("processor_basic"));
		registry.PreRegisterItem(StaticProcessor = new StaticPowerItem("processor_static"));
		registry.PreRegisterItem(EnergizedProcessor = new StaticPowerItem("processor_energized"));
		registry.PreRegisterItem(LumumProcessor = new StaticPowerItem("processor_lumum"));

		// Components
		registry.PreRegisterItem(MemoryChip = new StaticPowerItem("memory_chip"));
		registry.PreRegisterItem(LogicGatePowerSync = new StaticPowerItem("logic_gate_power_sync"));
		registry.PreRegisterItem(InvertedLogicGatePowerSync = new StaticPowerItem("inverted_logic_gate_power_sync"));
		registry.PreRegisterItem(LogicGateServo = new StaticPowerItem("logic_gate_servo"));
		registry.PreRegisterItem(Diode = new StaticPowerItem("diode"));
		registry.PreRegisterItem(Transistor = new StaticPowerItem("transistor"));
		registry.PreRegisterItem(InternalClock = new StaticPowerItem("internal_clock"));
		registry.PreRegisterItem(IOPort = new StaticPowerItem("io_port"));
		registry.PreRegisterItem(WireCopper = new StaticPowerItem("wire_copper"));
		registry.PreRegisterItem(WireSilver = new StaticPowerItem("wire_silver"));
		registry.PreRegisterItem(WireGold = new StaticPowerItem("wire_gold"));
		registry.PreRegisterItem(CoilCopper = new StaticPowerItem("coil_copper"));
		registry.PreRegisterItem(CoilSilver = new StaticPowerItem("coil_silver"));
		registry.PreRegisterItem(CoilGold = new StaticPowerItem("coil_gold"));

		// Materials
		registry.PreRegisterItem(GemRuby = new StaticPowerItem("gem_ruby"));
		registry.PreRegisterItem(GemSapphire = new StaticPowerItem("gem_sapphire"));
		registry.PreRegisterItem(Rubber = new StaticPowerItem("rubber"));
		registry.PreRegisterItem(Silicon = new StaticPowerItem("silicon"));
		registry.PreRegisterItem(CrystalStatic = new StaticPowerItem("crystal_static"));
		registry.PreRegisterItem(CrystalEnergized = new StaticPowerItem("crystal_energized"));
		registry.PreRegisterItem(CrystalLumum = new StaticPowerItem("crystal_lumum"));

		// Plates
		registry.PreRegisterItem(PlateIron = new StaticPowerItem("plate_iron"));
		registry.PreRegisterItem(PlateGold = new StaticPowerItem("plate_gold"));
		registry.PreRegisterItem(PlateCopper = new StaticPowerItem("plate_copper"));
		registry.PreRegisterItem(PlateTin = new StaticPowerItem("plate_tin"));
		registry.PreRegisterItem(PlateZinc = new StaticPowerItem("plate_zinc"));
		registry.PreRegisterItem(PlateSilver = new StaticPowerItem("plate_silver"));
		registry.PreRegisterItem(PlateLead = new StaticPowerItem("plate_lead"));
		registry.PreRegisterItem(PlateTungsten = new StaticPowerItem("plate_tungsten"));
		registry.PreRegisterItem(PlateMagnesium = new StaticPowerItem("plate_magnesium"));
		registry.PreRegisterItem(PlatePlatinum = new StaticPowerItem("plate_platinum"));
		registry.PreRegisterItem(PlateNickel = new StaticPowerItem("plate_nickel"));
		registry.PreRegisterItem(PlateAluminium = new StaticPowerItem("plate_aluminium"));
		registry.PreRegisterItem(PlateStatic = new StaticPowerItem("plate_static"));
		registry.PreRegisterItem(PlateEnergized = new StaticPowerItem("plate_energized"));
		registry.PreRegisterItem(PlateLumum = new StaticPowerItem("plate_lumum"));
		registry.PreRegisterItem(PlateInertInfusion = new StaticPowerItem("plate_inert_infusion"));
		registry.PreRegisterItem(PlateRedstoneAlloy = new StaticPowerItem("plate_redstone_alloy"));
		registry.PreRegisterItem(PlateBrass = new StaticPowerItem("plate_brass"));
		registry.PreRegisterItem(PlateBronze = new StaticPowerItem("plate_bronze"));
		
		// Gears
		registry.PreRegisterItem(GearCopper = new StaticPowerItem("gear_copper"));
		registry.PreRegisterItem(GearTin = new StaticPowerItem("gear_tin"));
		registry.PreRegisterItem(GearZinc = new StaticPowerItem("gear_zinc"));
		registry.PreRegisterItem(GearSilver = new StaticPowerItem("gear_silver"));
		registry.PreRegisterItem(GearLead = new StaticPowerItem("gear_lead"));
		registry.PreRegisterItem(GearTungsten = new StaticPowerItem("gear_tungsten"));
		registry.PreRegisterItem(GearMagnesium = new StaticPowerItem("gear_magnesium"));
		registry.PreRegisterItem(GearPlatinum = new StaticPowerItem("gear_platinum"));
		registry.PreRegisterItem(GearNickel = new StaticPowerItem("gear_nickel"));
		registry.PreRegisterItem(GearAluminium = new StaticPowerItem("gear_aluminium"));
		registry.PreRegisterItem(GearStatic = new StaticPowerItem("gear_static"));
		registry.PreRegisterItem(GearEnergized = new StaticPowerItem("gear_energized"));
		registry.PreRegisterItem(GearLumum = new StaticPowerItem("gear_lumum"));
		registry.PreRegisterItem(GearInertInfusion = new StaticPowerItem("gear_inert_infusion"));
		registry.PreRegisterItem(GearRedstoneAlloy = new StaticPowerItem("gear_redstone_alloy"));
		registry.PreRegisterItem(GearIron = new StaticPowerItem("gear_iron"));
		registry.PreRegisterItem(GearGold = new StaticPowerItem("gear_gold"));
		registry.PreRegisterItem(GearBrass = new StaticPowerItem("gear_brass"));
		registry.PreRegisterItem(GearBronze = new StaticPowerItem("gear_bronze"));
		
		// Dusts
		registry.PreRegisterItem(DustWood = new StaticPowerItem("dust_wood"));
		registry.PreRegisterItem(DustCopper = new StaticPowerItem("dust_copper"));
		registry.PreRegisterItem(DustTin = new StaticPowerItem("dust_tin"));
		registry.PreRegisterItem(DustZinc = new StaticPowerItem("dust_zinc"));
		registry.PreRegisterItem(DustSilver = new StaticPowerItem("dust_silver"));
		registry.PreRegisterItem(DustLead = new StaticPowerItem("dust_lead"));
		registry.PreRegisterItem(DustTungsten = new StaticPowerItem("dust_tungsten"));
		registry.PreRegisterItem(DustMagnesium = new StaticPowerItem("dust_magnesium"));
		registry.PreRegisterItem(DustPlatinum = new StaticPowerItem("dust_platinum"));
		registry.PreRegisterItem(DustNickel = new StaticPowerItem("dust_nickel"));
		registry.PreRegisterItem(DustAluminium = new StaticPowerItem("dust_aluminium"));
		registry.PreRegisterItem(DustStatic = new StaticPowerItem("dust_static"));
		registry.PreRegisterItem(DustEnergized = new StaticPowerItem("dust_energized"));
		registry.PreRegisterItem(DustLumum = new StaticPowerItem("dust_lumum"));
		registry.PreRegisterItem(DustInertInfusion = new StaticPowerItem("dust_inert_infusion"));
		registry.PreRegisterItem(DustRedstoneAlloy = new StaticPowerItem("dust_redstone_alloy"));
		registry.PreRegisterItem(DustCoal = new StaticPowerItem("dust_coal"));
		registry.PreRegisterItem(DustObsidian = new StaticPowerItem("dust_obsidian"));
		registry.PreRegisterItem(DustGold = new StaticPowerItem("dust_gold"));
		registry.PreRegisterItem(DustIron = new StaticPowerItem("dust_iron"));
		registry.PreRegisterItem(DustRuby = new StaticPowerItem("dust_ruby"));
		registry.PreRegisterItem(DustSapphire = new StaticPowerItem("dust_sapphire"));
		registry.PreRegisterItem(DustSulfur = new StaticPowerItem("dust_sulfur"));
		registry.PreRegisterItem(DustSaltpeter = new StaticPowerItem("dust_saltpeter"));
		registry.PreRegisterItem(DustCharcoal = new StaticPowerItem("dust_charcoal"));
		registry.PreRegisterItem(DustStaticInfusion = new StaticPowerItem("dust_static_infusion"));
		registry.PreRegisterItem(DustEnergizedInfusion = new StaticPowerItem("dust_energized_infusion"));
		registry.PreRegisterItem(DustLumumInfusion = new StaticPowerItem("dust_lumum_infusion"));
		registry.PreRegisterItem(DustBrass = new StaticPowerItem("dust_brass"));
		registry.PreRegisterItem(DustBronze = new StaticPowerItem("dust_bronze"));	
		registry.PreRegisterItem(DustCobalt = new StaticPowerItem("dust_cobolt"));
		registry.PreRegisterItem(DustArdite = new StaticPowerItem("dust_ardite"));

		// Upgrade Plates
		registry.PreRegisterItem(BasicUpgradePlate = new StaticPowerItem("upgrade_plate_basic"));
		registry.PreRegisterItem(StaticUpgradePlate = new StaticPowerItem("upgrade_plate_static"));
		registry.PreRegisterItem(EnergizedUpgradePlate = new StaticPowerItem("upgrade_plate_energized"));
		registry.PreRegisterItem(LumumUpgradePlate = new StaticPowerItem("upgrade_plate_lumum"));

		// Ingots
		registry.PreRegisterItem(IngotCopper = new StaticPowerItem("ingot_copper"));
		registry.PreRegisterItem(IngotTin = new StaticPowerItem("ingot_tin"));
		registry.PreRegisterItem(IngotZinc = new StaticPowerItem("ingot_zinc"));
		registry.PreRegisterItem(IngotSilver = new StaticPowerItem("ingot_silver"));
		registry.PreRegisterItem(IngotLead = new StaticPowerItem("ingot_lead"));
		registry.PreRegisterItem(IngotMagnesium = new StaticPowerItem("ingot_magnesium"));
		registry.PreRegisterItem(IngotTungsten = new StaticPowerItem("ingot_tungsten"));
		registry.PreRegisterItem(IngotPlatinum = new StaticPowerItem("ingot_platinum"));
		registry.PreRegisterItem(IngotNickel = new StaticPowerItem("ingot_nickel"));
		registry.PreRegisterItem(IngotBrass = new StaticPowerItem("ingot_brass"));
		registry.PreRegisterItem(IngotBronze = new StaticPowerItem("ingot_bronze"));
		registry.PreRegisterItem(IngotAluminium = new StaticPowerItem("ingot_aluminium"));
		registry.PreRegisterItem(IngotStatic = new StaticPowerItem("ingot_static"));
		registry.PreRegisterItem(IngotEnergized = new StaticPowerItem("ingot_energized"));
		registry.PreRegisterItem(IngotLumum = new StaticPowerItem("ingot_lumum"));
		registry.PreRegisterItem(IngotInertInfusion = new StaticPowerItem("ingot_inert_infusion"));
		registry.PreRegisterItem(IngotRedstoneAlloy = new StaticPowerItem("ingot_redstone_alloy"));

		// Nuggets
		registry.PreRegisterItem(NuggetCopper = new StaticPowerItem("nugget_copper"));
		registry.PreRegisterItem(NuggetTin = new StaticPowerItem("nugget_tin"));
		registry.PreRegisterItem(NuggetZinc = new StaticPowerItem("nugget_zinc"));
		registry.PreRegisterItem(NuggetSilver = new StaticPowerItem("nugget_silver"));
		registry.PreRegisterItem(NuggetLead = new StaticPowerItem("nugget_lead"));
		registry.PreRegisterItem(NuggetMagnesium = new StaticPowerItem("nugget_magnesium"));
		registry.PreRegisterItem(NuggetTungsten = new StaticPowerItem("nugget_tungsten"));
		registry.PreRegisterItem(NuggetPlatinum = new StaticPowerItem("nugget_platinum"));
		registry.PreRegisterItem(NuggetNickel = new StaticPowerItem("nugget_nickel"));
		registry.PreRegisterItem(NuggetAluminium = new StaticPowerItem("nugget_aluminium"));
		registry.PreRegisterItem(NuggetStatic = new StaticPowerItem("nugget_static"));
		registry.PreRegisterItem(NuggetEnergized = new StaticPowerItem("nugget_energized"));
		registry.PreRegisterItem(NuggetLumum = new StaticPowerItem("nugget_lumum"));
		registry.PreRegisterItem(NuggetInertInfusion = new StaticPowerItem("nugget_inert_infusion"));
		registry.PreRegisterItem(NuggetRedstoneAlloy = new StaticPowerItem("nugget_redstone_alloy"));
		registry.PreRegisterItem(NuggetBrass = new StaticPowerItem("nugget_brass"));
		registry.PreRegisterItem(NuggetBronze = new StaticPowerItem("nugget_bronze"));
	}
}
