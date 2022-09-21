package theking530.staticpower.integration.JEI.categories.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.FusionProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class FusionFurnaceRecipeCategory extends BaseJEIRecipeCategory<FusionFurnaceRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "fusion_furnace");
	private static final int INTPUT_SLOT_1 = 1;
	private static final int INTPUT_SLOT_2 = 2;
	private static final int INTPUT_SLOT_3 = 3;
	private static final int INTPUT_SLOT_4 = 4;
	private static final int INTPUT_SLOT_5 = 5;
	private static final int OUTPUT_SLOT = 6;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final FusionProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public FusionFurnaceRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.FusionFurnace.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(160, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.FusionFurnace.get()));
		pBar = new FusionProgressBar(79, 19);
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
	public Class<? extends FusionFurnaceRecipe> getRecipeClass() {
		return FusionFurnaceRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FusionFurnaceRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 36, 25, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 58, 13, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 80, 2, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 102, 13, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 124, 25, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 78, 38, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(FusionFurnaceRecipe recipe, double mouseX, double mouseY) {
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
	public void setIngredients(FusionFurnaceRecipe recipe, IIngredients ingredients) {
		// Set inputs
		List<Ingredient> input = new ArrayList<Ingredient>();
		for (StaticPowerIngredient ing : recipe.getInputs()) {
			input.add(ing.getIngredient());
		}
		ingredients.setInputIngredients(input);

		// Set the output.
		ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FusionFurnaceRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT_1, true, 79, 1);
		guiItemStacks.init(INTPUT_SLOT_2, true, 57, 12);
		guiItemStacks.init(INTPUT_SLOT_3, true, 101, 12);
		guiItemStacks.init(INTPUT_SLOT_4, true, 35, 24);
		guiItemStacks.init(INTPUT_SLOT_5, true, 123, 24);
		guiItemStacks.set(ingredients);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(OUTPUT_SLOT, false, 80, 40);
		probabilityStacks.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
