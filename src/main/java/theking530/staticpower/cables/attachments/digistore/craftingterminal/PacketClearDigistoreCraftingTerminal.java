package theking530.staticpower.cables.attachments.digistore.craftingterminal;

import java.util.function.Supplier;

import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketClearDigistoreCraftingTerminal extends NetworkMessage {
	protected int windowId;

	public PacketClearDigistoreCraftingTerminal() {

	}

	public PacketClearDigistoreCraftingTerminal(int windowId) {
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
			Container container = ctx.get().getSender().openContainer;
			if (container instanceof ContainerDigistoreCraftingTerminal && container.windowId == windowId) {
				ContainerDigistoreCraftingTerminal digistoreContainer = (ContainerDigistoreCraftingTerminal) container;
				digistoreContainer.clearCraftingSlots(ctx.get().getSender());
			}
		});
	}
}
