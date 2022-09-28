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
