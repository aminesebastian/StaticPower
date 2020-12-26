package theking530.staticpower.integration.JEI.categories.solderingtable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class SolderingTableRecipeCategory extends BaseJEIRecipeCategory<SolderingRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID,
			"soldering_table");
	private static final int OUTPUT_SLOT = 10;
	private static final int SOLDERING_IRON_SLOT = 11;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public SolderingTableRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.SolderingTable.getTranslationKey());
		background = guiHelper.createBlankDrawable(140, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SolderingTable));
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return locTitle.getString();
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
	public void draw(SolderingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 110, 21, 20, 20);
		GuiDrawUtilities.drawSlot(matrixStack, 8, 5, 16, 16);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				GuiDrawUtilities.drawSlot(matrixStack, 44 + x * 18, 5 + y * 18, 16, 16);
			}
		}

	}

	@Override
	public void setIngredients(SolderingRecipe recipe, IIngredients ingredients) {
		// Set the crafting inputs.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.addAll(recipe.getIngredients());

		// Add the soldering iron input.
		ITag<Item> tag = ItemTags.getCollection().get(new ResourceLocation("staticpower:soldering_iron"));
		Ingredient solderingIron = Ingredient.fromItemListStream(Stream.of(new Ingredient.TagList(tag)));
		input.add(solderingIron);

		// Set the input ingredients.
		ingredients.setInputIngredients(input);

		// Set the output.
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
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
