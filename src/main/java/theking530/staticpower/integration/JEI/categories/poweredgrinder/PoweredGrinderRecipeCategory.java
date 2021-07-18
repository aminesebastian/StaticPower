package theking530.staticpower.integration.JEI.categories.poweredgrinder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
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
import theking530.staticcore.gui.widgets.progressbars.GrinderProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class PoweredGrinderRecipeCategory extends BaseJEIRecipeCategory<GrinderRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "grinder");
	private static final int INTPUT_SLOT = 0;
	private static final int PRIMARY_OUTPUT_SLOT = 1;
	private static final int SECONDARY_OUTPUT_SLOT = 2;
	private static final int TERTIARY_OUTPUT_SLOT = 3;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final GrinderProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public PoweredGrinderRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.PoweredGrinder.getTranslationKey());
		background = guiHelper.createBlankDrawable(150, 70);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.PoweredGrinder));
		pBar = new GrinderProgressBar(79, 26);
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
	public Class<? extends GrinderRecipe> getRecipeClass() {
		return GrinderRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(GrinderRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 80, 6, 16, 16, 0);

		GuiDrawUtilities.drawSlot(matrixStack, 78, 46, 20, 20, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 104, 32, 20, 20, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 52, 32, 20, 20, 0);

		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(GrinderRecipe recipe, double mouseX, double mouseY) {
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
	public void setIngredients(GrinderRecipe recipe, IIngredients ingredients) {
		// Set inputs.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInputIngredient().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the output items.
		List<ProbabilityItemStackOutput> outputs = new ArrayList<ProbabilityItemStackOutput>();
		for (ProbabilityItemStackOutput output : recipe.getOutputItems()) {
			outputs.add(output);
		}
		ingredients.setOutputs(PluginJEI.PROBABILITY_ITEM_STACK, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, GrinderRecipe recipe, IIngredients ingredients) {
		// Set input.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 79, 5);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(PRIMARY_OUTPUT_SLOT, false, 80, 48);
		probabilityStacks.init(SECONDARY_OUTPUT_SLOT, false, 54, 34);
		probabilityStacks.init(TERTIARY_OUTPUT_SLOT, false, 106, 34);
		probabilityStacks.set(ingredients);

		guiItemStacks.set(ingredients);

		// Add the fluid.
		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
