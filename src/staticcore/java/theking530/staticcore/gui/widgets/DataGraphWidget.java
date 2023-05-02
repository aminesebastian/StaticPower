package theking530.staticcore.gui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
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
		this.setClipType(WidgetClipType.CLIP);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		int maxDataPoints = 60;
		float scale = (float) getWidth() / maxDataPoints;
		int yLines = (int) (Math.ceil(getHeight() / scale));
		SDColor backgroundColor = new SDColor(0.3f, 0.3f, 0.3f);
		SDColor lineColor = backgroundColor.copy().lighten(-0.01f, -0.01f, -0.01f, 0.0f);

		// Draw the background and lines.
		GuiDrawUtilities.drawSlot(matrix, getSize().getX(), getSize().getY(), 0, 0, 0);
		GuiDrawUtilities.drawRectangle(matrix, getWidth(), getHeight(), backgroundColor);
		for (int i = 0; i < maxDataPoints; i++) {
			GuiDrawUtilities.drawRectangle(matrix, 0.25f, getHeight(), i * scale, 0, 1, lineColor);
		}
		for (int i = 1; i < yLines; i++) {
			GuiDrawUtilities.drawRectangle(matrix, getWidth(), 0.25f, 0, i * scale, 1, lineColor);
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
				GraphDataPoint currentValue = dataSet.getData()[dataSet.getData().length - 1];

				float rangeDelta = (float) (totalRange.getMax() - totalRange.getMin());

				float currentValueY = (float) ((currentValue.getY() - totalRange.getMin()) / rangeDelta * getHeight());
				float clampedYPos = SDMath.clamp(currentValueY, 2, getHeight() - 15);

				String valueString = GuiTextUtilities.formatNumberAsString(currentValue.getY()).getString();
				float wordWidth = getFontRenderer().width(valueString) * 0.75f + 5;

				GuiDrawUtilities.drawGenericBackground(matrix, wordWidth + 4, 15, -10 - wordWidth + 0.5f,
						-clampedYPos - 14.5f, 50, new SDColor(0.5f, 0.5f, 0.5f, 0.95f));

				GuiDrawUtilities.drawString(matrix, valueString, -10, -clampedYPos - 4.5f, 51, 0.75f,
						dataSet.getLineColor().fromFloatToEightBit(), true);

				SDColor rectangleColor = dataSet.getLineColor().copy();
				rectangleColor.darken(0.2f, 0.2f, 0.2f, 0.0f);
				GuiDrawUtilities.drawRectangle(matrix, 6, 6, -5f, -currentValueY - 4f, 50, SDColor.BLACK);
				GuiDrawUtilities.drawRectangle(matrix, 4, 4, -4, -currentValueY - 3f, 50, rectangleColor);
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

	@SuppressWarnings("resource")
	protected void drawDataSet(PoseStack matrix, int maxPointsToDisplay, IGraphDataSet data, float valueScale,
			float partialTicks) {
		SDColor lineColor = data.getLineColor();

		GraphDataPoint[] points = data.getData();
		int maxPoints = (int) Math.min(points.length, maxPointsToDisplay + 1);

		// Render all the data points.
		if (maxPoints >= 2) {
			for (int i = 0; i < maxPoints - 1; i++) {
				GraphDataPoint current = getAdjustedCoord(data, points[i]);
				GraphDataPoint next = getAdjustedCoord(data, points[i + 1]);
				GuiDrawUtilities.drawLine(matrix, new Vector3D((float) current.getX(), (float) current.getY(), 1),
						new Vector3D((float) next.getX(), (float) next.getY(), 1), lineColor, data.getLineThickness());
			}
		} else if (points.length == 1) {
			float x = getWidth();
			float y = (float) -SDMath.clamp(points[0].getY() * valueScale, -data.getRange().getMax(),
					data.getRange().getMax());
			GuiDrawUtilities.drawLine(matrix, new Vector3D(x, y, 10), new Vector3D(x, y, 10), lineColor,
					data.getLineThickness());
		}

		// Draw the last line always.

		GraphDataPoint current = getAdjustedCoord(data, points[points.length - 1]);
		GraphDataPoint last = getAdjustedCoord(data,
				new GraphDataPoint(Minecraft.getInstance().level.getGameTime(), points[points.length - 1].getY()));

		GuiDrawUtilities.drawLine(matrix, new Vector3D((float) current.getX(), (float) current.getY(), 1),
				new Vector3D((float) last.getX(), (float) last.getY(), 1), lineColor, data.getLineThickness());
	}

	private GraphDataPoint getAdjustedCoord(IGraphDataSet data, GraphDataPoint point) {
		double domainDelta = data.getDomain().getMax() - data.getDomain().getMin();
		double rangeDelta = data.getRange().getMax() - data.getRange().getMin();
		float x = (float) (1 - ((point.getX() - data.getDomain().getMin()) / domainDelta) * -getWidth()) - getWidth();
		float y = (float) (((point.getY() - data.getRange().min) / rangeDelta) * -this.getHeight());
		return new GraphDataPoint(x, y);
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
