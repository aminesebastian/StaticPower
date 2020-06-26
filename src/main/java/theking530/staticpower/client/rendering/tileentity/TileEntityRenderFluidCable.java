package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.cables.fluid.TileEntityFluidCable;

public class TileEntityRenderFluidCable extends AbstractCableTileEntityRenderer<TileEntityFluidCable> {

	public TileEntityRenderFluidCable(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	protected void renderTileEntityBase(TileEntityFluidCable tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (tileEntity.fluidCableComponent.getFilledPercentage() > 0 && !tileEntity.fluidCableComponent.getFluidInTank(0).isEmpty()) {
			drawFluidCable(tileEntity.fluidCableComponent.getFluidInTank(0), tileEntity.fluidCableComponent.getFilledPercentage(), matrixStack, tileEntity.fluidCableComponent);
		}
	}

}
