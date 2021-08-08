package theking530.staticpower.tileentities.components.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class ContainerPowerMetricsSyncPacket extends NetworkMessage {
	private int windowId;
	private TransferMetrics secondsMetrics;
	private TransferMetrics minuteMetrics;
	private TransferMetrics hourlyMetrics;

	public ContainerPowerMetricsSyncPacket() {

	}

	/**
	 * @param secondsMetrics
	 * @param minuteMetrics
	 * @param hourlyMetrics
	 */
	public ContainerPowerMetricsSyncPacket(int windowId, TransferMetrics secondsMetrics, TransferMetrics minuteMetrics, TransferMetrics hourlyMetrics) {
		this.windowId = windowId;
		this.secondsMetrics = secondsMetrics;
		this.minuteMetrics = minuteMetrics;
		this.hourlyMetrics = hourlyMetrics;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeCompoundTag(secondsMetrics.serializeNBT());
		buffer.writeCompoundTag(minuteMetrics.serializeNBT());
		buffer.writeCompoundTag(hourlyMetrics.serializeNBT());
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		secondsMetrics = new TransferMetrics();
		secondsMetrics.deserializeNBT(buffer.readCompoundTag());

		minuteMetrics = new TransferMetrics();
		minuteMetrics.deserializeNBT(buffer.readCompoundTag());

		hourlyMetrics = new TransferMetrics();
		hourlyMetrics.deserializeNBT(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof IPowerMetricsSyncConsumer && container.windowId == windowId) {
				IPowerMetricsSyncConsumer powerCableContainer = (IPowerMetricsSyncConsumer) container;
				powerCableContainer.recieveMetrics(secondsMetrics, minuteMetrics, hourlyMetrics);
			}
		});
	}
}
