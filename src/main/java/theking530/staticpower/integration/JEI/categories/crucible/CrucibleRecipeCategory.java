package theking530.staticpower.integration.JEI.categories.crucible;

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
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class CrucibleRecipeCategory extends BaseJEIRecipeCategory<CrucibleRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "crucible");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private static final int FLUID_SLOT = 2;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public CrucibleRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Crucible.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.Crucible.get()));
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
	public Class<? extends CrucibleRecipe> getRecipeClass() {
		return CrucibleRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CrucibleRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 50, 12, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 75, 32, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluid(), 0, 0, 106, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());
		GuiHeatBarUtilities.drawHeatBar(matrixStack, 24, 6, 4, 48, 1.0f, recipe.getMinimumTemperature(), 0, getNetHighestMultipleOf10(recipe.getMinimumTemperature()));

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 28, 5, 72, 18, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getOutputFluid();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 72, 23, 1, progress, 5, false);
	}

	@Override
	public List<Component> getTooltipStrings(CrucibleRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX >= 8 && mouseX < 24 && mouseY < 54 && mouseY >= 4) {
			output.add(new TextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the heat bar tooltip.
		if (mouseX >= 28 && mouseX < 32 && mouseY < 54 && mouseY >= 4) {
			output.add(GuiTextUtilities.formatHeatToString(recipe.getMinimumTemperature()));
		}

		return output;
	}

	@Override
	public void setIngredients(CrucibleRecipe recipe, IIngredients ingredients) {
		// Add the inputs.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput().getIngredient());
		ingredients.setInputIngredients(input);

		// Add the output fluid if one exists.
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());

		// Set the output item stacks.
		if(!recipe.getOutput().isEmpty()) {
			ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
		}
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CrucibleRecipe recipe, IIngredients ingredients) {
		// Add the input.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 49, 11);
		guiItemStacks.set(ingredients);
		
		// Add the output item if it exists.
		if (recipe.hasItemOutput()) {
			// Set the outputs.
			IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
			probabilityStacks.init(OUTPUT_SLOT, false, 76, 33);
			probabilityStacks.set(ingredients);
		}

		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(FLUID_SLOT, false, 106, 4, 16, 52, getFluidTankDisplaySize(recipe.getOutputFluid()), false, null);
		fluids.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
