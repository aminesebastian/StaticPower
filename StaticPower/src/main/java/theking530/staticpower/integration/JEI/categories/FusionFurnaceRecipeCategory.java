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
import theking530.staticpower.client.gui.widgets.FusionProgressBar;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class FusionFurnaceRecipeCategory extends BaseJEIRecipeCategory<FusionFurnaceRecipe> {
	public static final RecipeType<FusionFurnaceRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "fusion_furnace"), FusionFurnaceRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final FusionProgressBar pBar;

	private ITickTimer processingTimer;

	public FusionFurnaceRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.FusionFurnace.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(160, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FusionFurnace.get()));
		pBar = new FusionProgressBar(79, 19);
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
	public RecipeType<FusionFurnaceRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FusionFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 36, 25, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 58, 13, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 80, 2, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 102, 13, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 124, 25, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 78, 38, 0);

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(FusionFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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
	public void setRecipe(IRecipeLayoutBuilder builder, FusionFurnaceRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 2).addIngredients(recipe.getInputs().get(0).getIngredient());
		if (recipe.getInputs().size() > 1) {
			builder.addSlot(RecipeIngredientRole.INPUT, 58, 13).addIngredients(recipe.getInputs().get(1).getIngredient());
		}
		if (recipe.getInputs().size() > 2) {
			builder.addSlot(RecipeIngredientRole.INPUT, 102, 13).addIngredients(recipe.getInputs().get(2).getIngredient());
		}
		if (recipe.getInputs().size() > 3) {
			builder.addSlot(RecipeIngredientRole.INPUT, 36, 25).addIngredients(recipe.getInputs().get(3).getIngredient());
		}
		if (recipe.getInputs().size() > 4) {
			builder.addSlot(RecipeIngredientRole.INPUT, 124, 25).addIngredients(recipe.getInputs().get(4).getIngredient());
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 40).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
		addPowerInputSlot(builder, 5, 6, 16, 48, recipe.getProcessingSection());

		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
