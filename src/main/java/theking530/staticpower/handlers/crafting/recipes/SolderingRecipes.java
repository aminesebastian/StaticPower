package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.items.ModItems;

public class SolderingRecipes {
	
	@SuppressWarnings("all")
	public static void registerSolderingRecipes() {
		
		//Circuit Recipes
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicCircuit, 4), new Object[]{"R R", " I ", "R R", 
		'I', ModItems.IronPlate, 'R', Items.REDSTONE});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticCircuit, 4), new Object[]{"R R", "GSG", "R R", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'S', ModItems.StaticPlate, 'C', ModItems.BasicCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedCircuit, 4), new Object[]{"R R", "GEG", "R R", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'E', ModItems.EnergizedPlate, 'C', ModItems.StaticCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumCircuit, 4), new Object[]{"R R", "GLG", "R R", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'L', ModItems.LumumPlate, 'C', ModItems.EnergizedCircuit});
		
		//Batteries --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicBattery), new Object[]{" C ", "IRI", "IRI", 
		'I', ModItems.IronPlate, 'R', Blocks.REDSTONE_BLOCK, 'C', ModItems.CopperPlate});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticBattery), new Object[]{" S ", "IDI", "IRI", 
		'I', ModItems.StaticIngot, 'R', Blocks.REDSTONE_BLOCK, 'S', ModItems.StaticNugget, 'D', Items.DIAMOND});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedBattery), new Object[]{" E ", "IDI", "IRI", 
		'I', ModItems.EnergizedIngot, 'R', Blocks.REDSTONE_BLOCK, 'E', ModItems.EnergizedNugget, 'D', ModItems.EnergizedEnergyCrystal});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumBattery), new Object[]{" L ", "IDI", "IRI", 
		'I', ModItems.LumumIngot, 'R', Blocks.REDSTONE_BLOCK, 'L', ModItems.LumumNugget, 'D', ModItems.LumumEnergyCrystal});	
		
		//Tools -------------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.ElectricSolderingIron), new Object[]{"I  "," IL"," LR",
		'R', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.ElectricSolderingIron), new Object[]{"  I","LI ","RL ",
		'R', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 4, 4)});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.CoordinateMarker), new Object[]{"IIR","IGI","CBC",
		'B', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'G', Blocks.GLASS, 'C', ModItems.BasicCircuit});	
		
		//Upgrades --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Items.REDSTONE, 'G',  ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', Blocks.PLANKS, 'G',  ModItems.BasicUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Items.REDSTONE, 'G',  ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.IRON_PICKAXE, 'G', ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.StaticPlate, 'G',  ModItems.StaticUpgradePlate});
			
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Items.REDSTONE, 'G',  ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.GOLDEN_PICKAXE, 'G', ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.EnergizedPlate, 'G',  ModItems.EnergizedUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', Blocks.LAPIS_BLOCK, 'G', ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Blocks.REDSTONE_BLOCK, 'G',  ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Blocks.GLOWSTONE, 'G',  ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.DIAMOND_PICKAXE, 'G', ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.LumumPlate, 'G',  ModItems.LumumUpgradePlate});
		
		//Filters --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.BasicCircuit, 'G', ModItems.IronNugget});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.UpgradedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.StaticCircuit, 'G', ModItems.StaticNugget});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.AdvancedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.EnergizedCircuit, 'G', ModItems.EnergizedNugget});
	}
}
