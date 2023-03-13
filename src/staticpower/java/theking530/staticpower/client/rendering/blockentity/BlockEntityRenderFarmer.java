package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.blockentities.machines.cropfarmer.BlockEntityBasicFarmer;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderFarmer extends StaticPowerBlockEntitySpecialRenderer<BlockEntityBasicFarmer> {

	public BlockEntityRenderFarmer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityBasicFarmer tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.Farmer");
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();
			float firstSectionFilledPercent = Math.min(filledPercentage * 2.0f, 1.0f);
			float secondSectionFilledPercentage = SDMath.clamp((filledPercentage - 0.5f) * 2.0f, 0.0f, 1.0f);

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.218f, 0.218f, 0.001f), new Vector3D(0.094f, firstSectionFilledPercent * 0.5625f * 0.5f, 1.0f),
					new Vector4D(0.0f, 1.0f - firstSectionFilledPercent, 0.4f, 1.0f), WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));
			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.688f, 0.218f, 0.001f), new Vector3D(0.094f, firstSectionFilledPercent * 0.5625f * 0.5f, 1.0f),
					new Vector4D(0.0f, 1.0f - firstSectionFilledPercent, 0.4f, 1.0f), WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));

			if (filledPercentage >= 0.5f) {
				WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.218f, 0.499f, 0.001f), new Vector3D(0.094f, secondSectionFilledPercentage * 0.57f * 0.5f, 1.0f),
						new Vector4D(1.0f, 1.0f - secondSectionFilledPercentage, 0.6f, 1.0f), WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));
				WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.688f, 0.499f, 0.001f), new Vector3D(0.094f, secondSectionFilledPercentage * 0.57f * 0.5f, 1.0f),
						new Vector4D(1.0f, 1.0f - secondSectionFilledPercentage, 0.6f, 1.0f), WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));
			}
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
