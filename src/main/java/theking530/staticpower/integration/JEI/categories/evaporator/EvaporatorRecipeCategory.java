package theking530.staticpower.integration.JEI.categories.evaporator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class EvaporatorRecipeCategory extends BaseJEIRecipeCategory<EvaporatorRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "evaporator");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;

	public EvaporatorRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Evaporator.getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.Evaporator));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	@Nonnull
	public Component getTitle() {
		return locTitle;
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
	public void draw(EvaporatorRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getInputFluid(), 0, 0, 50, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 104, 56, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiHeatBarUtilities.drawHeatBar(matrixStack, 8, 56, 16, 52, 1.0f, recipe.getRequiredHeat(), recipe.getRequiredHeat());

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 28, 5, 70, 23, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getInputFluid();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 70, 28, 1, progress, 5, false);
	}

	@Override
	public List<Component> getTooltipStrings(EvaporatorRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();

		// Render the heat bar tooltip.
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(GuiTextUtilities.formatHeatToString(recipe.getRequiredHeat()));
		}

		// Render the progress bar tooltip.
		if (mouseX > 69 && mouseX < 99 && mouseY > 21 && mouseY < 29) {
			output.add(new TextComponent("Required Time: " + recipe.getProcessingTime() / 20.0f + "s"));
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
