package theking530.staticpower.integration.JEI.categories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
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
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class FluidInfuserRecipeCategory extends BaseJEIRecipeCategory<FluidInfusionRecipe> {
	public static final RecipeType<FluidInfusionRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "fluid_infuser"), FluidInfusionRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;
	private final ArrowProgressBar pBar;

	public FluidInfuserRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.FluidInfuser.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FluidInfuser.get()));
		pBar = new ArrowProgressBar(51, 17);
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
	public RecipeType<FluidInfusionRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FluidInfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 31, 17, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 122, 15, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 77, 56, 1.0f, 16, 52, MachineSideMode.Never, true);

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 17, 5, 99, 23, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 17;
		FluidStack fluid = getNthFluidInput(recipeSlotsView, 0);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 99, 28, 1, progress, 5, false);

		// Draw the arrow progress bar.
		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(FluidInfusionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(Component.literal("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<Component> tooltips = new ArrayList<Component>();
			pBar.getTooltips(mouse, tooltips, false);
			for (Component tooltip : tooltips) {
				output.add(tooltip);
			}
		}

		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FluidInfusionRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 31, 17).addIngredients(recipe.getInput().getIngredient());

		addFluidIngredientSlot(builder, 77, 4, 16, 52, recipe.getRequiredFluid());
		addPowerInputSlot(builder, 5, 6, 16, 48, recipe.getProcessingSection());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 17).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());

		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
