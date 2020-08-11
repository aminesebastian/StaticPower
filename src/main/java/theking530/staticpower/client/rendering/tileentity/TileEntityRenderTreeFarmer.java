package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.Vector3D;
import theking530.common.utilities.Vector4D;
import theking530.staticpower.tileentities.powered.treefarmer.TileEntityTreeFarm;

public class TileEntityRenderTreeFarmer extends StaticPowerTileEntitySpecialRenderer<TileEntityTreeFarm> {

	public TileEntityRenderTreeFarmer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityTreeFarm tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();

			drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.28f, 0.3125f, 0.001f), new Vector3D(0.44f * 0.5f, filledPercentage * 0.03f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.6f, 0.1f));
			drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.5f, 0.3125f, 0.001f), new Vector3D(0.44f * 0.5f, filledPercentage * 0.03f, 1.0f), new Vector4D(1.0f, 1.0f - filledPercentage, 0.4f, 0.1f));

			if (filledPercentage >= 0.5f) {
				drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.278f, 0.564f, 0.001f), new Vector3D(0.065f, filledPercentage * 0.187f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.3f, 1.0f));
				drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.655f, 0.564f, 0.001f), new Vector3D(0.065f, filledPercentage * 0.187f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.3f, 1.0f));
			}
		}
	}
}
