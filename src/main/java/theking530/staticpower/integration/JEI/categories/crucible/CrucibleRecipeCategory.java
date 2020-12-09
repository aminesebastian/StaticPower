package theking530.staticpower.integration.JEI.categories.crucible;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class CrucibleRecipeCategory extends BaseJEIRecipeCategory<CrucibleRecipe> {
	public static final ResourceLocation CRUCIBLE_UID = new ResourceLocation(StaticPower.MOD_ID, "crucible");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private static final int FLUID_SLOT = 2;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public CrucibleRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Crucible.getTranslationKey());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Crucible));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return CRUCIBLE_UID;
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
	public Class<? extends CrucibleRecipe> getRecipeClass() {
		return CrucibleRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CrucibleRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 50, 12, 16, 16);
		GuiDrawUtilities.drawSlot(matrixStack, 75, 32, 20, 20);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 106, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());
		GuiHeatBarUtilities.drawHeatBar(matrixStack, 28, 54, 4, 48, 1.0f, recipe.getMinimumTemperature(), getNetHighestMultipleOf10(recipe.getMinimumTemperature()));

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 72, 18, 28, 5);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getOutputFluid();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 72, 23, 1, progress, 5, false);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(CrucibleRecipe recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		if (mouseX >= 8 && mouseX < 24 && mouseY < 54 && mouseY >= 4) {
			output.add(new StringTextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the heat bar tooltip.
		if (mouseX >= 28 && mouseX < 32 && mouseY < 54 && mouseY >= 4) {
			output.add(GuiTextUtilities.formatHeatToString(recipe.getMinimumTemperature()));
		}

		return output;
	}

	@Override
	public void setIngredients(CrucibleRecipe recipe, IIngredients ingredients) {
		// Add the inputs.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput().getIngredient());
		ingredients.setInputIngredients(input);

		// Add the output fluid if one exists.
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());

		// Set the output item stacks.
		if(!recipe.getOutput().isEmpty()) {
			ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
		}
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CrucibleRecipe recipe, IIngredients ingredients) {
		// Add the input.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 49, 11);
		guiItemStacks.set(ingredients);
		
		// Add the output item if it exists.
		if (recipe.hasItemOutput()) {
			// Set the outputs.
			IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
			probabilityStacks.init(OUTPUT_SLOT, false, 76, 33);
			probabilityStacks.set(ingredients);
		}

		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(FLUID_SLOT, false, 106, 4, 16, 52, getFluidTankDisplaySize(recipe.getOutputFluid()), false, null);
		fluids.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime() * recipe.getPowerCost(), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
