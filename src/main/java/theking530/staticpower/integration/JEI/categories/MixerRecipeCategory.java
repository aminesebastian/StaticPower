package theking530.staticpower.integration.JEI.categories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
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
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.progressbars.MixerProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class MixerRecipeCategory extends BaseJEIRecipeCategory<MixerRecipe> {
	public static final RecipeType<MixerRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "mixer"), MixerRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;
	private final MixerProgressBar mixerPBar;
	private final FluidProgressBar pBar;

	public MixerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.Mixer.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Mixer.get()));
		mixerPBar = new MixerProgressBar(54, 20);
		pBar = new FluidProgressBar(100, 23, 24, 8);
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
	public RecipeType<MixerRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(MixerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 56, 2, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 56, 38, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 32, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 80, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 128, 54, 1.0f, 16, 52, MachineSideMode.Never, true);

		// Draw the progress bar as a fluid (can't use the widget here because this is a
		// singleton class).
		GuiDrawUtilities.drawSlot(matrixStack, 24, 8, 100, 23, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 24;
		FluidStack fluid = recipe.getOutput();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 100, 31, 1, progress, 8, false);

		mixerPBar.setCurrentProgress(processingTimer.getValue());
		mixerPBar.setMaxProgress(processingTimer.getMaxValue());
		mixerPBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(MixerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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
	public void setRecipe(IRecipeLayoutBuilder builder, MixerRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 56, 2).addIngredients(recipe.getPrimaryItemInput().getIngredient());
		builder.addSlot(RecipeIngredientRole.INPUT, 56, 38).addIngredients(recipe.getSecondaryItemInput().getIngredient());

		int displayCapacity = getFluidTankDisplaySize(recipe.getPrimaryFluidInput(), recipe.getSecondaryFluidInput());

		// Add the input fluids.
		if (!recipe.getPrimaryFluidInput().isEmpty()) {
			addFluidIngredientSlot(builder, 32, 2, 16, 52, recipe.getPrimaryFluidInput(), displayCapacity);
		}
		if (!recipe.getSecondaryFluidInput().isEmpty()) {
			addFluidIngredientSlot(builder, 80, 2, 16, 52, recipe.getSecondaryFluidInput(), displayCapacity);
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 2).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput()).setFluidRenderer(getFluidTankDisplaySize(recipe.getOutput()),
				false, 16, 52);

		addPowerInputSlot(builder, 5, 6, 16, 48, recipe.getProcessingSection());

		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
