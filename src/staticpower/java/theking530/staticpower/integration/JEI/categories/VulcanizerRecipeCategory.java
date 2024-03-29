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
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class VulcanizerRecipeCategory extends BaseJEIRecipeCategory<VulcanizerRecipe> {
	public static final RecipeType<VulcanizerRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "vulcanizer"), VulcanizerRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;

	public VulcanizerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.Vulcanizer.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(132, 66);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Vulcanizer.get()));
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
	public RecipeType<VulcanizerRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(VulcanizerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 62, 4, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 104, 18, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 40, 56, 1.0f, 16, 52, MachineSideMode.Never, true);

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 34, 5, 62, 25, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 34.0f;
		FluidStack fluid = this.getNthFluidInput(recipeSlotsView, 0);
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 62, 30, 1, progress, 5, false);
	}

	@Override
	public List<Component> getTooltipStrings(VulcanizerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(Component.literal("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}
		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, VulcanizerRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 62, 4).addIngredients(recipe.getInputItem().getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 20).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());

		addPowerInputSlot(builder, 5, 6, 16, 48, recipe.getProcessingSection());
		addFluidIngredientSlot(builder, 40, 4, 16, 52, recipe.getInputFluid());

		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
