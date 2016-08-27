package theking530.staticpower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.blocks.decorative.EnergizedLamp;
import theking530.staticpower.blocks.decorative.LumumLamp;
import theking530.staticpower.blocks.decorative.ObsidianGlass;
import theking530.staticpower.blocks.decorative.StaticLamp;
import theking530.staticpower.conduits.fluidconduit.BlockFluidConduit;
import theking530.staticpower.conduits.itemconduit.BlockItemConduit;
import theking530.staticpower.conduits.staticconduit.BlockStaticConduit;
import theking530.staticpower.machines.basicfarmer.BlockBasicFarmer;
import theking530.staticpower.machines.batteries.BlockBattery;
import theking530.staticpower.machines.chargingstation.BlockChargingStation;
import theking530.staticpower.machines.cropsqueezer.BlockCropSqueezer;
import theking530.staticpower.machines.fluidgenerator.BlockFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.BlockFluidInfuser;
import theking530.staticpower.machines.fusionfurnace.BlockFusionFurnace;
import theking530.staticpower.machines.mechanicalsqueezer.BlockMechanicalSqueezer;
import theking530.staticpower.machines.poweredfurnace.BlockPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.BlockPoweredGrinder;
import theking530.staticpower.machines.quarry.BlockQuarry;
import theking530.staticpower.machines.solarpanel.BlockSolarPanel;
import theking530.staticpower.machines.solderingtable.BlockSolderingTable;
import theking530.staticpower.newconduits.BaseConduitBlock;
import theking530.staticpower.tileentity.chest.energizedchest.BlockEnergizedChest;
import theking530.staticpower.tileentity.chest.lumumchest.BlockLumumChest;
import theking530.staticpower.tileentity.chest.staticchest.BlockStaticChest;
import theking530.staticpower.tileentity.chunkloader.BlockChunkLoader;
import theking530.staticpower.tileentity.gates.BlockLogicGateBasePlate;
import theking530.staticpower.tileentity.gates.adder.BlockAdder;
import theking530.staticpower.tileentity.gates.and.BlockAndGate;
import theking530.staticpower.tileentity.gates.led.BlockLED;
import theking530.staticpower.tileentity.gates.notgate.BlockNotGate;
import theking530.staticpower.tileentity.gates.or.BlockOrGate;
import theking530.staticpower.tileentity.gates.powercell.BlockPowerCell;
import theking530.staticpower.tileentity.gates.subtractor.BlockSubtractorGate;
import theking530.staticpower.tileentity.gates.timer.BlockTimer;
import theking530.staticpower.tileentity.gates.transducer.BlockSignalMultiplier;
import theking530.staticpower.tileentity.vacuumchest.BlockVacuumChest;

public class ModBlocks {

	public static Block BaseConduitBlock;
	public static Block ChunkLoader;
	
	public static Block StaticLamp;
	public static Block EnergizedLamp;
	public static Block LumumLamp;
	public static Block StaticGrass;
	public static Block EnergizedGrass;
	public static Block EnergizedBlock;
	public static Block StaticBlock;
	public static Block LumumBlock;
	public static Block MachineBlock;
	public static Block ObsidianGlass;
	public static Block LaserFence;
	public static Block LaserLines;
	public static Block AdvancedEarth;
	public static Block ControlPanel;
	public static Block FluidGenerator;	
	public static Block FluidInfuser;	
	public static Block CropSqueezer;	
	public static Block PoweredGrinder;
	public static Block PoweredFurnace;	
	public static Block ChargingStation;	
	public static Block BasicFarmer;	
	public static Block BasicTank;
	public static Block AdvancedTank;	
	public static Block Farmer;
	public static Block MechanicalSqueezer;
	
	public static Block LogicGateBasePlate;
	public static Block SignalMultiplier;
	public static Block NotGate;
	public static Block PowerCell;
	public static Block Timer;
	public static Block Adder;
	public static Block And;
	public static Block Or;
	public static Block Subtractor;
	public static Block LED;
	
	public static Block BasicSolarPanel;
	public static Block StaticSolarPanel;
	public static Block EnergizedSolarPanel;
	public static Block LumumSolarPanel;
	public static Block CreativeSolarPanel;
	
	public static Block BasicBattery;
	public static Block StaticBattery;
	public static Block EnergizedBattery;
	public static Block LumumBattery;
	
	public static Block StaticConduit;
	public static Block FluidConduit;
	public static Block ItemConduit;
	public static Block SilverOre;
	public static Block TinOre;
	public static Block LeadOre;
	public static Block CopperOre;
	public static Block PlatinumOre;
	public static Block StaticCropPlant;
	public static Block EnergizedCropPlant;
	public static Block LumumCropPlant;
	public static Block Quarry;
	public static Block StaticChest;
	public static Block EnergizedChest;
	public static Block LumumChest;
	public static Block VacuumChest;
	//public static Block SolidGenerator;
	//public static Block WaterGenerator;
	//public static Block IndustrialGrinder;
	//public static Block IndustrialFurnace;	
	//public static Block BlockBreaker;
	public static Block SolderingTable;
	public static Block FusionFurnace;
	
	public static Block CrackedEnergizedBrick;
	
	public static Block StaticWood;
	public static Block EnergizedWood;
	public static Block LumumWood;
	
	public static Block StaticPlanks;
	public static Block EnergizedPlanks;
	public static Block LumumPlanks;
	
	public static Block StaticCobblestone;
	public static Block EnergizedCobblestone;
	public static Block LumumCobblestone;
	
	public static void init() {
		
		ChunkLoader = new BlockChunkLoader("ChunkLoader").setHardness(3.5f);
		RegisterHelper.registerBlock(ChunkLoader);
		
		BaseConduitBlock = new BaseConduitBlock(Material.IRON, "BaseConduitBlock").setHardness(3.5f);
		RegisterHelper.registerBlock(BaseConduitBlock);
		
		StaticWood = new InfusedWood("StaticWood").setHardness(3.5f);
		RegisterHelper.registerBlock(StaticWood);
		
		EnergizedWood = new InfusedWood("EnergizedWood").setHardness(3.5f);
		RegisterHelper.registerBlock(EnergizedWood);
		
		LumumWood = new InfusedWood("LumumWood").setHardness(3.5f);
		RegisterHelper.registerBlock(LumumWood);
		
		StaticPlanks = new BaseBlock(Material.WOOD, "StaticPlanks").setHardness(3.5f);
		RegisterHelper.registerBlock(StaticPlanks);
		OreDictionary.registerOre("plankWood", new ItemStack(StaticPlanks));
		
		EnergizedPlanks = new BaseBlock(Material.WOOD, "EnergizedPlanks").setHardness(3.5f);
		RegisterHelper.registerBlock(EnergizedPlanks);
		OreDictionary.registerOre("plankWood", new ItemStack(EnergizedPlanks));
		
		LumumPlanks = new BaseBlock(Material.WOOD, "LumumPlanks").setHardness(3.5f);
		RegisterHelper.registerBlock(LumumPlanks);
		OreDictionary.registerOre("plankWood", new ItemStack(LumumPlanks));
		
		StaticCobblestone = new BaseBlock(Material.ROCK, "StaticCobblestone").setHardness(3.5f);
		RegisterHelper.registerBlock(StaticCobblestone);
		
		EnergizedCobblestone = new BaseBlock(Material.ROCK, "EnergizedCobblestone").setHardness(3.5f);
		RegisterHelper.registerBlock(EnergizedCobblestone);
		
		LumumCobblestone = new BaseBlock(Material.ROCK, "LumumCobblestone").setHardness(3.5f);
		RegisterHelper.registerBlock(LumumCobblestone);

		StaticChest = new BlockStaticChest("StaticChest").setUnlocalizedName("StaticChest").setHardness(3.5f);
		RegisterHelper.registerBlock(StaticChest);
		
		EnergizedChest = new BlockEnergizedChest("EnergizedChest").setUnlocalizedName("EnergizedChest").setHardness(3.5f);
		RegisterHelper.registerBlock(EnergizedChest);
		
		LumumChest = new BlockLumumChest("LumumChest").setUnlocalizedName("LumumChest").setHardness(3.5f);
		RegisterHelper.registerBlock(LumumChest);
		
		VacuumChest = new BlockVacuumChest("VacuumChest").setUnlocalizedName("VacuumChest").setHardness(3.5f);
		RegisterHelper.registerBlock(VacuumChest);
		
		BasicBattery = new BlockBattery("BasicBattery", Tier.BASE).setHardness(3.5f);
		RegisterHelper.registerBlock(BasicBattery);
		StaticBattery = new BlockBattery("StaticBattery", Tier.STATIC).setHardness(3.5f);
		RegisterHelper.registerBlock(StaticBattery);	
		LumumBattery = new BlockBattery("LumumBattery", Tier.LUMUM).setHardness(3.5f);
		RegisterHelper.registerBlock(LumumBattery);			
		EnergizedBattery = new BlockBattery("EnergizedBattery", Tier.ENERGIZED).setHardness(3.5f);
		RegisterHelper.registerBlock(EnergizedBattery);
		
		FluidInfuser = new BlockFluidInfuser().setUnlocalizedName("FluidInfuser");
		RegisterHelper.registerBlock(FluidInfuser);
		
		FusionFurnace = new BlockFusionFurnace().setHardness(3.5f);
		RegisterHelper.registerBlock(FusionFurnace);
		
		ChargingStation = new BlockChargingStation().setHardness(3.5f);
		RegisterHelper.registerBlock(ChargingStation);
		
		Quarry = new BlockQuarry();
		RegisterHelper.registerBlock(Quarry);
		
		FluidGenerator = new BlockFluidGenerator().setUnlocalizedName("FluidGenerator");
		RegisterHelper.registerBlock(FluidGenerator);
		
		BasicFarmer = new BlockBasicFarmer().setUnlocalizedName("BasicFarmer");
		RegisterHelper.registerBlock(BasicFarmer);
		
		CropSqueezer = new BlockCropSqueezer().setUnlocalizedName("CropSqueezer");
		RegisterHelper.registerBlock(CropSqueezer);
		MechanicalSqueezer = new BlockMechanicalSqueezer().setUnlocalizedName("MechanicalSqueezer");
		RegisterHelper.registerBlock(MechanicalSqueezer);
		
		PoweredGrinder = new BlockPoweredGrinder().setUnlocalizedName("PoweredGrinder").setHardness(3.5f);
		RegisterHelper.registerBlock(PoweredGrinder);
		
		PoweredFurnace = new BlockPoweredFurnace().setUnlocalizedName("PoweredFurnace").setHardness(3.5f);
		RegisterHelper.registerBlock(PoweredFurnace);
		
		LogicGateBasePlate = new BlockLogicGateBasePlate(Material.IRON, "LogicGateBasePlate");
		RegisterHelper.registerBlock(LogicGateBasePlate);
		SignalMultiplier = new BlockSignalMultiplier("SignalMultiplier").setHardness(3.5f);
		RegisterHelper.registerBlock(SignalMultiplier);
		NotGate = new BlockNotGate("NotGate").setHardness(3.5f);
		RegisterHelper.registerBlock(NotGate);
		PowerCell = new BlockPowerCell("PowerCell").setHardness(3.5f);
		RegisterHelper.registerBlock(PowerCell);
		Timer = new BlockTimer("Timer").setHardness(3.5f);
		RegisterHelper.registerBlock(Timer);
		Adder = new BlockAdder("Adder").setHardness(3.5f);
		RegisterHelper.registerBlock(Adder);
		And = new BlockAndGate("And").setHardness(3.5f);
		RegisterHelper.registerBlock(And);
		Or = new BlockOrGate("Or").setHardness(3.5f);
		RegisterHelper.registerBlock(Or	);
		Subtractor = new BlockSubtractorGate("Subtractor").setHardness(3.5f);
		RegisterHelper.registerBlock(Subtractor	);
		LED = new BlockLED("LED").setHardness(3.5f);
		RegisterHelper.registerBlock(LED);
		
		
		SolderingTable = new BlockSolderingTable().setUnlocalizedName("SolderingTable").setHardness(3.5f);
		RegisterHelper.registerBlock(SolderingTable);
		
		BasicSolarPanel = new BlockSolarPanel("BasicSolarPanel", Tier.BASE).setHardness(3.5f);
		RegisterHelper.registerBlock(BasicSolarPanel);
		StaticSolarPanel = new BlockSolarPanel("StaticSolarPanel", Tier.STATIC).setHardness(3.5f);
		RegisterHelper.registerBlock(StaticSolarPanel);
		EnergizedSolarPanel = new BlockSolarPanel("EnergizedSolarPanel", Tier.ENERGIZED).setHardness(3.5f);
		RegisterHelper.registerBlock(EnergizedSolarPanel);	
		LumumSolarPanel = new BlockSolarPanel("LumumSolarPanel", Tier.LUMUM).setHardness(3.5f);
		RegisterHelper.registerBlock(LumumSolarPanel);
		CreativeSolarPanel = new BlockSolarPanel("CreativeSolarPanel", Tier.CREATIVE).setHardness(3.5f);
		RegisterHelper.registerBlock(CreativeSolarPanel);
		
		StaticLamp = new StaticLamp(Material.GLASS).setUnlocalizedName("StaticLamp");
		RegisterHelper.registerBlock(StaticLamp);
		
		EnergizedLamp = new EnergizedLamp(Material.GLASS).setUnlocalizedName("EnergizedLamp");
		RegisterHelper.registerBlock(EnergizedLamp);
		
		LumumLamp = new LumumLamp(Material.GLASS).setUnlocalizedName("LumumLamp");
		RegisterHelper.registerBlock(LumumLamp);	
		
		StaticBlock = new BaseBlock(Material.IRON, "StaticBlock");
		RegisterHelper.registerBlock(StaticBlock);
		
		EnergizedBlock = new BaseBlock(Material.IRON, "EnergizedBlock");
		RegisterHelper.registerBlock(EnergizedBlock);
		
		LumumBlock = new BaseBlock(Material.IRON, "LumumBlock");
		RegisterHelper.registerBlock(LumumBlock);
		
		StaticGrass = new StaticGrass(Material.GRASS).setUnlocalizedName("StaticGrass");
		RegisterHelper.registerBlock(StaticGrass);
		
		EnergizedGrass = new EnergizedGrass(Material.GRASS).setUnlocalizedName("EnergizedGrass");
		RegisterHelper.registerBlock(EnergizedGrass);
		
		MachineBlock = new BaseBlock(Material.IRON, "MachineBlock");
		RegisterHelper.registerBlock(MachineBlock);
		
		ObsidianGlass = new ObsidianGlass(Material.GLASS);
		RegisterHelper.registerBlock(ObsidianGlass);
		
		AdvancedEarth = new AdvancedEarth().setUnlocalizedName("AdvancedEarth").setHardness(3.5f);
		RegisterHelper.registerBlock(AdvancedEarth);
	
		StaticConduit = new BlockStaticConduit();
		RegisterHelper.registerBlock(StaticConduit);
		
		FluidConduit = new BlockFluidConduit();
		RegisterHelper.registerBlock(FluidConduit);	
		
		ItemConduit = new BlockItemConduit();
		RegisterHelper.registerBlock(ItemConduit);	
				
		CopperOre = new Ore("CopperOre", "pickaxe", 1).setHardness(3.5f);
		RegisterHelper.registerBlock(CopperOre);
		OreDictionary.registerOre("oreCopper", new ItemStack(CopperOre));
		
		TinOre = new Ore("TinOre", "pickaxe", 1).setHardness(3.5f);
		RegisterHelper.registerBlock(TinOre);
		OreDictionary.registerOre("oreTin", new ItemStack(TinOre));
		
		SilverOre = new Ore("SilverOre", "pickaxe", 2).setHardness(3.5f);
		RegisterHelper.registerBlock(SilverOre);
		OreDictionary.registerOre("oreSilver", new ItemStack(SilverOre));

		LeadOre = new Ore("LeadOre", "pickaxe", 2).setHardness(3.5f);
		RegisterHelper.registerBlock(LeadOre);
		OreDictionary.registerOre("oreLead", new ItemStack(LeadOre));
				
		PlatinumOre = new Ore("PlatinumOre", "pickaxe", 2).setHardness(3.5f);
		RegisterHelper.registerBlock(PlatinumOre);
		OreDictionary.registerOre("orePlatinum", new ItemStack(PlatinumOre));
		
		EnergizedCropPlant = new BaseCrop(Tier.ENERGIZED, "EnergizedCropPlant");
		RegisterHelper.registerBlock(EnergizedCropPlant);
		
		StaticCropPlant = new BaseCrop(Tier.STATIC, "StaticCropPlant");
		RegisterHelper.registerBlock(StaticCropPlant);
		
		LumumCropPlant = new BaseCrop(Tier.LUMUM, "LumumCropPlant");
		RegisterHelper.registerBlock(LumumCropPlant);
	}
}
