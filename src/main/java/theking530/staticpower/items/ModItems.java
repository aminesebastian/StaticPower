package theking530.staticpower.items;

import net.minecraft.item.Item;
import theking530.staticpower.Registry;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.book.StaticPowerBook;
import theking530.staticpower.items.containers.BaseBattery;
import theking530.staticpower.items.containers.BaseFluidCapsule;
import theking530.staticpower.items.itemfilter.FilterTier;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.materials.BaseDust;
import theking530.staticpower.items.materials.BaseGem;
import theking530.staticpower.items.materials.BaseIngot;
import theking530.staticpower.items.materials.BaseNugget;
import theking530.staticpower.items.tools.BlockReader;
import theking530.staticpower.items.tools.CoordinateMarker;
import theking530.staticpower.items.tools.ElectricSolderingIron;
import theking530.staticpower.items.tools.MetalHammer;
import theking530.staticpower.items.tools.SolderingIron;
import theking530.staticpower.items.tools.StaticWrench;
import theking530.staticpower.items.tools.WireCutters;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseQuarryingUpgrade;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;

public class ModItems {
	
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
	public static Item IronPlate;
	public static Item CopperPlate;
	public static Item TinPlate;
	public static Item SilverPlate;
	public static Item GoldPlate;
	public static Item LeadPlate;
	public static Item StaticPlate;
	public static Item EnergizedPlate;
	public static Item LumumPlate;
	
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
	
	public static Item SilverDust;
	public static Item TinDust;
	public static Item LeadDust;
	public static Item CopperDust;
	public static Item PlatinumDust;
	public static Item IronDust;
	public static Item GoldDust;
	public static Item InertInfusionBlend;
	public static Item StaticDust;
	public static Item EnergizedDust;
	public static Item LumumDust;
	public static Item NickelDust;
	public static Item AluminiumDust;
	public static Item RedstoneAlloyDust;
	
	public static Item SilverIngot;
	public static Item TinIngot;
	public static Item LeadIngot;
	public static Item CopperIngot;
	public static Item PlatinumIngot;
	public static Item InertIngot;
	public static Item EnergizedIngot;
	public static Item StaticIngot;
	public static Item LumumIngot;
	public static Item NickelIngot;
	public static Item AluminiumIngot;
	public static Item RedstoneAlloyIngot;
	
	public static Item SapphireGem;
	public static Item RubyGem;
	
	public static Item EnergizedNugget;
	public static Item StaticNugget;
	public static Item LumumNugget;
	public static Item IronNugget;
	
	public static Item WheatFlour;
	public static Item PotatoFlour;
	public static Item PotatoBread;
	
	public static Item ApplePie;
	public static Item StaticPie;
	public static Item EnergizedPie;
	public static Item LumumPie;

	public static void init(Registry registry) {				
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
		
		BaseFluidCapsule = new BaseFluidCapsule("BaseFluidCapsule", 2000, 40);
		registry.PreRegisterItem(BaseFluidCapsule);
		StaticFluidCapsule = new BaseFluidCapsule("StaticFluidCapsule", 4000, 80);
		registry.PreRegisterItem(StaticFluidCapsule);
		EnergizedFluidCapsule = new BaseFluidCapsule("EnergizedFluidCapsule", 8000, 160);
		registry.PreRegisterItem(EnergizedFluidCapsule);
		LumumFluidCapsule = new BaseFluidCapsule("LumumFluidCapsule", 16000, 320);
		registry.PreRegisterItem(LumumFluidCapsule);
		
		StaticWrench = new StaticWrench().setUnlocalizedName("StaticWrench");
		registry.PreRegisterItem(StaticWrench);
		SolderingIron = new SolderingIron("SolderingIron", 10);
		registry.PreRegisterItem(SolderingIron);
		ElectricSolderingIron = new ElectricSolderingIron("ElectricSolderingIron", 100);
		registry.PreRegisterItem(ElectricSolderingIron);
		CoordinateMarker = new CoordinateMarker("CoordinateMarker");
		registry.PreRegisterItem(CoordinateMarker);
		WireCutters = new WireCutters();
		registry.PreRegisterItem(WireCutters);
		MetalHammer = new MetalHammer();
		registry.PreRegisterItem(MetalHammer);
		NetworkReader = new BlockReader("NetworkReader");
		registry.PreRegisterItem(NetworkReader);
		
		LogicGatePowerSync = new ItemBase("LogicGatePowerSync");
		registry.PreRegisterItem(LogicGatePowerSync);
		InvertedLogicGatePowerSync = new ItemBase("InvertedLogicGatePowerSync");
		registry.PreRegisterItem(InvertedLogicGatePowerSync);
		LogicGateServo = new ItemBase("LogicGateServo");
		registry.PreRegisterItem(LogicGateServo);
		Diode = new ItemBase("Diode");
		registry.PreRegisterItem(Diode);
		Transistor = new ItemBase("Transistor");
		registry.PreRegisterItem(Transistor);
		InternalClock = new ItemBase("InternalClock");
		registry.PreRegisterItem(InternalClock);
		
		BasicItemFilter = new ItemFilter("BasicItemFilter", FilterTier.BASIC);
		registry.PreRegisterItem(BasicItemFilter);
		UpgradedItemFilter = new ItemFilter("UpgradedItemFilter", FilterTier.UPGRADED);
		registry.PreRegisterItem(UpgradedItemFilter);
		AdvancedItemFilter = new ItemFilter("AdvancedItemFilter", FilterTier.ADVANCED);
		registry.PreRegisterItem(AdvancedItemFilter);
		
		Rubber = new ItemBase("Rubber");
		registry.PreRegisterItem(Rubber);
		IOPort = new ItemBase("IOPort");
		registry.PreRegisterItem(IOPort);
		
		
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
		
		CopperWire = new ItemBase("CopperWire");
		registry.PreRegisterItem(CopperWire);
		SilverWire = new ItemBase("SilverWire");
		registry.PreRegisterItem(SilverWire);
		GoldWire = new ItemBase("GoldWire");
		registry.PreRegisterItem(GoldWire);
		CopperCoil = new ItemBase("CopperCoil");
		registry.PreRegisterItem(CopperCoil);
		SilverCoil = new ItemBase("SilverCoil");
		registry.PreRegisterItem(SilverCoil);
		GoldCoil = new ItemBase("GoldCoil");
		registry.PreRegisterItem(GoldCoil);
		IronPlate = new ItemBase("IronPlate");
		registry.PreRegisterItem(IronPlate);
		CopperPlate = new ItemBase("CopperPlate");
		registry.PreRegisterItem(CopperPlate);
		TinPlate = new ItemBase("TinPlate");
		registry.PreRegisterItem(TinPlate);
		SilverPlate = new ItemBase("SilverPlate");
		registry.PreRegisterItem(SilverPlate);
		GoldPlate = new ItemBase("GoldPlate");
		registry.PreRegisterItem(GoldPlate);
		LeadPlate = new ItemBase("LeadPlate");
		registry.PreRegisterItem(LeadPlate);
		StaticPlate = new ItemBase("StaticPlate");
		registry.PreRegisterItem(StaticPlate);
		EnergizedPlate = new ItemBase("EnergizedPlate");
		registry.PreRegisterItem(EnergizedPlate);
		LumumPlate = new ItemBase("LumumPlate");
		registry.PreRegisterItem(LumumPlate);
		
		EnergizedEnergyCrystal = new ItemBase("EnergizedEnergyCrystal");
		registry.PreRegisterItem(EnergizedEnergyCrystal);
		LumumEnergyCrystal = new ItemBase("LumumEnergyCrystal");
		registry.PreRegisterItem(LumumEnergyCrystal);
		
		EnergizedInfusionBlend = new ItemBase("EnergizedInfusionBlend");
		registry.PreRegisterItem(EnergizedInfusionBlend);
		LumumInfusionBlend = new ItemBase("LumumInfusionBlend");
		registry.PreRegisterItem(LumumInfusionBlend);
		
		BasicBattery = new BaseBattery("BasicPortableBattery", 50000, 1000);
		registry.PreRegisterItem(BasicBattery);
		StaticBattery = new BaseBattery("StaticPortableBattery", 125000, 2500);
		registry.PreRegisterItem(StaticBattery);
		EnergizedBattery = new BaseBattery("EnergizedPortableBattery", 625000, 5000);
		registry.PreRegisterItem(EnergizedBattery);
		LumumBattery = new BaseBattery("LumumPortableBattery", 2500000, 10000);
		registry.PreRegisterItem(LumumBattery);
		
		CreativeSpeedUpgrade = new BaseSpeedUpgrade("CreativeSpeedUpgrade", Tier.CREATIVE);
		registry.PreRegisterItem(CreativeSpeedUpgrade);	
		/**
		CreativeTankUpgrade = new BaseTankUpgrade("CreativeTankUpgrade", Tier.CREATIVE);
		registry.PreRegisterItem(CreativeTankUpgrade);	
		CreativeSpeedUpgrade = new BasePowerUpgrade("CreativeSpeedUpgrade", Tier.CREATIVE);
		registry.PreRegisterItem(CreativeSpeedUpgrade);
		*/
		
		BasicSpeedUpgrade = new BaseSpeedUpgrade("BasicSpeedUpgrade", Tier.BASE);
		registry.PreRegisterItem(BasicSpeedUpgrade);	
		StaticSpeedUpgrade = new BaseSpeedUpgrade("StaticSpeedUpgrade", Tier.STATIC);
		registry.PreRegisterItem(StaticSpeedUpgrade);	
		EnergizedSpeedUpgrade = new BaseSpeedUpgrade("EnergizedSpeedUpgrade", Tier.ENERGIZED);
		registry.PreRegisterItem(EnergizedSpeedUpgrade);	
		LumumSpeedUpgrade = new BaseSpeedUpgrade("LumumSpeedUpgrade", Tier.LUMUM);
		registry.PreRegisterItem(LumumSpeedUpgrade);	
		
		BasicTankUpgrade = new BaseTankUpgrade("BasicTankUpgrade", Tier.BASE);
		registry.PreRegisterItem(BasicTankUpgrade);	
		StaticTankUpgrade = new BaseTankUpgrade("StaticTankUpgrade", Tier.STATIC);
		registry.PreRegisterItem(StaticTankUpgrade);	
		EnergizedTankUpgrade = new BaseTankUpgrade("EnergizedTankUpgrade", Tier.ENERGIZED);
		registry.PreRegisterItem(EnergizedTankUpgrade);	
		LumumTankUpgrade = new BaseTankUpgrade("LumumTankUpgrade", Tier.LUMUM);
		registry.PreRegisterItem(LumumTankUpgrade);	
		
		BasicPowerUpgrade = new BasePowerUpgrade("BasicPowerUpgrade", Tier.BASE);
		registry.PreRegisterItem(BasicPowerUpgrade);
		StaticPowerUpgrade = new BasePowerUpgrade("StaticPowerUpgrade", Tier.STATIC);
		registry.PreRegisterItem(StaticPowerUpgrade);
		EnergizedPowerUpgrade = new BasePowerUpgrade("EnergizedPowerUpgrade", Tier.ENERGIZED);
		registry.PreRegisterItem(EnergizedPowerUpgrade);
		LumumPowerUpgrade = new BasePowerUpgrade("LumumPowerUpgrade", Tier.LUMUM);
		registry.PreRegisterItem(LumumPowerUpgrade);
		
		StaticQuarryingUpgrade = new BaseQuarryingUpgrade("StaticQuarryingUpgrade", Tier.STATIC);
		registry.PreRegisterItem(StaticQuarryingUpgrade);	
		EnergizedQuarryingUpgrade = new BaseQuarryingUpgrade("EnergizedQuarryingUpgrade", Tier.ENERGIZED);
		registry.PreRegisterItem(EnergizedQuarryingUpgrade);	
		LumumQuarryingUpgrade = new BaseQuarryingUpgrade("LumumQuarryingUpgrade", Tier.LUMUM);
		registry.PreRegisterItem(LumumQuarryingUpgrade);	
		
		BasicRangeUpgrade = new BaseRangeUpgrade("BasicRangeUpgrade", Tier.BASE);
		registry.PreRegisterItem(BasicRangeUpgrade);	
		StaticRangeUpgrade = new BaseRangeUpgrade("StaticRangeUpgrade", Tier.STATIC);
		registry.PreRegisterItem(StaticRangeUpgrade);	
		EnergizedRangeUpgrade = new BaseRangeUpgrade("EnergizedRangeUpgrade", Tier.ENERGIZED);
		registry.PreRegisterItem(EnergizedRangeUpgrade);	
		LumumRangeUpgrade = new BaseRangeUpgrade("LumumRangeUpgrade", Tier.LUMUM);
		registry.PreRegisterItem(LumumRangeUpgrade);	
		
	

		StaticBook = new StaticPowerBook("StaticBook");
		registry.PreRegisterItem(StaticBook);	

		registry.PreRegisterItem(StaticIngot = new BaseIngot("StaticIngot"));
		registry.PreRegisterItem(EnergizedIngot = new BaseIngot("EnergizedIngot"));
		registry.PreRegisterItem(LumumIngot = new BaseIngot("LumumIngot"));
		
		registry.PreRegisterItem(SilverIngot = new BaseIngot("SilverIngot"));
		registry.PreRegisterItem(TinIngot = new BaseIngot("TinIngot"));
		registry.PreRegisterItem(LeadIngot = new BaseIngot("LeadIngot"));
		registry.PreRegisterItem(CopperIngot = new BaseIngot("CopperIngot"));
		registry.PreRegisterItem(PlatinumIngot = new BaseIngot("PlatinumIngot"));
		registry.PreRegisterItem(NickelIngot = new BaseIngot("NickelIngot"));
		registry.PreRegisterItem(AluminiumIngot = new BaseIngot("AluminiumIngot"));

		registry.PreRegisterItem(SapphireGem = new BaseGem("SapphireGem"));
		registry.PreRegisterItem(RubyGem = new BaseGem("RubyGem"));

		
		registry.PreRegisterItem(InertIngot = new BaseIngot("InertIngot"));
		registry.PreRegisterItem(RedstoneAlloyIngot = new BaseIngot("RedstoneAlloyIngot"));
		
		registry.PreRegisterItem(StaticNugget = new BaseNugget("StaticNugget"));
		registry.PreRegisterItem(EnergizedNugget = new BaseNugget("EnergizedNugget"));
		registry.PreRegisterItem(LumumNugget = new BaseNugget("LumumNugget"));
		registry.PreRegisterItem(IronNugget = new BaseNugget("IronNugget"));
		
		registry.PreRegisterItem(SilverDust = new BaseDust("SilverDust"));
		registry.PreRegisterItem(TinDust = new BaseDust("TinDust"));
		registry.PreRegisterItem(LeadDust = new BaseDust("LeadDust"));
		registry.PreRegisterItem(CopperDust = new BaseDust("CopperDust"));
		registry.PreRegisterItem(PlatinumDust = new BaseDust("PlatinumDust"));
		registry.PreRegisterItem(IronDust = new BaseDust("IronDust"));
		registry.PreRegisterItem(GoldDust = new BaseDust("GoldDust"));	
		registry.PreRegisterItem(NickelDust = new BaseDust("NickelDust"));
		registry.PreRegisterItem(AluminiumDust = new BaseDust("AluminiumDust"));	

		registry.PreRegisterItem(InertInfusionBlend = new BaseDust("InertInfusionBlend"));
		registry.PreRegisterItem(StaticDust = new BaseDust("StaticDust"));
		registry.PreRegisterItem(EnergizedDust = new BaseDust("EnergizedDust"));
		registry.PreRegisterItem(LumumDust = new BaseDust("LumumDust"));
		registry.PreRegisterItem(RedstoneAlloyDust = new BaseDust("RedstoneAlloyDust"));
		
	}

}
