package theking530.staticcore.productivity.client;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import theking530.staticcore.productivity.IProductionCache;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.metrics.ProductionMetrics;
import theking530.staticcore.productivity.metrics.ProductivityTimeline;
import theking530.staticcore.productivity.metrics.ProductivityTimeline.ProductivityTimelineEntry;
import theking530.staticcore.productivity.metrics.ProductivityTimelineContainer;
import theking530.staticcore.productivity.product.ProductType;

public class ClientProductionCache<T> implements IProductionCache<T> {
	public static final double SMOOTHING_FACTOR = 10;

	private final ProductType<T> productType;
	private MetricPeriod period;

	private final Map<Integer, ProductionMetric> metrics;
	private final Map<Integer, ProductionMetric> nextMetrics;

	private final ProductivityTimelineContainer productionTimelines;
	private final ProductivityTimelineContainer consumptionTimelines;

	private long lastClientSyncTime;

	public ClientProductionCache(ProductType<T> productType) {
		this.productType = productType;
		metrics = new HashMap<>();
		nextMetrics = new HashMap<>();
		productionTimelines = new ProductivityTimelineContainer();
		consumptionTimelines = new ProductivityTimelineContainer();
	}

	@Override
	public void tick(long gameTime) {
		for (ProductionMetric lastProduced : metrics.values()) {
			if (nextMetrics.containsKey(lastProduced.getProductHash())) {
				lastProduced.interpolateTowards(nextMetrics.get(lastProduced.getProductHash()), SMOOTHING_FACTOR);
			}
		}
	}

	@Override
	public ProductType<T> getProductType() {
		return productType;
	}

	public Map<Integer, ProductionMetric> getMetrics() {
		return metrics;
	}

	public boolean isEmpty() {
		return metrics.isEmpty();
	}

	public void recieveMetrics(MetricPeriod period, ProductionMetrics recievedMetrics, long syncTime) {
		handleMetricPeriodChange(period);

		if (metrics.isEmpty()) {
			metrics.putAll(recievedMetrics.getMetrics());
		} else {
			metrics.clear();
			metrics.putAll(nextMetrics);
		}

		nextMetrics.clear();
		nextMetrics.putAll(recievedMetrics.getMetrics());
		lastClientSyncTime = syncTime;
	}

	public void recieveProductivityTimelines(MetricPeriod period, ProductivityTimeline timeline, long startTime,
			long endTime) {

		// If the timeline is empty, skip it.
		if (timeline.getEntries().isEmpty()) {
			return;
		}

		// Build a map of 60 empty entries at the period's interval.
		TreeMap<Long, ProductivityTimelineEntry> entries = new TreeMap<>();
		long time = startTime - (startTime % period.getMetricPeriodInTicks());
		for (int i = 0; i < 60; i++) {
			entries.put(time, new ProductivityTimelineEntry(0, time));
			time += period.getMetricPeriodInTicks();
		}

		// Iterate through the incoming timeline and add/accumulate entries to the list
		// we created above.
		for (ProductivityTimelineEntry timelineEntry : timeline.getEntries().values()) {
			long roundedTime = timelineEntry.tick() - (timelineEntry.tick() % period.getMetricPeriodInTicks());
			double value = timelineEntry.value();
			if (entries.containsKey(roundedTime)) {
				double existingValue = entries.get(roundedTime).value();
				ProductivityTimelineEntry newEntry = new ProductivityTimelineEntry(value + existingValue, roundedTime);
				entries.put(roundedTime, newEntry);
			}
		}

		// Create a new timeline representing the full 60 entries combined with the
		// incoming timeline.
		ProductivityTimeline expandedTimeline = new ProductivityTimeline(timeline.getStartTime(), timeline.getEndTime(),
				timeline.getProductHash(), timeline.getProductType(), timeline.getSerializedProduct(),
				timeline.getMetricType(), timeline.getPeriod(), entries);

		// Either append this to the existing timeline or create a new one.
		ProductivityTimelineContainer timelines = getTimeline(expandedTimeline.getMetricType());
		if (timelines.hasTimelineForProduct(expandedTimeline.getProductHash())) {
			ProductivityTimeline existingTimeline = timelines.getTimelineForProduct(expandedTimeline.getProductHash());
			existingTimeline.append(expandedTimeline);

			// Make sure we only keep the last 60 in memory.
			int toRemove = existingTimeline.getEntries().size() - 60;
			if (toRemove > 0) {
				for (int i = 0; i < toRemove; i++) {
					existingTimeline.getEntries().remove(existingTimeline.getEntries().firstKey());
				}
			}
		} else {
			timelines.addTimeline(expandedTimeline.getProductHash(), expandedTimeline);
		}
	}

	public ProductivityTimelineContainer getTimeline(MetricType type) {
		return type == MetricType.CONSUMPTION ? consumptionTimelines : productionTimelines;
	}

	@Override
	public boolean haveClientValuesUpdatedSince(long lastCheckTime) {
		return lastClientSyncTime > lastCheckTime;
	}

	protected void handleMetricPeriodChange(MetricPeriod period) {
		if (this.period == period) {
			return;
		}

		this.period = period;
		metrics.clear();
		nextMetrics.clear();
		productionTimelines.clear();
		consumptionTimelines.clear();
	}
}