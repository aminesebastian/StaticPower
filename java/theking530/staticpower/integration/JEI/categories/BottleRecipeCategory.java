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
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class BottleRecipeCategory extends BaseJEIRecipeCategory<BottleRecipe> {
	public static final RecipeType<BottleRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "bottler"), BottleRecipe.class);

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public BottleRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.Bottler.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(146, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.Bottler.get()));
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
	public RecipeType<BottleRecipe> getRecipeType() {
		return TYPE;
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
	public void draw(BottleRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 109, 12, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 107, 36, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getFluid(), 0, 0, 50, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the progress bar as a fluid.
		GuiDrawUtilities.drawSlot(matrixStack, 28, 5, 72, 18, 0);
		float progress = ((float) processingTimer.getValue() / processingTimer.getMaxValue()) * 28;
		FluidStack fluid = recipe.getFluid();
		GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1000, 1000, 72, 23, 1, progress, 5, false);
	}

	@Override
	public List<Component> getTooltipStrings(BottleRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ")
					.append(PowerTextFormatting.formatPowerToString(StaticPowerConfig.SERVER.bottlerPowerUsage.get() * StaticPowerConfig.SERVER.bottlerProcessingTime.get())));
		}

		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BottleRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 109, 12).addItemStack(recipe.getEmptyBottle());
		builder.addSlot(RecipeIngredientRole.INPUT, 50, 4).addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluid()).setFluidRenderer(getFluidTankDisplaySize(recipe.getFluid()),
				false, 16, 52);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 38).addItemStack(recipe.getFilledBottle());

		powerTimer = guiHelper.createTickTimer((int) StaticPowerConfig.SERVER.bottlerPowerUsage.get().longValue(),
				(int) (StaticPowerConfig.SERVER.bottlerPowerUsage.get() * StaticPowerConfig.SERVER.bottlerProcessingTime.get()), true);
		processingTimer = guiHelper.createTickTimer((int) StaticPowerConfig.SERVER.bottlerPowerUsage.get().longValue(),
				(int) StaticPowerConfig.SERVER.bottlerPowerUsage.get().longValue(), false);
	}
}
