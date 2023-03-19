package theking530.staticpower.data.crafting;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.StaticCoreRecipeManager;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.init.ModRecipeTypes;

public class StaticPowerRecipeRegistry {
	/**
	 * Checks to see if the provided itemstack is a valid former mold used in any
	 * recipe.
	 * 
	 * @param stack
	 * @return
	 */
	public static boolean isValidFormerMold(ItemStack stack) {
		for (AbstractStaticPowerRecipe recipe : StaticCoreRecipeManager.getRecipes()
				.get(ModRecipeTypes.FORMER_RECIPE_TYPE.get())) {
			FormerRecipe formerRecipe = (FormerRecipe) recipe;
			if (formerRecipe.getRequiredMold().test(stack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if the provided itemstack is a valid casting mold used in any
	 * recipe.
	 * 
	 * @param stack
	 * @return
	 */
	public static boolean isValidCastingMold(ItemStack stack) {
		for (AbstractStaticPowerRecipe recipe : StaticCoreRecipeManager.getRecipes()
				.get(ModRecipeTypes.CASTING_RECIPE_TYPE.get())) {
			CastingRecipe castingRecipe = (CastingRecipe) recipe;
			if (castingRecipe.getRequiredMold().test(stack)) {
				return true;
			}
		}
		return false;
	}
}
