package theking530.staticcore.gui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector2D;
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

		// If we have no data, stop here.
		Vector2D minMax = new Vector2D(0, 0);
		if (!dataSets.isEmpty()) {
			minMax = getTotalYValueRange();
			minMax.multiply(1.25f);
			float offset = 0; // ((Minecraft.getInstance().level.getGameTime() % 20) + partialTicks) / 20.0f;

			// Move us down so the origin of the graph is the bottom right corner and then
			// draw the data.
			matrix.pushPose();
			matrix.translate(getWidth() - (offset * scale), getSize().getY(), 0);
			for (String dataLabel : dataSets.keySet()) {
				if (dataSets.get(dataLabel).getData().length > 0) {
					drawDataSet(matrix, maxDataPoints, dataSets.get(dataLabel), minMax.getY(), scale);
				}
				double currentValue = dataSets.get(dataLabel).getData()[0];
				float currentValueY = (float) (currentValue / minMax.getY()) * getHeight();
				GuiDrawUtilities.drawString(matrix, GuiTextUtilities.formatNumberAsString(currentValue).getString(),-4, -currentValueY - 6, 10, 0.75f, dataSets.get(dataLabel).getLineColor().fromFloatToEightBit().encodeInInteger(), true);
				GuiDrawUtilities.drawRectangle(matrix, 4, 4, -5, -currentValueY - 2.5f, 5, dataSets.get(dataLabel).getLineColor());
			}
			matrix.popPose();
		}

		// Draw y axis values.
		GuiDrawUtilities.drawStringLeftAligned(matrix, GuiTextUtilities.formatNumberAsString(minMax.getX() < 0 ? minMax.getX() : 0).getString(), 4, getSize().getY() - 4, 10, 0.75f,
				SDColor.EIGHT_BIT_WHITE, true);
		if (minMax.getY() > minMax.getX()) {
			GuiDrawUtilities.drawStringLeftAligned(matrix, GuiTextUtilities.formatNumberAsString(minMax.getY()).getString(), 4, 8, 10, 0.75f, SDColor.EIGHT_BIT_WHITE, true);
		}
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

	protected Vector2D getTotalYValueRange() {
		// Allocate the min and max values.
		double min = Integer.MAX_VALUE;
		double max = Integer.MIN_VALUE;

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

	protected void drawDataSet(PoseStack matrix, int maxPointsToDisplay, IGraphDataSet data, float maxDataValue, float valueScale) {
		SDColor lineColor = data.getLineColor();
		
		double[] yAxis = data.getData();
		int maxPoints = (int) Math.min(yAxis.length, maxPointsToDisplay + 1);

		// Render all the data points.
		if (maxPoints >= 2) {
			for (int i = 0; i < maxPoints - 1; i++) {
				float x = -(i * valueScale) + 1f;
				float y = (float) (yAxis[i] / maxDataValue) * getHeight();
				float nextY = (float) (yAxis[i + 1] / maxDataValue) * getHeight();

				GuiDrawUtilities.drawLine(matrix, new Vector3D(x, -y, 1), new Vector3D(x - valueScale, -nextY, 1), lineColor, 3);
			}
		} else if (yAxis.length == 1) {
			float x = getWidth();
			float y = (float) -SDMath.clamp(yAxis[0] * valueScale, -maxDataValue, maxDataValue);
			GuiDrawUtilities.drawLine(matrix, new Vector3D(x, y, 10), new Vector3D(x, y, 10), lineColor, 5);
		}
	}

	public static class SupplierGraphDataSet extends AbstractGraphDataSet {
		private Supplier<double[]> dataSupplier;
		private Vector2D minMaxValues;

		public SupplierGraphDataSet(SDColor color, Supplier<double[]> dataSupplier) {
			super(color);
			this.minMaxValues = new Vector2D();
			this.dataSupplier = dataSupplier;
		}

		@Override
		public double[] getData() {
			double[] data = dataSupplier.get();
			for (int i = 0; i < data.length; i++) {
				if (data[i] > minMaxValues.getY()) {
					minMaxValues.setY((float) data[i]);
				} else if (data[i] < minMaxValues.getX()) {
					minMaxValues.setX((float) data[i]);
				}
			}
			return data;
		}

		@Override
		public Vector2D getMinMaxValues() {
			return minMaxValues;
		}
	}

	public static class DoubleGraphDataSet extends AbstractGraphDataSet {
		private double[] data;
		private Vector2D minMaxValues;

		public DoubleGraphDataSet(SDColor color, Collection<Double> data) {
			super(color);

			this.minMaxValues = new Vector2D();
			this.data = new double[data.size()];

			// Populate the data array.
			int index = 0;
			for (Double value : data) {
				this.data[index] = value;
				if (value > minMaxValues.getY()) {
					minMaxValues.setY(value.floatValue());
				} else if (value < minMaxValues.getX()) {
					minMaxValues.setX(value.floatValue());
				}
				index++;
			}
		}

		@Override
		public double[] getData() {
			return data;
		}

		@Override
		public Vector2D getMinMaxValues() {
			return minMaxValues;
		}
	}

	public static class FloatGraphDataSet extends AbstractGraphDataSet {
		private double[] data;
		private Vector2D minMaxValues;

		public FloatGraphDataSet(SDColor color, Collection<Float> data) {
			super(color);

			this.minMaxValues = new Vector2D();
			this.data = new double[data.size()];

			// Populate the data array.
			int index = 0;
			for (Float value : data) {
				this.data[index] = value;
				if (value > minMaxValues.getY()) {
					minMaxValues.setY(value);
				} else if (value < minMaxValues.getX()) {
					minMaxValues.setX(value);
				}
				index++;
			}
		}

		@Override
		public double[] getData() {
			return data;
		}

		@Override
		public Vector2D getMinMaxValues() {
			return minMaxValues;
		}
	}

	public static class DynamicGraphDataSet extends AbstractGraphDataSet {
		private List<Double> data;
		private int maxDataLength;
		private Vector2D minMaxValues;

		public DynamicGraphDataSet(SDColor color) {
			super(color);
			this.data = new ArrayList<Double>();
			this.maxDataLength = 0;
			this.minMaxValues = new Vector2D();
		}

		public void addNewDataPoint(double newData) {
			data.add(newData);
			if (maxDataLength > 0 && data.size() > maxDataLength) {
				data.remove(0);
			}

			// Capture the updated min and max values.
			if (newData > minMaxValues.getY()) {
				minMaxValues.setY((float) newData);
			} else if (newData < minMaxValues.getX()) {
				minMaxValues.setX((float) newData);
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

		@Override
		public Vector2D getMinMaxValues() {
			return minMaxValues;
		}
	}

	public static abstract class AbstractGraphDataSet implements IGraphDataSet {
		protected SDColor color;
		protected float lineThickness;

		public AbstractGraphDataSet(SDColor color) {
			this(color, 3);
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

		public double[] getData();

		public Vector2D getMinMaxValues();
	}
}
