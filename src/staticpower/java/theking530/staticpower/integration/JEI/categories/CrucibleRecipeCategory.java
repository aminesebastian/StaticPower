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
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class CrucibleRecipeCategory extends BaseJEIRecipeCategory<CrucibleRecipe> {
	public static final RecipeType<CrucibleRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "crucible"), CrucibleRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;

	public CrucibleRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.Crucible.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Crucible.get()));
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
	public RecipeType<CrucibleRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CrucibleRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 50, 12, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 75, 32, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 106, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiHeatBarUtilities.drawHeatBar(matrixStack, 24, 6, 4, 48, 1.0f, recipe.getProcessingSection().getMinimumHeat(), 0,
				getNetHighestMultipleOf10(recipe.getProcessingSection().getMinimumHeat()));

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 28, 5, 72, 18, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getOutputFluid();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 72, 23, 1, progress, 5, false);
	}

	@Override
	public List<Component> getTooltipStrings(CrucibleRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX >= 8 && mouseX < 24 && mouseY < 54 && mouseY >= 4) {
			output.add(Component.literal("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the heat bar tooltip.
		if (mouseX >= 28 && mouseX < 32 && mouseY < 54 && mouseY >= 4) {
			output.add(GuiTextUtilities.formatHeatToString(recipe.getProcessingSection().getMinimumHeat()));
		}

		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CrucibleRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 50, 12).addIngredients(recipe.getInput().getIngredient());
		builder.addSlot(RecipeIngredientRole.INPUT, 106, 4).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid())
				.setFluidRenderer(getFluidTankDisplaySize(recipe.getOutputFluid()), false, 16, 52);

		// Add the output item if it exists.
		if (recipe.hasItemOutput()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 34).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
		}
		
		addPowerInputSlot(builder, 5, 6, 16, 48, recipe.getProcessingSection());

		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
