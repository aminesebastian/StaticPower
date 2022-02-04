package theking530.staticpower.container;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.ContainerCraftingAmount;
import theking530.staticpower.network.NetworkMessage;

public class PacketCloseCurrentContainer extends NetworkMessage {
	protected int windowId;

	public PacketCloseCurrentContainer() {

	}

	public PacketCloseCurrentContainer(int windowId) {
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
			AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
			if (container instanceof ContainerCraftingAmount && container.containerId == windowId) {
				Minecraft.getInstance().setScreen(null);
			}
		});
	}
}
