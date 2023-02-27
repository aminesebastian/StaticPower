package theking530.staticpower.integration.JEI;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.utilities.Vector2D;

public abstract class BaseJEIRecipeCategory<T extends Recipe<Container>> implements IRecipeCategory<T> {
	protected IGuiHelper guiHelper;

	public BaseJEIRecipeCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
	}

	public Vector2D getGuiOrigin(PoseStack matrixStack) {
		Vector4f vector4f = new Vector4f(0, 0, 0, 1);
		vector4f.transform(matrixStack.last().pose());
		return new Vector2D(vector4f.x(), vector4f.y());
	}

	public int getFluidTankDisplaySize(FluidStack stack) {
		return getFluidTankDisplaySize(stack.getAmount());
	}

	public int getFluidTankDisplaySize(int amount) {
		return getNetHighestMultipleOf10(amount);
	}

	/**
	 * Gets the fluid input from the recipe slots view. The offset values determines
	 * which fluid input. Example: when offset == 1, we return the first fluid input
	 * we encounter. If offset == 4, we return the fourth fluid input, or an empty
	 * stack if there are not 4 fluid inputs.
	 * 
	 * @param recipeSlotsView
	 * @param offset
	 * @return
	 */
	public FluidStack getNthFluidInput(IRecipeSlotsView recipeSlotsView, int offset) {
		int hits = 0;
		for (IRecipeSlotView view : recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT)) {
			if (view.getDisplayedIngredient().get().getIngredient() instanceof FluidStack) {
				if (hits == offset) {
					return (FluidStack) view.getDisplayedIngredient().get().getIngredient();
				}
				hits++;
			}
		}
		return FluidStack.EMPTY;
	}

	public int getNetHighestMultipleOf10(int value) {
		// If the amount == 1, it's usually just for display so we want to render a 100%
		// full bar.
		if (value == 1) {
			return 1;
		}

		if (value < 50) {
			return 50;
		} else if (value < 100) {
			return 100;
		} else if (value < 250) {
			return 250;
		} else if (value < 500) {
			return 500;
		} else if (value < 1000) {
			return 1000;
		} else if (value < 2500) {
			return 2500;
		} else if (value < 5000) {
			return 5000;
		} else if (value < 10000) {
			return 10000;
		} else if (value < 15000) {
			return 15000;
		} else if (value < 20000) {
			return 20000;
		} else if (value < 30000) {
			return 30000;
		} else if (value < 50000) {
			return 50000;
		} else if (value < 100000) {
			return 100000;
		} else if (value < 1000000) {
			return 1000000;
		}
		return 0;
	}

	protected IRecipeSlotBuilder addFluidIngredientSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole recipeIngredientRole, int x, int y, int width, int height,
			FluidIngredient ingredient) {
		IRecipeSlotBuilder fluidSlot = builder.addSlot(RecipeIngredientRole.INPUT, x, y).setFluidRenderer(getFluidTankDisplaySize(ingredient.getAmount()), false, width, height);
		for (FluidStack fluid : ingredient.getFluids()) {
			fluidSlot.addFluidStack(fluid.getFluid(), ingredient.getAmount());
		}
		return fluidSlot;
	}
}
