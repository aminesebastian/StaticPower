package theking530.staticpower.integration.JEI.categories.cauldron;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class CauldronRecipeCategory extends BaseJEIRecipeCategory<CauldronRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "cauldron");
	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private static final int INPUT_FLUID_SLOT = 2;
	private static final int OUTPUT_FLUID_SLOT = 3;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar arrow;
	private ITickTimer timer;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.RustyCauldron.getTranslationKey());
		background = guiHelper.createBlankDrawable(130, 50);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.RustyCauldron));
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
	public Class<? extends CauldronRecipe> getRecipeClass() {
		return CauldronRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(CauldronRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 4, 6, 16, 16);
		GuiDrawUtilities.drawSlot(matrixStack, 105, 22, 20, 20);

		arrow.setPosition(23, 6);
		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);

		arrow.setPosition(80, 25);
		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);

		// Get the buffer and render the large hammer.
		Vector2D location = GuiDrawUtilities.translatePositionByMatrix(matrixStack, 0, 0);

		// Render the time.
		GuiDrawUtilities.drawStringWithSizeCentered(matrixStack, "Cook time: " + recipe.getRequiredTimeInCauldron(), 65, 4, 0.8f, Color.EIGHT_BIT_YELLOW, true);

		// Render the block.
		MatrixStack blockStack = new MatrixStack();
		float scale = 1.6f;
		{
			blockStack.push();
			blockStack.translate(1.95, -1.7, 1.0f);
			blockStack.scale(scale, scale, 16);
			blockStack.rotate(new Quaternion(32, 45, 0, true));

			BlockState blockState = ModBlocks.RustyCauldron.getDefaultState();
			Minecraft mc = Minecraft.getInstance();
			BlockRendererDispatcher blockRenderer = mc.getBlockRendererDispatcher();
			blockRenderer.renderBlock(blockState, blockStack, mc.getRenderTypeBuffers().getBufferSource(), 15728880, OverlayTexture.NO_OVERLAY);

			blockStack.pop();
		}

		// Render the fluid if it exists.
		if (!recipe.getRequiredFluid().isEmpty()) {
			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(recipe.getRequiredFluid());
			Color fluidColor = GuiDrawUtilities.getFluidColor(recipe.getRequiredFluid());
			float TEXEL = (1.0f / 16.0f);
			float time = SDMath.clamp(timer.getValue(), 0, timer.getMaxValue() / 2);
			float height = 1.0f;

			if (recipe.shouldDrainCauldron() || !recipe.getOutputFluid().isEmpty()) {
				height = (float) time / (timer.getMaxValue() / 2);
				height = 1.0f - height;
			}

			blockStack.push();
			blockStack.translate(location.getX() + 43, location.getY() + 17.7f, 265);
			blockStack.scale(scale * 16, scale * 16, scale);
			blockStack.rotate(new Quaternion(32, 45, 0, true));
			new BlockModel().drawPreviewCube(new Vector3f(2 * TEXEL, 12 * TEXEL - (9 * TEXEL * height), 2 * TEXEL), new Vector3f(12 * TEXEL, TEXEL * height, 12 * TEXEL), fluidColor,
					blockStack, sprite, new Vector3D(1.0f, 1.0f, 1.0f));
			blockStack.pop();
		}

		// Render the output fluid if it exists.
		if (!recipe.getOutputFluid().isEmpty()) {
			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(recipe.getOutputFluid());
			Color fluidColor = GuiDrawUtilities.getFluidColor(recipe.getOutputFluid());
			float TEXEL = (1.0f / 16.0f);
			float time = SDMath.clamp(timer.getValue(), timer.getMaxValue() / 2, timer.getMaxValue());
			time -= timer.getMaxValue() / 2;
			float height = (float) time / (timer.getMaxValue() / 2);

			blockStack.push();
			blockStack.translate(location.getX() + 43, location.getY() + 17.7f, 265);
			blockStack.scale(scale * 16, scale * 16, scale);
			blockStack.rotate(new Quaternion(32, 45, 0, true));
			new BlockModel().drawPreviewCube(new Vector3f(2 * TEXEL, 12 * TEXEL - (9 * TEXEL * height), 2 * TEXEL), new Vector3f(12 * TEXEL, TEXEL * height, 12 * TEXEL), fluidColor,
					blockStack, sprite, new Vector3D(1.0f, 1.0f, 1.0f));
			blockStack.pop();
		}
	}

	@Override
	public void setIngredients(CauldronRecipe recipe, IIngredients ingredients) {
		// Set the input ingrdient.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getRequiredFluid());

		// Set the output.
		ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());

		// Set the output fluid.
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CauldronRecipe recipe, IIngredients ingredients) {
		// Set the inputs.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INPUT_SLOT, true, 3, 5);
		guiItemStacks.set(ingredients);

		// Set the outputs.
		IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
		probabilityStacks.init(OUTPUT_SLOT, false, 107, 24);
		probabilityStacks.set(ingredients);

		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		if (!recipe.getRequiredFluid().isEmpty()) {
			fluids.init(INPUT_FLUID_SLOT, true, 56, 18, 10, 10, getFluidTankDisplaySize(recipe.getRequiredFluid()), false, null);
		}
		if (!recipe.getOutputFluid().isEmpty()) {
			fluids.init(OUTPUT_FLUID_SLOT, false, 56, 30, 10, 10, getFluidTankDisplaySize(recipe.getRequiredFluid()), false, null);
		}
		fluids.set(ingredients);

		// Set the timer.
		int processingTime = SDMath.clamp(recipe.getRequiredTimeInCauldron() / 2, 0, recipe.getRequiredTimeInCauldron());
		timer = guiHelper.createTickTimer(processingTime, processingTime, false);
	}
}
