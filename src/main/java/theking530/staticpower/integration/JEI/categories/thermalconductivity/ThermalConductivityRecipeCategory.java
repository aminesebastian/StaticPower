package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.integration.JEI.categories.thermalconductivity.ThermalConductivityRecipeProvider.ThermalConductivityJEIRecipeWrapper;

public class ThermalConductivityRecipeCategory extends BaseJEIRecipeCategory<ThermalConductivityJEIRecipeWrapper> {
	public static final ResourceLocation THERMAL_CONDUCTIVITY_UID = new ResourceLocation(StaticPower.MOD_ID, "thermal_conductivity");

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public ThermalConductivityRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent("gui.staticpower.heat");
		background = guiHelper.createBlankDrawable(170, 65);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CopperHeatSink));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return THERMAL_CONDUCTIVITY_UID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return locTitle.getString();
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends ThermalConductivityJEIRecipeWrapper> getRecipeClass() {
		return ThermalConductivityJEIRecipeWrapper.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(ThermalConductivityJEIRecipeWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 5, 5, 20, 20);
		GuiDrawUtilities.drawSlot(matrixStack, 30, 5, 135, 55);

		String conductivity = new StringTextComponent("Heat Conductivity: ").appendString(TextFormatting.BLUE.toString())
				.append(GuiTextUtilities.formatConductivityToString(recipe.getRecipe().getThermalConductivity())).getString();

		String heat = new StringTextComponent("Heat Generation: ").appendString(TextFormatting.GOLD.toString())
				.append(GuiTextUtilities.formatHeatRateToString(recipe.getRecipe().getHeatAmount())).getString();

		String overheatTemp = new StringTextComponent("← Overheat: ").appendString(TextFormatting.RED.toString())
				.append(GuiTextUtilities.formatHeatToString(recipe.getRecipe().getOverheatedTemperature())).getString();

		int yPos = 15;
		int xPos = 160;

		GuiDrawUtilities.drawStringWithSize(matrixStack, conductivity, xPos, yPos, 1.0f, Color.EIGHT_BIT_WHITE, true);

		if (recipe.getRecipe().getHeatAmount() > 0) {
			yPos += 10;
			GuiDrawUtilities.drawStringWithSize(matrixStack, heat, xPos, yPos, 1.0f, Color.EIGHT_BIT_WHITE, true);
		}

		if (recipe.getRecipe().hasOverheatingBehaviour()) {
			GuiDrawUtilities.drawSlot(matrixStack, 35, 32, 20, 20);
			GuiDrawUtilities.drawStringWithSize(matrixStack, overheatTemp, xPos, 44f, 1.0f, Color.EIGHT_BIT_WHITE, true);
		}

		if (!recipe.getFluidInput().isEmpty()) {
			if (recipe.getFluidInput().getFluid().getRegistryName().toString().contains("flowing")) {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Flowing)", 26f, 31, 0.5f, TextFormatting.BLUE, false);
			} else {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Still)", 21.5f, 31, 0.5f, TextFormatting.BLUE, false);
			}
		}

		if (!recipe.getOutputFluid().isEmpty()) {
			if (recipe.getOutputFluid().getFluid().getRegistryName().toString().contains("flowing")) {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Flowing)", 56f, 58, 0.5f, TextFormatting.WHITE, false);
			} else {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Still)", 51.5f, 58, 0.5f, TextFormatting.WHITE, false);
			}
		}
	}

	@Override
	public List<ITextComponent> getTooltipStrings(ThermalConductivityJEIRecipeWrapper recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		return output;
	}

	@Override
	public void setIngredients(ThermalConductivityJEIRecipeWrapper recipe, IIngredients ingredients) {
		// Add the input ingredients.
		if (!recipe.getRecipe().isAirRecipe()) {
			List<Ingredient> input = new ArrayList<Ingredient>();
			input.add(recipe.getInput());
			ingredients.setInputIngredients(input);
		}

		if (!recipe.getFluidInput().isEmpty()) {
			// Set the input fluid.
			ingredients.setInput(VanillaTypes.FLUID, recipe.getFluidInput());
		}

		// Add the output fluid if one exists.
		if (!recipe.getOutputFluid().isEmpty()) {
			ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
		}

		// Add the output items.
		if (!recipe.getOutputBlock().isEmpty()) {
			ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutputBlock());
		}
		if (!recipe.getOutputItem().isEmpty()) {
			ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutputItem());
		}
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ThermalConductivityJEIRecipeWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		// Set the input.
		if (!recipe.getRecipe().isAirRecipe()) {
			guiItemStacks.init(0, true, 6, 6);
		}

		// Set the input fluid.
		if (!recipe.getFluidInput().isEmpty()) {
			// Add the fluid.
			IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
			fluids.init(1, true, 5, 5, 20, 20, recipe.getFluidInput().getAmount(), true, null);
			fluids.set(ingredients);
		}

		// Set the overheated block output.
		if (!recipe.getOutputBlock().isEmpty()) {
			guiItemStacks.init(2, false, 37, 34);
		}

		guiItemStacks.set(ingredients);

		// Set the overheated item output.
		if (!recipe.getOutputItem().isEmpty()) {
			IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
			probabilityStacks.init(3, false, 37, 34);
			probabilityStacks.set(ingredients);
		}

		// Add the fluid.
		if (!recipe.getOutputFluid().isEmpty()) {
			IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
			fluids.init(4, false, 35, 32, 20, 20, recipe.getOutputFluid().getAmount(), false, null);
			fluids.set(ingredients);
		}
	}
}
