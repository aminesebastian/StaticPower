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
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.utilities.math.RectangleBounds;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.gui.widgets.GrinderProgressBar;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class PoweredGrinderRecipeCategory extends BaseJEIRecipeCategory<GrinderRecipe> {
	public static final RecipeType<GrinderRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "grinder"), GrinderRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final GrinderProgressBar pBar;

	private ITickTimer processingTimer;

	public PoweredGrinderRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.PoweredGrinder.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(150, 70);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PoweredGrinder.get()));
		pBar = new GrinderProgressBar(79, 26);
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
	public RecipeType<GrinderRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(GrinderRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 80, 6, 0);

		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 78, 46, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 104, 32, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 52, 32, 0);

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(GrinderRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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
	public void setRecipe(IRecipeLayoutBuilder builder, GrinderRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 6).addIngredients(recipe.getInputIngredient().getIngredient());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 48).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutputItems().get(0));
		if (recipe.getOutputItems().size() > 1) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 34).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutputItems().get(1));
		}
		if (recipe.getOutputItems().size() > 2) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 54, 34).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutputItems().get(2));
		}

		addPowerInputSlot(builder, 5, 6, 16, 56, recipe.getProcessingSection());
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
