package theking530.staticcore.productivity.client;

import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.productivity.metrics.ClientProductionMetric;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.product.ProductType;

public class MetricEntryContainer extends AbstractGuiWidget<MetricEntryContainer> {
	private final MetricType metricType;
	private BiConsumer<MetricEntryWidget, Integer> selectedMetricChanged;

	public MetricEntryContainer(MetricType metricType, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.metricType = metricType;
	}

	public void updateMetrics(ProductType<?> productType, ImmutableList<ClientProductionMetric> metrics) {
		int currentSize = getChildren().size();
		int requiredSize = metrics.size();
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
			ClientProductionMetric metric = metrics.get(i);
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
