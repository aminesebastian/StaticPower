package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.tileentities.cables.item.ItemRoutingPacket;
import theking530.staticpower.tileentities.cables.item.TileEntityItemCable;

@SuppressWarnings("deprecation")
public class TileEntityRenderItemCable extends StaticPowerTileEntitySpecialRenderer<TileEntityItemCable> {
	private static final Vector3f ITEM_RENDER_SCALE = new Vector3f(0.3f, 0.3f, 0.3f);

	public TileEntityRenderItemCable(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);

	}

	@Override
	protected void renderTileEntityBase(TileEntityItemCable tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		for (ItemRoutingPacket packet : tileEntity.cableComponent.getContainedItems()) {
			Direction dir = packet.getItemAnimationDirection();
			float lerpValue = packet.getItemMoveLerp();
			matrixStack.push();
			drawItemInWorld(tileEntity, packet.getContainedItem(), TransformType.FIXED, getAnimationOffset(lerpValue, dir), ITEM_RENDER_SCALE, partialTicks, matrixStack, buffer, combinedLight,
					combinedOverlay);
			matrixStack.pop();
		}
	}

	protected Vector3f getAnimationOffset(float lerpValue, Direction dir) {
		Vector3f baseOffset = new Vector3f(0.5f, 0.5f, 0.5f);
		Vector3f directionVector = new Vector3f(dir.getXOffset(), dir.getYOffset(), dir.getZOffset());
		directionVector.mul(0.5f);
		directionVector.mul(lerpValue);
		baseOffset.add(directionVector);
		return baseOffset;
	}
}
