package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
<<<<<<< HEAD
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.items.ModItems;

public class SolderingRecipes {
	
	@SuppressWarnings("all")
	public static void registerSolderingRecipes() {
		
		//Circuit Recipes
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicCircuit, 4), new Object[]{"R R", " I ", "R R", 
		'I', Items.IRON_INGOT, 'R', Items.REDSTONE});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticCircuit, 4), new Object[]{"RSR", "GCG", "RSR", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'S', ModItems.StaticIngot, 'C', ModItems.BasicCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedCircuit, 4), new Object[]{"RER", "GCG", "RER", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'E', ModItems.EnergizedIngot, 'C', ModItems.StaticCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumCircuit, 4), new Object[]{"RLR", "GCG", "RLR", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'L', ModItems.LumumIngot, 'C', ModItems.EnergizedCircuit});
=======
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.items.ModItems;

public class SolderingRecipes {
	
	@SuppressWarnings("all")
	public static void registerSolderingRecipes() {
		
		//Circuit Recipes
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicCircuit, 4), new Object[]{"R R", " I ", "R R", 
		'I', Items.IRON_INGOT, 'R', Items.REDSTONE});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticCircuit, 4), new Object[]{"RSR", "GCG", "RSR", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'S', ModItems.StaticIngot, 'C', ModItems.BasicCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedCircuit, 4), new Object[]{"RER", "GCG", "RER", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'E', ModItems.EnergizedIngot, 'C', ModItems.StaticCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumCircuit, 4), new Object[]{"RLR", "GCG", "RLR", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'L', ModItems.LumumIngot, 'C', ModItems.EnergizedCircuit});
		
		//Batteries --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicBattery), new Object[]{" G ", "IRI", "IRI", 
		'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'G', Items.GOLD_NUGGET});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticBattery), new Object[]{" S ", "IRI", "IDI", 
		'I', ModItems.StaticIngot, 'R', Items.REDSTONE, 'S', ModItems.StaticNugget, 'D', Items.DIAMOND});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedBattery), new Object[]{" E ", "IRI", "IDI", 
		'I', ModItems.EnergizedIngot, 'R', Blocks.REDSTONE_BLOCK, 'E', ModItems.EnergizedNugget, 'D', Items.DIAMOND});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumBattery), new Object[]{" L ", "IRI", "IDI", 
		'I', ModItems.LumumIngot, 'R', Blocks.REDSTONE_BLOCK, 'L', ModItems.LumumNugget, 'D', Items.DIAMOND});	
		
		//Tools -------------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.ElectricSolderingIron), new Object[]{"I  "," IL"," LR",
		'R', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)}));		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.ElectricSolderingIron), new Object[]{"  I","LI ","RL ",
		'R', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)}));	
>>>>>>> branch '1.10.2' of https://github.com/Theking5301/StaticPower.git
		
		//Upgrades --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicTankUpgrade), new Object[]{" L ","BGB","ICI",
		'B', Items.BUCKET, 'I', Items.IRON_INGOT, 'L', Blocks.LAPIS_BLOCK, 'C', ModItems.BasicCircuit, 'G', Blocks.GLASS});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicPowerUpgrade), new Object[]{"R R"," G ","ICI",
		'I', Items.IRON_INGOT, 'R', Blocks.REDSTONE_BLOCK, 'C', ModItems.BasicCircuit, 'G', Items.GOLD_INGOT});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicSpeedUpgrade), new Object[]{" P ","O O","ICI",
		'I', Items.IRON_INGOT, 'P', Items.GOLDEN_PICKAXE, 'C', ModItems.BasicCircuit, 'O', ModItems.IOPort});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticTankUpgrade), new Object[]{" L ","BGB","ICI",
		'B', Items.BUCKET, 'I', Items.IRON_INGOT, 'L', Blocks.LAPIS_BLOCK, 'C', ModItems.StaticCircuit, 'G', Blocks.GLASS});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticPowerUpgrade), new Object[]{"R R"," G ","ICI",
		'I', Items.IRON_INGOT, 'R', Blocks.REDSTONE_BLOCK, 'C', ModItems.StaticCircuit, 'G', Items.GOLD_INGOT});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticSpeedUpgrade), new Object[]{" P ","O O","ICI",
		'I', Items.IRON_INGOT, 'P', Items.GOLDEN_PICKAXE, 'C', ModItems.StaticCircuit, 'O', ModItems.IOPort});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticQuarryingUpgrade), new Object[]{" P "," C ","I I",
		'I', Items.IRON_PICKAXE, 'P', Items.IRON_PICKAXE, 'C', ModItems.StaticCircuit});
			
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedTankUpgrade), new Object[]{" L ","BGB","ICI",
		'B', Items.BUCKET, 'I', Items.IRON_INGOT, 'L', Blocks.LAPIS_BLOCK, 'C', ModItems.EnergizedCircuit, 'G', Blocks.GLASS});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedPowerUpgrade), new Object[]{"R R"," G ","ICI",
		'I', Items.IRON_INGOT, 'R', Blocks.REDSTONE_BLOCK, 'C', ModItems.EnergizedCircuit, 'G', Items.GOLD_INGOT});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedSpeedUpgrade), new Object[]{" P ","O O","ICI",
		'I', Items.IRON_INGOT, 'P', Items.GOLDEN_PICKAXE, 'C', ModItems.EnergizedCircuit, 'O', ModItems.IOPort});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedQuarryingUpgrade), new Object[]{" P "," C ","I I",
		'I', Items.GOLDEN_PICKAXE, 'P', Items.GOLDEN_PICKAXE, 'C', ModItems.EnergizedCircuit});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumTankUpgrade), new Object[]{" L ","BGB","ICI",
		'B', Items.BUCKET, 'I', Items.IRON_INGOT, 'L', Blocks.LAPIS_BLOCK, 'C', ModItems.LumumCircuit, 'G', Blocks.GLASS});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumPowerUpgrade), new Object[]{"R R"," G ","ICI",
		'I', Items.IRON_INGOT, 'R', Blocks.REDSTONE_BLOCK, 'C', ModItems.LumumCircuit, 'G', Items.GOLD_INGOT});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumSpeedUpgrade), new Object[]{" P ","O O","ICI",
		'I', Items.IRON_INGOT, 'P', Items.GOLDEN_PICKAXE, 'C', ModItems.LumumCircuit, 'O', ModItems.IOPort});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumQuarryingUpgrade), new Object[]{" P "," C ","I I",
		'I', Items.DIAMOND_PICKAXE, 'P', Items.DIAMOND_PICKAXE, 'C', ModItems.LumumCircuit});
		
		//Filters --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.BasicCircuit, 'G', Items.GOLD_NUGGET});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.UpgradedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.StaticCircuit, 'G', ModItems.StaticNugget});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.AdvancedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.EnergizedCircuit, 'G', ModItems.EnergizedNugget});
	}
}
