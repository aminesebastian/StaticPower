package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.ContainerCraftingAmount;
import theking530.staticpower.network.NetworkMessage;

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
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeItemStack(item);
		buffer.writeInt(amount);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		item = buffer.readItemStack();
		amount = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			Container container = serverPlayer.openContainer;
			if (container instanceof ContainerCraftingAmount && container.windowId == windowId) {
				ContainerCraftingAmount craftingContainer = (ContainerCraftingAmount) container;
				craftingContainer.updateCraftingResponse(item, amount);
			}
		});
	}
}
