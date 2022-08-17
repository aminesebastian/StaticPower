package theking530.staticpower.integration.JEI.categories.solderingtable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class SolderingTableRecipeCategory extends BaseJEIRecipeCategory<SolderingRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "soldering_table");
	private static final int OUTPUT_SLOT = 10;
	private static final int SOLDERING_IRON_SLOT = 11;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public SolderingTableRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.SolderingTable.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(140, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.SolderingTable.get()));
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
	public Class<? extends SolderingRecipe> getRecipeClass() {
		return SolderingRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
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
	public void setIngredients(SolderingRecipe recipe, IIngredients ingredients) {
		// Set the crafting inputs.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.addAll(recipe.getIngredients());
		input.add(recipe.getSolderingIron());

		// Set the input ingredients.
		ingredients.setInputIngredients(input);

		// Set the output.
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SolderingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				guiItemStacks.init(x + (y * 3), true, 43 + x * 18, 4 + y * 18);
			}
		}

		guiItemStacks.init(SOLDERING_IRON_SLOT, true, 7, 4);
		guiItemStacks.init(OUTPUT_SLOT, false, 111, 22);
		guiItemStacks.set(ingredients);
	}
}
