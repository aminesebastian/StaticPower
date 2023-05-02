package theking530.staticcore.productivity.metrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProductivityTimelineContainer {
	private final Map<Integer, ProductivityTimeline> timelines;

	public ProductivityTimelineContainer() {
		timelines = new HashMap<>();
	}

	public boolean hasTimelineForProduct(int productHash) {
		return timelines.containsKey(productHash);
	}

	public ProductivityTimeline getTimelineForProduct(int productHash) {
		return timelines.get(productHash);
	}

	public void addTimeline(int productHash, ProductivityTimeline timeline) {
		timelines.put(productHash, timeline);
	}

	public boolean removeTimeline(int productHash) {
		return timelines.remove(productHash) != null;
	}

	public Set<Integer> products() {
		return timelines.keySet();
	}

	public Collection<ProductivityTimeline> timelines() {
		return timelines.values();
	}

	public void clear() {
		timelines.clear();
	}

	public boolean isEmpty() {
		return timelines.isEmpty();
	}
}
