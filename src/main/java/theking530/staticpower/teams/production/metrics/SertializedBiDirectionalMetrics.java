package theking530.staticpower.teams.production.metrics;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.FriendlyByteBuf;

public class SertializedBiDirectionalMetrics {
	public static final SertializedBiDirectionalMetrics EMPTY = new SertializedBiDirectionalMetrics(Collections.emptyList(), Collections.emptyList());
	private final ImmutableList<SerializedMetricPeriod> inputs;
	private final ImmutableList<SerializedMetricPeriod> outputs;

	public SertializedBiDirectionalMetrics(List<SerializedMetricPeriod> inputs, List<SerializedMetricPeriod> outputs) {
		this.inputs = ImmutableList.copyOf(inputs);
		this.outputs = ImmutableList.copyOf(outputs);
	}

	public ImmutableList<SerializedMetricPeriod> getInputs() {
		return inputs;
	}

	public ImmutableList<SerializedMetricPeriod> getOutputs() {
		return outputs;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(inputs.size());
		for (SerializedMetricPeriod metric : inputs) {
			buffer.writeNbt(metric.serialize());
		}

		buffer.writeInt(outputs.size());
		for (SerializedMetricPeriod metric : outputs) {
			buffer.writeNbt(metric.serialize());
		}
	}

	public static SertializedBiDirectionalMetrics decode(FriendlyByteBuf buffer) {
		List<SerializedMetricPeriod> inputs = new LinkedList<>();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			inputs.add(SerializedMetricPeriod.deserialize(buffer.readNbt()));
		}

		List<SerializedMetricPeriod> outputs = new LinkedList<>();
		count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			outputs.add(SerializedMetricPeriod.deserialize(buffer.readNbt()));
		}

		return new SertializedBiDirectionalMetrics(inputs, outputs);
	}
}
