package theking530.staticpower.integration.JEI.poweredfurnace;

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
import net.minecraft.item.crafting.FurnaceRecipe;
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
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.powered.poweredfurnace.TileEntityPoweredFurnace;

public class PoweredFurnaceRecipeCategory extends BaseJEIRecipeCategory<FurnaceRecipe> {
	public static final ResourceLocation POWERED_FURNACE_UID = new ResourceLocation(StaticPower.MOD_ID, "powered_furnace");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public PoweredFurnaceRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.PoweredFurnace.getTranslationKey());
		background = guiHelper.createBlankDrawable(120, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.PoweredFurnace));
		pBar = new ArrowProgressBar(62, 19);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return POWERED_FURNACE_UID;
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
	public Class<? extends FurnaceRecipe> getRecipeClass() {
		return FurnaceRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FurnaceRecipe recipe, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(41, 19, 16, 16);
		GuiDrawUtilities.drawSlot(89, 17, 20, 20);

		// This doesn't actually draw the fluid, just the bars.
		GuiPowerBarUtilities.drawPowerBar(8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems((int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<String> getTooltipStrings(FurnaceRecipe recipe, double mouseX, double mouseY) {
		List<String> output = new ArrayList<String>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			String powerCost = GuiTextUtilities.formatEnergyToString(TileEntityPoweredFurnace.getCookTime(recipe) * TileEntityPoweredFurnace.DEFAULT_PROCESSING_COST).getFormattedText();
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
	public void setIngredients(FurnaceRecipe recipe, IIngredients ingredients) {
		// Sanity Check
		if (recipe.getIngredients().size() != 1) {
			return;
		}

		// Add the input ingredients.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getIngredients().get(0));
		ingredients.setInputIngredients(input);

		// Add the output item.
		List<ItemStack> outputs = new ArrayList<ItemStack>();
		outputs.add(recipe.getRecipeOutput());
		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FurnaceRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 40, 18);
		guiItemStacks.init(OUTPUT_SLOT, false, 90, 18);
		guiItemStacks.set(ingredients);

		powerTimer = guiHelper.createTickTimer(TileEntityPoweredFurnace.getCookTime(recipe), TileEntityPoweredFurnace.getCookTime(recipe) * TileEntityPoweredFurnace.DEFAULT_PROCESSING_COST, true);
		processingTimer = guiHelper.createTickTimer(TileEntityPoweredFurnace.getCookTime(recipe), TileEntityPoweredFurnace.getCookTime(recipe), false);
	}
}
