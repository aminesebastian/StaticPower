package theking530.staticpower.client.rendering.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.powered.powermonitor.TileEntityPowerMonitor;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderPowerMonitor extends StaticPowerTileEntitySpecialRenderer<TileEntityPowerMonitor> {
	private List<Double> data = new ArrayList<Double>();

	public TileEntityRenderPowerMonitor(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);

		for (int i = 0; i < 1000; i++) {
			data.add(SDMath.getRandomIntInRange(-500, 500) / 10.0);
		}
	}

	@Override
	public void renderTileEntityBase(TileEntityPowerMonitor tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight,
			int combinedOverlay) {

		// Render the bar.
		matrixStack.push();
		matrixStack.translate(0.09375f, 0.28125f, 0.725f);

		matrixStack.push();
		matrixStack.scale(0.8125f, 0.4375f, 1.0f);

		float gridSpacing = 0.02f;
		float aspectRatio = 7f / 13f;
		float yGridSpacing = gridSpacing / aspectRatio;
		int xLines = (int) Math.ceil(1f / gridSpacing);
		int yLines = (int) Math.ceil(1f / gridSpacing * aspectRatio);

		for (int i = 0; i < xLines; i++) {
			drawLine(tileEntity, matrixStack, buffer, new Vector3D(i * gridSpacing, 0.0f, 0.0f), new Vector3D(i * gridSpacing, 1.0f, 0.0f), 10.0f, new Color(0.1f, 0.1f, 0.125f, 0.5f));
		}
		for (int i = 0; i < yLines; i++) {
			if (i == yLines / 2) {
				drawLine(tileEntity, matrixStack, buffer, new Vector3D(0.0f, i * yGridSpacing, 0.0f), new Vector3D(1.0f, i * yGridSpacing, 0.0f), 10.0f, new Color(0.5f, 0.5f, 0.525f, 0.2f));
			} else {
				drawLine(tileEntity, matrixStack, buffer, new Vector3D(0.0f, i * yGridSpacing, 0.0f), new Vector3D(1.0f, i * yGridSpacing, 0.0f), 10.0f, new Color(0.1f, 0.1f, 0.125f, 0.5f));
			}
		}

		int startOffset = Math.max(0, tileEntity.getSecondsMetrics().getReceivedData().size() - xLines - 1);
		int endIndex = Math.min(tileEntity.getSecondsMetrics().getReceivedData().size(), xLines + 1);

		double maxValue = 0.0f;
		double minValue = 0.0f;
		for (int i = 1; i < endIndex; i++) {
			double value = tileEntity.getSecondsMetrics().getReceivedData().get(i + startOffset);
			if (value > maxValue) {
				maxValue = value;
			} else if (value < minValue) {
				minValue = value;
			}
		}
		double difference = Math.abs(minValue) + maxValue;
		float smoothAnimation = (gridSpacing * ((tileEntity.getWorld().getGameTime() % 20) / 20.0f));
		for (int i = 1; i < endIndex; i++) {
			double previous = (tileEntity.getSecondsMetrics().getReceivedData().get((i + startOffset) - 1) - minValue) / difference;
			double current = (tileEntity.getSecondsMetrics().getReceivedData().get(i + startOffset) - minValue) / difference;

			drawLine(tileEntity, matrixStack, buffer, new Vector3D((i - 1) * gridSpacing - smoothAnimation, (float) previous, 0.001f),
					new Vector3D(i * gridSpacing - smoothAnimation, (float) current, 0.001f), 10.0f, new Color(0.1f, 1.0f, 0.1f));
		}
		matrixStack.pop();

		// Draw the max value.
		drawTextInWorld(GuiTextUtilities.formatEnergyRateToString(maxValue).getString(), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.06f, 0.43f, 0.005f), 0.0035f,
				partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		// Draw the min value.
		drawTextInWorld(GuiTextUtilities.formatEnergyRateToString(minValue).getString(), tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.06f, 0.04f, 0.005f), 0.0035f,
				partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		// Draw the current value.
		drawTextInWorld(
				GuiTextUtilities.formatEnergyRateToString(tileEntity.getSecondsMetrics().getReceivedData().get(tileEntity.getSecondsMetrics().getReceivedData().size() - 1)).getString(),
				tileEntity, new Color(255.0f, 255.0f, 255.0f, 255.0f), new Vector3D(0.775f, 0.2245f, 0.005f), 0.0035f, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);

		matrixStack.pop();
	}
}
