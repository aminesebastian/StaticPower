package theking530.staticpower.integration.JEI;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;

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
		return getNetHighestMultipleOf10(stack.getAmount());
	}

	@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	public int getNetHighestMultipleOf10(int value) {
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
}
