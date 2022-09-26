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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class PackagerRecipeCategory extends BaseJEIRecipeCategory<PackagerRecipe> {
	public static final RecipeType<PackagerRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "packager"), PackagerRecipe.class);

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public PackagerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Packager.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(98, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Packager.get()));
		pBar = new ArrowProgressBar(50, 22);
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
	public RecipeType<PackagerRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Class<? extends PackagerRecipe> getRecipeClass() {
		return PackagerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(PackagerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 28, 22, 0);

		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 75, 20, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(PackagerRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getProcessingTime() * recipe.getPowerCost())));
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
	public void setRecipe(IRecipeLayoutBuilder builder, PackagerRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 28, 22).addIngredients(recipe.getInputIngredient().getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 22).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}