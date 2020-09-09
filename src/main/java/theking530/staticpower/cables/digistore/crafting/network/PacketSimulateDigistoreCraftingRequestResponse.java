package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.ContainerCraftingAmount;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.network.NetworkMessage;

public class PacketSimulateDigistoreCraftingRequestResponse extends NetworkMessage {
	protected int windowId;
	protected CraftingRequestResponse response;

	public PacketSimulateDigistoreCraftingRequestResponse() {

	}

	public PacketSimulateDigistoreCraftingRequestResponse(int windowId, CraftingRequestResponse response) {
		this.windowId = windowId;
		this.response = response;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeCompoundTag(response.serialze());
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		response = CraftingRequestResponse.read(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof ContainerCraftingAmount && container.windowId == windowId) {
				ContainerCraftingAmount craftingContainer = (ContainerCraftingAmount) container;
				craftingContainer.onCraftingResponseUpdated(response);
			}
		});
	}
}
