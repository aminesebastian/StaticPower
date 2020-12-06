package theking530.staticpower.integration.JEI;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Vector2D;

public abstract class BaseJEIRecipeCategory<T extends IRecipe<IInventory>> implements IRecipeCategory<T> {
	protected IGuiHelper guiHelper;

	public BaseJEIRecipeCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
	}

	public Vector2D getGuiOrigin(MatrixStack matrixStack) {
		Vector4f vector4f = new Vector4f(0, 0, 0, 1);
		vector4f.transform(matrixStack.getLast().getMatrix());
		return new Vector2D(vector4f.getX(), vector4f.getY());
	}

	public int getFluidTankDisplaySize(FluidStack stack) {
		return getNetHighestMultipleOf10(stack.getAmount());
	}

	public int getNetHighestMultipleOf10(int value) {
		if (value <= 5) {
			return 5;
		} else if (value < 10) {
			return 10;
		} else if (value < 25) {
			return 25;
		} else if (value < 50) {
			return 50;
		} else if (value < 100) {
			return 100;
		} else if (value < 250) {
			return 250;
		} else if (value < 500) {
			return 500;
		} else if (value < 1000) {
			return 1000;
		} else if (value < 10000) {
			return 10000;
		} else if (value < 100000) {
			return 100000;
		} else if (value < 1000000) {
			return 1000000;
		}
		return 0;
	}
}
