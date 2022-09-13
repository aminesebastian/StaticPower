package theking530.staticpower.integration.JEI.categories.lumbermill;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class LumberMillRecipeCategory extends BaseJEIRecipeCategory<LumberMillRecipe> {
	public static final RecipeType<LumberMillRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "lumber_mill"), LumberMillRecipe.class);

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public LumberMillRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.LumberMill.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(176, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.LumberMill.get()));
		pBar = new ArrowProgressBar(63, 19);
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
	public RecipeType<LumberMillRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Class<? extends LumberMillRecipe> getRecipeClass() {
		return LumberMillRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(LumberMillRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 41, 19, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 89, 17, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 119, 17, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 153, 54, 1.0f, 16, 48, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(LumberMillRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getPowerCost() * recipe.getProcessingTime())));
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
	public void setRecipe(IRecipeLayoutBuilder builder, LumberMillRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 41, 19).addIngredients(recipe.getInput().getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getPrimaryOutput());

		if (!recipe.getSecondaryOutput().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 121, 19).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getSecondaryOutput());
		}

		// Add the fluid.
		if (recipe.hasOutputFluid()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 153, 6).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid()).setFluidRenderer(getFluidTankDisplaySize(recipe.getOutputFluid()), false,
					16, 48);
		}

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
