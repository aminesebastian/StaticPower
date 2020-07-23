package theking530.staticpower.integration.JEI;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidStack;

public abstract class BaseJEIRecipeCategory<T extends IRecipe<IInventory>> implements IRecipeCategory<T> {
	protected IGuiHelper guiHelper;

	public BaseJEIRecipeCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
	}

	public int getFluidTankDisplaySize(FluidStack stack) {
		if (stack.getAmount() <= 100) {
			return 100;
		} else if (stack.getAmount() <= 1000) {
			return 1000;
		} else if (stack.getAmount() <= 10000) {
			return 10000;
		} else if (stack.getAmount() <= 100000) {
			return 100000;
		} else if (stack.getAmount() <= 1000000) {
			return 1000000;
		}
		return 0;
	}
}
