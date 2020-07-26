package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.Vector3D;
import theking530.staticpower.tileentities.powered.autosolderingtable.TileEntityAutoSolderingTable;

@SuppressWarnings("deprecation")
public class TileEntityRenderAutoSolderingTable extends StaticPowerTileEntitySpecialRenderer<TileEntityAutoSolderingTable> {

	public TileEntityRenderAutoSolderingTable(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntityAutoSolderingTable tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		matrixStack.rotate(new Quaternion(new Vector3f(0.0f, 0.0f, 0.0f), -90, true));
		int forwardBlockLightLevel = WorldRenderer.getCombinedLight(tileEntity.getWorld(), tileEntity.getPos().offset(tileEntity.getFacingDirection()));
		// Render any pattern items.
		for (int i = 0; i < 9; i++) {
			ItemStack stack = tileEntity.patternInventory.getStackInSlot(i);
			float xOffsetFactor = (i % 3) * 0.19f;
			float yOffsetFactor = (i / 3) * 0.19f;
			if (!stack.isEmpty()) {
				// Get the baked model and check if it wants to render the item in 3d or 2d.
				IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
				boolean render3D = itemModel.isGui3d();

				if (render3D) {
					drawItemInWorld(tileEntity, stack, TransformType.GUI, new Vector3D(0.31f + xOffsetFactor, 0.625f - yOffsetFactor, 1.005f), new Vector3D(0.12f, 0.12f, 0.02f), partialTicks, matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				} else {
					drawItemInWorld(tileEntity, stack, TransformType.GUI, new Vector3D(0.31f + xOffsetFactor, 0.625f - yOffsetFactor, 1.005f), new Vector3D(0.12f, 0.12f, 0.12f), partialTicks, matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				}
			}
		}
		matrixStack.pop();
	}
}
