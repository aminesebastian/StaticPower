package theking530.staticcore.productivity.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.product.ProductType;

public class MetricEntryContainer extends AbstractGuiWidget<MetricEntryContainer> {
	private final MetricType metricType;
	private BiConsumer<MetricEntryWidget, Integer> selectedMetricChanged;

	public MetricEntryContainer(MetricType metricType, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.metricType = metricType;
	}

	public void updateMetrics(ProductType<?> productType, Collection<ProductionMetric> rawMetrics) {
		// Sort such that the highest rates go to the top.
		List<ProductionMetric> sortedMetrics = new ArrayList<>(rawMetrics);
		if (metricType == MetricType.PRODUCTION) {
			sortedMetrics.sort(
					(m1, m2) -> Double.compare(m2.getProduced().getCurrentValue(), m1.getProduced().getCurrentValue()));
		} else {
			sortedMetrics.sort(
					(m1, m2) -> Double.compare(m2.getConsumed().getCurrentValue(), m1.getConsumed().getCurrentValue()));
		}

		int currentSize = getChildren().size();
		int requiredSize = sortedMetrics.size();
		int delta = requiredSize - currentSize;

		// Create or remove widgets as needed.
		if (delta > 0) {
			for (int i = 0; i < delta; i++) {
				MetricEntryWidget metricWidget = new MetricEntryWidget(metricType, 0, 0, 0, 20);
				registerWidget(metricWidget);
				metricWidget.setClickedCallback((widget) -> {
					if (selectedMetricChanged != null) {
						selectedMetricChanged.accept(widget, widget.getMetric().getProductHash());
					}
				});
			}
		} else if (requiredSize < currentSize) {
			for (int i = currentSize; i < currentSize + delta; i--) {
				removeChild(i);
			}
		}

		for (int i = 0; i < requiredSize; i++) {
			ProductionMetric metric = sortedMetrics.get(i);
			MetricEntryWidget metricWidget = (MetricEntryWidget) getChildren().get(i);
			metricWidget.setMetric(productType, metric);
			metricWidget.setVisible(!metric.getMetricValue(metricType).isZero());
		}

		repositionWidgets();
	}

	public void setSelectedMetricChanged(BiConsumer<MetricEntryWidget, Integer> selectedMetricChanged) {
		this.selectedMetricChanged = selectedMetricChanged;
	}

	@Override
	protected void renderWidgetForeground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
	}

	protected void repositionWidgets() {
		int tileHeight = 20;
		int targetWidth = 75;
		int maxPerRow = (int) (this.getWidth() / targetWidth);
		float tileWidth = this.getWidth() / maxPerRow;
		int yOffset = 0;
		int xOffset = 0;

		for (int i = 0; i < getChildren().size(); i++) {
			AbstractGuiWidget<?> widget = getChildren().get(i);
			widget.setSize(tileWidth, tileHeight);

			if (i != 0 && i % maxPerRow == 0) {
				yOffset += tileHeight;
				xOffset = 0;
			}

			if (i == 0) {
				widget.setPosition(xOffset, yOffset);
			} else {
				widget.setPosition(xOffset, yOffset);
			}

			xOffset += tileWidth;
		}

		this.setHeight(yOffset + tileHeight);
	}
}
