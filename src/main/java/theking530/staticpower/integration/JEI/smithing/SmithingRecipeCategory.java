package theking530.staticpower.integration.JEI.smithing;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
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
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.smithing.SmithingRecipeProvider.AutoSmithRecipeJEIWrapper;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class SmithingRecipeCategory extends BaseJEIRecipeCategory<AutoSmithRecipeJEIWrapper> {
	public static final ResourceLocation AUTO_SMITHING_UID = new ResourceLocation(StaticPower.MOD_ID, "auto_smith");
	private static final int INTPUT_SLOT = 0;
	private static final int MODIFIER_SLOT = 1;
	private static final int FLUID_MODIFIER_SLOT = 2;
	private static final int OUTPUT_SLOT = 3;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;
	private final ArrowProgressBar pBar;

	public SmithingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.AutoSmith.getTranslationKey());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.AutoSmith));
		pBar = new ArrowProgressBar(51, 17);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return AUTO_SMITHING_UID;
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
	public Class<? extends AutoSmithRecipeJEIWrapper> getRecipeClass() {
		return AutoSmithRecipeJEIWrapper.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(AutoSmithRecipeJEIWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 31, 17, 16, 16);
		GuiDrawUtilities.drawSlot(matrixStack, 122, 15, 20, 20);
		GuiDrawUtilities.drawSlot(matrixStack, 132, 15, 20, 20);

		// This doesn't actually draw the fluid, just the bars.
		if (!recipe.getRecipe().getModifierFluid().isEmpty()) {
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getRecipe().getModifierFluid(), 0, 0, 77, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		}

		// Draw the power bar.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the arrow progress bar.
		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(AutoSmithRecipeJEIWrapper recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new StringTextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(recipe.getRecipe().getPowerCost() * recipe.getRecipe().getProcessingTime())));
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
	public void setIngredients(AutoSmithRecipeJEIWrapper recipe, IIngredients ingredients) {
		// Add the input item.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput());
		input.add(recipe.getRecipe().getModifierMaterial().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the filled bottle output itemstack.
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getRecipe().getModifierFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, AutoSmithRecipeJEIWrapper recipe, IIngredients ingredients) {
		// Add the input and output slots.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 30, 16);
		guiItemStacks.init(MODIFIER_SLOT, true, 50, 16);
		guiItemStacks.init(OUTPUT_SLOT, false, 123, 16);

		// Set the items.
		guiItemStacks.set(ingredients);

		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		if (!recipe.getRecipe().getModifierFluid().isEmpty()) {
			fluids.init(FLUID_MODIFIER_SLOT, true, 77, 4, 16, 52, getFluidTankDisplaySize(recipe.getRecipe().getModifierFluid()), false, null);
			fluids.set(ingredients);
		}

		powerTimer = guiHelper.createTickTimer(recipe.getRecipe().getProcessingTime(), recipe.getRecipe().getProcessingTime() * recipe.getRecipe().getPowerCost(), true);
		processingTimer = guiHelper.createTickTimer(recipe.getRecipe().getProcessingTime(), recipe.getRecipe().getProcessingTime(), false);
	}
}
