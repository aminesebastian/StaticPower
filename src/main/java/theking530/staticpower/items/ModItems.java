package theking530.staticpower.items;

import net.minecraft.item.Item;
import theking530.staticpower.Registry;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.book.StaticPowerBook;
import theking530.staticpower.items.containers.BaseBattery;
import theking530.staticpower.items.containers.BaseFluidCapsule;
import theking530.staticpower.items.itemfilter.FilterTier;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.tools.BlockReader;
import theking530.staticpower.items.tools.CoordinateMarker;
import theking530.staticpower.items.tools.ElectricSolderingIron;
import theking530.staticpower.items.tools.MetalHammer;
import theking530.staticpower.items.tools.SolderingIron;
import theking530.staticpower.items.tools.StaticWrench;
import theking530.staticpower.items.tools.WireCutters;
import theking530.staticpower.items.upgrades.BaseDigistoreCapacityUpgrade;
import theking530.staticpower.items.upgrades.BaseOutputMultiplierUpgrade;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseQuarryingUpgrade;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;
import theking530.staticpower.items.upgrades.DigistoreMiscUpgrades;
import theking530.staticpower.items.upgrades.ExperienceVacuumUpgrade;
import theking530.staticpower.items.upgrades.TeleportUpgrade;

public class ModItems {
	public static SPItemMaterial Materials;
	
	public static Item BaseFluidCapsule;
	public static Item StaticFluidCapsule;
	public static Item EnergizedFluidCapsule;
	public static Item LumumFluidCapsule;
	
	public static Item Rubber;
	public static Item IOPort;
	public static Item CopperWire;
	public static Item SilverWire;
	public static Item GoldWire;
	public static Item CopperCoil;
	public static Item SilverCoil;
	public static Item GoldCoil;
	
	public static Item BlankMould;
	public static Item PlateMould;
	public static Item WireMould;
	
	public static Item BasicCircuit;
	public static Item StaticCircuit;
	public static Item EnergizedCircuit;
	public static Item LumumCircuit;
	
	public static Item BasicBattery;
	public static Item StaticBattery;
	public static Item EnergizedBattery;
	public static Item LumumBattery;
	
	public static Item BasicUpgradePlate;
	public static Item StaticUpgradePlate;
	public static Item EnergizedUpgradePlate;
	public static Item LumumUpgradePlate;
	
	public static Item TeleportUpgrade;
	public static Item ExperienceVacuumUpgrade;
	
	public static Item BasicSpeedUpgrade;
	public static Item StaticSpeedUpgrade;
	public static Item EnergizedSpeedUpgrade;
	public static Item LumumSpeedUpgrade;
	
	public static Item BasicRangeUpgrade;
	public static Item StaticRangeUpgrade;
	public static Item EnergizedRangeUpgrade;
	public static Item LumumRangeUpgrade;
	
	public static Item BasicTankUpgrade;
	public static Item StaticTankUpgrade;
	public static Item EnergizedTankUpgrade;
	public static Item LumumTankUpgrade;
	
	public static Item BasicPowerUpgrade;
	public static Item StaticPowerUpgrade;
	public static Item EnergizedPowerUpgrade;
	public static Item LumumPowerUpgrade;
	
	public static Item StaticEnergyCrystal;
	public static Item EnergizedEnergyCrystal;
	public static Item LumumEnergyCrystal;
	
	public static Item StaticInfusionBlend;
	public static Item EnergizedInfusionBlend;
	public static Item LumumInfusionBlend;
	
	public static Item StaticQuarryingUpgrade;
	public static Item EnergizedQuarryingUpgrade;
	public static Item LumumQuarryingUpgrade;
	
	public static Item CreativePowerUpgrade;
	public static Item CreativeTankUpgrade;
	public static Item CreativeSpeedUpgrade;

	public static BaseDigistoreCapacityUpgrade DigistoreCapacityUpgrade;
	public static DigistoreMiscUpgrades DigistoreMiscUpgrade;
	
	public static Item BasicOutputMultiplierUpgrade;
	public static Item StaticOutputMultiplierUpgrade;
	public static Item EnergizedOutputMultiplierUpgrade;
	public static Item LumumOutputMultiplierUpgrade;
	
	public static Item Silicon;
	public static Item MemoryChip;
	
	public static Item LogicGatePowerSync;
	public static Item InvertedLogicGatePowerSync;
	public static Item LogicGateServo;
	public static Item Diode;
	public static Item Transistor;
	public static Item InternalClock;
	
	public static Item StaticWrench;
	public static Item SolderingIron;
	public static Item ElectricSolderingIron;
	public static Item StaticBook;
	public static Item CoordinateMarker;
	public static Item WireCutters;
	public static Item MetalHammer;
	public static Item NetworkReader;
	
	public static Item BasicItemFilter;
	public static Item UpgradedItemFilter;
	public static Item AdvancedItemFilter;
	public static Item QuarryFilter;

	public static Item WheatFlour;
	public static Item PotatoFlour;
	public static Item PotatoBread;
	
	public static Item ApplePie;
	public static Item StaticPie;
	public static Item EnergizedPie;
	public static Item LumumPie;

	public static void init(Registry registry) {
		registry.PreRegisterItem(Materials = new SPItemMaterial());
		
		WheatFlour = new BaseFood("WheatFlour", 2);
		registry.PreRegisterItem(WheatFlour);	
		PotatoFlour = new BaseFood("PotatoFlour", 2);
		registry.PreRegisterItem(PotatoFlour);
		PotatoBread = new BaseFood("PotatoBread", 8);
		registry.PreRegisterItem(PotatoBread);
		
		ApplePie = new BaseFood("ApplePie", 6);
		registry.PreRegisterItem(ApplePie);
		StaticPie = new BaseFood("StaticPie", 7);
		registry.PreRegisterItem(StaticPie);
		EnergizedPie = new BaseFood("EnergizedPie", 8);
		registry.PreRegisterItem(EnergizedPie);
		LumumPie = new BaseFood("LumumPie", 9);
		registry.PreRegisterItem(LumumPie);
		
		StaticWrench = new StaticWrench().setUnlocalizedName("StaticWrench");
		registry.PreRegisterItem(StaticWrench);
		SolderingIron = new SolderingIron("SolderingIron", 20);
		registry.PreRegisterItem(SolderingIron);
		ElectricSolderingIron = new ElectricSolderingIron("ElectricSolderingIron", 50000);
		registry.PreRegisterItem(ElectricSolderingIron);
		CoordinateMarker = new CoordinateMarker("CoordinateMarker");
		registry.PreRegisterItem(CoordinateMarker);
		WireCutters = new WireCutters(64);
		registry.PreRegisterItem(WireCutters);
		MetalHammer = new MetalHammer(64);
		registry.PreRegisterItem(MetalHammer);
		NetworkReader = new BlockReader("NetworkReader");
		registry.PreRegisterItem(NetworkReader);
		
		registry.PreRegisterItem(Silicon = new ItemBase("Silicon"));
		registry.PreRegisterItem(MemoryChip = new ItemBase("MemoryChip"));
		registry.PreRegisterItem(LogicGatePowerSync = new ItemBase("LogicGatePowerSync"));
		registry.PreRegisterItem(InvertedLogicGatePowerSync = new ItemBase("InvertedLogicGatePowerSync"));
		registry.PreRegisterItem(LogicGateServo = new ItemBase("LogicGateServo"));
		registry.PreRegisterItem(Diode = new ItemBase("Diode"));
		registry.PreRegisterItem(Transistor = new ItemBase("Transistor"));
		registry.PreRegisterItem(InternalClock = new ItemBase("InternalClock"));
		
		BasicItemFilter = new ItemFilter("BasicItemFilter", FilterTier.BASIC);
		registry.PreRegisterItem(BasicItemFilter);
		UpgradedItemFilter = new ItemFilter("UpgradedItemFilter", FilterTier.UPGRADED);
		registry.PreRegisterItem(UpgradedItemFilter);
		AdvancedItemFilter = new ItemFilter("AdvancedItemFilter", FilterTier.ADVANCED);
		registry.PreRegisterItem(AdvancedItemFilter);

		registry.PreRegisterItem(Rubber = new ItemBase("Rubber"));
		registry.PreRegisterItem(IOPort = new ItemBase("IOPort"));
		
		
		BasicCircuit = new ItemBase("BasicCircuit");
		registry.PreRegisterItem(BasicCircuit);
		StaticCircuit = new ItemBase("StaticCircuit");
		registry.PreRegisterItem(StaticCircuit);
		EnergizedCircuit = new ItemBase("EnergizedCircuit");
		registry.PreRegisterItem(EnergizedCircuit);
		LumumCircuit = new ItemBase("LumumCircuit");
		registry.PreRegisterItem(LumumCircuit);
		
		BasicUpgradePlate = new ItemBase("BasicUpgradePlate");
		registry.PreRegisterItem(BasicUpgradePlate);
		StaticUpgradePlate = new ItemBase("StaticUpgradePlate");
		registry.PreRegisterItem(StaticUpgradePlate);
		EnergizedUpgradePlate = new ItemBase("EnergizedUpgradePlate");
		registry.PreRegisterItem(EnergizedUpgradePlate);
		LumumUpgradePlate = new ItemBase("LumumUpgradePlate");
		registry.PreRegisterItem(LumumUpgradePlate);
		
		registry.PreRegisterItem(CopperWire = new ItemBase("CopperWire"));
		registry.PreRegisterItem(SilverWire = new ItemBase("SilverWire"));
		registry.PreRegisterItem(GoldWire = new ItemBase("GoldWire"));
		registry.PreRegisterItem(CopperCoil = new ItemBase("CopperCoil"));
		registry.PreRegisterItem(SilverCoil = new ItemBase("SilverCoil"));
		registry.PreRegisterItem(GoldCoil = new ItemBase("GoldCoil"));
		
		registry.PreRegisterItem(PlateMould = new FormerMold("PlateMould"));
		registry.PreRegisterItem(WireMould = new FormerMold("WireMould"));
		registry.PreRegisterItem(BlankMould = new ItemBase("BlankMould"));
		
		EnergizedEnergyCrystal = new ItemBase("EnergizedEnergyCrystal");
		registry.PreRegisterItem(EnergizedEnergyCrystal);
		LumumEnergyCrystal = new ItemBase("LumumEnergyCrystal");
		registry.PreRegisterItem(LumumEnergyCrystal);
		
		EnergizedInfusionBlend = new ItemBase("EnergizedInfusionBlend");
		registry.PreRegisterItem(EnergizedInfusionBlend);
		LumumInfusionBlend = new ItemBase("LumumInfusionBlend");
		registry.PreRegisterItem(LumumInfusionBlend);
		
		BasicBattery = new BaseBattery("BasicPortableBattery", Tier.BASIC.getPortableBatteryCapacity());
		registry.PreRegisterItem(BasicBattery);
		StaticBattery = new BaseBattery("StaticPortableBattery", Tier.STATIC.getPortableBatteryCapacity());
		registry.PreRegisterItem(StaticBattery);
		EnergizedBattery = new BaseBattery("EnergizedPortableBattery", Tier.ENERGIZED.getPortableBatteryCapacity());
		registry.PreRegisterItem(EnergizedBattery);
		LumumBattery = new BaseBattery("LumumPortableBattery", Tier.LUMUM.getPortableBatteryCapacity());
		registry.PreRegisterItem(LumumBattery);
		
		registry.PreRegisterItem(StaticBook = new StaticPowerBook("StaticBook"));	
				
		CreativeSpeedUpgrade = new BaseSpeedUpgrade("CreativeSpeedUpgrade", Tier.CREATIVE);
		registry.PreRegisterItem(CreativeSpeedUpgrade);	
		/**
		CreativeTankUpgrade = new BaseTankUpgrade("CreativeTankUpgrade", Tier.CREATIVE);
		registry.PreRegisterItem(CreativeTankUpgrade);	
		CreativeSpeedUpgrade = new BasePowerUpgrade("CreativeSpeedUpgrade", Tier.CREATIVE);
		registry.PreRegisterItem(CreativeSpeedUpgrade);
		*/
		
		registry.PreRegisterItem(TeleportUpgrade = new TeleportUpgrade("TeleportUpgrade"));
		registry.PreRegisterItem(ExperienceVacuumUpgrade = new ExperienceVacuumUpgrade("ExperienceVacuumUpgrade"));
		
		BasicSpeedUpgrade = new BaseSpeedUpgrade("BasicSpeedUpgrade", Tier.BASIC);
		registry.PreRegisterItem(BasicSpeedUpgrade);	
		StaticSpeedUpgrade = new BaseSpeedUpgrade("StaticSpeedUpgrade", Tier.STATIC);
		registry.PreRegisterItem(StaticSpeedUpgrade);	
		EnergizedSpeedUpgrade = new BaseSpeedUpgrade("EnergizedSpeedUpgrade", Tier.ENERGIZED);
		registry.PreRegisterItem(EnergizedSpeedUpgrade);	
		LumumSpeedUpgrade = new BaseSpeedUpgrade("LumumSpeedUpgrade", Tier.LUMUM);
		registry.PreRegisterItem(LumumSpeedUpgrade);	
		
		BasicTankUpgrade = new BaseTankUpgrade("BasicTankUpgrade", Tier.BASIC);
		registry.PreRegisterItem(BasicTankUpgrade);	
		StaticTankUpgrade = new BaseTankUpgrade("StaticTankUpgrade", Tier.STATIC);
		registry.PreRegisterItem(StaticTankUpgrade);	
		EnergizedTankUpgrade = new BaseTankUpgrade("EnergizedTankUpgrade", Tier.ENERGIZED);
		registry.PreRegisterItem(EnergizedTankUpgrade);	
		LumumTankUpgrade = new BaseTankUpgrade("LumumTankUpgrade", Tier.LUMUM);
		registry.PreRegisterItem(LumumTankUpgrade);	
		
		registry.PreRegisterItem(BasicPowerUpgrade = new BasePowerUpgrade("BasicPowerUpgrade", Tier.BASIC));
		registry.PreRegisterItem(StaticPowerUpgrade = new BasePowerUpgrade("StaticPowerUpgrade", Tier.STATIC));
		registry.PreRegisterItem(EnergizedPowerUpgrade = new BasePowerUpgrade("EnergizedPowerUpgrade", Tier.ENERGIZED));
		registry.PreRegisterItem(LumumPowerUpgrade = new BasePowerUpgrade("LumumPowerUpgrade", Tier.LUMUM));
		
		registry.PreRegisterItem(StaticQuarryingUpgrade = new BaseQuarryingUpgrade("StaticQuarryingUpgrade", Tier.STATIC));	
		registry.PreRegisterItem(EnergizedQuarryingUpgrade = new BaseQuarryingUpgrade("EnergizedQuarryingUpgrade", Tier.ENERGIZED));	
		registry.PreRegisterItem(LumumQuarryingUpgrade = new BaseQuarryingUpgrade("LumumQuarryingUpgrade", Tier.LUMUM));	

		registry.PreRegisterItem(BasicRangeUpgrade = new BaseRangeUpgrade("BasicRangeUpgrade", Tier.BASIC));	
		registry.PreRegisterItem(StaticRangeUpgrade = new BaseRangeUpgrade("StaticRangeUpgrade", Tier.STATIC));	
		registry.PreRegisterItem(EnergizedRangeUpgrade = new BaseRangeUpgrade("EnergizedRangeUpgrade", Tier.ENERGIZED));	
		registry.PreRegisterItem(LumumRangeUpgrade = new BaseRangeUpgrade("LumumRangeUpgrade", Tier.LUMUM));	
		
		registry.PreRegisterItem(BasicOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("BasicOutputMultiplierUpgrade", Tier.BASIC));	
		registry.PreRegisterItem(StaticOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("StaticOutputMultiplierUpgrade", Tier.STATIC));	
		registry.PreRegisterItem(EnergizedOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("EnergizedOutputMultiplierUpgrade", Tier.ENERGIZED));	
		registry.PreRegisterItem(LumumOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("LumumOutputMultiplierUpgrade", Tier.LUMUM));	
		
		registry.PreRegisterItem(DigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("DigistoreCapacityUpgrade"));
		registry.PreRegisterItem(DigistoreMiscUpgrade = new DigistoreMiscUpgrades("DigistoreMiscUpgrade"));
		
		BaseFluidCapsule = new BaseFluidCapsule("BaseFluidCapsule", Tier.BASIC.getFluidCanisterCapacity());
		registry.PreRegisterItem(BaseFluidCapsule);
		StaticFluidCapsule = new BaseFluidCapsule("StaticFluidCapsule", Tier.STATIC.getFluidCanisterCapacity());
		registry.PreRegisterItem(StaticFluidCapsule);
		EnergizedFluidCapsule = new BaseFluidCapsule("EnergizedFluidCapsule", Tier.ENERGIZED.getFluidCanisterCapacity());
		registry.PreRegisterItem(EnergizedFluidCapsule);
		LumumFluidCapsule = new BaseFluidCapsule("LumumFluidCapsule", Tier.LUMUM.getFluidCanisterCapacity());
		registry.PreRegisterItem(LumumFluidCapsule);
	}
}
