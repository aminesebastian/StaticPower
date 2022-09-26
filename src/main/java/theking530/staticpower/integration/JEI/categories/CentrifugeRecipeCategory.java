package theking530.staticpower.integration.JEI.categories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.CentrifugeProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class CentrifugeRecipeCategory extends BaseJEIRecipeCategory<CentrifugeRecipe> {
	public static final RecipeType<CentrifugeRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "centrifuge"), CentrifugeRecipe.class);

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final CentrifugeProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Centrifuge.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(150, 70);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Centrifuge.get()));
		pBar = new CentrifugeProgressBar(79, 26);
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
	public RecipeType<CentrifugeRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Class<? extends CentrifugeRecipe> getRecipeClass() {
		return CentrifugeRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(CentrifugeRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 80, 6, 0);

		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 78, 46, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 104, 32, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 52, 32, 0);

		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		String rpmText = String.valueOf(recipe.getMinimumSpeed()) + " RPM";
		GuiDrawUtilities.drawSlot(matrixStack, Minecraft.getInstance().font.width(rpmText) + 4, 11, 103, 12.5f, 0);
		Minecraft.getInstance().font.drawShadow(matrixStack, rpmText, 105, 14, Color.EIGHT_BIT_WHITE.encodeInInteger());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(CentrifugeRecipe recipe, double mouseX, double mouseY) {
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
			output.add(new TextComponent("Required RPM: " + recipe.getMinimumSpeed()));
		}

		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 6).addIngredients(recipe.getInput().getIngredient());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 48).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput1());
		if (recipe.getOutputs().size() > 1) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 54, 34).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput2());
		}
		if (recipe.getOutputs().size() > 2) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 34).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput3());
		}

		// Add the fluid.
		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}