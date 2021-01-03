package theking530.staticpower.cables.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.power.PowerNetworkModule.TransferMetrics;
import theking530.staticpower.network.NetworkMessage;

public class PowerMetricsSyncPacket extends NetworkMessage {
	private int windowId;
	private TransferMetrics secondsMetrics;
	private TransferMetrics minuteMetrics;
	private TransferMetrics hourlyMetrics;

	public PowerMetricsSyncPacket() {

	}

	/**
	 * @param secondsMetrics
	 * @param minuteMetrics
	 * @param hourlyMetrics
	 */
	public PowerMetricsSyncPacket(int windowId, TransferMetrics secondsMetrics, TransferMetrics minuteMetrics, TransferMetrics hourlyMetrics) {
		this.windowId = windowId;
		this.secondsMetrics = secondsMetrics;
		this.minuteMetrics = minuteMetrics;
		this.hourlyMetrics = hourlyMetrics;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeCompoundTag(secondsMetrics.serialize());
		buffer.writeCompoundTag(minuteMetrics.serialize());
		buffer.writeCompoundTag(hourlyMetrics.serialize());

	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		secondsMetrics = TransferMetrics.deserialize(buffer.readCompoundTag());
		minuteMetrics = TransferMetrics.deserialize(buffer.readCompoundTag());
		hourlyMetrics = TransferMetrics.deserialize(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof ContainerPowerCable && container.windowId == windowId) {
				ContainerPowerCable powerCableContainer = (ContainerPowerCable) container;
				powerCableContainer.recievedMetrics(secondsMetrics, minuteMetrics, hourlyMetrics);
			}
		});
	}
}
