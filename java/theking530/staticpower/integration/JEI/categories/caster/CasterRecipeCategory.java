package theking530.staticpower.integration.JEI.categories.caster;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class CasterRecipeCategory extends BaseJEIRecipeCategory<CastingRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "caster");
	private static final int MOLD_SLOT = 0;
	private static final int FLUID_SLOT = 1;
	private static final int OUTPUT_SLOT = 2;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public CasterRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Caster.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(140, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.Caster.get()));
		pBar = new ArrowProgressBar(50, 14);
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
	public Class<? extends CastingRecipe> getRecipeClass() {
		return CastingRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CastingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		// Draw the slots.
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 76, 14, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 118, 30, 0);

		// Draw the bars.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getInputFluid(), 0, 0, 30, 54, 1.0f, 16, 48, MachineSideMode.Never, true);

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 41, 5, 97, 20, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 41;
		FluidStack fluid = recipe.getInputFluid();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 97, 25, 1, progress, 5, false);

		// Draw the progress bar.
		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
	}

	@Override
	public List<Component> getTooltipStrings(CastingRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getPowerCost() * recipe.getProcessingTime())));
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
	public void setIngredients(CastingRecipe recipe, IIngredients ingredients) {
		// Set the input mold.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getRequiredMold());
		ingredients.setInputIngredients(input);

		// Set the output.
		ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());

		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getInputFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CastingRecipe recipe, IIngredients ingredients) {
		// Add the input mold and output item.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(MOLD_SLOT, true, 75, 13);
		guiItemStacks.set(ingredients);

		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(OUTPUT_SLOT, false, 120, 32);
		probabilityStacks.set(ingredients);
		
		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(FLUID_SLOT, true, 30, 6, 16, 48, getFluidTankDisplaySize(recipe.getInputFluid()), false, null);
		fluids.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
