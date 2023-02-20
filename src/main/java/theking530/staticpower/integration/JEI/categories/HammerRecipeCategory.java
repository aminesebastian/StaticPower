package theking530.staticpower.integration.JEI.categories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class HammerRecipeCategory extends BaseJEIRecipeCategory<HammerRecipe> {
	public static final RecipeType<HammerRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "hammer"), HammerRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar arrow;

	public HammerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable("gui.staticpower.hammering");
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
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(HammerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 4, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 86, 14, 0);

		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		GuiDrawUtilities.drawItem(matrixStack, recipe.getHammer().getItems()[0], 31, 13, 0, 32, 32, 1.0f);
		if (!recipe.isBlockType()) {
			GuiDrawUtilities.drawString(matrixStack, "Requires Anvil", 89, 47, 0.0f, 1.0f, SDColor.EIGHT_BIT_GREY, false);
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, HammerRecipe recipe, IFocusGroup ingredients) {
		// Create the input ingredients.
		List<ItemStack> input = new ArrayList<ItemStack>();

		if (recipe.isBlockType()) {
			// Allocate the inputs block array.
			List<Block> blocks = ForgeRegistries.BLOCKS.tags().getTag(recipe.getBlock()).stream().toList();
			ItemStack[] inputBlocks = new ItemStack[blocks.size()];
			int index = 0;
			for (Block block : blocks) {
				inputBlocks[index] = new ItemStack(block);
				index++;
			}
			for (ItemStack stack : Ingredient.of(inputBlocks).getItems()) {
				input.add(stack);
			}
		} else {
			for (ItemStack stack : recipe.getInputItem().getIngredient().getItems()) {
				input.add(stack);
			}
		}

		builder.addSlot(RecipeIngredientRole.INPUT, 4, 16).addIngredients(VanillaTypes.ITEM_STACK, input);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 16).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());
	}
}
