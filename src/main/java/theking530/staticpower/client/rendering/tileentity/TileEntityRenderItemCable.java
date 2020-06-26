package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.cables.item.ItemRoutingParcelClient;
import theking530.staticpower.cables.item.TileEntityItemCable;

public class TileEntityRenderItemCable extends AbstractCableTileEntityRenderer<TileEntityItemCable> {

	public TileEntityRenderItemCable(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	protected void renderTileEntityBase(TileEntityItemCable tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		for (ItemRoutingParcelClient packet : tileEntity.cableComponent.getContainedItems()) {
			renderItemRoutingParcel(packet, tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		}
	}
}
