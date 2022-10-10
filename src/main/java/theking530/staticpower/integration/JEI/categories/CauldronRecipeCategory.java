package theking530.staticpower.integration.JEI.categories;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class CauldronRecipeCategory extends BaseJEIRecipeCategory<CauldronRecipe> {
	public static final RecipeType<CauldronRecipe> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "cauldron"), CauldronRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar arrow;
	private ITickTimer timer;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.RustyCauldron.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(140, 55);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RustyCauldron.get()));
		arrow = new ArrowProgressBar(57, 16);
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
	public RecipeType<CauldronRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 4, 6, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 105, 22, 0);

		arrow.setPosition(23, 6);
		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		arrow.setPosition(80, 25);
		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		// Get the buffer and render the large hammer.
		Vector2D location = GuiDrawUtilities.translatePositionByMatrix(matrixStack, 0, 0);

		// Render the time.
		GuiDrawUtilities.drawStringCentered(matrixStack, "Cook time: " + recipe.getRequiredTimeInCauldron(), 65, 4, 0.0f, 0.8f, SDColor.EIGHT_BIT_YELLOW, true);

		// Render the block.
		PoseStack blockStack = new PoseStack();
		float scale = 1.6f;
		{
			blockStack.pushPose();
			blockStack.translate(-4.5, -0.6f, 0.8f);
			blockStack.scale(scale, scale, 16);
			blockStack.mulPose(new Quaternion(32, 45, 0, true));

			BlockState blockState = ModBlocks.RustyCauldron.get().defaultBlockState();
			Minecraft mc = Minecraft.getInstance();
			BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
			blockRenderer.renderSingleBlock(blockState, blockStack, mc.renderBuffers().bufferSource(), 15728880, OverlayTexture.NO_OVERLAY);

			blockStack.pushPose();
			blockStack.translate(0.2, -0.18, 0.05);
			blockStack.scale(0.75f, 0.75f, 0.75f);
			BlockState fireState = Blocks.FIRE.defaultBlockState();
			blockRenderer.renderSingleBlock(fireState, blockStack, mc.renderBuffers().bufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
			blockStack.popPose();
			blockStack.popPose();
		}

		// Render the fluid if it exists.
		if (!recipe.getRequiredFluid().isEmpty()) {
			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(recipe.getRequiredFluid());
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(recipe.getRequiredFluid());
			float TEXEL = (1.0f / 16.0f);
			float time = SDMath.clamp(timer.getValue(), 0, timer.getMaxValue() / 2);
			float height = 1.0f;

			if (recipe.shouldDrainCauldron() || !recipe.getOutputFluid().isEmpty()) {
				height = (float) time / (timer.getMaxValue() / 2);
				height = 1.0f - height;
			}

			blockStack.pushPose();
			blockStack.translate(location.getX() + 43, location.getY() + 17.8f, 265);
			blockStack.scale(scale * 16, scale * 16, scale);
			blockStack.mulPose(new Quaternion(32, 45, 0, true));
			BlockModel.drawCubeInWorld(blockStack, new Vector3f(2 * TEXEL, 12 * TEXEL - (9 * TEXEL * height), 2 * TEXEL), new Vector3f(12 * TEXEL, TEXEL * height, 12 * TEXEL),
					fluidColor, sprite, new Vector3D(1.0f, 1.0f, 1.0f));
			blockStack.popPose();
		}

		// Render the output fluid if it exists.
		if (!recipe.getOutputFluid().isEmpty()) {
			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(recipe.getOutputFluid());
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(recipe.getOutputFluid());
			float TEXEL = (1.0f / 16.0f);
			float time = SDMath.clamp(timer.getValue(), timer.getMaxValue() / 2, timer.getMaxValue());
			time -= timer.getMaxValue() / 2;
			float height = (float) time / (timer.getMaxValue() / 2);

			blockStack.pushPose();
			blockStack.translate(location.getX() + 43, location.getY() + 17.7f, 265);
			blockStack.scale(scale * 16, scale * 16, scale);
			blockStack.mulPose(new Quaternion(32, 45, 0, true));
			BlockModel.drawCubeInWorld(blockStack, new Vector3f(2 * TEXEL, 12 * TEXEL - (9 * TEXEL * height), 2 * TEXEL), new Vector3f(12 * TEXEL, TEXEL * height, 12 * TEXEL),
					fluidColor, sprite, new Vector3D(1.0f, 1.0f, 1.0f));
			blockStack.popPose();
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CauldronRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 3, 5).addIngredients(recipe.getInput().getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 24).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());

		// Add the fluids.
		if (!recipe.getRequiredFluid().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.INPUT, 56, 18).addFluidStack(recipe.getRequiredFluid().getFluid(), recipe.getRequiredFluid().getAmount())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getRequiredFluid()), false, 10, 10);
		}
		if (!recipe.getOutputFluid().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 30).addFluidStack(recipe.getOutputFluid().getFluid(), recipe.getOutputFluid().getAmount())
					.setFluidRenderer(getFluidTankDisplaySize(recipe.getOutputFluid()), false, 10, 10);
		}

		// Set the timer.
		int processingTime = SDMath.clamp(recipe.getRequiredTimeInCauldron() / 2, 0, recipe.getRequiredTimeInCauldron());
		timer = guiHelper.createTickTimer(processingTime, processingTime, false);
	}
}
