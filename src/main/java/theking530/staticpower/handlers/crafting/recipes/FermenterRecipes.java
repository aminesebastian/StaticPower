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
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.BEETROOT), mash(35));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.BEETROOT_SEEDS), mash(15));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.POTATO), mash(25));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.CARROT), mash(20));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.WHEAT), mash(15));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.WHEAT_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.POISONOUS_POTATO), mash(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.MELON_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.MELON), mash(50));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.PUMPKIN_SEEDS), mash(10));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.PUMPKIN), mash(50));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.SPIDER_EYE), mash(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Items.SUGAR), mash(50));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.REEDS), mash(40));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK), mash(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.RED_MUSHROOM_BLOCK), mash(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.WATERLILY), mash(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.BROWN_MUSHROOM), mash(100));
		RegisterHelper.registerFermenterRecipe(new ItemStack(Blocks.RED_MUSHROOM), mash(100));
		oreDictionaryInputRecipe("treeSapling", mash(25));
		oreDictionaryInputRecipe("treeLeaves", mash(10));
	}
	public static FluidStack mash(int amount) {
		return new FluidStack(ModFluids.Mash, amount);
	}
	public static void oreDictionaryInputRecipe(String input, FluidStack output) {
		if(OreDictionary.getOres(input).size() > 0) {
			RegisterHelper.registerFermenterRecipe(OreDictionary.getOres(input).get(0), output);
		}
	}
}
