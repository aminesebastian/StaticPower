package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.tileentities.powered.squeezer.TileEntitySqueezer;

public class TileEntityRenderSqueezer extends StaticPowerTileEntitySpecialRenderer<TileEntitySqueezer> {

	public TileEntityRenderSqueezer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntitySqueezer tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();
			drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.25f, 0.219f, 0.001f), new Vector3D(0.25f, filledPercentage * 0.22f, 1.0f),
					new Vector4D(0.0f, 1.0f - filledPercentage, 1.0f, 1.0f));
			drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.5f, 0.219f, 0.001f), new Vector3D(0.25f, filledPercentage * 0.22f, 1.0f),
					new Vector4D(1.0f, 1.0f - filledPercentage, 0.0f, 1.0f));

			float processingPercentage = ((float) tileEntity.processingComponent.getCurrentProcessingTime() + (partialTicks / 20.0f)) / (float) tileEntity.processingComponent.getMaxProcessingTime();
			drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.44f, 0.75f - processingPercentage * 0.5f, 0.001f),
					new Vector3D(0.12f, processingPercentage * 0.5f, 1.0f), new Vector4D(0.0f, 1.0f - processingPercentage, 0.12f, 1.0f));
		}
	}
}
