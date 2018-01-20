package theking530.staticpower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.Registry;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.blocks.decorative.EnergizedLamp;
import theking530.staticpower.blocks.decorative.LumumLamp;
import theking530.staticpower.blocks.decorative.ObsidianGlass;
import theking530.staticpower.blocks.decorative.StaticLamp;
import theking530.staticpower.conduits.fluidconduit.BlockFluidConduit;
import theking530.staticpower.conduits.itemconduit.BlockItemConduit;
import theking530.staticpower.conduits.staticconduit.BlockStaticConduit;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.logic.gates.BlockLogicGateBasePlate;
import theking530.staticpower.logic.gates.adder.BlockAdder;
import theking530.staticpower.logic.gates.and.BlockAndGate;
import theking530.staticpower.logic.gates.led.BlockLED;
import theking530.staticpower.logic.gates.notgate.BlockNotGate;
import theking530.staticpower.logic.gates.or.BlockOrGate;
import theking530.staticpower.logic.gates.powercell.BlockPowerCell;
import theking530.staticpower.logic.gates.subtractor.BlockSubtractorGate;
import theking530.staticpower.logic.gates.timer.BlockTimer;
import theking530.staticpower.logic.gates.transducer.BlockSignalMultiplier;
import theking530.staticpower.machines.basicfarmer.BlockBasicFarmer;
import theking530.staticpower.machines.batteries.BlockBattery;
import theking530.staticpower.machines.chargingstation.BlockChargingStation;
import theking530.staticpower.machines.condenser.BlockCondenser;
import theking530.staticpower.machines.cropsqueezer.BlockCropSqueezer;
import theking530.staticpower.machines.distillery.BlockDistillery;
import theking530.staticpower.machines.fermenter.BlockFermenter;
import theking530.staticpower.machines.fluidgenerator.BlockFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.BlockFluidInfuser;
import theking530.staticpower.machines.fusionfurnace.BlockFusionFurnace;
import theking530.staticpower.machines.heatingelement.BlockHeatingElement;
import theking530.staticpower.machines.mechanicalsqueezer.BlockMechanicalSqueezer;
import theking530.staticpower.machines.poweredfurnace.BlockPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.BlockPoweredGrinder;
import theking530.staticpower.machines.quarry.BlockQuarry;
import theking530.staticpower.newconduits.BaseConduitBlock;
import theking530.staticpower.tileentity.chest.energizedchest.BlockEnergizedChest;
import theking530.staticpower.tileentity.chest.lumumchest.BlockLumumChest;
import theking530.staticpower.tileentity.chest.staticchest.BlockStaticChest;
import theking530.staticpower.tileentity.chunkloader.BlockChunkLoader;
import theking530.staticpower.tileentity.solarpanels.BlockSolarPanel;
import theking530.staticpower.tileentity.solderingtable.BlockSolderingTable;
import theking530.staticpower.tileentity.vacuumchest.BlockVacuumChest;

public class ModBlocks {

	public static Block BaseConduitBlock;
	public static Block ChunkLoader;
	
	//Blocks
	public static Block EnergizedBlock;
	public static Block StaticBlock;
	public static Block LumumBlock;
	public static Block BlockCopper;
	public static Block BlockTin;
	public static Block BlockSilver;
	public static Block BlockLead;
	public static Block BlockPlatinum;
	public static Block BlockNickel;
	public static Block BlockAluminium;
	public static Block BlockSapphire;
	public static Block BlockRuby;
	
	public static Block StaticLamp;
	public static Block EnergizedLamp;
	public static Block LumumLamp;
	public static Block StaticGrass;
	public static Block EnergizedGrass;
	public static Block MachineBlock;
	public static Block ObsidianGlass;
	public static Block LaserFence;
	public static Block LaserLines;
	public static Block AdvancedEarth;
	public static Block ControlPanel;
	
	//Machines
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
	public static Block Fermenter;
	public static Block HeatingElement;
	public static Block Distillery;
	public static Block Condenser;
	
	//Gates
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
	
	//Solar Panels
	public static Block BasicSolarPanel;
	public static Block StaticSolarPanel;
	public static Block EnergizedSolarPanel;
	public static Block LumumSolarPanel;
	public static Block CreativeSolarPanel;
	
	//Batteries
	public static Block BasicBattery;
	public static Block StaticBattery;
	public static Block EnergizedBattery;
	public static Block LumumBattery;
	
	//Conduits
	public static Block StaticConduit;
	public static Block FluidConduit;
	public static Block ItemConduit;
	
	//Ores
	public static Block SilverOre;
	public static Block TinOre;
	public static Block LeadOre;
	public static Block CopperOre;
	public static Block PlatinumOre;
	public static Block NickelOre;
	public static Block AluminiumOre;
	public static Block RubyOre;
	public static Block SapphireOre;
	
	//Misc
	public static Block Quarry;
	public static Block StaticChest;
	public static Block EnergizedChest;
	public static Block LumumChest;
	public static Block VacuumChest;
	public static Block SolderingTable;
	public static Block FusionFurnace;
	
	//Decorative
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
	
	public static void init(Registry registry) {
		
		ChunkLoader = new BlockChunkLoader("ChunkLoader").setHardness(3.5f);
		registry.PreRegisterBlock(ChunkLoader);
		
		HeatingElement = new BlockHeatingElement().setHardness(3.5f);
		registry.PreRegisterBlock(HeatingElement);
		
		BaseConduitBlock = new BaseConduitBlock(Material.IRON, "BaseConduitBlock").setHardness(3.5f);
		registry.PreRegisterBlock(BaseConduitBlock);
		
		StaticWood = new InfusedWood("StaticWood").setHardness(3.5f);
		registry.PreRegisterBlock(StaticWood);
		
		EnergizedWood = new InfusedWood("EnergizedWood").setHardness(3.5f);
		registry.PreRegisterBlock(EnergizedWood);
		
		LumumWood = new InfusedWood("LumumWood").setHardness(3.5f);
		registry.PreRegisterBlock(LumumWood);
		
		StaticPlanks = new BaseBlock(Material.WOOD, "StaticPlanks").setHardness(3.5f);
		registry.PreRegisterBlock(StaticPlanks);
		OreDictionary.registerOre("plankWood", new ItemStack(StaticPlanks));
		
		EnergizedPlanks = new BaseBlock(Material.WOOD, "EnergizedPlanks").setHardness(3.5f);
		registry.PreRegisterBlock(EnergizedPlanks);
		OreDictionary.registerOre("plankWood", new ItemStack(EnergizedPlanks));
		
		LumumPlanks = new BaseBlock(Material.WOOD, "LumumPlanks").setHardness(3.5f);
		registry.PreRegisterBlock(LumumPlanks);
		OreDictionary.registerOre("plankWood", new ItemStack(LumumPlanks));
		
		StaticCobblestone = new BaseBlock(Material.ROCK, "StaticCobblestone").setHardness(3.5f);
		registry.PreRegisterBlock(StaticCobblestone);
		
		EnergizedCobblestone = new BaseBlock(Material.ROCK, "EnergizedCobblestone").setHardness(3.5f);
		registry.PreRegisterBlock(EnergizedCobblestone);
		
		LumumCobblestone = new BaseBlock(Material.ROCK, "LumumCobblestone").setHardness(3.5f);
		registry.PreRegisterBlock(LumumCobblestone);

		StaticChest = new BlockStaticChest("StaticChest").setUnlocalizedName("StaticChest").setHardness(3.5f);
		registry.PreRegisterBlock(StaticChest);
		
		EnergizedChest = new BlockEnergizedChest("EnergizedChest").setUnlocalizedName("EnergizedChest").setHardness(3.5f);
		registry.PreRegisterBlock(EnergizedChest);
		
		LumumChest = new BlockLumumChest("LumumChest").setUnlocalizedName("LumumChest").setHardness(3.5f);
		registry.PreRegisterBlock(LumumChest);
		
		VacuumChest = new BlockVacuumChest("VacuumChest").setUnlocalizedName("VacuumChest").setHardness(3.5f);
		registry.PreRegisterBlock(VacuumChest);
		
		BasicBattery = new BlockBattery("BasicBattery", Tier.BASE).setHardness(3.5f);
		registry.PreRegisterBlock(BasicBattery);
		StaticBattery = new BlockBattery("StaticBattery", Tier.STATIC).setHardness(3.5f);
		registry.PreRegisterBlock(StaticBattery);	
		LumumBattery = new BlockBattery("LumumBattery", Tier.LUMUM).setHardness(3.5f);
		registry.PreRegisterBlock(LumumBattery);			
		EnergizedBattery = new BlockBattery("EnergizedBattery", Tier.ENERGIZED).setHardness(3.5f);
		registry.PreRegisterBlock(EnergizedBattery);
		
		FluidInfuser = new BlockFluidInfuser().setUnlocalizedName("FluidInfuser");
		registry.PreRegisterBlock(FluidInfuser);
		
		FusionFurnace = new BlockFusionFurnace().setHardness(3.5f);
		registry.PreRegisterBlock(FusionFurnace);
		
		ChargingStation = new BlockChargingStation().setHardness(3.5f);
		registry.PreRegisterBlock(ChargingStation);
		
		Quarry = new BlockQuarry();
		registry.PreRegisterBlock(Quarry);
		
		Distillery = new BlockDistillery().setUnlocalizedName("Distillery");
		registry.PreRegisterBlock(Distillery);
		
		Condenser = new BlockCondenser().setUnlocalizedName("Condenser");
		registry.PreRegisterBlock(Condenser);
		
		FluidGenerator = new BlockFluidGenerator().setUnlocalizedName("FluidGenerator");
		registry.PreRegisterBlock(FluidGenerator);
		
		BasicFarmer = new BlockBasicFarmer().setUnlocalizedName("BasicFarmer");
		registry.PreRegisterBlock(BasicFarmer);
		
		CropSqueezer = new BlockCropSqueezer().setUnlocalizedName("CropSqueezer");
		registry.PreRegisterBlock(CropSqueezer);
		MechanicalSqueezer = new BlockMechanicalSqueezer().setUnlocalizedName("MechanicalSqueezer");
		registry.PreRegisterBlock(MechanicalSqueezer);
		
		Fermenter = new BlockFermenter().setUnlocalizedName("Fermenter");
		registry.PreRegisterBlock(Fermenter);
		
		PoweredGrinder = new BlockPoweredGrinder().setUnlocalizedName("PoweredGrinder").setHardness(3.5f);
		registry.PreRegisterBlock(PoweredGrinder);
		
		PoweredFurnace = new BlockPoweredFurnace().setUnlocalizedName("PoweredFurnace").setHardness(3.5f);
		registry.PreRegisterBlock(PoweredFurnace);
		
		LogicGateBasePlate = new BlockLogicGateBasePlate(Material.IRON, "LogicGateBasePlate");
		registry.PreRegisterBlock(LogicGateBasePlate);
		SignalMultiplier = new BlockSignalMultiplier("SignalMultiplier").setHardness(3.5f);
		registry.PreRegisterBlock(SignalMultiplier);
		NotGate = new BlockNotGate("NotGate").setHardness(3.5f);
		registry.PreRegisterBlock(NotGate);
		PowerCell = new BlockPowerCell("PowerCell").setHardness(3.5f);
		registry.PreRegisterBlock(PowerCell);
		Timer = new BlockTimer("Timer").setHardness(3.5f);
		registry.PreRegisterBlock(Timer);
		Adder = new BlockAdder("Adder").setHardness(3.5f);
		registry.PreRegisterBlock(Adder);
		And = new BlockAndGate("And").setHardness(3.5f);
		registry.PreRegisterBlock(And);
		Or = new BlockOrGate("Or").setHardness(3.5f);
		registry.PreRegisterBlock(Or	);
		Subtractor = new BlockSubtractorGate("Subtractor").setHardness(3.5f);
		registry.PreRegisterBlock(Subtractor);
		LED = new BlockLED("LED").setHardness(3.5f);
		registry.PreRegisterBlock(LED);
		
		
		SolderingTable = new BlockSolderingTable().setUnlocalizedName("SolderingTable").setHardness(3.5f);
		registry.PreRegisterBlock(SolderingTable);
		
		BasicSolarPanel = new BlockSolarPanel("BasicSolarPanel", Tier.BASE).setHardness(3.5f);
		registry.PreRegisterBlock(BasicSolarPanel);
		StaticSolarPanel = new BlockSolarPanel("StaticSolarPanel", Tier.STATIC).setHardness(3.5f);
		registry.PreRegisterBlock(StaticSolarPanel);
		EnergizedSolarPanel = new BlockSolarPanel("EnergizedSolarPanel", Tier.ENERGIZED).setHardness(3.5f);
		registry.PreRegisterBlock(EnergizedSolarPanel);	
		LumumSolarPanel = new BlockSolarPanel("LumumSolarPanel", Tier.LUMUM).setHardness(3.5f);
		registry.PreRegisterBlock(LumumSolarPanel);
		CreativeSolarPanel = new BlockSolarPanel("CreativeSolarPanel", Tier.CREATIVE).setHardness(3.5f);
		registry.PreRegisterBlock(CreativeSolarPanel);
		
		StaticLamp = new StaticLamp(Material.GLASS).setUnlocalizedName("StaticLamp");
		registry.PreRegisterBlock(StaticLamp);
		
		EnergizedLamp = new EnergizedLamp(Material.GLASS).setUnlocalizedName("EnergizedLamp");
		registry.PreRegisterBlock(EnergizedLamp);
		
		LumumLamp = new LumumLamp(Material.GLASS).setUnlocalizedName("LumumLamp");
		registry.PreRegisterBlock(LumumLamp);	
		
		StaticBlock = new BaseBlock(Material.IRON, "StaticBlock");
		registry.PreRegisterBlock(StaticBlock);	
		EnergizedBlock = new BaseBlock(Material.IRON, "EnergizedBlock");
		registry.PreRegisterBlock(EnergizedBlock);	
		LumumBlock = new BaseBlock(Material.IRON, "LumumBlock");
		registry.PreRegisterBlock(LumumBlock);
		BlockCopper = new BaseBlock(Material.IRON, "BlockCopper");
		registry.PreRegisterBlock(BlockCopper);	
		OreDictionary.registerOre("blockCopper", new ItemStack(BlockCopper));
		BlockTin = new BaseBlock(Material.IRON, "BlockTin");
		registry.PreRegisterBlock(BlockTin);	
		OreDictionary.registerOre("blockTin", new ItemStack(BlockTin));
		BlockSilver = new BaseBlock(Material.IRON, "BlockSilver");
		registry.PreRegisterBlock(BlockSilver);
		OreDictionary.registerOre("blockSilver", new ItemStack(BlockSilver));
		BlockLead = new BaseBlock(Material.IRON, "BlockLead");
		registry.PreRegisterBlock(BlockLead);	
		OreDictionary.registerOre("blockLead", new ItemStack(BlockLead));
		BlockPlatinum = new BaseBlock(Material.IRON, "BlockPlatinum");
		registry.PreRegisterBlock(BlockPlatinum);
		OreDictionary.registerOre("blockPlatinum", new ItemStack(BlockPlatinum));	
		BlockNickel = new BaseBlock(Material.IRON, "BlockNickel");
		registry.PreRegisterBlock(BlockNickel);	
		OreDictionary.registerOre("blockNickel", new ItemStack(BlockNickel));
		BlockAluminium = new BaseBlock(Material.IRON, "BlockAluminium");
		registry.PreRegisterBlock(BlockAluminium);
		OreDictionary.registerOre("blockAluminium", new ItemStack(BlockAluminium));
		BlockSapphire = new BaseBlock(Material.IRON, "BlockSapphire");
		registry.PreRegisterBlock(BlockSapphire);	
		OreDictionary.registerOre("blockSapphire", new ItemStack(BlockSapphire));
		BlockRuby = new BaseBlock(Material.IRON, "BlockRuby");
		registry.PreRegisterBlock(BlockRuby);
		OreDictionary.registerOre("blockRuby", new ItemStack(BlockRuby));
		
		
		StaticGrass = new StaticGrass(Material.GRASS).setUnlocalizedName("StaticGrass");
		registry.PreRegisterBlock(StaticGrass);
		
		EnergizedGrass = new EnergizedGrass(Material.GRASS).setUnlocalizedName("EnergizedGrass");
		registry.PreRegisterBlock(EnergizedGrass);
		
		MachineBlock = new BaseBlock(Material.IRON, "MachineBlock");
		registry.PreRegisterBlock(MachineBlock);
		
		ObsidianGlass = new ObsidianGlass(Material.GLASS);
		registry.PreRegisterBlock(ObsidianGlass);
		
		AdvancedEarth = new AdvancedEarth().setUnlocalizedName("AdvancedEarth").setHardness(3.5f);
		registry.PreRegisterBlock(AdvancedEarth);
	
		StaticConduit = new BlockStaticConduit();
		registry.PreRegisterBlock(StaticConduit);
		
		FluidConduit = new BlockFluidConduit();
		registry.PreRegisterBlock(FluidConduit);	
		
		ItemConduit = new BlockItemConduit();
		registry.PreRegisterBlock(ItemConduit);	
				
		CopperOre = new Ore("CopperOre", "pickaxe", 1).setHardness(3.5f);
		registry.PreRegisterBlock(CopperOre);
		OreDictionary.registerOre("oreCopper", new ItemStack(CopperOre));
		
		TinOre = new Ore("TinOre", "pickaxe", 1).setHardness(3.5f);
		registry.PreRegisterBlock(TinOre);
		OreDictionary.registerOre("oreTin", new ItemStack(TinOre));
		
		SilverOre = new Ore("SilverOre", "pickaxe", 2).setHardness(3.5f);
		registry.PreRegisterBlock(SilverOre);
		OreDictionary.registerOre("oreSilver", new ItemStack(SilverOre));

		LeadOre = new Ore("LeadOre", "pickaxe", 2).setHardness(3.5f);
		registry.PreRegisterBlock(LeadOre);
		OreDictionary.registerOre("oreLead", new ItemStack(LeadOre));
				
		PlatinumOre = new Ore("PlatinumOre", "pickaxe", 2).setHardness(3.5f);
		registry.PreRegisterBlock(PlatinumOre);
		OreDictionary.registerOre("orePlatinum", new ItemStack(PlatinumOre));
		
		NickelOre = new Ore("NickelOre", "pickaxe", 1).setHardness(3.5f);
		registry.PreRegisterBlock(NickelOre);
		OreDictionary.registerOre("oreNickel", new ItemStack(NickelOre));
		
		AluminiumOre = new Ore("AluminiumOre", "pickaxe", 2).setHardness(3.5f);
		registry.PreRegisterBlock(AluminiumOre);
		OreDictionary.registerOre("oreAluminium", new ItemStack(AluminiumOre));

		SapphireOre = new GemOre("SapphireOre", "pickaxe", 2, ModItems.SapphireGem, 1, 3).setHardness(3.5f);
		registry.PreRegisterBlock(SapphireOre);
		OreDictionary.registerOre("oreSapphire", new ItemStack(SapphireOre));
				
		RubyOre = new GemOre("RubyOre", "pickaxe", 2, ModItems.RubyGem, 1, 3).setHardness(3.5f);
		registry.PreRegisterBlock(RubyOre);
		OreDictionary.registerOre("oreRuby", new ItemStack(RubyOre));
		

	}
}
