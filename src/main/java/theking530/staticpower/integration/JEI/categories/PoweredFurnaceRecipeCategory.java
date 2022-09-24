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
import net.minecraft.world.item.crafting.SmeltingRecipe;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.machines.poweredfurnace.BlockEntityPoweredFurnace;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class PoweredFurnaceRecipeCategory extends BaseJEIRecipeCategory<SmeltingRecipe> {
	public static final RecipeType<SmeltingRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "powered_furnace"), SmeltingRecipe.class);

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public PoweredFurnaceRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.PoweredFurnace.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(120, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PoweredFurnace.get()));
		pBar = new ArrowProgressBar(62, 19);
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
	public RecipeType<SmeltingRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Class<? extends SmeltingRecipe> getRecipeClass() {
		return SmeltingRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(SmeltingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 41, 19, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 89, 17, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		float experience = recipe.getExperience();
		if (experience > 0) {
			TranslatableComponent experienceString = new TranslatableComponent("gui.staticpower.experience", experience);
			GuiDrawUtilities.drawStringCentered(matrixStack, experienceString.getString(), 100, 10, 0.0f, 1f, Color.EIGHT_BIT_GREY, false);
		}

		int processingTicks = (int) (recipe.getCookingTime() * BlockEntityPoweredFurnace.DEFAULT_PROCESSING_TIME_MULT);
		GuiDrawUtilities.drawStringCentered(matrixStack, GuiTextUtilities.formatTicksToTimeUnit(processingTicks).getString(), 110, 55, 0.0f, 1f, Color.EIGHT_BIT_GREY, false);
	}

	@Override
	public List<Component> getTooltipStrings(SmeltingRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ")
					.append(PowerTextFormatting.formatPowerToString(BlockEntityPoweredFurnace.getCookTime(recipe) * StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get())));
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
	public void setRecipe(IRecipeLayoutBuilder builder, SmeltingRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 41, 19).addIngredients(recipe.getIngredients().get(0));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStack(recipe.getResultItem());

		powerTimer = guiHelper.createTickTimer(BlockEntityPoweredFurnace.getCookTime(recipe),
				(int) (BlockEntityPoweredFurnace.getCookTime(recipe) * StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get()), true);
		processingTimer = guiHelper.createTickTimer(BlockEntityPoweredFurnace.getCookTime(recipe), BlockEntityPoweredFurnace.getCookTime(recipe), false);
	}
}
