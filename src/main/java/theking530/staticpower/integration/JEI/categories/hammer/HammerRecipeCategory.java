package theking530.staticpower.integration.JEI.categories.hammer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.Color;
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

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar arrow;

	public HammerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent("gui.staticpower.hammering");
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
	public void draw(HammerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 4, 16, 16, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 86, 14, 20, 20, 0);

		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);

		// Get the buffer and render the large hammer.
		Vector2D location = GuiDrawUtilities.translatePositionByMatrix(matrixStack, 21, -1);
		RenderSystem.pushMatrix();
		RenderSystem.translatef(location.getX(), location.getY(), 0.0F);
		RenderSystem.scalef(2.5F, 2.5F, 2.5F);
		Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(ModItems.IronMetalHammer), 0, 0);
		RenderSystem.popMatrix();

		if (!recipe.isBlockType()) {
			GuiDrawUtilities.drawStringWithSize(matrixStack, "Requires Anvil", 89, 47, 1.0f, Color.EIGHT_BIT_GREY, false);
		}
	}

	@Override
	public void setIngredients(HammerRecipe recipe, IIngredients ingredients) {
		// Create the input ingredients.
		List<Ingredient> input = new ArrayList<Ingredient>();

		if (recipe.isBlockType()) {
			// Allocate the inputs block array.
			ItemStack[] inputBlocks = new ItemStack[recipe.getInputTag().getValues().size()];
			int index = 0;
			for (Block block : recipe.getInputTag().getValues()) {
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
