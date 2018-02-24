package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.Craft;

public class FermenterRecipes {
	
	public static void registerFermenterRecipes() {
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.BEETROOT), mash(35));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.BEETROOT_SEEDS), mash(15));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.POTATO), mash(25));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.CARROT), mash(20));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.WHEAT), mash(15));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.WHEAT_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.POISONOUS_POTATO), mash(100));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.MELON_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.MELON), mash(50));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.PUMPKIN_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(Craft.block(Blocks.PUMPKIN), mash(50));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.SPIDER_EYE), mash(100));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.SUGAR), mash(50));
		RegisterHelper.registerFermenterRecipe(Craft.item(Items.REEDS), mash(40));
		RegisterHelper.registerFermenterRecipe(Craft.block(Blocks.WATERLILY), mash(100));
		RegisterHelper.registerFermenterRecipe(Craft.block(Blocks.BROWN_MUSHROOM), mash(100));
		RegisterHelper.registerFermenterRecipe(Craft.block(Blocks.RED_MUSHROOM), mash(100));

		RegisterHelper.registerFermenterRecipe(Craft.ore("treeSapling"), mash(25));
		RegisterHelper.registerFermenterRecipe(Craft.ore("treeLeaves"), mash(10));
	}
	public static FluidStack mash(int amount) {
		return new FluidStack(ModFluids.Mash, amount);
	}
}
