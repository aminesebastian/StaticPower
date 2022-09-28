package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.cables.item.ItemRoutingParcelClient;
import theking530.staticpower.cables.item.BlockEntityItemCable;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderItemCable extends AbstractCableTileEntityRenderer<BlockEntityItemCable> {

	public BlockEntityRenderItemCable(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderTileEntityBase(BlockEntityItemCable tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.ItemCable");
		for (ItemRoutingParcelClient packet : tileEntity.cableComponent.getContainedItems()) {
			renderItemRoutingParcel(packet, tileEntity, pos, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
