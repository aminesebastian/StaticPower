package theking530.staticpower.teams.productivity;

import com.google.common.collect.ImmutableList;

import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.containers.ScrollBox;
import theking530.staticcore.productivity.metrics.SerializedMetricPeriod;

public class MetricEntryContainer extends ScrollBox {
	private ImmutableList<SerializedMetricPeriod> metrics;

	public MetricEntryContainer(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.metrics = ImmutableList.of();
	}

	public void updateMetrics(ImmutableList<SerializedMetricPeriod> metrics) {
		this.metrics = metrics;
	}

	protected void recreateWidgets() {
		for (int i = 0; i < getChildren().size(); i++) {
			AbstractGuiWidget<?> widget = getChildren().get(i);
			if (i == 0) {
				widget.setPosition(0, 0);
			} else {
				widget.setPosition(0, getChildren().get(i - 1).getYPosition() + getChildren().get(i - 1).getSize().getY());
			}
		}
	}
}
