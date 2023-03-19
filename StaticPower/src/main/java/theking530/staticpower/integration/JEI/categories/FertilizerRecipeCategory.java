package theking530.staticpower.integration.JEI.categories;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class FertilizerRecipeCategory extends BaseJEIRecipeCategory<FertalizerRecipe> {
	public static final RecipeType<FertalizerRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "fertilization"), FertalizerRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public FertilizerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable("gui.staticpower.fertlization");
		background = guiHelper.createBlankDrawable(130, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BasicFarmer.get()));
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
	public RecipeType<FertalizerRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(FertalizerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 53, 56, 1.0f, 24, 40, MachineSideMode.Never, true);
		MutableComponent bonus = Component.translatable("gui.staticpower.fertlization_chance").append(": ")
				.append(GuiTextUtilities.formatNumberAsStringOneDecimal(recipe.getFertalizationAmount() * 100));
		bonus.append(Component.literal("%"));
		GuiDrawUtilities.drawStringCentered(matrixStack, bonus.getString(), 65, 9, 0.0f, 1f, SDColor.EIGHT_BIT_DARK_GREY, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FertalizerRecipe recipe, IFocusGroup ingredients) {
		addFluidIngredientSlot(builder, 53, 16, 24, 40, recipe.getRequiredFluid()).addTooltipCallback((recipeSlotView, tooltip) -> {
			tooltip.add(tooltip.get(tooltip.size() - 1));

			MutableComponent bonus = Component.translatable("gui.staticpower.fertlization_bonus",
					GuiTextUtilities.formatNumberAsStringOneDecimal(recipe.getFertalizationAmount() * 100));
			bonus.append(Component.literal("%"));
			bonus.withStyle(ChatFormatting.GOLD);
			tooltip.set(tooltip.size() - 2, bonus);
		});
	}
}
