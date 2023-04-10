package theking530.staticcore.productivity.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class ClientProductionMetrics {
	public static final ClientProductionMetrics EMPTY = new ClientProductionMetrics(Collections.emptyList(),
			Collections.emptyList());
	private final ImmutableList<ClientProductionMetric> consumption;
	private final ImmutableList<ClientProductionMetric> production;

	public ClientProductionMetrics(List<ProductionMetric> consumption, List<ProductionMetric> production) {
		List<ClientProductionMetric> transformedConsumption = new ArrayList<>();
		for (ProductionMetric consumptionMetric : consumption) {
			transformedConsumption.add(new ClientProductionMetric(consumptionMetric));
		}
		
		List<ClientProductionMetric> transformedProduction = new ArrayList<>();
		for (ProductionMetric productionMetric : production) {
			transformedProduction.add(new ClientProductionMetric(productionMetric));
		}
		
		this.consumption = ImmutableList.copyOf(transformedConsumption);
		this.production = ImmutableList.copyOf(transformedProduction);
	}

	public ImmutableList<ClientProductionMetric> getConsumption() {
		return consumption;
	}

	public ImmutableList<ClientProductionMetric> getProduction() {
		return production;
	}

	public boolean isEmpty() {
		return consumption.size() == 0 && production.size() == 0;
	}

}
