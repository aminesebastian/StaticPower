package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.tileentities.powered.squeezer.TileEntitySqueezer;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderSqueezer extends StaticPowerTileEntitySpecialRenderer<TileEntitySqueezer> {

	public TileEntityRenderSqueezer(BlockEntityRenderDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void renderTileEntityBase(TileEntitySqueezer tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();
			WorldRenderingUtilities.drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.25f, 0.219f, 0.001f), new Vector3D(0.25f, filledPercentage * 0.22f, 1.0f),
					new Vector4D(0.0f, 1.0f - filledPercentage, 1.0f, 1.0f));
			WorldRenderingUtilities.drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.5f, 0.219f, 0.001f), new Vector3D(0.25f, filledPercentage * 0.22f, 1.0f),
					new Vector4D(1.0f, 1.0f - filledPercentage, 0.0f, 1.0f));

			float processingPercentage = ((float) tileEntity.processingComponent.getCurrentProcessingTime() + (partialTicks / 20.0f)) / (float) tileEntity.processingComponent.getMaxProcessingTime();
			WorldRenderingUtilities.drawFluidQuadUnlit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.44f, 0.75f - processingPercentage * 0.5f, 0.001f),
					new Vector3D(0.12f, processingPercentage * 0.5f, 1.0f), new Vector4D(0.0f, 1.0f - processingPercentage, 0.12f, 1.0f));
		}
	}
}
