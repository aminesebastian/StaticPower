package theking530.staticcore.productivity.metrics;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.FriendlyByteBuf;

public class ServerProductionMetrics {
	public static final ServerProductionMetrics EMPTY = new ServerProductionMetrics(Collections.emptyList(), Collections.emptyList());
	private final ImmutableList<ProductionMetric> consumption;
	private final ImmutableList<ProductionMetric> production;

	public ServerProductionMetrics(List<ProductionMetric> inputs, List<ProductionMetric> outputs) {
		this.consumption = ImmutableList.copyOf(inputs);
		this.production = ImmutableList.copyOf(outputs);
	}

	public ImmutableList<ProductionMetric> getConsumption() {
		return consumption;
	}

	public ImmutableList<ProductionMetric> getProduction() {
		return production;
	}

	public boolean isEmpty() {
		return consumption.size() == 0 && production.size() == 0;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(consumption.size());
		for (ProductionMetric metric : consumption) {
			buffer.writeNbt(metric.serialize());
		}

		buffer.writeInt(production.size());
		for (ProductionMetric metric : production) {
			buffer.writeNbt(metric.serialize());
		}
	}

	public static ServerProductionMetrics decode(FriendlyByteBuf buffer) {
		List<ProductionMetric> inputs = new LinkedList<>();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			inputs.add(ProductionMetric.deserialize(buffer.readNbt()));
		}

		List<ProductionMetric> outputs = new LinkedList<>();
		count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			outputs.add(ProductionMetric.deserialize(buffer.readNbt()));
		}

		return new ServerProductionMetrics(inputs, outputs);
	}
}
