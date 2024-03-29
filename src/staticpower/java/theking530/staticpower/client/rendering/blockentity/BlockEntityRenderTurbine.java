package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.client.StaticPowerBlockEntitySpecialRenderer;
import theking530.staticcore.client.rendering.BlockModel;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blockentities.power.turbine.BlockEntityTurbine;
import theking530.staticpower.blockentities.power.turbine.BlockEntityTurbine.TurbineRenderingState;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderTurbine extends StaticPowerBlockEntitySpecialRenderer<BlockEntityTurbine> {
	/**
	 * Block renderer to render the blades model.
	 */
	protected static BlockRenderDispatcher blockRenderer;

	public BlockEntityRenderTurbine(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityTurbine tileEntity, BlockPos pos, float partialTicks,
			PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.Turbine");
		// Set the block renderer if we haven't already.
		if (blockRenderer == null) {
			blockRenderer = Minecraft.getInstance().getBlockRenderer();
		}

		// If the model data exists, use it to render the turbine blades.
		if (tileEntity.hasTurbineBlades()) {
			// Get the block state and model.
			BlockState state = tileEntity.getBlockState();
			ResourceLocation modelLocation = tileEntity.getTurbileBladesItem().getInWorldModel();
			BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

			// Get the data.
			ModelData data = tileEntity.getModelData();

			if (data.has(BlockEntityTurbine.TURBINE_RENDERING_STATE)) {
				// Get the thread safe rotation container.
				TurbineRenderingState renderingState = data.get(BlockEntityTurbine.TURBINE_RENDERING_STATE);

				// Increment the rotation (or decrement, that will be handled automatically).
				renderingState.rotate(Minecraft.getInstance().getDeltaFrameTime());

				// Push a new matrix.
				matrixStack.pushPose();

				// Perform the first translation, then rotation (having moved into local space).
				// Repeat this three times.
				matrixStack.translate(0, 0.25, 0);
				matrixStack.translate(0.5, 0, 0.5);
				matrixStack.mulPose(new Quaternion(0, renderingState.rotationAngle, 0, true));
				matrixStack.translate(-0.5, 0, -0.5);
				blockRenderer.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.solid()),
						state, model, 0.0f, 0.0f, 0.0f, combinedLight, combinedOverlay, data, RenderType.cutout());

				matrixStack.translate(0, 0.2, 0);
				matrixStack.translate(0.5, 0, 0.5);
				matrixStack.mulPose(new Quaternion(0, 30, 0, true));
				matrixStack.translate(-0.5, 0, -0.5);
				blockRenderer.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.solid()),
						state, model, 0.0f, 0.0f, 0.0f, combinedLight, combinedOverlay, data, RenderType.cutout());

				matrixStack.translate(0, 0.2, 0);
				matrixStack.translate(0.5, 0, 0.5);
				matrixStack.mulPose(new Quaternion(0, 30, 0, true));
				matrixStack.translate(-0.5, 0, -0.5);
				blockRenderer.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.solid()),
						state, model, 0.0f, 0.0f, 0.0f, combinedLight, combinedOverlay, data, RenderType.cutout());

				matrixStack.translate(0, 0.22, 0);
				matrixStack.translate(0.5, 0, 0.5);
				matrixStack.mulPose(new Quaternion(0, 30, 0, true));
				matrixStack.translate(-0.5, 0, -0.5);
				blockRenderer.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.solid()),
						state, model, 0.0f, 0.0f, 0.0f, combinedLight, combinedOverlay, data, RenderType.cutout());

				// Pop the matrix we pushed.
				matrixStack.popPose();
			}
		}

		// If there is fluid, render it.
		if (!tileEntity.inputFluidTankComponent.getFluid().isEmpty()) {
			// Get the fluid.
			FluidStack fluid = tileEntity.inputFluidTankComponent.getFluid();

			// Get the fluid attributes and add alpha to the fluid.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			fluidColor.setAlpha(0.8f);

			// Get the height and y position.
			float height = tileEntity.inputFluidTankComponent.getVisualFillLevel() * TEXEL * 15;
			float yPosition = 16.0f * TEXEL - (16f * TEXEL * height);

			// Render the fluid.
			BlockModel.drawCubeInWorld(matrixStack, new Vector3f(1 * TEXEL, yPosition - (TEXEL * 1), 1 * TEXEL),
					new Vector3f(14f * TEXEL, 16f * TEXEL * height, 14f * TEXEL), fluidColor, sprite,
					new Vector3D(1.0f, height, 1.0f));
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
