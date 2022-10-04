package theking530.staticpower.teams.production.metrics;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.teams.TeamManager;

public class PacketRecieveProductionMetrics extends NetworkMessage {
	private List<SerializedMetricPeriod> metrics;

	public PacketRecieveProductionMetrics() {

	}

	public PacketRecieveProductionMetrics(List<SerializedMetricPeriod> metrics) {
		this.metrics = metrics;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(metrics.size());
		for (SerializedMetricPeriod metric : metrics) {
			buffer.writeNbt(metric.serialize());
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		metrics = new LinkedList<>();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			metrics.add(SerializedMetricPeriod.deserialize(buffer.readNbt()));
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TeamManager.getLocalTeam().getProductionManager().tempClientMetrics = metrics;
		});
	}
}
