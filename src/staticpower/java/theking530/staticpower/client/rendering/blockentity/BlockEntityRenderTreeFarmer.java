package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.client.StaticPowerBlockEntitySpecialRenderer;
import theking530.staticcore.client.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.utilities.math.Vector4D;
import theking530.staticpower.blockentities.machines.treefarmer.BlockEntityTreeFarm;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderTreeFarmer extends StaticPowerBlockEntitySpecialRenderer<BlockEntityTreeFarm> {

	public BlockEntityRenderTreeFarmer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityTreeFarm tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.TreeFarmer");
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();

			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.28f, 0.3125f, 0.001f),
					new Vector3D(0.44f * 0.5f, filledPercentage * 0.03f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.6f, 0.1f),
					WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));
			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.5f, 0.3125f, 0.001f),
					new Vector3D(0.44f * 0.5f, filledPercentage * 0.03f, 1.0f), new Vector4D(1.0f, 1.0f - filledPercentage, 0.4f, 0.1f),
					WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));

			if (filledPercentage >= 0.5f) {
				WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.278f, 0.564f, 0.001f),
						new Vector3D(0.065f, filledPercentage * 0.187f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.3f, 1.0f), WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));
				WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.655f, 0.564f, 0.001f),
						new Vector3D(0.065f, filledPercentage * 0.187f, 1.0f), new Vector4D(0.0f, 1.0f - filledPercentage, 0.3f, 1.0f), WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));
			}
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
