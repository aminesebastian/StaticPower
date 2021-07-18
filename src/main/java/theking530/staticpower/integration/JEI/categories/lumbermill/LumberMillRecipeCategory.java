package theking530.staticpower.integration.JEI.categories.lumbermill;

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
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class LumberMillRecipeCategory extends BaseJEIRecipeCategory<LumberMillRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "lumber_mill");
	private static final int INTPUT_SLOT = 0;
	private static final int PRIMARY_OUTPUT_SLOT = 1;
	private static final int SECONDARY_OUTPUT_SLOT = 2;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public LumberMillRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.LumberMill.getTranslationKey());
		background = guiHelper.createBlankDrawable(176, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.LumberMill));
		pBar = new ArrowProgressBar(63, 19);
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
	public Class<? extends LumberMillRecipe> getRecipeClass() {
		return LumberMillRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(LumberMillRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 41, 19, 16, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 89, 17, 20, 20, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 119, 17, 20, 20, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 153, 54, 1.0f, 16, 48, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(LumberMillRecipe recipe, double mouseX, double mouseY) {
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
	public void setIngredients(LumberMillRecipe recipe, IIngredients ingredients) {
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the output items.
		List<ProbabilityItemStackOutput> outputs = new ArrayList<ProbabilityItemStackOutput>();
		outputs.add(recipe.getPrimaryOutput());
		if (!recipe.getSecondaryOutput().isEmpty()) {
			outputs.add(recipe.getSecondaryOutput());
		}

		ingredients.setOutputs(PluginJEI.PROBABILITY_ITEM_STACK, outputs);

		// Set the output fluids.
		if (recipe.hasOutputFluid()) {
			ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
		}
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, LumberMillRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 40, 18);
		guiItemStacks.set(ingredients);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(PRIMARY_OUTPUT_SLOT, false, 91, 19);
		if (!recipe.getSecondaryOutput().isEmpty()) {
			probabilityStacks.init(SECONDARY_OUTPUT_SLOT, false, 121, 19);
		}
		probabilityStacks.set(ingredients);

		// Add the fluid.
		if (recipe.hasOutputFluid()) {
			IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
			fluids.init(3, false, 153, 6, 16, 48, getFluidTankDisplaySize(recipe.getOutputFluid()), false, null);
			fluids.set(ingredients);
		}

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
