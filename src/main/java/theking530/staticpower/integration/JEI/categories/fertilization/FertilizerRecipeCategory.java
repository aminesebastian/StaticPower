package theking530.staticpower.integration.JEI.categories.fertilization;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class FertilizerRecipeCategory extends BaseJEIRecipeCategory<FertalizerRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "fertilization");
	private static final int FLUID_INPUT = 0;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public FertilizerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent("gui.staticpower.fertlization");
		background = guiHelper.createBlankDrawable(130, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.BasicFarmer));
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
	public Class<? extends FertalizerRecipe> getRecipeClass() {
		return FertalizerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FertalizerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getRequiredFluid(), 0, 0, 53, 56, 1.0f, 24, 40, MachineSideMode.Never, true);
		TranslatableComponent bonus = new TranslatableComponent("gui.staticpower.fertlization_bonus", GuiTextUtilities.formatNumberAsStringOneDecimal(recipe.getFertalizationAmount() * 100));
		bonus.append(new TextComponent("%"));
		GuiDrawUtilities.drawStringCentered(matrixStack, bonus.getString(), 65, 9, 0.0f, 0.9f, Color.EIGHT_BIT_YELLOW, true);
	}

	@Override
	public void setIngredients(FertalizerRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.FLUID, recipe.getRequiredFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FertalizerRecipe recipe, IIngredients ingredients) {
		// Add the input fluids.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.init(FLUID_INPUT, true, 53, 16, 24, 40, 1, true, null);
		fluids.set(ingredients);

		fluids.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
			tooltip.add(tooltip.get(tooltip.size() - 1));
			
			TranslatableComponent bonus = new TranslatableComponent("gui.staticpower.fertlization_bonus", GuiTextUtilities.formatNumberAsStringOneDecimal(recipe.getFertalizationAmount() * 100));
			bonus.append(new TextComponent("%"));
			bonus.withStyle(ChatFormatting.GOLD);
			tooltip.set(tooltip.size() - 2, bonus);
		});
	}
}