package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;

public class ShapedRecipes {

		@SuppressWarnings("all")
		private static void registerShapedRecipes() {
			
			//Static Wrench --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.StaticWrench), new Object[]{" IC"," SI","S  ",
			'S', "ingotSilver", 'I', Items.IRON_INGOT, 'C', ModItems.StaticCrop}));
			
			//Static Wrench --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SolderingIron), new Object[]{"I  "," IL"," LR",
			'R', Items.REDSTONE, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)}));		
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SolderingIron), new Object[]{"  I","LI ","RL ",
			'R', Items.REDSTONE, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)}));			
			
			//Fluid Conduit
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.FluidConduit), 8), new Object[]{" S ","SGS"," S ",
			'S', "ingotSilver", 'G', Blocks.GLASS}));
			
			//Item Conduit
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ItemConduit), 8), new Object[]{" T ","TGT"," T ",
			'T', "ingotTin", 'G', Blocks.GLASS}));
			
			//Static Block --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticBlock), new Object[]{"XXX","XXX","XXX",
			'X', ModItems.StaticIngot});
			
			//Energized Block --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.EnergizedBlock), new Object[]{"XXX","XXX","XXX",
			'X', ModItems.EnergizedIngot});
			
			//Static Block --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.LumumBlock), new Object[]{"XXX","XXX","XXX",
			'X', ModItems.LumumIngot});
			
			//Ingots ------------------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModItems.StaticIngot), new Object[]{"XXX","XXX","XXX",
			'X', ModItems.StaticNugget});
			GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedIngot), new Object[]{"XXX","XXX","XXX",
			'X', ModItems.EnergizedNugget});
			GameRegistry.addRecipe(new ItemStack(ModItems.LumumIngot), new Object[]{"XXX","XXX","XXX",
			'X', ModItems.LumumNugget});
			
			//Machine Block --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.MachineBlock, new Object[]{"TGT", "CIC", "TGT", 
			'G', Blocks.GLASS, 'I', ModItems.IOPort, 'C', ModItems.BasicCircuit, 'T', "ingotTin"}));
			
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
			
			//Powered Furnace --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.PoweredFurnace), new Object[]{"UUU", "RBR", "III", 
			'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET});
			
			//Quarry --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.Quarry), new Object[]{"PHP", "EBE", "ELE", 
			'P', Items.DIAMOND_PICKAXE, 'H', Blocks.HOPPER, 'B', ModBlocks.MachineBlock, 'E', ModItems.EnergizedCircuit, 'L', ModItems.LumumCircuit});
			
			//Fluid Infuser --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.FluidInfuser), new Object[]{" U ", "PBP", "RIR", 
			'I', ModItems.IOPort, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET, 'P', Blocks.PISTON});
			
			//Crop Squeezer --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.CropSqueezer), new Object[]{"FUF", "RBR", "III", 
			'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET, 'F', Items.FLINT});
			
			//Fusion Furnace --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.FusionFurnace), new Object[]{"FIF", "RBR", "CCC", 
			'F', ModBlocks.PoweredFurnace, 'R', Items.REDSTONE, 'B', ModBlocks.MachineBlock, 'C', ModItems.BasicCircuit, 'I', ModItems.IOPort});
			
			//Fluid Generator  --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.FluidGenerator), new Object[]{" U ", "CBC", "VIV", 
			'V', "ingotCopper", 'C', ModItems.BasicCircuit, 'I', Items.GOLD_INGOT, 'B', ModBlocks.MachineBlock, 'U', Items.BUCKET, 'G', Blocks.GLASS}));
			
			//Static Battery --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticBattery), new Object[]{"SSS", "RBR", "SSS", 
			'S', ModBlocks.StaticBlock, 'R', Blocks.REDSTONE_BLOCK, 'B', ModBlocks.MachineBlock});			
			
			//Static Solar Panel --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticSolarPanel), new Object[]{"   ", "EEE", "CIC", 
			'E', ModItems.StaticIngot, 'C', ModItems.BasicCircuit, 'I', ModItems.IOPort});
			
			//Static Chest --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModBlocks.StaticChest), new Object[]{"SSS", "SCS", "SSS", 
			'S', ModItems.StaticIngot, 'C', Blocks.CHEST});		
			
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
			GameRegistry.addRecipe(new ItemStack(ModItems.StaticHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.StaticIngot});
			GameRegistry.addRecipe(new ItemStack(ModItems.StaticChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.StaticIngot});
			GameRegistry.addRecipe(new ItemStack(ModItems.StaticLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.StaticIngot});
			GameRegistry.addRecipe(new ItemStack(ModItems.StaticBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.StaticIngot});			
			//Energized Armor --------------------------------------------------------------------------------------------------
			GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedHelmet), new Object[]{"EEE", "E E", "   ",  'E', ModItems.EnergizedIngot});
			GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedChestplate), new Object[]{"E E", "EEE", "EEE",  'E', ModItems.EnergizedIngot});
			GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedLeggings), new Object[]{"EEE", "E E", "E E",  'E', ModItems.EnergizedIngot});
			GameRegistry.addRecipe(new ItemStack(ModItems.EnergizedBoots), new Object[]{"   ", "E E", "E E",  'E', ModItems.EnergizedIngot});
		}
	
	public static void registerFullRecipes() {
		registerShapedRecipes();
	}
}
