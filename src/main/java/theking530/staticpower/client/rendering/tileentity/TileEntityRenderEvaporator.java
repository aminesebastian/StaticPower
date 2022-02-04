package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.tileentities.nonpowered.evaporator.TileEntityEvaporator;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderEvaporator extends StaticPowerTileEntitySpecialRenderer<TileEntityEvaporator> {

	public TileEntityRenderEvaporator(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(TileEntityEvaporator tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		int forwardBlockLightLevel = LevelRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().relative(tileEntity.getFacingDirection()));

		if (!tileEntity.inputTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.inputTankComponent.getVisualFillLevel();

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.inputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.219f, 0.185f, 0.001f), new Vector3D(0.16f, filledPercentage * 0.254f * 0.5f, 1.0f),
					new Vector4D(0.0f, 1.0f - filledPercentage, 1.0f, 1.0f), forwardBlockLightLevel);

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.inputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.379f, 0.185f, 0.001f), new Vector3D(0.16f, filledPercentage * 0.254f * 0.5f, 1.0f),
					new Vector4D(1.0f, 1.0f - filledPercentage, 0.0f, 1.0f), forwardBlockLightLevel);

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.inputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.539f, 0.185f, 0.001f), new Vector3D(0.16f, filledPercentage * 0.254f * 0.5f, 1.0f),
					new Vector4D(1.0f, 1.0f - filledPercentage, 0.0f, 1.0f), forwardBlockLightLevel);
			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.inputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.699f, 0.185f, 0.001f), new Vector3D(0.09f, filledPercentage * 0.254f * 0.5f, 1.0f),
					new Vector4D(0.0f, 1.0f - filledPercentage, 0.7f, 1.0f), forwardBlockLightLevel);
		}
		if (!tileEntity.outputTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.outputTankComponent.getVisualFillLevel();

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.outputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.219f, 0.815f - (0.283f * filledPercentage), 0.001f),
					new Vector3D(0.282f, filledPercentage * 0.565f * 0.5f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 1.0f, 1.0f), forwardBlockLightLevel);

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.outputTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.5f, 0.815f - (0.283f * filledPercentage), 0.001f),
					new Vector3D(0.282f, filledPercentage * 0.565f * 0.5f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 1.0f, 1.0f), forwardBlockLightLevel);
		}
	}
}
