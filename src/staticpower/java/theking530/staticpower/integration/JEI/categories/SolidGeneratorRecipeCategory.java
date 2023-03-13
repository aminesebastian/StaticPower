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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class SolidGeneratorRecipeCategory extends BaseJEIRecipeCategory<SolidFuelRecipe> {
	public static final RecipeType<SolidFuelRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "solid_generator"), SolidFuelRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;
	private final FireProgressBar fireBar;

	private ITickTimer processingTimer;

	public SolidGeneratorRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.SolidGenerator.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(90, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SolidGenerator.get()));
		pBar = new ArrowProgressBar(55, 19).setFlipped(true);
		fireBar = new FireProgressBar(67, 40);
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
	public RecipeType<SolidFuelRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(SolidFuelRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 66, 21, 0);

		fireBar.setCurrentProgress(processingTimer.getValue());
		fireBar.setMaxProgress(processingTimer.getMaxValue());
		fireBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		@SuppressWarnings("resource")
		Font fontRenderer = Minecraft.getInstance().font;
		String powerGeneration = PowerTextFormatting.formatPowerToString(recipe.getFuelAmount() * StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get()).getString();

		int width = fontRenderer.width(powerGeneration) + 6;
		int startPos = 51 - (fontRenderer.width(powerGeneration) / 2);
		GuiDrawUtilities.drawRectangle(matrixStack, width, 12, startPos - 3, 5, 0, new SDColor(0, 0, 0, 0.25f));
		fontRenderer.drawShadow(matrixStack, powerGeneration, startPos, 7, SDColor.EIGHT_BIT_WHITE.encodeInInteger());

	}

	@Override
	public List<Component> getTooltipStrings(SolidFuelRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			int burnTime = ForgeHooks.getBurnTime(recipe.getInput(), null);
			output.add(Component.literal("Generates: ").append(PowerTextFormatting.formatPowerToString(StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get() * burnTime)));
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
	public void setRecipe(IRecipeLayoutBuilder builder, SolidFuelRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 66, 21).addItemStack(recipe.getInput());

		int burnTime = recipe.getFuelAmount();
		addPowerOutputSlot(builder, 5, 6, 16, 48, MachineRecipeProcessingSection.hardcoded(burnTime, StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get(), 0, 0));

		processingTimer = guiHelper.createTickTimer(burnTime, burnTime, false);
	}
}
