package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;

public class SolderingRecipes {
	
	@SuppressWarnings("all")
	public static void registerSolderingRecipes() {
		
		//Circuit Recipes
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicCircuit), new Object[]{"WWW", "RIR", "WWW", 
		'I', ingredientOre("plateIron"), 'R', Items.REDSTONE, 'W', ingredientFromItem(ModItems.CopperWire)});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticCircuit), new Object[]{"WWW", "RSR", "WWW", 
		'R', Items.REDSTONE, 'S', ModItems.StaticPlate, 'W', ingredientFromItem(ModItems.CopperWire)});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedCircuit), new Object[]{"WWW", "RER", "WWW", 
		'R', Items.REDSTONE, 'E', ModItems.EnergizedPlate, 'W', ingredientFromItem(ModItems.SilverWire)});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumCircuit), new Object[]{"WWW", "RLR", "WWW", 
		'R', Items.REDSTONE, 'L', ModItems.LumumPlate, 'W', ingredientFromItem(ModItems.GoldWire)});
		
		//Batteries --------------------------------------------------------------------------------------------------
		for(int i=0; i<OreDictionary.getOres("nuggetIron").size(); i++) {
			RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicBattery), new Object[]{" C ", "IRI", "IRI", 
					'I', ingredientOre("plateIron"), 'R', Blocks.REDSTONE_BLOCK, 'C', Items.IRON_NUGGET});		
		}
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticBattery), new Object[]{" S ", "IDI", "IRI", 
		'I', ModItems.StaticIngot, 'R', Blocks.REDSTONE_BLOCK, 'S', ModItems.StaticNugget, 'D', Items.DIAMOND});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedBattery), new Object[]{" E ", "IDI", "IRI", 
		'I', ModItems.EnergizedIngot, 'R', Blocks.REDSTONE_BLOCK, 'E', ModItems.EnergizedNugget, 'D', ModItems.EnergizedEnergyCrystal});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumBattery), new Object[]{" L ", "IDI", "IRI", 
		'I', ModItems.LumumIngot, 'R', Blocks.REDSTONE_BLOCK, 'L', ModItems.LumumNugget, 'D', ModItems.LumumEnergyCrystal});	
		
		//Tools -------------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.ElectricSolderingIron), new Object[]{"I  "," IL"," LR",
		'R', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 1, 4)});		
	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.CoordinateMarker), new Object[]{"IIR","IGI","CBC",
		'B', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'G', ingredientOre("blockGlass"), 'C', ModItems.BasicCircuit});	
		
		//Upgrades --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ingredientOre("plateTin"), 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ingredientOre("plateTin"), 'R', Items.REDSTONE, 'G',  ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ingredientOre("plateTin"), 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ingredientOre("plankWood"), 'G',  ModItems.BasicUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ingredientOre("plateTin"), 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ingredientOre("plateTin"), 'R', Items.REDSTONE, 'G',  ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ingredientOre("plateTin"), 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.IRON_PICKAXE, 'G', ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.StaticPlate, 'G',  ModItems.StaticUpgradePlate});
			
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ingredientOre("plateTin"), 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ingredientOre("plateTin"), 'R', Items.REDSTONE, 'G',  ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ingredientOre("plateTin"), 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.GOLDEN_PICKAXE, 'G', ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.EnergizedPlate, 'G',  ModItems.EnergizedUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ingredientOre("plateTin"), 'L', Blocks.LAPIS_BLOCK, 'G', ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ingredientOre("plateTin"), 'R', Blocks.REDSTONE_BLOCK, 'G',  ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ingredientOre("plateTin"), 'P', Blocks.GLOWSTONE, 'G',  ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.DIAMOND_PICKAXE, 'G', ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.LumumPlate, 'G',  ModItems.LumumUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.basicCapacityUpgrade, new Object[]{"SCS"," G ","SCS",
		'S', Items.STICK, 'C',  ingredientOre("chestWood"), 'G',  ModItems.BasicUpgradePlate});	
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.ironCapacityUpgrade, new Object[]{" P ","UPU"," P ",
		'P', ingredientOre("plateIron"), 'U', ModItems.DigistoreCapacityUpgrade.basicCapacityUpgrade});		
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.goldCapacityUpgrade, new Object[]{" P ","UPU"," P ",
		'P', ingredientOre("plateGold"), 'U', ModItems.DigistoreCapacityUpgrade.ironCapacityUpgrade});	
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.leadCapacityUpgrade, new Object[]{" P ","UPU"," P ",
		'P', ingredientOre("plateLead"), 'U', ModItems.DigistoreCapacityUpgrade.basicCapacityUpgrade});	
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.obsidianCapacityUpgrade, new Object[]{"C C"," G ","C C",
		'C', Blocks.OBSIDIAN, 'G',  ModItems.BasicUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.staticCapacityUpgrade, new Object[]{" P ","UPU"," P ",
		'P', ModItems.StaticPlate, 'U', ModItems.DigistoreCapacityUpgrade.goldCapacityUpgrade});		
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.energizedCapacityUpgrade, new Object[]{" P ","UPU"," P ",
		'P', ModItems.EnergizedPlate, 'U', ModItems.DigistoreCapacityUpgrade.staticCapacityUpgrade});		
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreCapacityUpgrade.lumumCapacityUpgrade, new Object[]{" P ","UPU"," P ",
		'P', ModItems.LumumPlate, 'U', ModItems.DigistoreCapacityUpgrade.energizedCapacityUpgrade});
		
		RegisterHelper.registerSolderingRecipe(ModItems.DigistoreMiscUpgrade.VoidUprgade, new Object[]{"OOO","OBO","OOO",
		'O', Blocks.OBSIDIAN, 'B', ModItems.BasicUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicOutputMultiplierUpgrade), new Object[]{" F ","FGF","F F",
		'F', Item.getItemFromBlock(Blocks.GRAVEL), 'G', ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticOutputMultiplierUpgrade), new Object[]{" F ","FGF","F F",
		'F', Items.FLINT,  'G', ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedOutputMultiplierUpgrade), new Object[]{" F ","FGF","F F",
		'F', ingredientOre("itemSilicon"), 'G', ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumOutputMultiplierUpgrade), new Object[]{" F ","FGF","F F",
		'F', Item.getItemFromBlock(Blocks.PISTON), 'G', ModItems.LumumUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.TeleportUpgrade), new Object[]{"EHE"," P ","EHE",
		'H', Item.getItemFromBlock(Blocks.HOPPER), 'E', Items.ENDER_PEARL, 'P',  ModItems.EnergizedUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.ExperienceVacuumUpgrade), new Object[]{"CGC","GPG","CGC",
		'G', ingredientOre("blockGlass"), 'C', ModItems.BaseFluidCapsule, 'P',  ModItems.StaticUpgradePlate});
		
		//Filters --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.BasicCircuit, 'G', Items.IRON_NUGGET});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.UpgradedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.StaticCircuit, 'G', ModItems.StaticNugget});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.AdvancedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.EnergizedCircuit, 'G', ModItems.EnergizedNugget});
		
		//Gate Components
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModBlocks.LogicGateBasePlate, 2), new Object[]{"   ", "   ", "SBS", 
		'S', ingredientOre("plateIron"), 'B', ModItems.BasicCircuit});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LogicGateServo, 4), new Object[]{"S S", " C ", "QBQ", 
		'S', ingredientOre("plateIron"), 'B', ModItems.BasicBattery, 'C', ModItems.CopperCoil, 'Q', ingredientOre("wireSilver")});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LogicGatePowerSync, 2), new Object[]{"   ", " R ", " S ", 
		'S', ModItems.BasicCircuit, 'R', Blocks.REDSTONE_TORCH});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.InvertedLogicGatePowerSync, 2), new Object[]{" Q ", " R ", " S ", 
		'S', ModItems.BasicCircuit, 'R', Blocks.REDSTONE_TORCH, 'Q', Items.REDSTONE});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.Diode, 8), new Object[]{"   ", "SRS", "QCQ", 
		'S', ingredientOre("plateIron"), 'Q', ingredientOre("wireSilver"), 'R', Item.getItemFromBlock(Blocks.UNPOWERED_REPEATER), 'C', ModItems.BasicCircuit});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.Transistor, 8), new Object[]{" S ", "SRS", "QCQ", 
		'S', ingredientOre("plateIron"), 'Q', ingredientOre("wireSilver"), 'R', Item.getItemFromBlock(Blocks.LEVER), 'C', ModItems.BasicCircuit});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.InternalClock, 2), new Object[]{" S ", "SRS", "QCQ", 
		'S', ingredientOre("plateIron"), 'Q', ingredientOre("wireSilver"), 'R', Items.QUARTZ, 'C', ModItems.BasicCircuit});
		
		//Fluid Canisters
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticFluidCapsule), new Object[]{"PPP", "CPC", "PPP", 
		'P', ModItems.StaticPlate, 'C', ModItems.BaseFluidCapsule});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedFluidCapsule), new Object[]{"PPP", "CPC", "PPP", 
		'P', ModItems.EnergizedPlate, 'C', ModItems.StaticFluidCapsule});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumFluidCapsule), new Object[]{"PPP", "CPC", "PPP", 
		'P', ModItems.LumumPlate, 'C', ModItems.EnergizedFluidCapsule});	
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.MemoryChip, 8), new Object[]{"PPP", "SSS", "QCQ", 
		'P', ingredientOre("plateIron"), 'Q', ingredientOre("wireSilver"), 'S', ingredientOre("itemSilicon"), 'C', ModItems.BasicCircuit});
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
}
