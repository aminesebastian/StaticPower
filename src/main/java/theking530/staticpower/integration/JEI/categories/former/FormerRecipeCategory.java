package theking530.staticpower.integration.JEI.categories.former;

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
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class FormerRecipeCategory extends BaseJEIRecipeCategory<FormerRecipe> {
	public static final ResourceLocation FORMER_UID = new ResourceLocation(StaticPower.MOD_ID, "former");
	private static final int MOLD_SLOT = 0;
	private static final int INTPUT_SLOT = 1;
	private static final int OUTPUT_SLOT = 2;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public FormerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Former.getTranslationKey());
		background = guiHelper.createBlankDrawable(140, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Former));
		pBar = new ArrowProgressBar(84, 19);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return FORMER_UID;
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
	public Class<? extends FormerRecipe> getRecipeClass() {
		return FormerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FormerRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 40, 19, 16, 16);
		GuiDrawUtilities.drawSlot(matrixStack, 63, 19, 16, 16);
		GuiDrawUtilities.drawSlot(matrixStack, 110, 17, 20, 20);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(FormerRecipe recipe, double mouseX, double mouseY) {
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
	public void setIngredients(FormerRecipe recipe, IIngredients ingredients) {
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getRequiredMold());
		input.add(recipe.getInputIngredient().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the output.
		ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FormerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(MOLD_SLOT, true, 39, 18);
		guiItemStacks.init(INTPUT_SLOT, true, 62, 18);
		guiItemStacks.set(ingredients);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(OUTPUT_SLOT, false, 112, 19);
		probabilityStacks.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime() * recipe.getPowerCost(), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
