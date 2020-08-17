package theking530.staticpower.integration.JEI.evaporator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.common.gui.widgets.valuebars.GuiHeatBarUtilities;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.Reference;

public class EvaporatorRecipeCategory extends BaseJEIRecipeCategory<EvaporatorRecipe> {
	public static final ResourceLocation EVAPORATOR_UID = new ResourceLocation(Reference.MOD_ID, "evaporator");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;

	public EvaporatorRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Evaporator.getTranslationKey());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Evaporator));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return EVAPORATOR_UID;
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
	public Class<? extends EvaporatorRecipe> getRecipeClass() {
		return EvaporatorRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(EvaporatorRecipe recipe, double mouseX, double mouseY) {
		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(recipe.getInputFluid(), 0, 0, 50, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(recipe.getOutputFluid(), 0, 0, 104, 56, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiHeatBarUtilities.drawHeatBar(8, 56, 16, 52, 1.0f, recipe.getRequiredHeat(), recipe.getRequiredHeat());

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(70, 23, 28, 5);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getInputFluid();
		GuiFluidBarUtilities.drawFluidBar(fluid, 1000, 1000, 70, 28, 1, progress, 5, false);
	}

	@Override
	public List<String> getTooltipStrings(EvaporatorRecipe recipe, double mouseX, double mouseY) {
		List<String> output = new ArrayList<String>();

		// Render the heat bar tooltip.
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(GuiTextUtilities.formatHeatToString(recipe.getRequiredHeat()).getFormattedText());
		}

		// Render the progress bar tooltip.
		if (mouseX > 69 && mouseX < 99 && mouseY > 21 && mouseY < 29) {
			output.add("Required Time: " + recipe.getProcessingTime() / 20.0f + "s");
		}

		return output;
	}

	@Override
	public void setIngredients(EvaporatorRecipe recipe, IIngredients ingredients) {
		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getInputFluid());

		// Set the output flud.
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, EvaporatorRecipe recipe, IIngredients ingredients) {
		// Add the fluids.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(INTPUT_SLOT, true, 50, 4, 16, 52, getFluidTankDisplaySize(recipe.getInputFluid()), false, null);
		fluids.init(OUTPUT_SLOT, false, 104, 4, 16, 52, getFluidTankDisplaySize(recipe.getOutputFluid()), false, null);
		fluids.set(ingredients);

		// Creat the timer.
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
