package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.tileentities.nonpowered.solderingtable.TileEntitySolderingTable;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderSolderingTable extends StaticPowerTileEntitySpecialRenderer<TileEntitySolderingTable> {

	public TileEntityRenderSolderingTable(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntitySolderingTable tileEntity, BlockPos pos, float partialTicks,
			MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		matrixStack.rotate(new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -90, true));

		// Render any pattern items.
		for (int i = 0; i < 9; i++) {
			ItemStack stack = tileEntity.patternInventory.getStackInSlot(i);
			float xOffsetFactor = (i % 3) * 0.15f;
			float yOffsetFactor = (i / 3) * 0.15f;
			if (!stack.isEmpty()) {
				// Get the baked model and check if it wants to render the item in 3d or 2d.
				IBakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null,
						null);
				boolean render3D = itemModel.isGui3d();

				if (render3D) {
					drawItemInWorld(tileEntity, stack, TransformType.GUI,
							new Vector3D(0.35f + xOffsetFactor, -0.38f - yOffsetFactor, 0.88f),
							new Vector3D(0.135f, 0.135f, 0.03f), partialTicks, matrixStack, buffer, combinedLight,
							combinedOverlay);
				} else {
					drawItemInWorld(tileEntity, stack, TransformType.GUI,
							new Vector3D(0.35f + xOffsetFactor, -0.38f - yOffsetFactor, 0.88f),
							new Vector3D(0.125f, 0.125f, 0.125f), partialTicks, matrixStack, buffer, combinedLight,
							combinedOverlay);
				}
			}
		}
		matrixStack.pop();

		// Render the soldering iron.
		if (!tileEntity.solderingIronInventory.getStackInSlot(0).isEmpty()) {
			drawItemInWorld(tileEntity, tileEntity.solderingIronInventory.getStackInSlot(0), TransformType.FIXED,
					new Vector3D(0.25f, 1.05f, 0.185f), new Vector3D(0.3f, 0.3f, 0.3f), partialTicks, matrixStack,
					buffer, combinedLight, combinedOverlay);
		}
	}
}
