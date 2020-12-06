package theking530.staticpower.integration.JEI.condenser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class CondenserRecipeCategory extends BaseJEIRecipeCategory<CondensationRecipe> {
	public static final ResourceLocation CONDENSER_UID = new ResourceLocation(StaticPower.MOD_ID, "condenser");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;

	public CondenserRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Condenser.getTranslationKey());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Condenser));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return CONDENSER_UID;
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
	public Class<? extends CondensationRecipe> getRecipeClass() {
		return CondensationRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CondensationRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getInputFluid(), 0, 0, 50, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 104, 56, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiHeatBarUtilities.drawHeatBar(matrixStack, 8, 56, 16, 52, 1.0f, recipe.getHeatGeneration(), recipe.getHeatGeneration());

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 70, 23, 28, 5);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getInputFluid();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 70, 28, 1, progress, 5, false);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(CondensationRecipe recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();

		// Render the heat bar tooltip.
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new StringTextComponent("Generates: ").append(GuiTextUtilities.formatHeatToString(recipe.getHeatGeneration())));
		}

		// Render the progress bar tooltip.
		if (mouseX > 69 && mouseX < 99 && mouseY > 21 && mouseY < 29) {
			output.add(new StringTextComponent("Required Time: " + recipe.getProcessingTime() / 20.0f + "s"));
		}

		return output;
	}

	@Override
	public void setIngredients(CondensationRecipe recipe, IIngredients ingredients) {
		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getInputFluid());

		// Set the output flud.
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CondensationRecipe recipe, IIngredients ingredients) {
		// Add the fluids.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(INTPUT_SLOT, true, 50, 4, 16, 52, getFluidTankDisplaySize(recipe.getInputFluid()), false, null);
		fluids.init(OUTPUT_SLOT, false, 104, 4, 16, 52, getFluidTankDisplaySize(recipe.getOutputFluid()), false, null);
		fluids.set(ingredients);

		// Create the timer.
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
