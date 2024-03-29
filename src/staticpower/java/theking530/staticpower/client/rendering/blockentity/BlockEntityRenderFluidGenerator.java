package theking530.staticpower.client.rendering.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.client.StaticPowerBlockEntitySpecialRenderer;
import theking530.staticcore.client.rendering.WorldRenderingUtilities;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.utilities.math.Vector4D;
import theking530.staticpower.blockentities.power.fluidgenerator.BlockEntityFluidGenerator;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderFluidGenerator extends StaticPowerBlockEntitySpecialRenderer<BlockEntityFluidGenerator> {

	public BlockEntityRenderFluidGenerator(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityFluidGenerator tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.FluidGenerator");
		// Draw the fluid bar.
		if (!tileEntity.fluidTankComponent.isEmpty()) {
			float filledPercentage = tileEntity.fluidTankComponent.getVisualFillLevel();
			float firstSectionFilledPercent = Math.min(filledPercentage * 2.0f, 1.0f);
			float secondSectionFilledPercentage = SDMath.clamp((filledPercentage - 0.5f) * 2.0f, 0.0f, 1.0f);

			// Draw the bottom part of the bar.
			WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.2175f, 0.185f, 0.001f),
					new Vector3D(0.125f, firstSectionFilledPercent * 0.63f * 0.5f, 1.0f), new Vector4D(0.0f, 1.0f - firstSectionFilledPercent, 0.4f, 1.0f),
					WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));

			// Draw the top part of the bar.
			if (filledPercentage >= 0.5f) {
				WorldRenderingUtilities.drawFluidQuadLit(tileEntity.fluidTankComponent.getFluid(), matrixStack, buffer, new Vector3D(0.2175f, 0.5f, 0.001f),
						new Vector3D(0.125f, secondSectionFilledPercentage * 0.627f * 0.5f, 1.0f), new Vector4D(1.0f, 1.0f - secondSectionFilledPercentage, 0.6f, 1.0f),
						WorldRenderingUtilities.getForwardFacingLightLevel(tileEntity));
			}
		}

		// Draw the empty power bar.
		WorldRenderingUtilities.drawTexturedQuadUnlit(StaticCoreSprites.GUI_POWER_BAR_BG, matrixStack, buffer, new Vector3D(0.657f, 0.18f, 0.0001f), new Vector3D(0.125f, 0.635f, 1.0f),
				new Vector4D(0.0f, 0.0f, 1.0f, 1.0f), SDColor.WHITE);

		// Draw the filled power bar.
		if (tileEntity.powerStorage.getStoredPower() > 0) {
			// Render the power bar.
			float height = (float) StaticPowerEnergyUtilities.getStoredEnergyPercentScaled(tileEntity.powerStorage, 1.0f);
			Vector4D uv = new Vector4D(0.0f, 1.0f - height, 1.0f, 1.0f);
			WorldRenderingUtilities.drawTexturedQuadUnlit(StaticCoreSprites.GUI_POWER_BAR_FG, matrixStack, buffer, new Vector3D(0.657f, 0.18f, 0.0005f), new Vector3D(0.125f, height * 0.635f, 1.0f),
					uv, SDColor.WHITE);
		}
		Minecraft.getInstance().getProfiler().pop();
	}
}
