package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeLong(craftResponseId);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		craftResponseId = buffer.readLong();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			Container container = serverPlayer.openContainer;
			if (container instanceof AbstractContainerDigistoreTerminal && container.windowId == windowId) {
				AbstractContainerDigistoreTerminal<?> digistoreContainer = (AbstractContainerDigistoreTerminal<?>) container;
				digistoreContainer.getDigistoreNetwork().ifPresent(module -> {
					module.getCraftingManager().cancelCraftingRequest(craftResponseId);
				});
			}
		});
	}
}
