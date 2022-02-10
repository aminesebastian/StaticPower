package theking530.staticpower.integration.JEI;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;

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

	public void addProbabilityTooltips(IRecipeLayout recipeLayout, int startingSlotIndex, ProbabilityItemStackOutput... outputs) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		// Add the output percentage to the tooltip for the ingredient.
		guiItemStacks.addTooltipCallback(new ITooltipCallback<ItemStack>() {
			@Override
			public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<Component> tooltip) {
				// Only perform for inputs.
				if (!input) {
					// Capture and remove the last line.
					Component lastLine = tooltip.get(tooltip.size() - 1);
					tooltip.remove(tooltip.size() - 1);

					// Transform into the output index space.
					int outputIndex = slotIndex - startingSlotIndex;

					// Get the output.
					ProbabilityItemStackOutput outputWrapper = outputs[outputIndex];

					// Formulate the output percentage tooltip and then add it.
					if (outputWrapper.getOutputChance() != 1.0f) {
						Component outputPercentage = new TranslatableComponent("gui.staticpower.output_chance").append(": ")
								.append(ChatFormatting.GREEN.toString() + String.valueOf((int) (outputWrapper.getOutputChance() * 100)) + "%");
						tooltip.add(outputPercentage);
					}

					// Add the tooltip for the bonus output.
					if (outputWrapper.getAdditionalBonus() > 0) {
						Component bonus = new TranslatableComponent("gui.staticpower.bonus_output").withStyle(ChatFormatting.GREEN).append(": ")
								.append(ChatFormatting.GOLD.toString() + String.valueOf(outputWrapper.getAdditionalBonus()) + ChatFormatting.GRAY.toString()
										+ ChatFormatting.ITALIC.toString() + " (" + String.valueOf((int) (outputWrapper.getBonusChance() * 100)) + "%)");
						tooltip.add(bonus);
					}

					// Add back the last line.
					tooltip.add(lastLine);
				}
			}
		});
	}
}
