package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class PacketGetCurrentCraftingQueue extends NetworkMessage {
	protected int windowId;

	public PacketGetCurrentCraftingQueue() {

	}

	public PacketGetCurrentCraftingQueue(int windowId) {
		this.windowId = windowId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			AbstractContainerMenu container = serverPlayer.containerMenu;
			if (container instanceof AbstractContainerDigistoreTerminal && container.containerId == windowId) {
				AbstractContainerDigistoreTerminal<?> terminalContainer = (AbstractContainerDigistoreTerminal<?>) container;
				terminalContainer.getDigistoreNetwork().ifPresent(module -> {
					ListTag craftingListNBT = module.getCraftingManager().serializeCraftingQueue();
					PacketReturnCurrentCraftingQueue craftingQueueResponse = new PacketReturnCurrentCraftingQueue(windowId, craftingListNBT);
					StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) ctx.get().getSender(), craftingQueueResponse);
				});
			}
		});
	}
}
