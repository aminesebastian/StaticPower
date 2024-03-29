package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.client.StaticPowerBlockEntitySpecialRenderer;
import theking530.staticcore.client.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.utilities.math.Vector4D;
import theking530.staticpower.blockentities.power.battery.BlockEntityBattery;
import theking530.staticpower.client.StaticPowerSprites;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderBatteryBlock extends StaticPowerBlockEntitySpecialRenderer<BlockEntityBattery> {

	public BlockEntityRenderBatteryBlock(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityBattery tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.Battery");
		// Get the energy percentage.
		float height = (float) StaticPowerEnergyUtilities.getStoredEnergyPercentScaled(tileEntity.powerStorage, 0.955f);

		// Calculate the UV to use when rendering.
		Vector4D uv = new Vector4D(0.0f, 1.0f - height, 1.0f, 1.0f);

		// Move the height into world space.
		height *= 0.55f;

		// For each of the four sides.
		for (Direction dir : Direction.values()) {
			// If this is a top or bottom side, do nothing.
			if (dir.getAxis() == Direction.Axis.Y) {
				continue;
			}

			// Rotate to face the direction.
			matrixStack.pushPose();
			WorldRenderingUtilities.rotateMatrixToFaceSide(dir, matrixStack);

			// Render the bar.
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticPowerSprites.BATTERY_BLOCK_BAR, matrixStack, buffer, new Vector3D(0.237f, 0.238f, -0.03f),
					new Vector3D(0.526f, height, 1.0f), uv, SDColor.WHITE);

			matrixStack.popPose();
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
