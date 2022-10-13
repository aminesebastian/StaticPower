package theking530.staticpower.teams.productivity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.product.ProductType;

public class MetricEntryContainer extends AbstractGuiWidget<MetricEntryContainer> {
	private final MetricType metricType;

	public MetricEntryContainer(MetricType metricType, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.metricType = metricType;
	}

	public void updateMetrics(ProductType<?> productType, ImmutableList<ProductionMetric> metrics) {
		this.clearChildren();
		for (ProductionMetric metric : metrics) {
			if (metric.getMetric(metricType) > 0) {
				MetricEntryWidget metricWidget = new MetricEntryWidget(metricType, 0, 0, 0, 20);
				metricWidget.setMetric(productType, metric);
				registerWidget(metricWidget);
			}
		}
		repositionWidgets();
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
