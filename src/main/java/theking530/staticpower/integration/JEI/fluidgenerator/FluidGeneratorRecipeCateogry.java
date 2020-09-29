package theking530.staticpower.integration.JEI.fluidgenerator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class FluidGeneratorRecipeCateogry extends BaseJEIRecipeCategory<FluidGeneratorRecipe> {
	public static final ResourceLocation FLUID_GENERATOR_UID = new ResourceLocation(StaticPower.MOD_ID, "fluid_generator");

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private final FireProgressBar flameBar;
	private final ArrowProgressBar pBar;
	private ITickTimer processingTimer;

	public FluidGeneratorRecipeCateogry(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.FluidGenerator.getTranslationKey());
		background = guiHelper.createBlankDrawable(100, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.FluidGenerator));
		pBar = new ArrowProgressBar(60, 19).setFlipped(true);
		flameBar = new FireProgressBar(42, 40);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return FLUID_GENERATOR_UID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return locTitle.getString();
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends FluidGeneratorRecipe> getRecipeClass() {
		return FluidGeneratorRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FluidGeneratorRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluid(), 0, 0, 77, 54, 1.0f, 16, 48, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, recipe.getPowerGeneration(), recipe.getPowerGeneration());

		// Draw the progress bars.
		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);

		flameBar.setCurrentProgress(processingTimer.getValue());
		flameBar.setMaxProgress(processingTimer.getMaxValue());
		flameBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);

		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		String powerGeneration = GuiTextUtilities.formatEnergyRateToString(recipe.getPowerGeneration()).getString();
		fontRenderer.drawString(matrixStack, powerGeneration, 51 - (fontRenderer.getStringWidth(powerGeneration) / 2), 5, Color.EIGHT_BIT_DARK_GREY.encodeInInteger());

	}

	@Override
	public List<ITextComponent> getTooltipStrings(FluidGeneratorRecipe recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new StringTextComponent("Generates: ").append(GuiTextUtilities.formatEnergyRateToString(recipe.getPowerGeneration())));
		}

		return output;
	}

	@Override
	public void setIngredients(FluidGeneratorRecipe recipe, IIngredients ingredients) {
		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FluidGeneratorRecipe recipe, IIngredients ingredients) {
		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(3, true, 77, 6, 16, 48, getFluidTankDisplaySize(recipe.getFluid()), false, null);
		fluids.set(ingredients);

		int burnTime = 100;
		processingTimer = guiHelper.createTickTimer(burnTime, burnTime, false);
	}
}
