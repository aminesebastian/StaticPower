package theking530.staticpower.integration.JEI.lumbermill;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticpower.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModFluids;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.Reference;

public class LumberMillRecipeCategory extends BaseJEIRecipeCategory<LumberMillRecipe> {
	private final String locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private static final int INTPUT_SLOT = 0;
	private static final int PRIMARY_OUTPUT_SLOT = 1;
	private static final int SECONDARY_OUTPUT_SLOT = 2;
	public static final ResourceLocation LUMBER_MILL_UID = new ResourceLocation(Reference.MOD_ID, "lumber_mill");

	public LumberMillRecipeCategory(IGuiHelper guiHelper) {
		locTitle = "Lumber Mill";
		background = guiHelper.createBlankDrawable(176, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.LumberMill));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return LUMBER_MILL_UID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return locTitle;
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends LumberMillRecipe> getRecipeClass() {
		return LumberMillRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(LumberMillRecipe recipe, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(31, 19, 16, 16);
		GuiDrawUtilities.drawSlot(91, 19, 16, 16);
		GuiDrawUtilities.drawSlot(121, 19, 16, 16);
		GuiFluidBarUtilities.drawFluidBar(recipe.getOutputFluid(), 1000, recipe.getOutputFluid().getAmount(), 155, 54, 1.0f, 16, 48, MachineSideMode.Never, true);
	}

	@Override
	public void setIngredients(LumberMillRecipe recipe, IIngredients ingredients) {
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput());
		ingredients.setInputIngredients(input);

		List<ItemStack> outputs = new ArrayList<ItemStack>();
		outputs.add(recipe.getPrimaryOutput().getItem());

		if (recipe.hasSecondaryOutput()) {
			outputs.add(recipe.getSecondaryOutput().getItem());
		}

		if (recipe.hasOutputFluid()) {
			ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
		}

		ingredients.setOutputs(VanillaTypes.ITEM, outputs);

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, LumberMillRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 30, 18);
		guiItemStacks.init(PRIMARY_OUTPUT_SLOT, false, 90, 18);
		guiItemStacks.init(SECONDARY_OUTPUT_SLOT, false, 120, 18);

		guiItemStacks.set(ingredients);
	}
}
