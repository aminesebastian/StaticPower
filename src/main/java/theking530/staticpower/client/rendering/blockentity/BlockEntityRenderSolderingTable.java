package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.nonpowered.solderingtable.BlockEntitySolderingTable;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderSolderingTable extends StaticPowerBlockEntitySpecialRenderer<BlockEntitySolderingTable> {

	public BlockEntityRenderSolderingTable(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntitySolderingTable tileEntity, BlockPos pos, float partialTicks,
			PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		matrixStack.mulPose(new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -90, true));

		// Render any pattern items.
		for (int i = 0; i < 9; i++) {
			ItemStack stack = tileEntity.patternInventory.getStackInSlot(i);
			float xOffsetFactor = (i % 3) * 0.15f;
			float yOffsetFactor = (i / 3) * 0.15f;
			if (!stack.isEmpty()) {
				// Get the baked model and check if it wants to render the item in 3d or 2d.
				BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(stack, null,
						null, combinedOverlay);
				boolean render3D = itemModel.isGui3d();

				if (render3D) {
					WorldRenderingUtilities.drawItemInWorld(tileEntity, stack, TransformType.GUI,
							new Vector3D(0.35f + xOffsetFactor, -0.38f - yOffsetFactor, 0.88f),
							new Vector3D(0.135f, 0.135f, 0.03f), partialTicks, matrixStack, buffer, combinedLight,
							combinedOverlay);
				} else {
					WorldRenderingUtilities.drawItemInWorld(tileEntity, stack, TransformType.GUI,
							new Vector3D(0.35f + xOffsetFactor, -0.38f - yOffsetFactor, 0.88f),
							new Vector3D(0.125f, 0.125f, 0.125f), partialTicks, matrixStack, buffer, combinedLight,
							combinedOverlay);
				}
			}
		}
		matrixStack.popPose();

		// Render the soldering iron.
		if (!tileEntity.solderingIronInventory.getStackInSlot(0).isEmpty()) {
			WorldRenderingUtilities.drawItemInWorld(tileEntity, tileEntity.solderingIronInventory.getStackInSlot(0), TransformType.FIXED,
					new Vector3D(0.2f, 1.07f, 0.185f), new Vector3D(0.3f, 0.3f, 0.3f), new Vector3D(0, 0, 30), partialTicks, matrixStack,
					buffer, combinedLight, combinedOverlay);
		}
	}
}
