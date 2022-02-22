package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.cables.fluid.TileEntityFluidCable;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderFluidCable extends AbstractCableTileEntityRenderer<TileEntityFluidCable> {

	public TileEntityRenderFluidCable(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(TileEntityFluidCable tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (tileEntity.fluidCableComponent.getFilledPercentage() > 0 && !tileEntity.fluidCableComponent.getFluidInTank(0).isEmpty()) {
			drawFluidCable(tileEntity.fluidCableComponent.getFluidInTank(0), tileEntity.fluidCableComponent.getVisualFilledPercentage(), tileEntity.fluidRenderRadius, matrixStack, tileEntity.fluidCableComponent);
		}
	}

}
