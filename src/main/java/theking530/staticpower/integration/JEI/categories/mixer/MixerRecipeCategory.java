package theking530.staticpower.integration.JEI.categories.mixer;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.progressbars.MixerProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class MixerRecipeCategory extends BaseJEIRecipeCategory<MixerRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "mixer");
	private static final int PRIMARY_ITEM_INPUT_SLOT = 0;
	private static final int SECONDARY_ITEM_INPUT_SLOT = 1;
	private static final int PRIMARY_FLUID_INPUT_SLOT = 2;
	private static final int SECONDARY_FLUID_INPUT_SLOT = 3;
	private static final int OUTPUT_SLOT = 4;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;
	private final MixerProgressBar mixerPBar;
	private final FluidProgressBar pBar;

	public MixerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Mixer.getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Mixer));
		mixerPBar = new MixerProgressBar(54, 20);
		pBar = new FluidProgressBar(100, 23, 24, 8);
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
	public Class<? extends MixerRecipe> getRecipeClass() {
		return MixerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(MixerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 56, 2, 16, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 56, 38, 16, 16, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getPrimaryFluidInput(), 0, 0, 32, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getSecondaryFluidInput(), 0, 0, 80, 54, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutput(), 0, 0, 128, 54, 1.0f, 16, 52, MachineSideMode.Never, true);

		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the progress bar as a fluid (can't use the widget here because this is a singleton class).
		GuiDrawUtilities.drawSlot(matrixStack, 100, 23, 24, 8, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 24;
		FluidStack fluid = recipe.getOutput();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 100, 31, 1, progress, 8, false);
		
		
		mixerPBar.setCurrentProgress(processingTimer.getValue());
		mixerPBar.setMaxProgress(processingTimer.getMaxValue());
		mixerPBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<Component> getTooltipStrings(MixerRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(recipe.getPowerCost() * recipe.getProcessingTime())));
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
	public void setIngredients(MixerRecipe recipe, IIngredients ingredients) {
		// Add the input items.
		List<Ingredient> inputs = new ArrayList<Ingredient>();
		inputs.add(recipe.getPrimaryItemInput().getIngredient());
		inputs.add(recipe.getSecondaryItemInput().getIngredient());
		ingredients.setInputIngredients(inputs);

		// Set the input fluids.
		List<FluidStack> fluids = new ArrayList<FluidStack>();
		fluids.add(recipe.getPrimaryFluidInput());
		fluids.add(recipe.getSecondaryFluidInput());
		ingredients.setInputs(VanillaTypes.FLUID, fluids);

		// Set the output.
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MixerRecipe recipe, IIngredients ingredients) {
		// Add the input and output slots.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(PRIMARY_ITEM_INPUT_SLOT, true, 55, 1);
		guiItemStacks.init(SECONDARY_ITEM_INPUT_SLOT, true, 55, 37);

		// Set the items.
		guiItemStacks.set(ingredients);

		// Add the input fluids.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(PRIMARY_FLUID_INPUT_SLOT, true, 32, 2, 16, 52, getFluidTankDisplaySize(recipe.getPrimaryFluidInput()), false, null);
		fluids.init(SECONDARY_FLUID_INPUT_SLOT, true, 80, 2, 16, 52, getFluidTankDisplaySize(recipe.getSecondaryFluidInput()), false, null);
		fluids.init(OUTPUT_SLOT, false, 128, 2, 16, 52, getFluidTankDisplaySize(recipe.getOutput()), false, null);
		fluids.set(ingredients);

		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), (int) (recipe.getProcessingTime() * recipe.getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
