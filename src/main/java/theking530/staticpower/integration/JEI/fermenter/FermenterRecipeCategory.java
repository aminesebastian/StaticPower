package theking530.staticpower.integration.JEI.fermenter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.common.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.powered.fermenter.TileEntityFermenter;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.MetricConverter;
import theking530.staticpower.utilities.Reference;

public class FermenterRecipeCategory extends BaseJEIRecipeCategory<FermenterRecipe> {
	public static final ResourceLocation FERMENTER_UID = new ResourceLocation(Reference.MOD_ID, "fermenter");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public FermenterRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Fermenter.getTranslationKey());
		background = guiHelper.createBlankDrawable(176, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Fermenter));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return FERMENTER_UID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return locTitle.getFormattedText();
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends FermenterRecipe> getRecipeClass() {
		return FermenterRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FermenterRecipe recipe, double mouseX, double mouseY) {
		// Draw the slots.
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				GuiDrawUtilities.drawSlot(40 + j * 18, 4 + i * 18, 16, 16);
			}
		}

		GuiDrawUtilities.drawSlot(112, 34, 20, 20);

		// Draw the power bar.
		GuiPowerBarUtilities.drawPowerBar(8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(recipe.getOutputFluidStack(), 0, 0, 153, 54, 1.0f, 16, 48, MachineSideMode.Never, true);

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(97, 24, 48, 5);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 45;
		FluidStack fluid = recipe.getOutputFluidStack();
		GuiFluidBarUtilities.drawFluidBar(fluid, 1000, 1000, 97, 29, 1, progress, 5, false);
	}

	@Override
	public List<String> getTooltipStrings(FermenterRecipe recipe, double mouseX, double mouseY) {
		List<String> output = new ArrayList<String>();

		// Add a tooltip for the energy bar.
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			String powerCost = new MetricConverter(TileEntityFermenter.DEFAULT_POWER_USAGE * TileEntityFermenter.DEFAULT_PROCESSING_TIME).getValueAsString(true);
			output.add("Usage: " + powerCost + "FE");
		}

		return output;
	}

	@Override
	public void setIngredients(FermenterRecipe recipe, IIngredients ingredients) {
		// Set the input items.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInputIngredient().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the output ingredients.
		ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(ModItems.DistilleryGrain));

		// Set the output fluid.
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluidStack());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FermenterRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 39, 3);
		guiItemStacks.init(OUTPUT_SLOT, false, 113, 35);
		guiItemStacks.set(ingredients);

		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(3, false, 153, 6, 16, 48, getFluidTankDisplaySize(recipe.getOutputFluidStack()), false, null);
		fluids.set(ingredients);

		powerTimer = guiHelper.createTickTimer(TileEntityFermenter.DEFAULT_POWER_USAGE, TileEntityFermenter.DEFAULT_POWER_USAGE * TileEntityFermenter.DEFAULT_PROCESSING_TIME, true);
		processingTimer = guiHelper.createTickTimer(TileEntityFermenter.DEFAULT_POWER_USAGE, TileEntityFermenter.DEFAULT_POWER_USAGE, false);
	}
}
