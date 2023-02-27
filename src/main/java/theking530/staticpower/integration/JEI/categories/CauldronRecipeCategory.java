package theking530.staticpower.integration.JEI.categories;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.SDMath;
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
		background = guiHelper.createBlankDrawable(140, 60);
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

	@Override
	public void draw(CauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 4, 6, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 105, 22, 0);

		arrow.setPosition(23, 6);
		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		arrow.setPosition(80, 25);
		arrow.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		// Render the time.
		GuiDrawUtilities.drawStringCentered(matrixStack, "Cook time: " + recipe.getRequiredTimeInCauldron(), 65, 4, 0.0f, 0.8f, SDColor.EIGHT_BIT_YELLOW, true);
		matrixStack.pushPose();

		// Setup the perspective
		matrixStack.translate(41, 44, 150);
		matrixStack.mulPose(new Quaternion(-30, 45, 0, true));

		// Render the block.
		Lighting.setupForEntityInInventory();
		GuiDrawUtilities.drawBlockState(matrixStack, ModBlocks.RustyCauldron.get().defaultBlockState(), BlockPos.ZERO, ModelData.EMPTY, new Vector3D(0, 0, 0),
				new Vector3D(0, 90, 0), new Vector3D(26, -26, 26));

		float smoothedTime = (float) Math.pow(((float) timer.getValue() / timer.getMaxValue()), 2);
		if (!recipe.getRequiredFluid().isEmpty()) {
			FluidStack fluid = getNthFluidInput(recipeSlotsView, 0);
			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			float height = 1.0f;
			if (recipe.shouldDrainAfterCraft() || !recipe.getOutputFluid().isEmpty()) {
				height = 1.0f - smoothedTime;
			}

			matrixStack.pushPose();
			matrixStack.translate(21f, -16 - (height * 9), 5);
			matrixStack.scale(-18, 18 * height, 18);

			RenderSystem.enableDepthTest();
			BlockModel.drawCubeInGui(matrixStack, Vector3f.ZERO, new Vector3f(1, 1, 1), fluidColor, sprite, new Vector3D(1.0f, 1.0f, 1.0f));
			RenderSystem.disableDepthTest();
			matrixStack.popPose();
		}

		// Render the output fluid if it exists.
		if (!recipe.getOutputFluid().isEmpty()) {
			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(recipe.getOutputFluid());
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(recipe.getOutputFluid());
			float height = smoothedTime;
			matrixStack.pushPose();
			matrixStack.translate(21f, -12 - (height * 12), 5);
			matrixStack.scale(-18, 18 * height, 18);

			RenderSystem.enableDepthTest();
			BlockModel.drawCubeInGui(matrixStack, Vector3f.ZERO, new Vector3f(1, 1, 1), fluidColor, sprite, new Vector3D(1.0f, 1.0f, 1.0f));
			RenderSystem.disableDepthTest();
			matrixStack.popPose();
		}
		matrixStack.popPose();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CauldronRecipe recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 4, 6).addIngredients(recipe.getInput().getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 24).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutput());

		// Revisit this later with a custom renderer.
		// Add the fluids.
		if (!recipe.getRequiredFluid().isEmpty()) {
			addFluidIngredientSlot(builder, RecipeIngredientRole.INPUT, 51, 18, 16, 16, recipe.getRequiredFluid()).setFluidRenderer(100, false, 16, 16);
		}
		if (!recipe.getOutputFluid().isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 51, 29).addFluidStack(recipe.getOutputFluid().getFluid(), recipe.getOutputFluid().getAmount()).setFluidRenderer(100, false,
					16, 16);
		}

		// Set the timer.
		int processingTime = SDMath.clamp(recipe.getRequiredTimeInCauldron() / 2, 0, recipe.getRequiredTimeInCauldron());
		timer = guiHelper.createTickTimer(processingTime / 2, processingTime, false);
	}
}
