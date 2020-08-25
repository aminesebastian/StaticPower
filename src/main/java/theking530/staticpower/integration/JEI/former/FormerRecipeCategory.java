package theking530.staticpower.integration.JEI.former;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

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
		return locTitle.getFormattedText();
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
	public void draw(FormerRecipe recipe, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(40, 19, 16, 16);
		GuiDrawUtilities.drawSlot(63, 19, 16, 16);
		GuiDrawUtilities.drawSlot(110, 17, 20, 20);
		GuiPowerBarUtilities.drawPowerBar(8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems((int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<String> getTooltipStrings(FormerRecipe recipe, double mouseX, double mouseY) {
		List<String> output = new ArrayList<String>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			String powerCost = GuiTextUtilities.formatEnergyToString(recipe.getProcessingTime() * recipe.getPowerCost()).getFormattedText();
			output.add("Usage: " + powerCost);
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<ITextComponent> tooltips = new ArrayList<ITextComponent>();
			pBar.getTooltips(mouse, tooltips, false);
			for (ITextComponent tooltip : tooltips) {
				output.add(tooltip.getFormattedText());
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

		List<ItemStack> outputs = new ArrayList<ItemStack>();
		outputs.add(recipe.getRecipeOutput());

		ingredients.setOutputs(VanillaTypes.ITEM, outputs);

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FormerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(MOLD_SLOT, true, 39, 18);
		guiItemStacks.init(INTPUT_SLOT, true, 62, 18);
		guiItemStacks.init(OUTPUT_SLOT, false, 111, 18);
		guiItemStacks.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime() * recipe.getPowerCost(), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
