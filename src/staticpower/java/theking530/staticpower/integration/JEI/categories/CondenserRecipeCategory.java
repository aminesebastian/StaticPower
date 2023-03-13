package theking530.staticpower.integration.JEI.categories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class CondenserRecipeCategory extends BaseJEIRecipeCategory<CondensationRecipe> {
	public static final RecipeType<CondensationRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "condenser"), CondensationRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;

	public CondenserRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.Condenser.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Condenser.get()));
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
	public RecipeType<CondensationRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CondensationRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 50, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 104, 56, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiHeatBarUtilities.drawHeatBar(matrixStack, 8, 4, 16, 52, 1.0f, recipe.getProcessingSection().getHeat(), 0, recipe.getProcessingSection().getHeat());

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 28, 5, 70, 23, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = getNthFluidInput(recipeSlotsView, 0);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 70, 28, 1, progress, 5, false);
	}

	@Override
	public List<Component> getTooltipStrings(CondensationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();

		// Render the heat bar tooltip.
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(Component.literal("Generates: ").append(GuiTextUtilities.formatHeatToString(recipe.getProcessingSection().getHeat())));
		}

		// Render the progress bar tooltip.
		if (mouseX > 69 && mouseX < 99 && mouseY > 21 && mouseY < 29) {
			output.add(Component.literal("Required Time: " + recipe.getProcessingTime() / 20.0f + "s"));
		}

		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CondensationRecipe recipe, IFocusGroup ingredients) {
		addFluidIngredientSlot(builder, 50, 4, 16, 52, recipe.getInputFluid());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 4).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid())
				.setFluidRenderer(getFluidTankDisplaySize(recipe.getOutputFluid()), false, 16, 52);

		// Create the timer.
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
