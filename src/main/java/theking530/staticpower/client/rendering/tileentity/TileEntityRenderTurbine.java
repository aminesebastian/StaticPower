package theking530.staticpower.client.rendering.tileentity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.BlockModel;
import theking530.staticpower.tileentities.powered.turbine.TileEntityTurbine;
import theking530.staticpower.tileentities.powered.turbine.TileEntityTurbine.TurbineRenderingState;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderTurbine extends StaticPowerTileEntitySpecialRenderer<TileEntityTurbine> {
	/**
	 * Fluid rendering model.
	 */
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	/**
	 * Block renderer to render the blades model.
	 */
	protected static BlockRendererDispatcher blockRenderer;

	public TileEntityRenderTurbine(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityTurbine tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight,
			int combinedOverlay) {

		// Set the block renderer if we haven't already.
		if (blockRenderer == null) {
			blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
		}

		// Get the rendering safe world.
		IBlockDisplayReader world = MinecraftForgeClient.getRegionRenderCacheOptional(tileEntity.getWorld(), pos).map(IBlockDisplayReader.class::cast).orElseGet(() -> tileEntity.getWorld());

		// Get the block state, model, and model data.
		BlockState state = world.getBlockState(pos);
		IBakedModel model = Minecraft.getInstance().getModelManager().getModel(StaticPowerAdditionalModels.TURBINE_BLADES);
		IModelData data = model.getModelData(tileEntity.getWorld(), pos, tileEntity.getWorld().getBlockState(pos), ModelDataManager.getModelData(tileEntity.getWorld(), pos));

		// If the model data exists, use it to render the turbine blades.
		if (data.hasProperty(TileEntityTurbine.TURBINE_RENDERING_STATE)) {
			// Get the thread safe rotation container.
			TurbineRenderingState renderingState = data.getData(TileEntityTurbine.TURBINE_RENDERING_STATE);

			// Update the delta time.
			float currentTime = Animation.getWorldTime(Minecraft.getInstance().world, partialTicks);
			float delta = currentTime - renderingState.lastUpdateTime;
			renderingState.lastUpdateTime = currentTime;

			// Increment the rotation (or decrement, that will be handled automatically).
			renderingState.rotate(delta);

			// Push a new matrix.
			matrixStack.push();

			// Perform the first translation, then rotation (having moved into local space).
			// Repeat this three times.
			matrixStack.translate(0, 0.25, 0);
			matrixStack.translate(0.5, 0, 0.5);
			matrixStack.rotate(new Quaternion(0, renderingState.rotationAngle, 0, true));
			matrixStack.translate(-0.5, 0, -0.5);
			blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, matrixStack, buffer.getBuffer(RenderType.getSolid()), false, new Random(), 42, combinedLight, data);

			matrixStack.translate(0, 0.2, 0);
			matrixStack.translate(0.5, 0, 0.5);
			matrixStack.rotate(new Quaternion(0, 30, 0, true));
			matrixStack.translate(-0.5, 0, -0.5);
			blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, matrixStack, buffer.getBuffer(RenderType.getSolid()), false, new Random(), 42, combinedLight, data);
			
			matrixStack.translate(0, 0.2, 0);
			matrixStack.translate(0.5, 0, 0.5);
			matrixStack.rotate(new Quaternion(0, 30, 0, true));
			matrixStack.translate(-0.5, 0, -0.5);
			blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, matrixStack, buffer.getBuffer(RenderType.getSolid()), false, new Random(), 42, combinedLight, data);

			matrixStack.translate(0, 0.2, 0);
			matrixStack.translate(0.5, 0, 0.5);
			matrixStack.rotate(new Quaternion(0, 30, 0, true));
			matrixStack.translate(-0.5, 0, -0.5);
			blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, matrixStack, buffer.getBuffer(RenderType.getSolid()), false, new Random(), 42, combinedLight, data);

			// Pop the matrix we pushed.
			matrixStack.pop();
		}

		// If there is fluid, render it.
		if (!tileEntity.inputFluidTankComponent.getFluid().isEmpty()) {
			// Get the fluid.
			FluidStack fluid = tileEntity.inputFluidTankComponent.getFluid();

			// Get the fluid attributes and add alpha to the fluid.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			fluidColor.setW(0.3f);

			// Get the height and y position.
			float height = tileEntity.inputFluidTankComponent.getVisualFillLevel();
			float yPosition = 16.0f * TEXEL - (16f * TEXEL * height);

			// Render the fluid.
			CUBE_MODEL.drawPreviewCube(new Vector3f(1 * TEXEL, yPosition - 0.08f, 1 * TEXEL), new Vector3f(14f * TEXEL, 16f * TEXEL * height, 14f * TEXEL), fluidColor, matrixStack, sprite,
					new Vector2D(1.0f, height));
		}
	}
}
