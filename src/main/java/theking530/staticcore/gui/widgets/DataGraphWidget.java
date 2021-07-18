package theking530.staticcore.gui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class DataGraphWidget extends AbstractGuiWidget {
	private Map<String, IGraphDataSet> dataSets;
	private List<String> xAxisLabels;

	public DataGraphWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		dataSets = new HashMap<String, IGraphDataSet>();
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		// Return early if there is no data.
		if (dataSets.size() == 0) {
			return;
		}

		// Calculate the max data height and segment length (THIS SHOULD BE CACHED).
		int maxSegmentCount = getMaxDataSetLength();
		float maxDataHeight = getSize().getY() - 0.5f;
		float segmentLength = getSize().getX() / maxSegmentCount;

		// Get the min and max values.
		Vector2D minMax = getTotalYValueRange();
		Vector2D xAxisRange = getXAxisRange(minMax);
		float xAxisDifference = xAxisRange.getY() - xAxisRange.getX();
		xAxisDifference = xAxisDifference == 0.0f ? 1.0f : xAxisDifference;
		float valueScale = maxDataHeight / xAxisDifference;

		// Draw the background.
		GuiDrawUtilities.drawSlot(matrix, getPosition().getX(), this.getPosition().getY(), getSize().getX(), getSize().getY(), 0);

		// Move us down and a little to the left so the origin of the graph is the
		// bottom right corner.
		matrix.push();
		matrix.translate(0.1f + getPosition().getX(), getSize().getY() - 0.5f - ((xAxisDifference * valueScale) / 2) + getPosition().getY(), 0);

		// Draw the 0 line.
		GuiDrawUtilities.drawColoredRectangle(matrix, 0, 0, getSize().getX(), 0.5f, 1, Color.GREY);

		// Draw the grids.
		for (int i = 0; i < maxSegmentCount; i++) {
			GuiDrawUtilities.drawColoredRectangle(matrix, i * segmentLength, 0.25f - getSize().getY() / 2, 0.5f, getSize().getY() - 0.5f, 1, Color.GREY);

			if (xAxisLabels != null && (i == 0 || i == maxSegmentCount - 1) && i < xAxisLabels.size()) {
				if (i == 0) {
					GuiDrawUtilities.drawStringWithSizeCentered(matrix, xAxisLabels.get(i), 8, 5 + getSize().getY() / 2, 0.45f, Color.EIGHT_BIT_DARK_GREY, false);
				} else if (i == maxSegmentCount - 1) {
					GuiDrawUtilities.drawStringWithSizeCentered(matrix, xAxisLabels.get(i), getSize().getX() - 8, 5 + getSize().getY() / 2, 0.45f, Color.EIGHT_BIT_DARK_GREY, false);
				}
			}
		}

		// Set appropriate GL attributes.
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Draw all the data sets.
		for (String dataLabel : dataSets.keySet()) {
			if (dataSets.get(dataLabel).getData().length > 0) {
				drawDataSet(matrix, dataSets.get(dataLabel), valueScale, segmentLength, maxDataHeight / 2);
			}
		}

		// Clear the gl attributes and pop the transform matrix.
		GL11.glDepthMask(true);
		GL11.glPopAttrib();

		// Draw y axis values.
		GuiDrawUtilities.drawStringWithSizeLeftAligned(matrix, GuiTextUtilities.formatNumberAsString(minMax.getX()).getString(), 1.5f, getSize().getY() / 2 - 2, 0.55f,
				Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSizeLeftAligned(matrix, GuiTextUtilities.formatNumberAsString(minMax.getY()).getString(), 1.5f, -getSize().getY() / 2 + 5, 0.55f,
				Color.EIGHT_BIT_DARK_GREY, false);
		GuiDrawUtilities.drawStringWithSizeLeftAligned(matrix, "0", 1.5f, -2f, 0.55f, Color.EIGHT_BIT_DARK_GREY, false);

		matrix.pop();
	}

	public void setXAxisLabels(List<String> labels) {
		xAxisLabels = labels;
	}

	public void setDataSet(String label, IGraphDataSet dataSet) {
		dataSets.put(label, dataSet);
	}

	public IGraphDataSet getDataSet(String label) {
		return dataSets.get(label);
	}

	protected Vector2D getTotalYValueRange() {
		// Allocate the min and max values.
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		// Calculate the min and max values.
		for (String dataLabel : dataSets.keySet()) {
			for (double data : dataSets.get(dataLabel).getData()) {
				if (data > max) {
					max = data;
				}
				if (data < min) {
					min = data;
				}
			}
		}

		// Return the values.
		return new Vector2D((float) min, (float) max);
	}

	protected int getMaxDataSetLength() {
		// Calculate the longest data set length.
		int length = 0;

		// Check each data set.
		for (String dataLabel : dataSets.keySet()) {
			int dataSetLength = dataSets.get(dataLabel).getData().length;
			if (dataSetLength > length) {
				length = dataSetLength;
			}
		}

		// Return the max length.
		return length;
	}

	protected Vector2D getXAxisRange(Vector2D minMaxValues) {
		// Get the raw values.
		float minAxis = minMaxValues.getX();
		float maxAxis = minMaxValues.getY();

		// Shift so it always ends with a 0.
		minAxis = ((minAxis / 10) * 10) + 10;
		maxAxis = ((maxAxis / 10) * 10) + 10;

		// Return those values.
		return new Vector2D(minAxis, maxAxis);
	}

	protected void drawDataSet(MatrixStack matrix, IGraphDataSet data, float valueScale, float segmentLength, float maxDataHeight) {
		Color lineColor = data.getLineColor();
		GL11.glColor4d(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha());
		GL11.glLineWidth(data.getLineThickness());
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

		Vector2D origin = GuiDrawUtilities.translatePositionByMatrix(matrix, 0, 0);
		double[] yAxis = data.getData();
		double x;
		double y;
		double nextY;

		// Render all the data points.
		if (yAxis.length >= 2) {
			for (int i = 0; i < yAxis.length; i++) {
				x = i * segmentLength;
				y = -SDMath.clamp(yAxis[i] * valueScale, -maxDataHeight, maxDataHeight);
				nextY = i < yAxis.length - 1 ? -SDMath.clamp(yAxis[i + 1] * valueScale, -maxDataHeight, maxDataHeight) : y;

				bufferBuilder.pos(origin.getX() + x, origin.getY() + y, 1).endVertex();
				bufferBuilder.pos(origin.getX() + x + segmentLength, origin.getY() + nextY, 1).endVertex();
			}
		} else if (yAxis.length == 1) {
			y = -SDMath.clamp(yAxis[0] * valueScale, -maxDataHeight, maxDataHeight);
			bufferBuilder.pos(origin.getX(), origin.getY() + y, 1).endVertex();
			bufferBuilder.pos(origin.getX() + getSize().getX(), origin.getY() + y, 1).endVertex();
		}

		// Draw all the points.
		tessellator.draw();

		// Draw the value label.
		if (yAxis.length > 0) {
			double lastValue = yAxis[yAxis.length - 1];
			float textYPos = (float) (-lastValue * valueScale) + 6;
			textYPos = SDMath.clamp(textYPos, -maxDataHeight, maxDataHeight - 2);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GuiDrawUtilities.drawStringWithSize(matrix, GuiTextUtilities.formatNumberAsString(lastValue).getString(), yAxis.length * segmentLength, textYPos, 0.55f,
					lineColor.fromFloatToEightBit(), false);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}

	public static class SupplierGraphDataSet extends AbstractGraphDataSet {
		private Supplier<double[]> dataSupplier;

		public SupplierGraphDataSet(Color color, Supplier<double[]> dataSupplier) {
			super(color);
			this.dataSupplier = dataSupplier;
		}

		@Override
		public double[] getData() {
			return dataSupplier.get();
		}
	}

	public static class ListGraphDataSet extends AbstractGraphDataSet {
		private List<Double> data;

		public ListGraphDataSet(Color color, List<Double> data) {
			super(color);
			this.data = data;
		}

		@Override
		public double[] getData() {
			double[] array = new double[data.size()];
			for (int i = 0; i < array.length; i++) {
				array[i] = data.get(i);
			}
			return array;
		}
	}

	public static class DynamicGraphDataSet extends AbstractGraphDataSet {
		private List<Double> data;
		private int maxDataLength;

		public DynamicGraphDataSet(Color color) {
			super(color);
			this.data = new ArrayList<Double>();
			this.maxDataLength = 0;
		}

		public void addNewDataPoint(double newData) {
			data.add(newData);
			if (maxDataLength > 0 && data.size() > maxDataLength) {
				data.remove(0);
			}
		}

		public DynamicGraphDataSet setMaxDataPoints(int maxPoints) {
			maxDataLength = maxPoints;
			return this;
		}

		@Override
		public double[] getData() {
			double[] target = new double[data.size()];
			for (int i = 0; i < target.length; i++) {
				target[i] = data.get(i);
			}
			return target;
		}
	}

	public static abstract class AbstractGraphDataSet implements IGraphDataSet {
		protected Color color;
		protected float lineThickness;

		public AbstractGraphDataSet(Color color) {
			this(color, 3);
		}

		public AbstractGraphDataSet(Color color, float lineThickness) {
			this.lineThickness = lineThickness;
			this.color = color;
		}

		@Override
		public Color getLineColor() {
			return color;
		}

		@Override
		public float getLineThickness() {
			return lineThickness;
		}
	}

	public static interface IGraphDataSet {
		public Color getLineColor();

		public float getLineThickness();

		public double[] getData();
	}
}
