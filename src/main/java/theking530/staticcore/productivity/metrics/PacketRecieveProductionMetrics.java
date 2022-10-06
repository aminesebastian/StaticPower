package theking530.staticcore.productivity.metrics;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.teams.TeamManager;

public class PacketRecieveProductionMetrics extends NetworkMessage {
	private SertializedBiDirectionalMetrics metrics;

	public PacketRecieveProductionMetrics() {

	}

	public PacketRecieveProductionMetrics(SertializedBiDirectionalMetrics metrics) {
		this.metrics = metrics;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		metrics.encode(buffer);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		metrics = SertializedBiDirectionalMetrics.decode(buffer);
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TeamManager.getLocalTeam().getProductionManager().tempClientMetrics = metrics;
		});
	}
}
