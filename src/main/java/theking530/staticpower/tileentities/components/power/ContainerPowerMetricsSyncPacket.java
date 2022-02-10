package theking530.staticpower.tileentities.components.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
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
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeNbt(metrics.serializeNBT());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		metrics = new PowerTransferMetrics();
		metrics.deserializeNBT(buffer.readNbt());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
			if (container instanceof IPowerMetricsSyncConsumer && container.containerId == windowId) {
				IPowerMetricsSyncConsumer powerCableContainer = (IPowerMetricsSyncConsumer) container;
				powerCableContainer.recieveMetrics(metrics);
			}
		});
	}
}
