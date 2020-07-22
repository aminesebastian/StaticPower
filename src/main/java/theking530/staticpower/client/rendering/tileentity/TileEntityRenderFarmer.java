package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.Vector3D;
import theking530.common.utilities.Vector4D;
import theking530.staticpower.tileentities.powered.basicfarmer.TileEntityBasicFarmer;

public class TileEntityRenderFarmer extends StaticPowerTileEntitySpecialRenderer<TileEntityBasicFarmer> {

	public TileEntityRenderFarmer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityBasicFarmer tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = (float) tileEntity.fluidTankComponent.getFluidAmount() / tileEntity.fluidTankComponent.getCapacity();
			drawFluidQuad(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.185f, 0.25f, 0.001f), new Vector3D(0.125f, filledPercentage * 0.535f * 0.5f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.4f, 1.0f));
			drawFluidQuad(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.69f, 0.25f, 0.001f), new Vector3D(0.125f, filledPercentage * 0.535f * 0.5f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.4f, 1.0f));

			if (filledPercentage >= 0.5f) {
				drawFluidQuad(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.185f, 0.515f, 0.001f), new Vector3D(0.125f, filledPercentage * 0.535f * 0.5f, 1.0f), new Vector4D(1.0f, 1.0f - filledPercentage, 0.6f, 1.0f));
				drawFluidQuad(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.69f, 0.515f, 0.001f), new Vector3D(0.125f, filledPercentage * 0.535f * 0.5f, 1.0f), new Vector4D(1.0f, 1.0f - filledPercentage, 0.6f, 1.0f));
			}
		}
	}
}
