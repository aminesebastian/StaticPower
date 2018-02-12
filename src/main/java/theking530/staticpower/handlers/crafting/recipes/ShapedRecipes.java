package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.blocks.crops.ModPlants;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.armor.ModArmor;

public class ShapedRecipes {

	@SuppressWarnings("all")
	private static void registerShapedRecipes() {		
		
		//Static Wrench --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_staticWrench", "StaticPower", new ItemStack(ModItems.StaticWrench), new Object[]{" IC"," SI","S  ",
		'S', ingredientOre("ingotSilver"), 'I', ingredientFromItem(Items.IRON_INGOT), 'C', ingredientFromItem(ModPlants.StaticCrop)});
		
		//Static Wrench --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_wireCutter", "StaticPower", new ItemStack(ModItems.WireCutters), new Object[]{" I ","LLI"," L ",
		'L', Ingredient.fromStacks(new ItemStack(Items.DYE, 1, 4)), 'I', ingredientFromItem(Items.IRON_INGOT)});
		
		//Soldering Iron --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_solderingIron1", "StaticPower", new ItemStack(ModItems.SolderingIron), new Object[]{"I  "," IL"," LR",
		'R', ingredientFromItem(Items.REDSTONE), 'I', ingredientFromItem(Items.IRON_INGOT), 'L', Ingredient.fromStacks(new ItemStack(Items.DYE, 1, 4))});		
		RegisterHelper.addShapedRecipe("StaticPower_solderingIron2", "StaticPower", new ItemStack(ModItems.SolderingIron), new Object[]{"  I","LI ","RL ",
		'R', ingredientFromItem(Items.REDSTONE), 'I', ingredientFromItem(Items.IRON_INGOT), 'L', Ingredient.fromStacks(new ItemStack(Items.DYE, 1, 4))});			
		
		//Coordinate Marker --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_coordinateMarker", "StaticPower", new ItemStack(ModItems.CoordinateMarker), new Object[]{"IGI","ICI","IOI",
		'C', ingredientFromItem(ModItems.BasicCircuit), 'I', ingredientFromItem(Items.IRON_INGOT), 'G', ingredientFromBlock(Blocks.GLASS), 'O', ingredientOre("ingotCopper")});	
		
		//Metal Hammer 
		RegisterHelper.addShapedRecipe("StaticPower_metalHammer", "StaticPower", new ItemStack(ModItems.MetalHammer), new Object[]{"III","ISI"," S ",
		'S', ingredientFromItem(Items.STICK), 'I', ingredientFromItem(Items.IRON_INGOT)});		
		
		//Fluid Conduit
		RegisterHelper.addShapedRecipe("StaticPower_fluidConduit", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.FluidConduit), 8), new Object[]{" S ","SGS"," S ",
		'S', ingredientOre("ingotSilver"), 'G', ingredientFromBlock(Blocks.GLASS)});
				
		//Item Conduit
		RegisterHelper.addShapedRecipe("StaticPower_itemConduit", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.ItemConduit), 8), new Object[]{" T ","TGT"," T ",
		'T', ingredientOre("ingotTin"), 'G', ingredientFromBlock(Blocks.GLASS)});
		
		//Item Conduit
		RegisterHelper.addShapedRecipe("StaticPower_staticConduit", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.StaticConduit), 8), new Object[]{" S ","SGS"," S ",
		'S', ingredientFromItem(ModItems.SilverIngot), 'G', ingredientFromBlock(Blocks.GLASS)});
		
		//Metal Blocks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_staticBlock", "StaticPower", new ItemStack(ModBlocks.StaticBlock), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.StaticIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_energizedBlock", "StaticPower", new ItemStack(ModBlocks.EnergizedBlock), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.EnergizedIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_lumumBlock", "StaticPower", new ItemStack(ModBlocks.LumumBlock), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.LumumIngot)});
		
		RegisterHelper.addShapedRecipe("StaticPower_cooperBlock", "StaticPower", new ItemStack(ModBlocks.BlockCopper), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.CopperIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_tinBlock", "StaticPower", new ItemStack(ModBlocks.BlockTin), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.TinIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_silverBlock", "StaticPower", new ItemStack(ModBlocks.BlockSilver), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.SilverIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_leadBlock", "StaticPower", new ItemStack(ModBlocks.BlockLead), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.LeadIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_platinumBlock", "StaticPower", new ItemStack(ModBlocks.BlockPlatinum), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.PlatinumIngot)});
		
		RegisterHelper.addShapedRecipe("StaticPower_nickelBlock", "StaticPower", new ItemStack(ModBlocks.BlockNickel), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.NickelIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_aluminiumBlock", "StaticPower", new ItemStack(ModBlocks.BlockAluminium), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.AluminiumIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_sapphireBlock", "StaticPower", new ItemStack(ModBlocks.BlockSapphire), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.SapphireGem)});
		RegisterHelper.addShapedRecipe("StaticPower_rubyBlock", "StaticPower", new ItemStack(ModBlocks.BlockRuby), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.RubyGem)});
		
		//Ingots ------------------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_staticIngot", "StaticPower", new ItemStack(ModItems.StaticIngot), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.StaticNugget)});
		RegisterHelper.addShapedRecipe("StaticPower_energizedIngot", "StaticPower", new ItemStack(ModItems.EnergizedIngot), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.EnergizedNugget)});
		RegisterHelper.addShapedRecipe("StaticPower_lumumIngot", "StaticPower", new ItemStack(ModItems.LumumIngot), new Object[]{"XXX","XXX","XXX",
		'X', ingredientFromItem(ModItems.LumumNugget)});
		
		//Coils ------------------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_copperCoil", "StaticPower", new ItemStack(ModItems.CopperCoil), new Object[]{"XXX","XSX","XXX",
		'X', ingredientFromItem(ModItems.CopperWire), 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.addShapedRecipe("StaticPower_silverCoil", "StaticPower", new ItemStack(ModItems.SilverCoil), new Object[]{"XXX","XSX","XXX",
		'X', ingredientFromItem(ModItems.SilverWire), 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.addShapedRecipe("StaticPower_goldCoil", "StaticPower", new ItemStack(ModItems.GoldCoil), new Object[]{"XXX","XSX","XXX",
		'X', ingredientFromItem(ModItems.GoldWire), 'S', ingredientFromItem(Items.STICK)});
		
		//Energy Crystals ---------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_energizedEnergyCrystal", "StaticPower", new ItemStack(ModItems.EnergizedEnergyCrystal), new Object[]{" B ","BDB"," B ",
		'B', ingredientFromItem(ModItems.EnergizedInfusionBlend), 'D', ingredientFromItem(Items.DIAMOND)});
		RegisterHelper.addShapedRecipe("StaticPower_lumumEnergyCrystal", "StaticPower", new ItemStack(ModItems.LumumEnergyCrystal), new Object[]{" B ","BDB"," B ",
		'B', ingredientFromItem(ModItems.LumumInfusionBlend), 'D', ingredientFromItem(Items.DIAMOND)});
		
		//Machine Block --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_machineBlock", "StaticPower", new ItemStack(ModBlocks.MachineBlock), new Object[]{"TGT", "GCG", "TGT", 
		'G', ingredientFromBlock((Blocks.GLASS)), 'C', ingredientFromItem(ModItems.BasicCircuit), 'T', ingredientFromItem(ModItems.TinPlate)});
		
		//Heating Element --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_heatingElement", "StaticPower", new ItemStack(ModBlocks.HeatingElement), new Object[]{"WCW", "W W", "WCW", 
		'C', ingredientFromItem(ModItems.CopperPlate), 'W', ingredientFromItem(ModItems.CopperCoil)});
		
		//Fermenter --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_fermenter", "StaticPower", new ItemStack(ModBlocks.Fermenter), new Object[]{"IPI", "GMG", "IPI", 
		'G', ingredientFromBlock((Blocks.GLASS)) , 'I', ingredientFromItem(ModItems.IronPlate), 'P', ingredientFromBlock(Blocks.PISTON), 'M', ingredientFromBlock(ModBlocks.MachineBlock)});
		
		//Distillery --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_distillery", "StaticPower", new ItemStack(ModBlocks.Distillery), new Object[]{"GGG", "BMB", "CCC", 
		'G', ingredientFromBlock((Blocks.GLASS)) , 'C', ingredientFromItem(ModItems.CopperPlate), 'B', ingredientFromItem(Items.BUCKET), 'M', ingredientFromBlock(ModBlocks.MachineBlock)});
		
		//Condenser --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_condenser", "StaticPower", new ItemStack(ModBlocks.Condenser), new Object[]{"CCC", "BMB", "GGG", 
		'G', ingredientFromBlock((Blocks.GLASS)) , 'C', ingredientFromItem(ModItems.CopperPlate), 'B', ingredientFromItem(Items.BUCKET), 'M', ingredientFromBlock(ModBlocks.MachineBlock)});
		
		//Obsidian Glass --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_obsidianGlass", "StaticPower", new ItemStack(ModBlocks.ObsidianGlass, 4), new Object[]{"O O", " G ", "O O", 
		'G', ingredientFromBlock((Blocks.GLASS)), 'O', ingredientFromBlock(Blocks.OBSIDIAN)});
		
		//Soldering Table --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_solderingTable", "StaticPower", new ItemStack(ModBlocks.SolderingTable), new Object[]{"III","S S","S S",
		'I', ingredientFromItem(Items.IRON_INGOT), 'S', ingredientFromBlock(Blocks.STONE)});
		
		//I/O Port --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_iOPort", "StaticPower", new ItemStack(ModItems.IOPort, 2), new Object[]{" L ", "LGL", " L ", 
		'G', ingredientFromBlock((Blocks.GLASS)), 'L', ingredientFromBlock((Blocks.LEVER))});	
		
		//Powered Grinder --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_poweredGrinder", "StaticPower", new ItemStack(ModBlocks.PoweredGrinder), new Object[]{"FFF", "RBR", "III", 
		'I', ingredientFromItem(Items.IRON_INGOT), 'R', ingredientFromItem(Items.REDSTONE), 'B', ingredientFromBlock(ModBlocks.MachineBlock), 'F', ingredientFromItem(Items.FLINT)});
		
		//Advanced Earth --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_advancedEarth", "StaticPower", new ItemStack(ModBlocks.AdvancedEarth), new Object[]{" E ","EDE"," E ",
		'E', ingredientFromItem(ModItems.EnergizedPlate), 'D', ingredientFromBlock(Blocks.DIRT)});
		
		//Powered Furnace --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_poweredFurnace", "StaticPower", new ItemStack(ModBlocks.PoweredFurnace), new Object[]{"IUI", "RBR", "CCC", 
		'I', ingredientFromItem(Items.IRON_INGOT), 'R', ingredientFromItem(Items.REDSTONE), 'B', ingredientFromBlock(ModBlocks.MachineBlock), 'U', ingredientFromItem(Items.BUCKET), 'C', ingredientOre("ingotCopper")});
		
		//Quarry --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_quarry", "StaticPower", new ItemStack(ModBlocks.Quarry), new Object[]{"PHP", "EBE", "ELE", 
		'P', ingredientFromItem(Items.DIAMOND_PICKAXE), 'H', ingredientFromBlock(Blocks.HOPPER), 'B', ingredientFromBlock(ModBlocks.MachineBlock), 'E', ingredientFromItem(ModItems.EnergizedCircuit), 'L', ingredientFromItem(ModItems.LumumCircuit)});
		
		//Fluid Infuser --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_fluidInfuser", "StaticPower", new ItemStack(ModBlocks.FluidInfuser), new Object[]{" U ", "PBP", "RIR", 
		'I', ingredientFromItem(ModItems.IOPort), 'R', ingredientFromItem(Items.REDSTONE), 'B', ingredientFromBlock(ModBlocks.MachineBlock), 'U', ingredientFromItem(Items.BUCKET), 'P', ingredientFromBlock(Blocks.PISTON)});
		
		//Crop Squeezer --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_cropSqueezer", "StaticPower", new ItemStack(ModBlocks.CropSqueezer), new Object[]{"FPF", "RBR", "IUI", 
		'I', ingredientFromItem(Items.IRON_INGOT), 'R', ingredientFromItem(Items.REDSTONE), 'P', ingredientFromBlock(Blocks.PISTON), 'B', ingredientFromBlock(ModBlocks.MachineBlock), 'U', ingredientFromItem(Items.BUCKET), 'F', ingredientFromItem(Items.FLINT)});
		
		//Mechanical Squeezer --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_mechanicalSqueezer", "StaticPower", new ItemStack(ModBlocks.MechanicalSqueezer), new Object[]{"FFF", "PBP", "SSS", 
		'P', ingredientFromBlock(Blocks.PISTON), 'B', ingredientFromItem(Items.BUCKET), 'F', ingredientFromItem(Items.FLINT), 'S', ingredientFromBlock(Blocks.STONE)});
	
		//Fusion Furnace --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_fusionFurnace", "StaticPower", new ItemStack(ModBlocks.FusionFurnace), new Object[]{"FIF", "RBR", "CCC", 
		'F', ingredientFromBlock(ModBlocks.PoweredFurnace), 'R', ingredientFromItem(Items.REDSTONE), 'B', ingredientFromBlock(ModBlocks.MachineBlock), 'C', ingredientFromItem(ModItems.BasicCircuit), 'I', ingredientFromItem(ModItems.IOPort)});
		
		//Charging Station --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_chargingStation", "StaticPower", new ItemStack(ModBlocks.ChargingStation), new Object[]{" H ", "RMR", "CBC", 
		'C', ingredientFromItem(ModItems.BasicCircuit), 'H', ingredientFromItem(ModItems.CopperPlate), 'R', ingredientFromItem(ModItems.GoldPlate), 'M', ingredientFromBlock(ModBlocks.MachineBlock), 'B', ingredientFromItem(ModItems.BasicBattery)});
		
		//Basic Farmer --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_basicFarmer", "StaticPower", new ItemStack(ModBlocks.BasicFarmer), new Object[]{" H ", "RMR", "DCD", 
		'C', ingredientFromItem(ModItems.BasicCircuit), 'H', ingredientFromItem(Items.IRON_HOE), 'R', ingredientFromItem(ModItems.IronPlate), 'M', ingredientFromBlock(ModBlocks.MachineBlock), 'D', ingredientFromBlock(Blocks.DIRT)});
		
		//Fluid Generator  --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_fluidGenerator", "StaticPower", new ItemStack(ModBlocks.FluidGenerator), new Object[]{" U ", "CBC", "VIV", 
		'V', ingredientFromItem(ModItems.CopperPlate), 'C', ingredientFromItem(ModItems.BasicCircuit), 'I', ingredientFromItem(Items.GOLD_INGOT), 'B', ingredientFromBlock(ModBlocks.MachineBlock), 'U', ingredientFromItem(Items.BUCKET)});

		//Batteries --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_staticBattery", "StaticPower", new ItemStack(ModBlocks.StaticBattery), new Object[]{"SSS", "BMB", "BBB", 
		'S', ingredientFromItem(ModItems.StaticPlate), 'B', ingredientFromItem(ModItems.StaticBattery), 'M', ingredientFromBlock(ModBlocks.MachineBlock)});			
		RegisterHelper.addShapedRecipe("StaticPower_energizedBattery", "StaticPower", new ItemStack(ModBlocks.EnergizedBattery), new Object[]{"SSS", "BMB", "BBB", 
		'S', ingredientFromItem(ModItems.EnergizedPlate), 'B', ModItems.EnergizedBattery, 'M', ingredientFromBlock(ModBlocks.MachineBlock)});		
		RegisterHelper.addShapedRecipe("StaticPower_lumumBattery", "StaticPower", new ItemStack(ModBlocks.LumumBattery), new Object[]{"SSS", "BMB", "BBB",  
		'S', ingredientFromItem(ModItems.LumumPlate), 'B', ingredientFromItem(ModItems.LumumBattery), 'M', ingredientFromBlock(ModBlocks.MachineBlock)});		
		
		//Digistore
		RegisterHelper.addShapedRecipe("StaticPower_digistore", "StaticPower", new ItemStack(ModBlocks.Digistore), new Object[]{"TTT","TCT","TMT",
		'M', ingredientFromItem(ModItems.MemoryChip), 'T', ingredientOre("plateTin"), 'C', ingredientOre("chestWood")});
		
		//Digistore Manager
		RegisterHelper.addShapedRecipe("StaticPower_digistoreManager", "StaticPower", new ItemStack(ModBlocks.DigistoreManager), new Object[]{"TTT","IDI","TMT",
		'M', ingredientFromItem(ModItems.StaticCircuit), 'T', ingredientOre("plateTin"), 'D', ingredientFromBlock(ModBlocks.Digistore), 'I', ingredientFromItem(ModItems.IOPort)});
		
		//Digistore Extender
		RegisterHelper.addShapedRecipe("StaticPower_digistoreNetworkExtender", "StaticPower", new ItemStack(ModBlocks.DigistoreNetworkExtender, 8), new Object[]{"TTT", "I I","TTT",
		'T', ingredientOre("plateTin"), 'I', ingredientFromItem(ModItems.IOPort)});
		
		//Static Solar Panel --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_basicSolarPanel", "StaticPower", new ItemStack(ModBlocks.BasicSolarPanel), new Object[]{"   ", "LLL", "CIC", 
		'L', ingredientFromBlock(Blocks.LAPIS_BLOCK), 'C', ingredientFromItem(ModItems.BasicCircuit), 'I', ingredientFromItem(ModItems.IOPort)});
		
		//Gates --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_timer", "StaticPower", new ItemStack(ModBlocks.Timer), new Object[]{"   ", "RSV", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate), 'S', ingredientFromItem(ModItems.LogicGateServo), 'R', ingredientFromItem(ModItems.LogicGatePowerSync), 'V', ingredientFromItem(ModItems.InvertedLogicGatePowerSync)});
		RegisterHelper.addShapedRecipe("StaticPower_signalMultiplier", "StaticPower", new ItemStack(ModBlocks.SignalMultiplier), new Object[]{"   ", "RSV", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate), 'S', ingredientFromItem(Items.COMPARATOR), 'R', ingredientFromItem(ModItems.LogicGatePowerSync), 'V', ingredientFromItem(ModItems.InvertedLogicGatePowerSync)});
		RegisterHelper.addShapedRecipe("StaticPower_notGate", "StaticPower", new ItemStack(ModBlocks.NotGate), new Object[]{"   ", "R V", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate),'R', ingredientFromItem(ModItems.LogicGatePowerSync), 'V', ingredientFromItem(ModItems.InvertedLogicGatePowerSync)});
		RegisterHelper.addShapedRecipe("StaticPower_powerCell", "StaticPower", new ItemStack(ModBlocks.PowerCell), new Object[]{"   ", "RQR", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate),'R', ingredientFromItem(ModItems.LogicGatePowerSync), 'Q', ingredientFromBlock(Blocks.REDSTONE_BLOCK)});
		RegisterHelper.addShapedRecipe("StaticPower_adder", "StaticPower", new ItemStack(ModBlocks.Adder), new Object[]{" T ", "RQR", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate),'R', ingredientFromItem(ModItems.LogicGatePowerSync), 'T', ingredientFromItem(ModItems.Transistor),'Q', ingredientFromItem(ModItems.MemoryChip)});
		RegisterHelper.addShapedRecipe("StaticPower_andGate", "StaticPower", new ItemStack(ModBlocks.And), new Object[]{"   ", "RQR", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate),'R', ingredientFromItem(ModItems.LogicGatePowerSync), 'Q', ingredientFromItem(ModItems.MemoryChip)});
		RegisterHelper.addShapedRecipe("StaticPower_orGate", "StaticPower", new ItemStack(ModBlocks.Or), new Object[]{"   ", "RQV", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate),'R', ingredientFromItem(ModItems.LogicGatePowerSync), 'V', ingredientFromItem(ModItems.InvertedLogicGatePowerSync), 'Q', ingredientFromItem(ModItems.MemoryChip)});
		RegisterHelper.addShapedRecipe("StaticPower_subtractor", "StaticPower", new ItemStack(ModBlocks.Subtractor), new Object[]{" T ", "VQV", "PPP", 
		'P', ingredientFromBlock(ModBlocks.LogicGateBasePlate), 'V', ingredientFromItem(ModItems.InvertedLogicGatePowerSync), 'T', ingredientFromItem(ModItems.Transistor), 'Q', ingredientFromItem(ModItems.MemoryChip)});
		RegisterHelper.addShapedRecipe("StaticPower_LED", "StaticPower", new ItemStack(ModBlocks.LED, 4), new Object[]{"GGG", "GDG", "SCS", 
		'G', ingredientFromBlock(Blocks.GLASS), 'D', ingredientFromItem(ModItems.Diode), 'S', ingredientFromItem(ModItems.SilverWire), 'C', ingredientFromItem(ModItems.BasicCircuit)});
		
		//Static Chest --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_staticChest", "StaticPower", new ItemStack(ModBlocks.StaticChest), new Object[]{"SSS", "SCS", "SSS", 
		'S', ingredientFromItem(ModItems.StaticIngot), 'C', ingredientFromBlock(Blocks.CHEST)});		
		
		//Fluid Capsules --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_baseFluidCapsule", "StaticPower", new ItemStack(ModItems.BaseFluidCapsule), new Object[]{"PGP", "PGP", "PGP", 
		'P', ingredientFromItem(ModItems.IronPlate), 'G', ingredientFromBlock(Blocks.GLASS_PANE)});			

		//Energized Chest --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_energizedChest", "StaticPower", new ItemStack(ModBlocks.EnergizedChest), new Object[]{"EEE", "ECE", "EEE", 
		'E', ingredientFromItem(ModItems.EnergizedIngot), 'C',  ingredientFromBlock(ModBlocks.StaticChest)});		
		
		//Lumum Chest --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_lumumChest", "StaticPower", new ItemStack(ModBlocks.LumumChest), new Object[]{"LLL", "LCL", "LLL", 
		'L', ingredientFromItem(ModItems.LumumIngot), 'C',  ingredientFromBlock(ModBlocks.EnergizedChest)});		
		
		//Vacuum Chest
		RegisterHelper.addShapedRecipe("StaticPower_vacuumChest", "StaticPower", new ItemStack(ModBlocks.VacuumChest), new Object[]{"EHE", " C ", "IBI", 
		'H',  ingredientFromBlock(Blocks.HOPPER), 'C',  ingredientFromBlock(Blocks.CHEST), 'B', ingredientFromItem(ModItems.StaticCircuit), 'E', ingredientFromItem(Items.ENDER_PEARL), 'I', ingredientFromItem(Items.IRON_INGOT)});	
		
		//Static Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_staticHelmet", "StaticPower", new ItemStack(ModArmor.StaticHelmet), new Object[]{"EEE", "EBE", "   ",  'E', ingredientFromItem(ModItems.StaticIngot), 'B', ingredientFromItem(ModItems.StaticBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_staticChestplate", "StaticPower", new ItemStack(ModArmor.StaticChestplate), new Object[]{"EBE", "EEE", "EEE",  'E', ingredientFromItem(ModItems.StaticIngot), 'B', ingredientFromItem(ModItems.StaticBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_staticLeggings", "StaticPower", new ItemStack(ModArmor.StaticLeggings), new Object[]{"EEE", "EBE", "E E",  'E', ingredientFromItem(ModItems.StaticIngot), 'B', ingredientFromItem(ModItems.StaticBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_staticBoots", "StaticPower", new ItemStack(ModArmor.StaticBoots), new Object[]{"   ", "EBE", "E E",  'E', ingredientFromItem(ModItems.StaticIngot), 'B', ingredientFromItem(ModItems.StaticBattery)});		
		//Energized Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_energizedHelmet", "StaticPower", new ItemStack(ModArmor.EnergizedHelmet), new Object[]{"EEE", "EBE", "   ",  'E', ingredientFromItem(ModItems.EnergizedIngot), 'B', ingredientFromItem(ModItems.EnergizedBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_energizedChestplate", "StaticPower", new ItemStack(ModArmor.EnergizedChestplate), new Object[]{"EBE", "EEE", "EEE",  'E', ingredientFromItem(ModItems.EnergizedIngot), 'B', ingredientFromItem(ModItems.EnergizedBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_energizedLeggings", "StaticPower", new ItemStack(ModArmor.EnergizedLeggings), new Object[]{"EEE", "EBE", "E E",  'E', ingredientFromItem(ModItems.EnergizedIngot), 'B', ingredientFromItem(ModItems.EnergizedBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_energizedBoots", "StaticPower", new ItemStack(ModArmor.EnergizedBoots), new Object[]{"   ", "EBE", "E E",  'E', ingredientFromItem(ModItems.EnergizedIngot), 'B', ingredientFromItem(ModItems.EnergizedBattery)});		
		//Lumum Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_lumumHelmet", "StaticPower", new ItemStack(ModArmor.LumumHelmet), new Object[]{"EEE", "EBE", "   ",  'E', ingredientFromItem(ModItems.LumumIngot), 'B', ingredientFromItem(ModItems.LumumBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_lumumChestplate", "StaticPower", new ItemStack(ModArmor.LumumChestplate), new Object[]{"EBE", "EEE", "EEE",  'E', ingredientFromItem(ModItems.LumumIngot), 'B', ingredientFromItem(ModItems.LumumBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_lumumLeggings", "StaticPower", new ItemStack(ModArmor.LumumLeggings), new Object[]{"EEE", "EBE", "E E",  'E', ingredientFromItem(ModItems.LumumIngot), 'B', ingredientFromItem(ModItems.LumumBattery)});
		RegisterHelper.addShapedRecipe("StaticPower_lumumBoots", "StaticPower", new ItemStack(ModArmor.LumumBoots), new Object[]{"   ", "EBE", "E E",  'E', ingredientFromItem(ModItems.LumumIngot), 'B', ingredientFromItem(ModItems.LumumBattery)});		
		
		//Copper Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_copperHelmet", "StaticPower", new ItemStack(ModArmor.CopperHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.CopperIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_copperChestplate", "StaticPower", new ItemStack(ModArmor.CopperChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.CopperIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_copperLeggings", "StaticPower", new ItemStack(ModArmor.CopperLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.CopperIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_copperBoots", "StaticPower", new ItemStack(ModArmor.CopperBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.CopperIngot)});		
		//Tin Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_tinHelmet", "StaticPower", new ItemStack(ModArmor.TinHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.TinIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_tinChestplate", "StaticPower", new ItemStack(ModArmor.TinChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.TinIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_tinLeggings", "StaticPower", new ItemStack(ModArmor.TinLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.TinIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_tinBoots", "StaticPower", new ItemStack(ModArmor.TinBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.TinIngot)});		
		//Lead Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_leadHelmet", "StaticPower", new ItemStack(ModArmor.LeadHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.LeadIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_leadChestplate", "StaticPower", new ItemStack(ModArmor.LeadChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.LeadIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_leadLeggings", "StaticPower", new ItemStack(ModArmor.LeadLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.LeadIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_leadBoots", "StaticPower", new ItemStack(ModArmor.LeadBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.LeadIngot)});		
		//Silver Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_silverHelmet", "StaticPower", new ItemStack(ModArmor.SilverHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.SilverIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_silverChestplate", "StaticPower", new ItemStack(ModArmor.SilverChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.SilverIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_silverLeggings", "StaticPower", new ItemStack(ModArmor.SilverLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.SilverIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_silverBoots", "StaticPower", new ItemStack(ModArmor.SilverBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.SilverIngot)});		
		//Platinum Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_platinumHelmet", "StaticPower", new ItemStack(ModArmor.PlatinumHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.PlatinumIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_platinumChestplate", "StaticPower", new ItemStack(ModArmor.PlatinumChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.PlatinumIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_platinumLeggings", "StaticPower", new ItemStack(ModArmor.PlatinumLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.PlatinumIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_platinumBoots", "StaticPower", new ItemStack(ModArmor.PlatinumBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.PlatinumIngot)});		
		//Aluminium Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_aluminiumHelmet", "StaticPower", new ItemStack(ModArmor.AluminiumHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.AluminiumIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_aluminiumChestplate", "StaticPower", new ItemStack(ModArmor.AluminiumChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.AluminiumIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_aluminiumLeggings", "StaticPower", new ItemStack(ModArmor.AluminiumLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.AluminiumIngot)});
		RegisterHelper.addShapedRecipe("StaticPower_aluminiumBoots", "StaticPower", new ItemStack(ModArmor.AluminiumBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.AluminiumIngot)});		
		//Sapphire Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_sapphireHelmet", "StaticPower", new ItemStack(ModArmor.SapphireHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.SapphireGem)});
		RegisterHelper.addShapedRecipe("StaticPower_sapphireChestplate", "StaticPower", new ItemStack(ModArmor.SapphireChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.SapphireGem)});
		RegisterHelper.addShapedRecipe("StaticPower_sapphireLeggings", "StaticPower", new ItemStack(ModArmor.SapphireLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.SapphireGem)});
		RegisterHelper.addShapedRecipe("StaticPower_sapphireBoots", "StaticPower", new ItemStack(ModArmor.SapphireBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.SapphireGem)});		
		//Ruby Armor --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapedRecipe("StaticPower_rubyHelmet", "StaticPower", new ItemStack(ModArmor.RubyHelmet), new Object[]{"EEE", "E E", "   ",  'E', ingredientFromItem(ModItems.RubyGem)});
		RegisterHelper.addShapedRecipe("StaticPower_rubyChestplate", "StaticPower", new ItemStack(ModArmor.RubyChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ingredientFromItem(ModItems.RubyGem)});
		RegisterHelper.addShapedRecipe("StaticPower_rubyLeggings", "StaticPower", new ItemStack(ModArmor.RubyLeggings), new Object[]{"EEE", "E E", "E E",  'E', ingredientFromItem(ModItems.RubyGem)});
		RegisterHelper.addShapedRecipe("StaticPower_rubyBoots", "StaticPower", new ItemStack(ModArmor.RubyBoots), new Object[]{"   ", "E E", "E E",  'E', ingredientFromItem(ModItems.RubyGem)});		
	
		//Apple Pie
		RegisterHelper.addShapedRecipe("StaticPower_applePie", "StaticPower", new ItemStack(ModItems.ApplePie), new Object[]{"AS ","E  ","   ",
		'S', ingredientFromItem(Items.SUGAR), 'A', ingredientFromItem(Items.EGG), 'E', ingredientFromItem(Items.APPLE)});
	}
	public static Ingredient ingredientFromBlock(Block block) {
		return ingredientFromItem(Item.getItemFromBlock(block));
	}
	public static Ingredient ingredientOre(String ore) {
		return new OreIngredient(ore);
	}
	public static Ingredient ingredientFromItem(Item item) {
		return Ingredient.fromItem(item);
	}
	public static void registerFullRecipes() {
		registerShapedRecipes();
	}
}
