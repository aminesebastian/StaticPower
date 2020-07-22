package theking530.staticpower.integration.JEI.solidgenerator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.progressbars.ArrowProgressBar;
import theking530.common.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.powered.solidgenerator.TileEntitySolidGenerator;
import theking530.staticpower.utilities.MetricConverter;
import theking530.staticpower.utilities.Reference;

public class SolidGeneratorRecipeCategory extends BaseJEIRecipeCategory<SolidFuelRecipe> {
	public static final ResourceLocation SOLID_GENERATOR_UID = new ResourceLocation(Reference.MOD_ID, "solid_generator");
	private static final int INTPUT_SLOT = 0;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawableStatic flamesBg;
	private final IDrawableStatic flames;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public SolidGeneratorRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.SolidGenerator.getTranslationKey());
		background = guiHelper.createBlankDrawable(90, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SolidGenerator));
		flamesBg = guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/flames.png"), 0, 14, 14, 14).setTextureSize(32, 32).build();
		flames = guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/gui/flames.png"), 0, 0, 14, 14).setTextureSize(32, 32).build();
		pBar = new ArrowProgressBar(55, 19).setFlipped(true);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return SOLID_GENERATOR_UID;
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
	public Class<? extends SolidFuelRecipe> getRecipeClass() {
		return SolidFuelRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(SolidFuelRecipe recipe, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(66, 19, 16, 16);

		GuiPowerBarUtilities.drawPowerBar(8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		flamesBg.draw(67, 40);
		flames.draw(67, 40, (int) (((float)processingTimer.getValue() / processingTimer.getMaxValue()) * 14.0f), 0, 0, 0);

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems((int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<String> getTooltipStrings(SolidFuelRecipe recipe, double mouseX, double mouseY) {
		List<String> output = new ArrayList<String>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			int burnTime = ForgeHooks.getBurnTime(recipe.getFuel());
			String generation = new MetricConverter(TileEntitySolidGenerator.DEFAULT_POWER_GENERATION * burnTime).getValueAsString(true);
			output.add("Generates: " + generation + "FE");
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
	public void setIngredients(SolidFuelRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, recipe.getFuel());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SolidFuelRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 65, 18);
		guiItemStacks.set(ingredients);

		int burnTime = ForgeHooks.getBurnTime(recipe.getFuel());
		powerTimer = guiHelper.createTickTimer(Math.max(1, burnTime / 10), TileEntitySolidGenerator.DEFAULT_POWER_GENERATION * burnTime, false);
		processingTimer = guiHelper.createTickTimer(burnTime, burnTime, false);
	}
}
