package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.cables.item.ItemRoutingParcelClient;
import theking530.staticpower.cables.item.TileEntityItemCable;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderItemCable extends AbstractCableTileEntityRenderer<TileEntityItemCable> {

	public TileEntityRenderItemCable(BlockEntityRenderDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	protected void renderTileEntityBase(TileEntityItemCable tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		for (ItemRoutingParcelClient packet : tileEntity.cableComponent.getContainedItems()) {
			renderItemRoutingParcel(packet, tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		}
	}
}
