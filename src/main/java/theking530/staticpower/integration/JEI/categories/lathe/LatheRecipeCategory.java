package theking530.staticpower.integration.JEI.categories.lathe;

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
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class LatheRecipeCategory extends BaseJEIRecipeCategory<LatheRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "lathe");
	private static final int PRIMARY_OUTPUT_SLOT = 9;
	private static final int SECONDARY_OUTPUT_SLOT = 10;
	private static final int FLUID_OUTPUT_SLOT = 11;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public LatheRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Lathe.getTranslationKey());
		background = guiHelper.createBlankDrawable(176, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Lathe));
		pBar = new ArrowProgressBar(91, 6);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return UID;
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
	public Class<? extends LatheRecipe> getRecipeClass() {
		return LatheRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(LatheRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				GuiDrawUtilities.drawSlot(matrixStack, 32 + x * 18, 5 + y * 18, 16, 16, 0);
			}
		}

		GuiDrawUtilities.drawSlot(matrixStack, 120, 5, 20, 20, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 120, 39, 20, 20, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 153, 54, 1.0f, 16, 48, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(LatheRecipe recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new StringTextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<ITextComponent> tooltips = new ArrayList<ITextComponent>();
			pBar.getTooltips(mouse, tooltips, false);
			for (ITextComponent tooltip : tooltips) {
				output.add(tooltip);
			}
		}

		return output;
	}

	@Override
	public void setIngredients(LatheRecipe recipe, IIngredients ingredients) {
		List<Ingredient> inputs = new ArrayList<Ingredient>();
		for (StaticPowerIngredient input : recipe.getInputs()) {
			inputs.add(input.getIngredient());
		}
		ingredients.setInputIngredients(inputs);

		// Set the output fluid.
		if (recipe.hasOutputFluid()) {
			ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
		}

		// Set the output items.
		List<ProbabilityItemStackOutput> outputs = new ArrayList<ProbabilityItemStackOutput>();
		outputs.add(recipe.getPrimaryOutput());
		outputs.add(recipe.getSecondaryOutput());
		ingredients.setOutputs(PluginJEI.PROBABILITY_ITEM_STACK, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, LatheRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		// Add the inputs.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				guiItemStacks.init(x + (y * 3), true, 31 + x * 18, 4 + y * 18);
			}
		}

		// Set the ingredients.
		guiItemStacks.set(ingredients);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(PRIMARY_OUTPUT_SLOT, false, 122, 7);
		probabilityStacks.init(SECONDARY_OUTPUT_SLOT, false, 122, 41);
		probabilityStacks.set(ingredients);

		// Add the fluid.
		if (recipe.hasOutputFluid()) {
			IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
			fluids.init(FLUID_OUTPUT_SLOT, false, 153, 6, 16, 48, getFluidTankDisplaySize(recipe.getOutputFluid()), false, null);
			fluids.set(ingredients);
		}

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
