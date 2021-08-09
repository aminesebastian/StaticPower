package theking530.staticpower.tileentities.components.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class ContainerPowerMetricsSyncPacket extends NetworkMessage {
	private int windowId;
	private PowerTransferMetrics metrics;

	public ContainerPowerMetricsSyncPacket() {

	}

	public ContainerPowerMetricsSyncPacket(int windowId, PowerTransferMetrics metrics) {
		this.windowId = windowId;
		this.metrics = metrics;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeCompoundTag(metrics.serializeNBT());
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		metrics = new PowerTransferMetrics();
		metrics.deserializeNBT(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof IPowerMetricsSyncConsumer && container.windowId == windowId) {
				IPowerMetricsSyncConsumer powerCableContainer = (IPowerMetricsSyncConsumer) container;
				powerCableContainer.recieveMetrics(metrics);
			}
		});
	}
}
