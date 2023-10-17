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
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.RectangleBounds;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.cokeoven.CokeOvenRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class CokeOvenRecipeCategory extends BaseJEIRecipeCategory<CokeOvenRecipe> {
	public static final RecipeType<CokeOvenRecipe> TYPE = new RecipeType<>(
			new ResourceLocation(StaticPower.MOD_ID, "coke_oven"), CokeOvenRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;
	private final FireProgressBar fBar;

	private ITickTimer processingTimer;

	public CokeOvenRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.CokeOvenBrick.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(120, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(ModBlocks.CokeOvenBrick.get()));
		pBar = new ArrowProgressBar(39, 19);
		fBar = new FireProgressBar(16, 39);
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
	public RecipeType<CokeOvenRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CokeOvenRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX,
			double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 16, 19, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 70, 17, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 98, 56, 1.0f, 16, 52,
				MachineSideMode.Never, true);

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		fBar.setCurrentProgress(processingTimer.getValue());
		fBar.setMaxProgress(processingTimer.getMaxValue());
		fBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		float experience = recipe.getExperience();
		if (experience > 0) {
			MutableComponent experienceString = Component.translatable("gui.staticcore.experience", experience);
			GuiDrawUtilities.drawStringCentered(matrixStack, experienceString.getString(), 52, 10, 0.0f, 1f,
					SDColor.EIGHT_BIT_GREY, false);
		}

		int processingTicks = (int) (recipe.getProcessingTime());
		GuiDrawUtilities.drawStringCentered(matrixStack,
				GuiTextUtilities.formatTicksToTimeUnit(processingTicks).getString(), 52, 55, 0.0f, 1f,
				SDColor.EIGHT_BIT_GREY, false);
	}

	@Override
	public List<Component> getTooltipStrings(CokeOvenRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX,
			double mouseY) {
		List<Component> output = new ArrayList<Component>();

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
	public void setRecipe(IRecipeLayoutBuilder builder, CokeOvenRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 19).addIngredients(recipe.getInput().getIngredient());

		if (!recipe.getOutput().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 19).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK,
					recipe.getOutput());
		}

		if (!recipe.getOutputFluid().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 4)
					.addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getOutputFluid()), false, 16, 52);
		}

		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
