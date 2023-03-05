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
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class FermenterRecipeCategory extends BaseJEIRecipeCategory<FermenterRecipe> {
	public static final RecipeType<FermenterRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "fermenter"), FermenterRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer processingTimer;

	public FermenterRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.Fermenter.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(176, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Fermenter.get()));
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
	public RecipeType<FermenterRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FermenterRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		// Draw the slots.
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 40 + j * 18, 4 + i * 18, 0);
			}
		}

		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 112, 34, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getOutputFluidStack(), 0, 0, 153, 54, 1.0f, 16, 48, MachineSideMode.Never, true);

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 48, 5, 97, 24, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 45;
		FluidStack fluid = recipe.getOutputFluidStack();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 97, 29, 1, progress, 5, false);
	}

	@Override
	public List<Component> getTooltipStrings(FermenterRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();

		// Add a tooltip for the energy bar.
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(Component.literal("Usage: ")
					.append(PowerTextFormatting.formatPowerToString(StaticPowerConfig.SERVER.fermenterPowerUsage.get() * StaticPowerConfig.SERVER.fermenterProcessingTime.get())));

		}

		return output;
	}

	public void setRecipe(IRecipeLayoutBuilder builder, FermenterRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 40, 4).addIngredients(recipe.getInputIngredient().getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 114, 36).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getResidualOutput());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 153, 6).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluidStack())
				.setFluidRenderer(getFluidTankDisplaySize(recipe.getOutputFluidStack()), false, 16, 48);

		addPowerInputSlot(builder, 5, 6, 16, 48, recipe.getProcessingSection());

		processingTimer = guiHelper.createTickTimer(StaticPowerConfig.SERVER.fermenterProcessingTime.get(), (int) StaticPowerConfig.SERVER.fermenterPowerUsage.get().longValue(),
				false);
	}
}
