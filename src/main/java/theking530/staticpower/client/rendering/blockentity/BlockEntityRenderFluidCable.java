package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.cables.fluid.BlockEntityFluidCable;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderFluidCable extends AbstractCableTileEntityRenderer<BlockEntityFluidCable> {

	public BlockEntityRenderFluidCable(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(BlockEntityFluidCable tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.FluidCable");
		if (tileEntity.fluidCableComponent.getFilledPercentage() > 0 && !tileEntity.fluidCableComponent.getFluidInTank(0).isEmpty()) {
			drawFluidCable(tileEntity.fluidCableComponent.getFluidInTank(0), tileEntity.fluidCableComponent.getVisualFilledPercentage(), tileEntity.fluidRenderRadius, matrixStack,
					tileEntity.fluidCableComponent);
		}

//		for (Direction dir : tileEntity.fluidCableComponent.flowMap.keySet()) {
//			float flowAmount = tileEntity.fluidCableComponent.flowMap.get(dir);
//			if (flowAmount == 0) {
//				continue;
//			}
//
//			Vector3D position = new Vector3D(0.5f, 1.0f, 0.5f);
//			Vector3D offset = new Vector3D(dir.getNormal());
//			offset.multiply(0.25f);
//			position.add(offset);
//
//			matrixStack.pushPose();
//			matrixStack.translate(position.getX(), position.getY(), position.getZ());
//			float playerRot = Minecraft.getInstance().player.getViewYRot(1.0f);
//			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -playerRot + 180, true));
//			matrixStack.translate(-position.getX(), -position.getY(), -position.getZ());
//
//			WorldRenderingUtilities.drawTextInWorld(this.blockRenderer, GuiTextUtilities.formatNumberAsString(flowAmount).getString(), SDColor.EIGHT_BIT_WHITE, position,
//					0.007f, matrixStack, buffer, combinedLight, combinedOverlay);
//			matrixStack.popPose();
//		}

		Minecraft.getInstance().getProfiler().pop();
	}

	@Override
	public boolean shouldRenderOffScreen(BlockEntityFluidCable p_112138_) {
		return false;
	}

	@Override
	public int getViewDistance() {
		return 48;
	}
}
