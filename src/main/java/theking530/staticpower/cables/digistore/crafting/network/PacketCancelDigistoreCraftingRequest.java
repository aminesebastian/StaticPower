package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.network.NetworkMessage;

public class PacketCancelDigistoreCraftingRequest extends NetworkMessage {
	protected int windowId;
	protected long craftResponseId;

	public PacketCancelDigistoreCraftingRequest() {

	}

	public PacketCancelDigistoreCraftingRequest(int windowId, long craftResponseId) {
		this.windowId = windowId;
		this.craftResponseId = craftResponseId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeLong(craftResponseId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		craftResponseId = buffer.readLong();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			AbstractContainerMenu container = serverPlayer.containerMenu;
			if (container instanceof AbstractContainerDigistoreTerminal && container.containerId == windowId) {
				AbstractContainerDigistoreTerminal<?> digistoreContainer = (AbstractContainerDigistoreTerminal<?>) container;
				digistoreContainer.getDigistoreNetwork().ifPresent(module -> {
					module.getCraftingManager().cancelCraftingRequest(craftResponseId);
				});
			}
		});
	}
}
