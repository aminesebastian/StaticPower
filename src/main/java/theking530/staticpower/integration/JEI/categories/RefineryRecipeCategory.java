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
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class RefineryRecipeCategory extends BaseJEIRecipeCategory<RefineryRecipe> {
	public static final RecipeType<RefineryRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "refinery"), RefineryRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;
	private final FluidProgressBar fluidBar1;
	private final FluidProgressBar fluidBar2;
	private final FluidProgressBar fluidBar3;

	public RefineryRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.RefineryController.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(170, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RefineryController.get()));
		fluidBar1 = new FluidProgressBar(75, 27, 24, 4);
		fluidBar2 = new FluidProgressBar(75, 32, 24, 4);
		fluidBar3 = new FluidProgressBar(75, 42, 24, 4);
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
	public RecipeType<RefineryRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(RefineryRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 79, 2, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getPrimaryFluidInput(), 0, 0, 32, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getSecondaryFluidInput(), 0, 0, 54, 54, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput1(), 0, 0, 104, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput2(), 0, 0, 126, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput3(), 0, 0, 148, 54, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the progress bar as a fluid (can't use the widget here because this is a
		// singleton class).
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 24;

		int yOffset = 0;
		if (!recipe.getFluidOutput1().isEmpty()) {
			GuiDrawUtilities.drawSlot(matrixStack, 24, 4, 75, 27, 0);
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput1(), 1000, 1000, 75, 31, 1, progress, 4, false);
			yOffset += 7;
		}
		if (!recipe.getFluidOutput2().isEmpty()) {
			GuiDrawUtilities.drawSlot(matrixStack, 24, 4, 75, 27 + yOffset, 0);
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput2(), 1000, 1000, 75, 31 + +yOffset, 1, progress, 4, false);
			yOffset += 7;
		}
		if (!recipe.getFluidOutput3().isEmpty()) {
			GuiDrawUtilities.drawSlot(matrixStack, 24, 4, 75, 27 + yOffset, 0);
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput3(), 1000, 1000, 75, 31 + +yOffset, 1, progress, 4, false);
		}
	}

	@Override
	public List<Component> getTooltipStrings(RefineryRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 0) {
			output.add(Component.literal("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (fluidBar1.isPointInsideBounds(mouse)) {
			fluidBar1.getTooltips(mouse, output, false);
		}
		if (fluidBar2.isPointInsideBounds(mouse)) {
			fluidBar2.getTooltips(mouse, output, false);
		}
		if (fluidBar3.isPointInsideBounds(mouse)) {
			fluidBar3.getTooltips(mouse, output, false);
		}
		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RefineryRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 79, 2).addIngredients(recipe.getCatalyst().getIngredient());

		// Add the input fluid(s).
		if (!recipe.getPrimaryFluidInput().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.INPUT, 32, 2).addIngredient(ForgeTypes.FLUID_STACK, recipe.getPrimaryFluidInput())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getPrimaryFluidInput()), false, 16, 52);
		}
		if (!recipe.getSecondaryFluidInput().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.INPUT, 54, 2).addIngredient(ForgeTypes.FLUID_STACK, recipe.getSecondaryFluidInput())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getSecondaryFluidInput()), false, 16, 52);
		}

		// Add the fluid output(s).
		if (!recipe.getFluidOutput1().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 2).addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluidOutput1())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getFluidOutput1()), false, 16, 52);
		}
		if (!recipe.getFluidOutput2().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 2).addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluidOutput2())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getFluidOutput2()), false, 16, 52);
		}
		if (!recipe.getFluidOutput3().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 148, 2).addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluidOutput3())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getFluidOutput3()), false, 16, 52);
		}

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
