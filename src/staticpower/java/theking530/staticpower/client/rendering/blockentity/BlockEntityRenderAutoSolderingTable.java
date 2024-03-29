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
import theking530.staticcore.client.StaticPowerBlockEntitySpecialRenderer;
import theking530.staticcore.client.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blockentities.machines.autosolderingtable.BlockEntityAutoSolderingTable;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderAutoSolderingTable extends StaticPowerBlockEntitySpecialRenderer<BlockEntityAutoSolderingTable> {

	public BlockEntityRenderAutoSolderingTable(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityAutoSolderingTable tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.AutoSolderingTable");
		matrixStack.pushPose();
		matrixStack.mulPose(new Quaternion(new Vector3f(0.0f, 0.0f, 0.0f), -90, true));
		int forwardBlockLightLevel = WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity);
		// Render any pattern items.
		for (int i = 0; i < 9; i++) {
			ItemStack stack = tileEntity.patternInventory.getStackInSlot(i);
			float xOffsetFactor = (i % 3) * 0.19f;
			float yOffsetFactor = (i / 3) * 0.19f;
			if (!stack.isEmpty()) {
				// Get the baked model and check if it wants to render the item in 3d or 2d.
				BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, combinedOverlay);
				boolean render3D = itemModel.isGui3d();

				if (render3D) {
					WorldRenderingUtilities.drawItemInWorld(tileEntity, stack, TransformType.GUI, new Vector3D(0.31f + xOffsetFactor, 0.625f - yOffsetFactor, 1.005f),
							new Vector3D(0.12f, 0.12f, 0.02f), partialTicks, matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				} else {
					WorldRenderingUtilities.drawItemInWorld(tileEntity, stack, TransformType.GUI, new Vector3D(0.31f + xOffsetFactor, 0.625f - yOffsetFactor, 1.005f),
							new Vector3D(0.12f, 0.12f, 0.12f), partialTicks, matrixStack, buffer, forwardBlockLightLevel, combinedOverlay);
				}
			}
		}
		matrixStack.popPose();
		Minecraft.getInstance().getProfiler().pop();
	}
}
