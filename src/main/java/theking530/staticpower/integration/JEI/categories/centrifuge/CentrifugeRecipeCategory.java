package theking530.staticpower.integration.JEI.categories.centrifuge;

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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.CentrifugeProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class CentrifugeRecipeCategory extends BaseJEIRecipeCategory<CentrifugeRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "centrifuge");
	private static final int INTPUT_SLOT = 0;
	private static final int PRIMARY_OUTPUT_SLOT = 1;
	private static final int SECONDARY_OUTPUT_SLOT = 2;
	private static final int TERTIARY_OUTPUT_SLOT = 3;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final CentrifugeProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Centrifuge.getDescriptionId());
		background = guiHelper.createBlankDrawable(150, 70);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.Centrifuge));
		pBar = new CentrifugeProgressBar(79, 26);
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
	public Class<? extends CentrifugeRecipe> getRecipeClass() {
		return CentrifugeRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CentrifugeRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 80, 6, 16, 16, 0);

		GuiDrawUtilities.drawSlot(matrixStack, 78, 46, 20, 20, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 104, 32, 20, 20, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 52, 32, 20, 20, 0);

		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		String rpmText = String.valueOf(recipe.getMinimumSpeed()) + " RPM";
		GuiDrawUtilities.drawSlot(matrixStack, 103, 12.5f, Minecraft.getInstance().font.width(rpmText) + 4, 11, 0);
		Minecraft.getInstance().font.drawShadow(matrixStack, rpmText, 105, 14, Color.EIGHT_BIT_WHITE.encodeInInteger());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<Component> getTooltipStrings(CentrifugeRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<Component> tooltips = new ArrayList<Component>();
			pBar.getTooltips(mouse, tooltips, false);
			for (Component tooltip : tooltips) {
				output.add(tooltip);
			}
			output.add(new TextComponent("Required RPM: " + recipe.getMinimumSpeed()));
		}

		return output;
	}

	@Override
	public void setIngredients(CentrifugeRecipe recipe, IIngredients ingredients) {
		// Set the input.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the output.
		ingredients.setOutputs(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutputs());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRecipe recipe, IIngredients ingredients) {
		// Set the inputs.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 79, 5);
		guiItemStacks.set(ingredients);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(PRIMARY_OUTPUT_SLOT, false, 80, 48);
		probabilityStacks.init(SECONDARY_OUTPUT_SLOT, false, 54, 34);
		probabilityStacks.init(TERTIARY_OUTPUT_SLOT, false, 106, 34);
		probabilityStacks.set(ingredients);

		// Add the fluid.
		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
