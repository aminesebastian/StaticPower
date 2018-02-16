package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.CraftHelpers;

public class FermenterRecipes {
	
	public static void registerFermenterRecipes() {
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.BEETROOT), mash(35));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.BEETROOT_SEEDS), mash(15));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.POTATO), mash(25));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.CARROT), mash(20));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.WHEAT), mash(15));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.WHEAT_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.POISONOUS_POTATO), mash(100));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.MELON_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.MELON), mash(50));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.PUMPKIN_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromBlock(Blocks.PUMPKIN), mash(50));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.SPIDER_EYE), mash(100));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.SUGAR), mash(50));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromItem(Items.REEDS), mash(40));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromBlock(Blocks.WATERLILY), mash(100));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromBlock(Blocks.BROWN_MUSHROOM), mash(100));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientFromBlock(Blocks.RED_MUSHROOM), mash(100));

		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientOre("treeSapling"), mash(25));
		RegisterHelper.registerFermenterRecipe(CraftHelpers.ingredientOre("treeLeaves"), mash(10));
	}
	public static FluidStack mash(int amount) {
		return new FluidStack(ModFluids.Mash, amount);
	}
}
