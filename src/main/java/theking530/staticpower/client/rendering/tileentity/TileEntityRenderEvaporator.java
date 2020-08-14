package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.Vector3D;
import theking530.common.utilities.Vector4D;
import theking530.staticpower.tileentities.nonpowered.evaporator.TileEntityEvaporator;

public class TileEntityRenderEvaporator extends StaticPowerTileEntitySpecialRenderer<TileEntityEvaporator> {

	public TileEntityRenderEvaporator(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityEvaporator tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.inputTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.inputTankComponent.getVisualFillLevel();

			drawFluidQuadUnlit(tileEntity.inputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.219f, 0.185f, 0.001f), new Vector3D(0.282f, filledPercentage * 0.254f * 0.5f, 1.0f),
					new Vector4D(0.0f, 0.15f - (filledPercentage * 0.15f), 0.4f, 0.25f));

			drawFluidQuadUnlit(tileEntity.inputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.5f, 0.185f, 0.001f), new Vector3D(0.282f, filledPercentage * 0.254f * 0.5f, 1.0f),
					new Vector4D(0.0f, 0.15f - (filledPercentage * 0.15f), 0.4f, 0.25f));
		}
		if (!tileEntity.outputTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.outputTankComponent.getVisualFillLevel();

			drawFluidQuadUnlit(tileEntity.outputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.219f, 0.531f, 0.001f), new Vector3D(0.282f, filledPercentage * 0.565f * 0.5f, 1.0f),
					new Vector4D(0.0f, 0.15f - (filledPercentage * 0.15f), 0.4f, 0.25f));

			drawFluidQuadUnlit(tileEntity.outputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.5f, 0.531f, 0.001f), new Vector3D(0.282f, filledPercentage * 0.565f * 0.5f, 1.0f),
					new Vector4D(0.0f, 0.15f - (filledPercentage * 0.15f), 0.4f, 0.25f));
		}
	}
}
