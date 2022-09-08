package theking530.staticpower.integration.JEI.categories.solidgenerator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class SolidGeneratorRecipeCategory extends BaseJEIRecipeCategory<SolidFuelRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "solid_generator");
	private static final int INTPUT_SLOT = 0;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;
	private final FireProgressBar fireBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public SolidGeneratorRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.SolidGenerator.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(90, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.SolidGenerator.get()));
		pBar = new ArrowProgressBar(55, 19).setFlipped(true);
		fireBar = new FireProgressBar(67, 40);
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
	public Class<? extends SolidFuelRecipe> getRecipeClass() {
		return SolidFuelRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(SolidFuelRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 66, 19, 0);

		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		fireBar.setCurrentProgress(processingTimer.getValue());
		fireBar.setMaxProgress(processingTimer.getMaxValue());
		fireBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		Font fontRenderer = Minecraft.getInstance().font;
		String powerGeneration = StaticPowerEnergyTextUtilities.formatPowerRateToString(StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get()).getString();
		fontRenderer.draw(matrixStack, powerGeneration, 51 - (fontRenderer.width(powerGeneration) / 2), 5, Color.EIGHT_BIT_DARK_GREY.encodeInInteger());

	}

	@Override
	public List<Component> getTooltipStrings(SolidFuelRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			int burnTime = ForgeHooks.getBurnTime(recipe.getFuel(), null);
			output.add(new TextComponent("Generates: ").append(StaticPowerEnergyTextUtilities.formatPowerToString(StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get() * burnTime)));
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
	public void setIngredients(SolidFuelRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, recipe.getFuel());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SolidFuelRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 65, 18);
		guiItemStacks.set(ingredients);

		int burnTime = recipe.getFuelAmount();
		powerTimer = guiHelper.createTickTimer(Math.max(1, burnTime / 10), (int) (StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get() * burnTime), false);
		processingTimer = guiHelper.createTickTimer(burnTime, burnTime, false);
	}
}
