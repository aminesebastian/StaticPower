package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.cables.item.TileEntityItemCable;

@SuppressWarnings("deprecation")
public class TileEntityRenderItemCable extends StaticPowerTileEntitySpecialRenderer<TileEntityItemCable> {

	public TileEntityRenderItemCable(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);

	}

	@Override
	protected void renderTileEntityBase(TileEntityItemCable tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.pipeInventory.getStackInSlot(0).isEmpty()) {
			float xOffset = tileEntity.getItemMovePercent();
			Vector3f offset = new Vector3f(xOffset, 0.5f, 0.5f);
			Vector3f scale = new Vector3f(0.3f, 0.3f, 0.3f);
			drawItemInWorld(tileEntity, tileEntity.pipeInventory.getStackInSlot(0), TransformType.FIXED, offset, scale, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		}
	}
}
