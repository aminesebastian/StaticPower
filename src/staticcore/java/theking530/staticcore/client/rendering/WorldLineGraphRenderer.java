package theking530.staticcore.client.rendering;

import java.util.HashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import theking530.staticcore.gui.widgets.DataGraphWidget.IGraphDataSet;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticcore.utilities.math.Vector3D;

public class WorldLineGraphRenderer {
	private float gridSpacing;
	@SuppressWarnings("unused")
	private float width;
	@SuppressWarnings("unused")
	private float height;
	private float aspectRatio;
	private int xLines;
	private HashMap<String, IGraphDataSet> dataSets;

	/**
	 * @param gridSpacing
	 * @param width
	 * @param height
	 */
	public WorldLineGraphRenderer(float gridSpacing, float width, float height) {
		this.gridSpacing = gridSpacing;
		this.width = width;
		this.height = height;
		this.aspectRatio = height / width;
		this.xLines = (int) Math.ceil(1f / gridSpacing);
		this.dataSets = new HashMap<String, IGraphDataSet>();
	}

	public void render(float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
		this.renderLineGraphBackground(partialTicks, matrixStack, buffer, combinedLight);
		this.renderData(partialTicks, matrixStack, buffer, combinedLight);
	}

	public void addUpdateData(String label, IGraphDataSet data) {
		this.dataSets.put(label, data);
	}

	@SuppressWarnings("resource")
	private void renderData(float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
		// Do nothing if there is no data.
		if (dataSets.size() == 0) {
			return;
		}

		// Calculate the start and end offsets for the data.
		int startOffset = Math.max(0, dataSets.values().stream().findFirst().get().getData().length - xLines - 2);
		int endIndex = Math.min(dataSets.values().stream().findFirst().get().getData().length, xLines + 2);

		// Capture the minimum and maximum values in all of the data.
		double maxValue = 0;
		double minValue = 0;
		for (IGraphDataSet data : dataSets.values()) {
			// Do nothing if the data set is empty.
			if (data.getData().length == 0) {
				continue;
			}

			// Get the min max values.
			Vector2D minMax = data.getMinMaxValues();

			// Capture the values.
			if (minMax.getY() > maxValue) {
				maxValue = minMax.getY();
			} else if (minMax.getX() < minValue) {
				minValue = minMax.getX();
			}
		}

		// Capture the absolute difference between the min and max values.
		// We use this to make sure we always center around 0.
		double maxDifference = Math.abs(minValue) + Math.abs(maxValue);
		float smoothAnimation = (gridSpacing * ((Minecraft.getInstance().level.getGameTime() % 20) / 20.0f));
		// Draw the data lines.
		if (minValue == 0 && maxValue == 0) {
			for (IGraphDataSet data : dataSets.values()) {
				// Draw line.
				WorldRenderingUtilities.drawLine(matrixStack, buffer, new Vector3D(0, 0.5f, 0.001f), new Vector3D(1, 0.5f, 0.001f), data.getLineThickness(), data.getLineColor());

			}
		} else {
			for (IGraphDataSet data : dataSets.values()) {
				for (int i = 1; i < endIndex; i++) {
					double prev = (data.getData()[(i + startOffset) - 1] - maxDifference) / (maxDifference * 2);
					double curr = (data.getData()[i + startOffset] - maxDifference) / (maxDifference * 2);

					// Draw line.
					WorldRenderingUtilities.drawLine(matrixStack, buffer, new Vector3D((i - 1) * gridSpacing - smoothAnimation, (float) prev + 1, 0.001f),
							new Vector3D(i * gridSpacing - smoothAnimation, (float) curr + 1, 0.001f), data.getLineThickness(), data.getLineColor());
				}
			}
		}

	}

	private void renderLineGraphBackground(float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
		float yGridSpacing = gridSpacing / aspectRatio;
		int xLines = (int) Math.ceil(1f / gridSpacing);
		int yLines = (int) Math.ceil(1f / gridSpacing * aspectRatio);

		for (int i = 0; i < xLines; i++) {
			WorldRenderingUtilities.drawLine(matrixStack, buffer, new Vector3D(i * gridSpacing, 0.0f, 0.0f), new Vector3D(i * gridSpacing, 1.0f, 0.0f), 10.0f,
					new SDColor(0.1f, 0.1f, 0.125f, 0.5f));
		}
		for (int i = 0; i < yLines; i++) {
			if (i == yLines / 2) {
				WorldRenderingUtilities.drawLine(matrixStack, buffer, new Vector3D(0.0f, i * yGridSpacing + (yGridSpacing / 2), 0.0f),
						new Vector3D(1.0f, i * yGridSpacing + (yGridSpacing / 2), 0.0f), 10.0f, new SDColor(0.5f, 0.5f, 0.525f, 0.2f));
			} else {
				WorldRenderingUtilities.drawLine(matrixStack, buffer, new Vector3D(0.0f, i * yGridSpacing + (yGridSpacing / 2), 0.0f),
						new Vector3D(1.0f, i * yGridSpacing + (yGridSpacing / 2), 0.0f), 10.0f, new SDColor(0.1f, 0.1f, 0.125f, 0.5f));
			}
		}
	}
}
