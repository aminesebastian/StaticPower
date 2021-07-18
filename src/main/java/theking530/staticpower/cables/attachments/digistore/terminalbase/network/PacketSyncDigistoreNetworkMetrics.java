package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.network.NetworkMessage;

public class PacketSyncDigistoreNetworkMetrics extends NetworkMessage {
	protected int windowId;
	private int usedCapacity;
	private int maxCapacity;
	private int usedTypes;
	private int maxTypes;

	public PacketSyncDigistoreNetworkMetrics() {

	}

	public PacketSyncDigistoreNetworkMetrics(int windowId, int usedCapacity, int maxCapacity, int usedTypes, int maxTypes) {
		this.windowId = windowId;
		this.usedCapacity = usedCapacity;
		this.maxCapacity = maxCapacity;
		this.usedTypes = usedTypes;
		this.maxTypes = maxTypes;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeInt(usedCapacity);
		buffer.writeInt(maxCapacity);
		buffer.writeInt(usedTypes);
		buffer.writeInt(maxTypes);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		this.usedCapacity = buffer.readInt();
		this.maxCapacity = buffer.readInt();
		this.usedTypes = buffer.readInt();
		this.maxTypes = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof AbstractContainerDigistoreTerminal && container.windowId == windowId) {
				AbstractContainerDigistoreTerminal<?> digistoreContainer = (AbstractContainerDigistoreTerminal<?>) container;
				digistoreContainer.updateMetrics(usedCapacity, maxCapacity, usedTypes, maxTypes);
			}
		});
	}
}
