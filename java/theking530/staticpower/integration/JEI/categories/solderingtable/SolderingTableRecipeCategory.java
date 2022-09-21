package theking530.staticpower.integration.JEI.categories.solderingtable;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.categories.thermalconductivity.ThermalConductivityJEIRecipeWrapper;

public class SolderingTableRecipeCategory extends BaseJEIRecipeCategory<SolderingRecipe> {
	public static final RecipeType<SolderingRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "soldering_table"), SolderingRecipe.class);

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public SolderingTableRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.SolderingTable.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(140, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SolderingTable.get()));
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
	public Class<? extends SolderingRecipe> getRecipeClass() {
		return SolderingRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	@Nonnull
	public RecipeType<SolderingRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public void draw(SolderingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 110, 21, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 8, 5, 0);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 44 + x * 18, 5 + y * 18, 0);
			}
		}

	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, SolderingRecipe recipe, IFocusGroup ingredients) {

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				builder.addSlot(RecipeIngredientRole.INPUT, 44 + x * 18, 5 + y * 18).addIngredients(recipe.getIngredients().get(x + (y * 3)));
			}
		}

		builder.addSlot(RecipeIngredientRole.INPUT, 8, 5).addIngredients(recipe.getSolderingIron());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 23).addItemStack(recipe.getResultItem());
	}
}
