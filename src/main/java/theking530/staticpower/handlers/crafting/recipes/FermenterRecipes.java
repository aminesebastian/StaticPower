package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.fluids.ModFluids;

public class FermenterRecipes {
	
	public static void registerFermenterRecipes() {
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.BEETROOT), ethanol(35));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.POTATO), ethanol(25));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.CARROT), ethanol(20));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.WHEAT), ethanol(15));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.MELON), ethanol(50));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.PUMPKIN), ethanol(50));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.SPIDER_EYE), ethanol(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.SUGAR), ethanol(50));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.REEDS), ethanol(40));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK), ethanol(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.RED_MUSHROOM_BLOCK), ethanol(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.WATERLILY), ethanol(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.BROWN_MUSHROOM), ethanol(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.RED_MUSHROOM), ethanol(100));
		oreDictionaryInputRecipe("treeSapling", ethanol(25));
		oreDictionaryInputRecipe("treeLeaves", ethanol(10));
	}
	public static FluidStack ethanol(int amount) {
		return new FluidStack(ModFluids.Ethanol, amount);
	}
	public static void oreDictionaryInputRecipe(String input, FluidStack output) {
		if(OreDictionary.getOres(input).size() > 0) {
			RegisterHelper.registerFermenterRecipe(OreDictionary.getOres(input).get(0), output);
		}
	}
}
