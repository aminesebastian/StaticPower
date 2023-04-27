package theking530.staticcore.gui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector3D;

public class DataGraphWidget extends AbstractGuiWidget<DataGraphWidget> {
	private Map<String, IGraphDataSet> dataSets;

	public DataGraphWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		dataSets = new HashMap<String, IGraphDataSet>();
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		int maxDataPoints = 60;
		float scale = (float) getWidth() / maxDataPoints;
		int yLines = (int) (Math.ceil(getHeight() / scale));
		SDColor backgroundColor = new SDColor(0.2f, 0.2f, 0.2f);
		SDColor lineColor = backgroundColor.copy().lighten(-0.05f, -0.05f, -0.05f, 0.0f);

		// Draw the background and lines.
		GuiDrawUtilities.drawSlot(matrix, getSize().getX(), getSize().getY(), 0, 0, 0);
		GuiDrawUtilities.drawRectangle(matrix, getWidth(), getHeight(), backgroundColor);
		for (int i = 0; i < maxDataPoints; i++) {
			GuiDrawUtilities.drawRectangle(matrix, 0.5f, getHeight(), i * scale, 0, 1, lineColor);
		}
		for (int i = 1; i < yLines; i++) {
			GuiDrawUtilities.drawRectangle(matrix, getWidth(), 0.5f, 0, i * scale, 1, lineColor);
		}

		GraphDataRange totalRange = getTotalContainedRange();

		// If we have no data, stop here.
		if (!dataSets.isEmpty()) {

			// Move us down so the origin of the graph is the bottom right corner and then
			// draw the data.
			matrix.pushPose();
			matrix.translate(getWidth(), getSize().getY(), 0);
			for (Entry<String, IGraphDataSet> data : dataSets.entrySet()) {
				IGraphDataSet dataSet = data.getValue();
				if (dataSet.getData().length > 0) {
					drawDataSet(matrix, maxDataPoints, dataSet, scale, partialTicks);
				}
				GraphDataPoint currentValue = dataSet.getData()[0];
				float currentValueY = (float) (currentValue.getY() / Math.max(totalRange.getMax(), 1)) * getHeight();
				float valueLabelY = -currentValueY + 1;
				valueLabelY = SDMath.clamp(valueLabelY, -getHeight() - 2, 1);
				GuiDrawUtilities.drawString(matrix,
						GuiTextUtilities.formatNumberAsString(currentValue.getY()).getString(), 8, valueLabelY, 10,
						0.75f, dataSet.getLineColor().fromFloatToEightBit().encodeInInteger(), true);
				GuiDrawUtilities.drawRectangle(matrix, 4, 4, -4, -currentValueY - 3f, 5, dataSet.getLineColor());
			}
			matrix.popPose();
		}

		// Draw y axis values.
		GuiDrawUtilities.drawStringLeftAligned(matrix,
				GuiTextUtilities.formatNumberAsString(totalRange.getMin() < 0 ? totalRange.getMin() : 0).getString(), 4,
				getSize().getY() - 4, 10, 0.75f, SDColor.EIGHT_BIT_WHITE, true);
		if (totalRange.getMax() > totalRange.getMin()) {
			GuiDrawUtilities.drawStringLeftAligned(matrix,
					GuiTextUtilities.formatNumberAsString(totalRange.getMax()).getString(), 4, 8, 10, 0.75f,
					SDColor.EIGHT_BIT_WHITE, true);
		}
	}

	public GraphDataRange getTotalContainedRange() {
		double min = 0, max = 0;
		for (IGraphDataSet dataSet : dataSets.values()) {
			if (dataSet.getRange().getMin() < min) {
				min = dataSet.getRange().getMin();
			}
			if (dataSet.getRange().getMax() > max) {
				max = dataSet.getRange().getMax();
			}
		}

		return new GraphDataRange(min, max);
	}

	public void setDataSet(String label, IGraphDataSet dataSet) {
		dataSets.put(label, dataSet);
	}

	public IGraphDataSet getDataSet(String label) {
		return dataSets.get(label);
	}

	public void clearAllData() {
		dataSets.clear();
	}

	protected void drawDataSet(PoseStack matrix, int maxPointsToDisplay, IGraphDataSet data, float valueScale,
			float partialTicks) {
		SDColor lineColor = data.getLineColor();

		GraphDataPoint[] yAxis = data.getData();
		int maxPoints = (int) Math.min(yAxis.length, maxPointsToDisplay + 1);

		// Render all the data points.
		if (maxPoints >= 2) {
			for (int i = 0; i < maxPoints - 1; i++) {
				float x =  -(float) yAxis[i].getX() * valueScale;
				float y = (float) (yAxis[i].getY() / data.getRange().getMax()) * getHeight();
				float nextY = (float) (yAxis[i + 1].getY() / data.getRange().getMax()) * getHeight();

				GuiDrawUtilities.drawLine(matrix, new Vector3D(x, -y, 1), new Vector3D(x - valueScale, -nextY, 1),
						lineColor, data.getLineThickness());
			}
		} else if (yAxis.length == 1) {
			float x = getWidth();
			float y = (float) -SDMath.clamp(yAxis[0].getY() * valueScale, -data.getRange().getMax(),
					data.getRange().getMax());
			GuiDrawUtilities.drawLine(matrix, new Vector3D(x, y, 10), new Vector3D(x, y, 10), lineColor,
					data.getLineThickness());
		}
	}

	public static class DynamicGraphDataSet extends AbstractGraphDataSet {
		private List<GraphDataPoint> data;
		private int maxDataLength;

		private GraphDataRange domain;
		private GraphDataRange range;

		public DynamicGraphDataSet(SDColor color) {
			this(color, 0, 0, 0, 0);
		}

		public DynamicGraphDataSet(SDColor color, double minDomain, double maxDomain) {
			this(color, minDomain, maxDomain, 0, 0);
		}

		public DynamicGraphDataSet(SDColor color, double minDomain, double maxDomain, double minRange,
				double maxRange) {
			super(color);
			this.data = new ArrayList<GraphDataPoint>();
			this.maxDataLength = 0;

			this.domain = new GraphDataRange(minDomain, maxDomain);
			this.range = new GraphDataRange(minRange, maxRange);
		}

		public void addNewDataPoint(double x, double y) {
			data.add(new GraphDataPoint(x, y));
			if (maxDataLength > 0 && data.size() > maxDataLength) {
				data.remove(0);
			}
		}

		public DynamicGraphDataSet setMaxDataPoints(int maxPoints) {
			maxDataLength = maxPoints;
			return this;
		}

		@Override
		public GraphDataPoint[] getData() {
			GraphDataPoint[] target = new GraphDataPoint[data.size()];
			for (int i = 0; i < target.length; i++) {
				target[i] = data.get(i);
			}
			return target;
		}

		@Override
		public GraphDataRange getDomain() {
			return domain;
		}

		@Override
		public GraphDataRange getRange() {
			return range;
		}

		public void setDomain(GraphDataRange domain) {
			this.domain = domain;
		}

		public void setRange(GraphDataRange range) {
			this.range = range;
		}
	}

	public static abstract class AbstractGraphDataSet implements IGraphDataSet {
		protected SDColor color;
		protected float lineThickness;

		public AbstractGraphDataSet(SDColor color) {
			this(color, 5);
		}

		public AbstractGraphDataSet(SDColor color, float lineThickness) {
			this.lineThickness = lineThickness;
			this.color = color;
		}

		@Override
		public SDColor getLineColor() {
			return color;
		}

		@Override
		public float getLineThickness() {
			return lineThickness;
		}
	}

	public static interface IGraphDataSet {
		public SDColor getLineColor();

		public float getLineThickness();

		public GraphDataPoint[] getData();

		public GraphDataRange getDomain();

		public GraphDataRange getRange();
	}

	public static class GraphDataPoint {
		double x;
		double y;

		public GraphDataPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
	}

	public static class GraphDataRange {
		double min;
		double max;

		public GraphDataRange(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}
	}
}
