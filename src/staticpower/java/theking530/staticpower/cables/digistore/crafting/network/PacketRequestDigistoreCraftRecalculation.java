package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.ContainerCraftingAmount;

public class PacketRequestDigistoreCraftRecalculation extends NetworkMessage {
	protected int windowId;
	protected ItemStack item;
	protected int amount;

	public PacketRequestDigistoreCraftRecalculation() {

	}

	public PacketRequestDigistoreCraftRecalculation(int windowId, ItemStack item, int amount) {
		this.windowId = windowId;
		this.item = item;
		this.amount = amount;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeItem(item);
		buffer.writeInt(amount);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		item = buffer.readItem();
		amount = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			AbstractContainerMenu container = serverPlayer.containerMenu;
			if (container instanceof ContainerCraftingAmount && container.containerId == windowId) {
				ContainerCraftingAmount craftingContainer = (ContainerCraftingAmount) container;
				craftingContainer.updateCraftingResponse(item, amount);
			}
		});
	}
}
