package theking530.staticpower.integration.JEI.categories.hammer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class HammerRecipeCategory extends BaseJEIRecipeCategory<HammerRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "hammer");
	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar arrow;

	public HammerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent("gui.staticpower.hammering");
		background = guiHelper.createBlankDrawable(110, 50);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.IronMetalHammer));
		arrow = new ArrowProgressBar(57, 16);
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
	public Class<? extends HammerRecipe> getRecipeClass() {
		return HammerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(HammerRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 4, 16, 16, 16);
		GuiDrawUtilities.drawSlot(matrixStack, 86, 14, 20, 20);

		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);

		// Get the buffer and render the large hammer.
		Vector2D location = GuiDrawUtilities.translatePositionByMatrix(matrixStack, 21, -1);
		RenderSystem.pushMatrix();
		RenderSystem.translatef(location.getX(), location.getY(), 0.0F);
		RenderSystem.scalef(3F, 3F, 3F);
		Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(ModItems.IronMetalHammer), 0, 0);
		RenderSystem.popMatrix();
	}

	@Override
	public void setIngredients(HammerRecipe recipe, IIngredients ingredients) {
		// Create the input ingrdients.
		List<Ingredient> input = new ArrayList<Ingredient>();

		// Allocate the inputs block array.
		ItemStack[] inputBlocks = new ItemStack[recipe.getInputTag().getAllElements().size()];
		int index = 0;
		for (Block block : recipe.getInputTag().getAllElements()) {
			inputBlocks[index] = new ItemStack(block);
			index++;
		}

		// Add the hammer and input blocks.
		input.add(Ingredient.fromStacks(inputBlocks));
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
