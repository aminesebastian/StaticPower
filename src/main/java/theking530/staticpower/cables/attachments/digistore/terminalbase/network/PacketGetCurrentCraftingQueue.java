package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class PacketGetCurrentCraftingQueue extends NetworkMessage {
	protected int windowId;

	public PacketGetCurrentCraftingQueue() {

	}

	public PacketGetCurrentCraftingQueue(int windowId) {
		this.windowId = windowId;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			Container container = serverPlayer.openContainer;
			if (container instanceof AbstractContainerDigistoreTerminal && container.windowId == windowId) {
				AbstractContainerDigistoreTerminal<?> terminalContainer = (AbstractContainerDigistoreTerminal<?>) container;
				terminalContainer.getDigistoreNetwork().ifPresent(module -> {
					ListNBT craftingListNBT = module.getCraftingManager().serializeCraftingQueue();
					PacketReturnCurrentCraftingQueue craftingQueueResponse = new PacketReturnCurrentCraftingQueue(windowId, craftingListNBT);
					StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayerEntity) ctx.get().getSender(), craftingQueueResponse);
				});
			}
		});
	}
}
