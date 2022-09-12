package theking530.staticpower.integration.JEI.categories.refinery;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
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
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class RefineryRecipeCategory extends BaseJEIRecipeCategory<RefineryRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "refinery");
	private static final int CATALYST_SLOT = 0;
	private static final int PRIMARY_FLUID_INPUT_SLOT = 1;
	private static final int SECONDARY_FLUID_INPUT_SLOT = 2;
	private static final int OUTPUT_SLOT_1 = 3;
	private static final int OUTPUT_SLOT_2 = 4;
	private static final int OUTPUT_SLOT_3 = 5;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;
	private final FluidProgressBar fluidBar1;
	private final FluidProgressBar fluidBar2;
	private final FluidProgressBar fluidBar3;

	public RefineryRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.RefineryController.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(170, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.RefineryController.get()));
		fluidBar1 = new FluidProgressBar(75, 27, 24, 4);
		fluidBar2 = new FluidProgressBar(75, 32, 24, 4);
		fluidBar3 = new FluidProgressBar(75, 42, 24, 4);
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
	public Class<? extends RefineryRecipe> getRecipeClass() {
		return RefineryRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(RefineryRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 79, 2, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getPrimaryFluidInput(), 0, 0, 32, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getSecondaryFluidInput(), 0, 0, 54, 54, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput1(), 0, 0, 104, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput2(), 0, 0, 126, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput3(), 0, 0, 148, 54, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the progress bar as a fluid (can't use the widget here because this is a
		// singleton class).
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 24;

		int yOffset = 0;
		if (!recipe.getFluidOutput1().isEmpty()) {
			GuiDrawUtilities.drawSlot(matrixStack, 24, 4, 75, 27, 0);
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput1(), 1000, 1000, 75, 31, 1, progress, 4, false);
			yOffset += 7;
		}
		if (!recipe.getFluidOutput2().isEmpty()) {
			GuiDrawUtilities.drawSlot(matrixStack, 24, 4, 75, 27 + yOffset, 0);
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput2(), 1000, 1000, 75, 31 + +yOffset, 1, progress, 4, false);
			yOffset += 7;
		}
		if (!recipe.getFluidOutput3().isEmpty()) {
			GuiDrawUtilities.drawSlot(matrixStack, 24, 4, 75, 27 + yOffset, 0);
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluidOutput3(), 1000, 1000, 75, 31 + +yOffset, 1, progress, 4, false);
		}
	}

	@Override
	public List<Component> getTooltipStrings(RefineryRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 0) {
			output.add(new TextComponent("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (fluidBar1.isPointInsideBounds(mouse)) {
			fluidBar1.getTooltips(mouse, output, false);
		}
		if (fluidBar2.isPointInsideBounds(mouse)) {
			fluidBar2.getTooltips(mouse, output, false);
		}
		if (fluidBar3.isPointInsideBounds(mouse)) {
			fluidBar3.getTooltips(mouse, output, false);
		}
		return output;
	}

	@Override
	public void setIngredients(RefineryRecipe recipe, IIngredients ingredients) {
		// Add the input items.
		List<Ingredient> inputs = new ArrayList<Ingredient>();
		inputs.add(recipe.getCatalyst().getIngredient());
		ingredients.setInputIngredients(inputs);

		// Set the input fluids.
		List<FluidStack> fluids = new ArrayList<FluidStack>();
		if (!recipe.getPrimaryFluidInput().isEmpty()) {
			fluids.add(recipe.getPrimaryFluidInput());
		}
		if (!recipe.getSecondaryFluidInput().isEmpty()) {
			fluids.add(recipe.getSecondaryFluidInput());
		}

		ingredients.setInputs(VanillaTypes.FLUID, fluids);

		// Set the output(s).
		List<FluidStack> outputFluids = new ArrayList<FluidStack>();
		if (!recipe.getFluidOutput1().isEmpty()) {
			outputFluids.add(recipe.getFluidOutput1());
		}
		if (!recipe.getFluidOutput2().isEmpty()) {
			outputFluids.add(recipe.getFluidOutput2());
		}
		if (!recipe.getFluidOutput3().isEmpty()) {
			outputFluids.add(recipe.getFluidOutput3());
		}
		ingredients.setOutputs(VanillaTypes.FLUID, outputFluids);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RefineryRecipe recipe, IIngredients ingredients) {
		// Add the input and output slots.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(CATALYST_SLOT, true, 78, 1);

		// Set the items.
		guiItemStacks.set(ingredients);

		// Add the input fluid(s).
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		if (!recipe.getPrimaryFluidInput().isEmpty()) {
			fluids.init(PRIMARY_FLUID_INPUT_SLOT, true, 32, 2, 16, 52, getFluidTankDisplaySize(recipe.getPrimaryFluidInput()), false, null);
		}
		if (!recipe.getSecondaryFluidInput().isEmpty()) {
			fluids.init(SECONDARY_FLUID_INPUT_SLOT, true, 54, 2, 16, 52, getFluidTankDisplaySize(recipe.getSecondaryFluidInput()), false, null);
		}

		// Add the fluid output(s).
		if (!recipe.getFluidOutput1().isEmpty()) {
			fluids.init(OUTPUT_SLOT_1, false, 104, 2, 16, 52, getFluidTankDisplaySize(recipe.getFluidOutput1()), false, null);
		}
		if (!recipe.getFluidOutput2().isEmpty()) {
			fluids.init(OUTPUT_SLOT_2, false, 126, 2, 16, 52, getFluidTankDisplaySize(recipe.getFluidOutput2()), false, null);
		}
		if (!recipe.getFluidOutput3().isEmpty()) {
			fluids.init(OUTPUT_SLOT_3, false, 148, 2, 16, 52, getFluidTankDisplaySize(recipe.getFluidOutput3()), false, null);
		}
		fluids.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
