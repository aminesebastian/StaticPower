package theking530.staticpower.client.rendering.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.DataGraphWidget.FloatGraphDataSet;
import theking530.staticcore.rendering.WorldLineGraphRenderer;
import theking530.staticcore.rendering.WorldRenderingUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.power.PowerTransferMetrics.MetricCategory;
import theking530.staticpower.tileentities.powered.powermonitor.TileEntityPowerMonitor;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderPowerMonitor extends StaticPowerTileEntitySpecialRenderer<TileEntityPowerMonitor> {
	private WorldLineGraphRenderer graphRenderer;

	public TileEntityRenderPowerMonitor(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		graphRenderer = new WorldLineGraphRenderer(0.02f, 13f, 7f);
	}

	@Override
	public void renderTileEntityBase(TileEntityPowerMonitor tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight,
			int combinedOverlay) {
		if (tileEntity.getMetrics().getData(MetricCategory.TICKS).getInputValues().size() <= 1) {
			return;
		}

		// Render the bar.
		matrixStack.push();
		matrixStack.translate(0.09375f, 0.284f, 0.725f);

		matrixStack.push();
		matrixStack.scale(0.8125f, 0.433f, 1.0f);
		
		FloatGraphDataSet recievedData = new FloatGraphDataSet(new Color(0.1f, 1.0f, 0.2f, 0.75f), tileEntity.getMetrics().getData(MetricCategory.SECONDS).getInputValues());
		FloatGraphDataSet providedData = new FloatGraphDataSet(new Color(1.0f, 0.1f, 0.2f, 0.75f), tileEntity.getMetrics().getData(MetricCategory.SECONDS).getOutputValues());

		graphRenderer.addUpdateData("Received", recievedData);
		graphRenderer.addUpdateData("Provided", providedData);
		graphRenderer.render(partialTicks, matrixStack, buffer, combinedLight);

		matrixStack.pop();

		double maxValue = recievedData.getMinMaxValues().getY();
		double minValue = providedData.getMinMaxValues().getX();

		// Draw the max value.
		WorldRenderingUtilities.drawTextInWorld(this.renderDispatcher, GuiTextUtilities.formatEnergyRateToString(maxValue).getString(), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f),
				new Vector3D(0.07f, 0.43f, 0.002f), 0.0035f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		// Draw the min value.
		WorldRenderingUtilities.drawTextInWorld(this.renderDispatcher, GuiTextUtilities.formatEnergyRateToString(minValue).getString(), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f),
				new Vector3D(0.07f, 0.04f, 0.002f), 0.0035f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		// Draw the current value.
		WorldRenderingUtilities.drawTextInWorld(this.renderDispatcher,
				GuiTextUtilities.formatEnergyRateToString(tileEntity.getMetrics().getData(MetricCategory.TICKS).getInputValues().peekLast()).getString(), tileEntity,
				new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.74f, 0.2245f, 0.002f), 0.0035f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		matrixStack.pop();
	}
}
