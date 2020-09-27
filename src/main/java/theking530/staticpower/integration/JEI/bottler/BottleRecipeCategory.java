package theking530.staticpower.integration.JEI.bottler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.powered.bottler.TileEntityBottler;

public class BottleRecipeCategory extends BaseJEIRecipeCategory<BottleRecipe> {
	public static final ResourceLocation BOTTLER_UID = new ResourceLocation(StaticPower.MOD_ID, "bottler");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public BottleRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Bottler.getTranslationKey());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Bottler));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return BOTTLER_UID;
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
	public Class<? extends BottleRecipe> getRecipeClass() {
		return BottleRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(BottleRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(109, 12, 16, 16);
		GuiDrawUtilities.drawSlot(107, 36, 20, 20);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(recipe.getFluid(), 0, 0, 50, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(72, 18, 28, 5);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getFluid();
		GuiFluidBarUtilities.drawFluidBar(fluid, 1000, 1000, 72, 23, 1, progress, 5, false);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(BottleRecipe recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new StringTextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(
					TileEntityBottler.DEFAULT_PROCESSING_COST * TileEntityBottler.DEFAULT_PROCESSING_TIME)));
		}

		return output;
	}

	@Override
	public void setIngredients(BottleRecipe recipe, IIngredients ingredients) {
		// Add the input empty bottle.
		ingredients.setInput(VanillaTypes.ITEM, recipe.getEmptyBottle());

		// Set the filled bottle output itemstack.
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getFilledBottle());

		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BottleRecipe recipe, IIngredients ingredients) {
		// Add the input and output slots.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 108, 11);
		guiItemStacks.init(OUTPUT_SLOT, false, 108, 37);

		// Set the items.
		guiItemStacks.set(ingredients);

		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(3, true, 50, 4, 16, 52, getFluidTankDisplaySize(recipe.getFluid()), false, null);
		fluids.set(ingredients);

		powerTimer = guiHelper.createTickTimer(TileEntityBottler.DEFAULT_PROCESSING_COST,
				TileEntityBottler.DEFAULT_PROCESSING_COST * TileEntityBottler.DEFAULT_PROCESSING_TIME, true);
		processingTimer = guiHelper.createTickTimer(TileEntityBottler.DEFAULT_PROCESSING_COST,
				TileEntityBottler.DEFAULT_PROCESSING_COST, false);
	}
}
