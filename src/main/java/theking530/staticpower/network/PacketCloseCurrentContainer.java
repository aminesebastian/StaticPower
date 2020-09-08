package theking530.staticpower.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminal.autocrafting.ContainerCraftingAmount;

public class PacketCloseCurrentContainer extends NetworkMessage {
	protected int windowId;

	public PacketCloseCurrentContainer() {

	}

	public PacketCloseCurrentContainer(int windowId) {
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
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof ContainerCraftingAmount && container.windowId == windowId) {
				Minecraft.getInstance().displayGuiScreen(null);
			}
		});
	}
}
