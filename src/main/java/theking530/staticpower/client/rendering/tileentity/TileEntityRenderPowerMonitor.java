package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.StaticPowerEnergyTextUtilities;
import theking530.api.energy.metrics.MetricsTimeUnit;
import theking530.staticcore.rendering.WorldLineGraphRenderer;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.powered.powermonitor.TileEntityPowerMonitor;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderPowerMonitor extends StaticPowerTileEntitySpecialRenderer<TileEntityPowerMonitor> {
	private WorldLineGraphRenderer graphRenderer;

	public TileEntityRenderPowerMonitor(BlockEntityRendererProvider.Context context) {
		super(context);
		graphRenderer = new WorldLineGraphRenderer(0.02f, 13, 7);
	}

	@Override
	public void renderTileEntityBase(TileEntityPowerMonitor tileEntity, BlockPos pos, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		if (tileEntity.getMetrics().getData(MetricsTimeUnit.TICKS).getInputValues().size() <= 1) {
			return;
		}

		// Render the bar.
		matrixStack.pushPose();
		matrixStack.translate(0.09375f, 0.284f, 0.725f);

		matrixStack.pushPose();
		matrixStack.scale(0.8125f, 0.433f, 1.0f);

		graphRenderer.addUpdateData("Received", tileEntity.getRecievedData());
		graphRenderer.addUpdateData("Provided", tileEntity.getProvidedData());
		graphRenderer.render(partialTicks, matrixStack, buffer, combinedLight);

		matrixStack.popPose();

		double maxValue = tileEntity.getRecievedData().getMinMaxValues().getY();
		double minValue = tileEntity.getProvidedData().getMinMaxValues().getX();

		// Draw the max value.
		WorldRenderingUtilities.drawTextInWorld(this.renderer, StaticPowerEnergyTextUtilities.formatPowerRateToString(maxValue).getString(), tileEntity,
				new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.07f, 0.43f, 0.025f), 0.0035f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		// Draw the min value.
		WorldRenderingUtilities.drawTextInWorld(this.renderer, StaticPowerEnergyTextUtilities.formatPowerRateToString(minValue).getString(), tileEntity,
				new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.07f, 0.04f, 0.025f), 0.0035f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		// Draw the current value.
		WorldRenderingUtilities.drawTextInWorld(this.renderer,
				StaticPowerEnergyTextUtilities.formatPowerRateToString(tileEntity.getMetrics().getData(MetricsTimeUnit.TICKS).getInputValues().peekLast()).getString(), tileEntity,
				new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.74f, 0.2145f, 0.025f), 0.0035f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		matrixStack.popPose();
	}
}
