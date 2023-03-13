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
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class FluidGeneratorRecipeCateogry extends BaseJEIRecipeCategory<FluidGeneratorRecipe> {
	public static final RecipeType<FluidGeneratorRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "fluid_generator"), FluidGeneratorRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private final FireProgressBar flameBar;
	private final ArrowProgressBar pBar;
	private ITickTimer processingTimer;

	public FluidGeneratorRecipeCateogry(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.FluidGenerator.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(100, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FluidGenerator.get()));
		pBar = new ArrowProgressBar(60, 19).setFlipped(true);
		flameBar = new FireProgressBar(42, 40);
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
	public RecipeType<FluidGeneratorRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FluidGeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 77, 54, 1.0f, 16, 48, MachineSideMode.Never, true);

		// Draw the progress bars.
		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		flameBar.setCurrentProgress(processingTimer.getValue());
		flameBar.setMaxProgress(processingTimer.getMaxValue());
		flameBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		String powerGeneration = PowerTextFormatting.formatPowerRateToString(recipe.getPowerGeneration()).getString();
		GuiDrawUtilities.drawStringCentered(matrixStack, powerGeneration, 51, 32, 1, 1, SDColor.EIGHT_BIT_DARK_GREY, false);

		double ticksPerBucket = 1000 / Math.max(recipe.getFluid().getAmount(), 1);
		double generationPerBucket = ticksPerBucket * recipe.getPowerGeneration();
		String powerPerTick = PowerTextFormatting.formatPowerToString(generationPerBucket).getString();
		GuiDrawUtilities.drawStringLeftAligned(matrixStack, powerPerTick, 43, 12, 1, 1, SDColor.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawItem(matrixStack, new ItemStack(this.getNthFluidInput(recipeSlotsView, 0).getFluid().getBucket()), 25, 1, 1);

	}

	@Override
	public List<Component> getTooltipStrings(FluidGeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(Component.literal("Generates: ").append(PowerTextFormatting.formatPowerRateToString(recipe.getPowerGeneration())));
		}

		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FluidGeneratorRecipe recipe, IFocusGroup ingredients) {
		addFluidIngredientSlot(builder, 77, 6, 16, 48, recipe.getFluid());
		addPowerOutputSlot(builder, 5, 6, 16, 48, MachineRecipeProcessingSection.hardcoded(100, recipe.getPowerGeneration(), 0, 0));

		int burnTime = 100;
		processingTimer = guiHelper.createTickTimer(burnTime, burnTime, false);
	}
}
