package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.armor.ModArmor;
import theking530.staticpower.world.plants.ModPlants;

public class ShapedRecipes {

	@SuppressWarnings("all")
	private static void registerShapedRecipes() {		
		//Static Wrench --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.StaticWrench), new Object[]{" IC"," SI","S  ",
		'S', "ingotSilver", 'I', Items.IRON_INGOT, 'C', ModPlants.StaticCrop}));
		
		//Soldering Iron --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SolderingIron), new Object[]{"I  "," IL"," LR",
		'R', Items.REDSTONE, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)}));		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SolderingIron), new Object[]{"  I","LI ","RL ",
		'R', Items.REDSTONE, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)}));			
		
		//Metal Hammer 
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.MetalHammer), new Object[]{"III","ISI"," S ",
		'S', Items.STICK, 'I', Items.IRON_INGOT}));		
		
		//Fluid Conduit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.FluidConduit), 8), new Object[]{" S ","SGS"," S ",
		'S', "ingotSilver", 'G', Blocks.GLASS}));
		
		//Item Conduit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ItemConduit), 8), new Object[]{" T ","TGT"," T ",
		'T', "ingotTin", 'G', Blocks.GLASS}));
		
		//Metal Blocks --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticBlock), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.StaticIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.EnergizedBlock), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.EnergizedIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.LumumBlock), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.LumumIngot});
		
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockCopper), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.CopperIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockTin), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.TinIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockSilver), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.SilverIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockLead), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.LeadIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockPlatinum), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.PlatinumIngot});
		
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockNickel), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.NickelIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockAluminium), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.AluminiumIngot});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockSapphire), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.SapphireGem});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BlockRuby), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.RubyGem});
		
		//Ingots ------------------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModItems.StaticIngot), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.StaticNugget});
		GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedIngot), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.EnergizedNugget});
		GameRegistry.addRecipe(new ItemStack(ModItems.LumumIngot), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.LumumNugget});
		GameRegistry.addRecipe(new ItemStack(Items.IRON_INGOT), new Object[]{"XXX","XXX","XXX",
		'X', ModItems.IronNugget});
		
		//Coils ------------------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModItems.CopperCoil), new Object[]{"XXX","XSX","XXX",
		'X', ModItems.CopperWire, 'S', Items.STICK});
		GameRegistry.addRecipe(new ItemStack(ModItems.SilverCoil), new Object[]{"XXX","XSX","XXX",
		'X', ModItems.SilverWire, 'S', Items.STICK});
		GameRegistry.addRecipe(new ItemStack(ModItems.GoldCoil), new Object[]{"XXX","XSX","XXX",
		'X', ModItems.GoldWire, 'S', Items.STICK});
		
		//Energy Crystals ---------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedEnergyCrystal), new Object[]{" B ","BDB"," B ",
		'B', ModItems.EnergizedInfusionBlend, 'D', Items.DIAMOND});
		GameRegistry.addRecipe(new ItemStack(ModItems.LumumEnergyCrystal), new Object[]{" B ","BDB"," B ",
		'B', ModItems.LumumInfusionBlend, 'D', Items.DIAMOND});
		
		//Machine Block --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.MachineBlock), new Object[]{"TGT", "GCG", "TGT", 
		'G', Blocks.GLASS, 'C', ModItems.BasicCircuit, 'T', ModItems.TinPlate});
		
		//Heating Element --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.HeatingElement), new Object[]{"WCW", "W W", "WCW", 
		'R', ModItems.SilverPlate , 'C', ModItems.CopperPlate, 'W', ModItems.CopperCoil});
		
		//Fermenter --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.Fermenter), new Object[]{"IPI", "GMG", "IPI", 
		'G', Blocks.GLASS , 'I', ModItems.IronPlate, 'P', Blocks.PISTON, 'M', ModBlocks.MachineBlock});
		
		//Distillery --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.Distillery), new Object[]{"GGG", "BMB", "CCC", 
		'G', Blocks.GLASS , 'C', ModItems.CopperPlate, 'B', Items.BUCKET, 'M', ModBlocks.MachineBlock});
		
		//Condenser --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.Condenser), new Object[]{"CCC", "BMB", "GGG", 
		'G', Blocks.GLASS , 'C', ModItems.CopperPlate, 'B', Items.BUCKET, 'M', ModBlocks.MachineBlock});
		
		//Obsidian Glass --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.ObsidianGlass, 4), new Object[]{"O O", " G ", "O O", 
		'G', Blocks.GLASS, 'O', Blocks.OBSIDIAN});
		
		//Soldering Table --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.SolderingTable), new Object[]{"III","S S","S S",
		'I', Items.IRON_INGOT, 'S', Blocks.STONE});
		
		//I/O Port --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.IOPort, new Object[]{" G ", "RLR", " G ", 
		'G', Blocks.GLASS, 'L', Blocks.LEVER, 'R', Items.REDSTONE}));	
		
		//Powered Grinder --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.PoweredGrinder), new Object[]{"FFF", "RBR", "III", 
		'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'F', Items.FLINT});
		
		//Advanced Earth --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.AdvancedEarth), new Object[]{"GGG","GDG","GGG",
		'G', ModItems.GoldPlate, 'D', Blocks.DIRT});
		
		//Powered Furnace --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PoweredFurnace), new Object[]{"IUI", "RBR", "CCC", 
		'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET, 'C', "ingotCopper"}));
		
		//Quarry --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.Quarry), new Object[]{"PHP", "EBE", "ELE", 
		'P', Items.DIAMOND_PICKAXE, 'H', Blocks.HOPPER, 'B', ModBlocks.MachineBlock, 'E', ModItems.EnergizedCircuit, 'L', ModItems.LumumCircuit});
		
		//Fluid Infuser --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.FluidInfuser), new Object[]{" U ", "PBP", "RIR", 
		'I', ModItems.IOPort, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET, 'P', Blocks.PISTON});
		
		//Crop Squeezer --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.CropSqueezer), new Object[]{"FPF", "RBR", "IUI", 
		'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'P', Blocks.PISTON, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET, 'F', Items.FLINT});
		
		//Mechanical Squeezer --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.MechanicalSqueezer), new Object[]{"FFF", "PBP", "SSS", 
		'P', Blocks.PISTON, 'B', Items.BUCKET, 'F', Items.FLINT, 'S', Blocks.STONE});
	
		//Fusion Furnace --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.FusionFurnace), new Object[]{"FIF", "RBR", "CCC", 
		'F', ModBlocks.PoweredFurnace, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'C', ModItems.BasicCircuit, 'I', ModItems.IOPort});
		
		//Charging Station --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.ChargingStation), new Object[]{" H ", "RMR", "CBC", 
		'C', ModItems.BasicCircuit, 'H', ModItems.CopperPlate, 'R', ModItems.GoldPlate, 'M', ModBlocks.MachineBlock, 'B', ModItems.BasicBattery});
		
		//Basic Farmer --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.BasicFarmer), new Object[]{" H ", "RMR", "DCD", 
		'C', ModItems.BasicCircuit, 'H', Items.IRON_HOE, 'R', ModItems.IronPlate, 'M', ModBlocks.MachineBlock, 'D', Blocks.DIRT});
		
		//Fluid Generator  --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.FluidGenerator), new Object[]{" U ", "CBC", "VIV", 
		'V', "ingotCopper", 'C', ModItems.BasicCircuit, 'I', Items.GOLD_INGOT, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET, 'G', Blocks.GLASS}));
		
		//Batteries --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticBattery), new Object[]{"SSS", "BMB", "BBB", 
		'S', ModItems.StaticPlate, 'B', ModItems.StaticBattery, 'M', ModBlocks.MachineBlock});			
		GameRegistry.addRecipe(new ItemStack(ModBlocks.EnergizedBattery), new Object[]{"SSS", "BMB", "BBB", 
		'S', ModItems.EnergizedPlate, 'B', ModItems.EnergizedBattery, 'M', ModBlocks.MachineBlock});		
		GameRegistry.addRecipe(new ItemStack(ModBlocks.LumumBattery), new Object[]{"SSS", "BMB", "BBB",  
		'S', ModItems.LumumPlate, 'B', ModItems.LumumBattery, 'M', ModBlocks.MachineBlock});		
		
		//Static Solar Panel --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticSolarPanel), new Object[]{"   ", "EEE", "CIC", 
		'E', ModItems.StaticIngot, 'C', ModItems.BasicCircuit, 'I', ModItems.IOPort});
		
		//Gates --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.Timer), new Object[]{"   ", "RSV", "PPP", 
		'P', ModBlocks.LogicGateBasePlate, 'S', ModItems.LogicGateServo, 'R', ModItems.LogicGatePowerSync, 'V', ModItems.InvertedLogicGatePowerSync});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.SignalMultiplier), new Object[]{"   ", "RSV", "PPP", 
		'P', ModBlocks.LogicGateBasePlate, 'S', Items.COMPARATOR, 'R', ModItems.LogicGatePowerSync, 'V', ModItems.InvertedLogicGatePowerSync});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.NotGate), new Object[]{"   ", "R V", "PPP", 
		'P', ModBlocks.LogicGateBasePlate,'R', ModItems.LogicGatePowerSync, 'V', ModItems.InvertedLogicGatePowerSync});
		GameRegistry.addRecipe(new ItemStack(ModBlocks.PowerCell), new Object[]{"   ", "RQR", "PPP", 
		'P', ModBlocks.LogicGateBasePlate,'R', ModItems.LogicGatePowerSync, 'Q', Blocks.REDSTONE_BLOCK});
		
		//Static Chest --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticChest), new Object[]{"SSS", "SCS", "SSS", 
		'S', ModItems.StaticIngot, 'C', Blocks.CHEST});		
		
		//Fluid Capsules --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModItems.BaseFluidCapsule), new Object[]{"SCS", "SCS", "SCS", 
		'S', ModItems.IronPlate, 'C', Blocks.GLASS});		
		GameRegistry.addRecipe(new ItemStack(ModItems.StaticFluidCapsule), new Object[]{"SSS", "SCS", "SSS", 
		'S', ModItems.StaticPlate, 'C', ModItems.BaseFluidCapsule});		
		GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedFluidCapsule), new Object[]{"SSS", "SCS", "SSS", 
		'S', ModItems.EnergizedPlate, 'C', ModItems.StaticFluidCapsule});		
		GameRegistry.addRecipe(new ItemStack(ModItems.LumumFluidCapsule), new Object[]{"SSS", "SCS", "SSS", 
		'S', ModItems.LumumPlate, 'C', ModItems.EnergizedFluidCapsule});		
		
		//Energized Chest --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.EnergizedChest), new Object[]{"EEE", "ECE", "EEE", 
		'E', ModItems.EnergizedIngot, 'C', ModBlocks.StaticChest});		
		
		//Lumum Chest --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModBlocks.LumumChest), new Object[]{"LLL", "LCL", "LLL", 
		'L', ModItems.LumumIngot, 'C', ModBlocks.EnergizedChest});		
		
		//Vacuum Chest
		GameRegistry.addRecipe(new ItemStack(ModBlocks.VacuumChest), new Object[]{"EHE", " C ", "IBI", 
		'H', Blocks.HOPPER, 'C', Blocks.CHEST, 'B', ModItems.StaticCircuit, 'E', Items.ENDER_PEARL, 'I', Items.IRON_INGOT});	
		
		//Static Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.StaticHelmet), new Object[]{"EEE", "EBE", "   ",  'E', ModItems.StaticIngot, 'B', ModItems.StaticBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.StaticChestplate), new Object[]{"EBE", "EEE", "EEE",  'E', ModItems.StaticIngot, 'B', ModItems.StaticBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.StaticLeggings), new Object[]{"EEE", "EBE", "E E",  'E', ModItems.StaticIngot, 'B', ModItems.StaticBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.StaticBoots), new Object[]{"   ", "EBE", "E E",  'E', ModItems.StaticIngot, 'B', ModItems.StaticBattery});		
		//Energized Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.EnergizedHelmet), new Object[]{"EEE", "EBE", "   ",  'E', ModItems.EnergizedIngot, 'B', ModItems.EnergizedBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.EnergizedChestplate), new Object[]{"EBE", "EEE", "EEE",  'E', ModItems.EnergizedIngot, 'B', ModItems.EnergizedBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.EnergizedLeggings), new Object[]{"EEE", "EBE", "E E",  'E', ModItems.EnergizedIngot, 'B', ModItems.EnergizedBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.EnergizedBoots), new Object[]{"   ", "EBE", "E E",  'E', ModItems.EnergizedIngot, 'B', ModItems.EnergizedBattery});		
		//Lumum Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.LumumHelmet), new Object[]{"EEE", "EBE", "   ",  'E', ModItems.LumumIngot, 'B', ModItems.LumumBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.LumumChestplate), new Object[]{"EBE", "EEE", "EEE",  'E', ModItems.LumumIngot, 'B', ModItems.LumumBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.LumumLeggings), new Object[]{"EEE", "EBE", "E E",  'E', ModItems.LumumIngot, 'B', ModItems.LumumBattery});
		GameRegistry.addRecipe(new ItemStack(ModArmor.LumumBoots), new Object[]{"   ", "EBE", "E E",  'E', ModItems.LumumIngot, 'B', ModItems.LumumBattery});		
		
		//Copper Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.CopperHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.CopperIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.CopperChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.CopperIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.CopperLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.CopperIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.CopperBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.CopperIngot});		
		//Tin Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.TinHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.TinIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.TinChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.TinIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.TinLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.TinIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.TinBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.TinIngot});		
		//Lead Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.LeadHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.LeadIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.LeadChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.LeadIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.LeadLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.LeadIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.LeadBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.LeadIngot});		
		//Silver Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.SilverHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.SilverIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.SilverChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.SilverIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.SilverLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.SilverIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.SilverBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.SilverIngot});		
		//Platinum Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.PlatinumHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.PlatinumIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.PlatinumChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.PlatinumIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.PlatinumLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.PlatinumIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.PlatinumBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.PlatinumIngot});		
		//Aluminium Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.AluminiumHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.AluminiumIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.AluminiumChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.AluminiumIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.AluminiumLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.AluminiumIngot});
		GameRegistry.addRecipe(new ItemStack(ModArmor.AluminiumBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.AluminiumIngot});		
		//Sapphire Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.SapphireHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.SapphireGem});
		GameRegistry.addRecipe(new ItemStack(ModArmor.SapphireChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.SapphireGem});
		GameRegistry.addRecipe(new ItemStack(ModArmor.SapphireLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.SapphireGem});
		GameRegistry.addRecipe(new ItemStack(ModArmor.SapphireBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.SapphireGem});		
		//Ruby Armor --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ItemStack(ModArmor.RubyHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.RubyGem});
		GameRegistry.addRecipe(new ItemStack(ModArmor.RubyChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.RubyGem});
		GameRegistry.addRecipe(new ItemStack(ModArmor.RubyLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.RubyGem});
		GameRegistry.addRecipe(new ItemStack(ModArmor.RubyBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.RubyGem});		
	
	
	}
	
	public static void registerFullRecipes() {
		registerShapedRecipes();
	}
}
