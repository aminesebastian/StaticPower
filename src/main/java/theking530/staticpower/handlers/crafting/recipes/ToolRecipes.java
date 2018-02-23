package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.handlers.crafting.CraftHelpers;
import theking530.staticpower.items.ItemMaterials;
import theking530.staticpower.items.tools.basictools.ModTools;

public class ToolRecipes {
	
	public static void registerToolRecipes() {	
		registerToolSet(ingredientOre("ingotCopper"), "copper", new ItemStack(ModTools.CopperAxe), new ItemStack(ModTools.CopperHoe), new ItemStack(ModTools.CopperPickaxe), new ItemStack(ModTools.CopperShovel), new ItemStack(ModTools.CopperSword));
		registerToolSet(ingredientOre("ingotTin"), "tin", new ItemStack(ModTools.TinAxe), new ItemStack(ModTools.TinHoe), new ItemStack(ModTools.TinPickaxe), new ItemStack(ModTools.TinShovel), new ItemStack(ModTools.TinSword));
		registerToolSet(ingredientOre("ingotSilver"), "silver", new ItemStack(ModTools.SilverAxe), new ItemStack(ModTools.SilverHoe), new ItemStack(ModTools.SilverPickaxe), new ItemStack(ModTools.SilverShovel), new ItemStack(ModTools.SilverSword));
		registerToolSet(ingredientOre("ingotLead"), "lead", new ItemStack(ModTools.LeadAxe), new ItemStack(ModTools.LeadHoe), new ItemStack(ModTools.LeadPickaxe), new ItemStack(ModTools.LeadShovel), new ItemStack(ModTools.LeadSword));
		registerToolSet(ingredientOre("ingotPlatinum"), "platinum", new ItemStack(ModTools.PlatinumAxe), new ItemStack(ModTools.PlatinumHoe), new ItemStack(ModTools.PlatinumPickaxe), new ItemStack(ModTools.PlatinumShovel), new ItemStack(ModTools.PlatinumSword));
		registerToolSet(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotStatic), "static", new ItemStack(ModTools.StaticAxe), new ItemStack(ModTools.StaticHoe), new ItemStack(ModTools.StaticPickaxe), new ItemStack(ModTools.StaticShovel), new ItemStack(ModTools.StaticSword));
		registerToolSet(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotEnergized), "energized", new ItemStack(ModTools.EnergizedAxe), new ItemStack(ModTools.EnergizedHoe), new ItemStack(ModTools.EnergizedPickaxe), new ItemStack(ModTools.EnergizedShovel), new ItemStack(ModTools.EnergizedSword));
		registerToolSet(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotLumum), "lumum", new ItemStack(ModTools.LumumAxe), new ItemStack(ModTools.LumumHoe), new ItemStack(ModTools.LumumPickaxe), new ItemStack(ModTools.LumumShovel), new ItemStack(ModTools.LumumSword));
	}
	public static void registerToolSet(Ingredient material, String prefix, ItemStack axe, ItemStack hoe, ItemStack pickaxe, ItemStack shovel, ItemStack sword) {
		RegisterHelper.registerSolderingRecipe(axe, new Object[]{"MM ","MS "," S ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(axe, new Object[]{" MM"," SM"," S ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(hoe, new Object[]{"MM "," S "," S ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(hoe, new Object[]{" MM"," S "," S ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(pickaxe, new Object[]{"MMM"," S "," S ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(shovel, new Object[]{"M  ","S  ","S  ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(shovel, new Object[]{" M "," S "," S ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(shovel, new Object[]{"  M","  S","  S",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(sword, new Object[]{"M  ","M  ","S  ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(sword, new Object[]{" M "," M "," S ",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
		RegisterHelper.registerSolderingRecipe(sword, new Object[]{"  M","  M","  S",
				'M', material, 'S', ingredientFromItem(Items.STICK)});
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


