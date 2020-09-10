package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.DigistoreNetworkCraftingManager;
import theking530.staticpower.network.NetworkMessage;

public class PacketReturnCurrentCraftingQueue extends NetworkMessage {
	protected int windowId;
	protected CompoundNBT craftingQueue;

	public PacketReturnCurrentCraftingQueue() {

	}

	public PacketReturnCurrentCraftingQueue(int windowId, ListNBT serializedCraftingQueue) {
		this.windowId = windowId;
		this.craftingQueue = new CompoundNBT();
		this.craftingQueue.put("queue", serializedCraftingQueue);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeCompoundTag(craftingQueue);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		craftingQueue = buffer.readCompoundTag();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof AbstractContainerDigistoreTerminal && container.windowId == windowId) {
				AbstractContainerDigistoreTerminal<?> terminalContainer = (AbstractContainerDigistoreTerminal<?>) container;
				List<CraftingRequestResponse> craftingRequests = DigistoreNetworkCraftingManager.deserializeCraftingQueue(craftingQueue.getList("queue", Constants.NBT.TAG_COMPOUND));
				terminalContainer.updateCraftingQueue(craftingRequests);
			}
		});
	}
}
