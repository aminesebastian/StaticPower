package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.DigistoreNetworkCraftingManager;
import theking530.staticpower.network.NetworkMessage;

public class PacketReturnCurrentCraftingQueue extends NetworkMessage {
	protected int windowId;
	protected CompoundTag craftingQueue;

	public PacketReturnCurrentCraftingQueue() {

	}

	public PacketReturnCurrentCraftingQueue(int windowId, ListTag serializedCraftingQueue) {
		this.windowId = windowId;
		this.craftingQueue = new CompoundTag();
		this.craftingQueue.put("queue", serializedCraftingQueue);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeNbt(craftingQueue);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		craftingQueue = buffer.readNbt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
			if (container instanceof AbstractContainerDigistoreTerminal && container.containerId == windowId) {
				AbstractContainerDigistoreTerminal<?> terminalContainer = (AbstractContainerDigistoreTerminal<?>) container;
				List<CraftingRequestResponse> craftingRequests = DigistoreNetworkCraftingManager.deserializeCraftingQueue(craftingQueue.getList("queue", Constants.NBT.TAG_COMPOUND));
				terminalContainer.updateCraftingQueue(craftingRequests);
			}
		});
	}
}
