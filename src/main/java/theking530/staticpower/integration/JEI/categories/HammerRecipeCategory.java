package theking530.staticpower.integration.JEI.categories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class HammerRecipeCategory extends BaseJEIRecipeCategory<HammerRecipe> {
	public static final RecipeType<HammerRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "hammer"), HammerRecipe.class);
	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar arrow;

	public HammerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent("gui.staticpower.hammering");
		background = guiHelper.createBlankDrawable(110, 50);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.IronMetalHammer.get()));
		arrow = new ArrowProgressBar(57, 16);
	}

	@Override
	@Nonnull
	public Component getTitle() {
		return locTitle;
	}

	@Override
	@Nonnull
	public RecipeType<HammerRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends HammerRecipe> getRecipeClass() {
		return HammerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(HammerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 4, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 86, 14, 0);

		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		// Get the buffer and render the large hammer.
		GuiDrawUtilities.drawSprite(matrixStack, new ResourceLocation("staticpower", "items/tools/hammer_iron"), 32, 32, 23, 5, 0, 0, 0, 1, 1, Color.WHITE);
		if (!recipe.isBlockType()) {
			GuiDrawUtilities.drawString(matrixStack, "Requires Anvil", 89, 47, 0.0f, 1.0f, Color.EIGHT_BIT_GREY, false);
		}
	}

	@Override
	public void setIngredients(HammerRecipe recipe, IIngredients ingredients) {
		// Create the input ingredients.
		List<Ingredient> input = new ArrayList<Ingredient>();

		if (recipe.isBlockType()) {
			// Allocate the inputs block array.
			List<Block> blocks = ForgeRegistries.BLOCKS.tags().getTag(recipe.getInputTag()).stream().toList();
			ItemStack[] inputBlocks = new ItemStack[blocks.size()];
			int index = 0;
			for (Block block : blocks) {
				inputBlocks[index] = new ItemStack(block);
				index++;
			}
			input.add(Ingredient.of(inputBlocks));
		} else {
			input.add(recipe.getInputItem().getIngredient());
		}

		// Add the hammer and input blocks.
		ingredients.setInputIngredients(input);

		// Set the output.
		ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, HammerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INPUT_SLOT, true, 3, 15);
		guiItemStacks.set(ingredients);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(OUTPUT_SLOT, false, 88, 16);
		probabilityStacks.set(ingredients);
	}
}
