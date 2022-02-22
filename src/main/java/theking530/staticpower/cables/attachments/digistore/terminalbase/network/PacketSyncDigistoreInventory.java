package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;

public class PacketSyncDigistoreInventory extends NetworkMessage {
	protected int windowId;
	private CompoundTag inventory;

	public PacketSyncDigistoreInventory() {

	}

	public PacketSyncDigistoreInventory(int windowId, DigistoreInventorySnapshot inventory) {
		this.windowId = windowId;
		this.inventory = inventory.serialize();
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			NbtIo.writeCompressed(inventory, out);
			buffer.writeByteArray(out.toByteArray());
		} catch (Exception e) {
			StaticPower.LOGGER.error(
					"An error occured when attempting to serialize a JEI recipe for display in an IJEIReipceTransferHandler.",
					e);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();

		try {
			byte[] compressedData = buffer.readByteArray();
			inventory = NbtIo.readCompressed(new ByteArrayInputStream(compressedData));
		} catch (Exception e) {
			StaticPower.LOGGER.error(
					"An error occured when attempting to deserialize a JEI recipe for display in an IJEIReipceTransferHandler.",
					e);
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
			if (container instanceof AbstractContainerDigistoreTerminal && container.containerId == windowId) {
				DigistoreInventorySnapshot snapshot = DigistoreInventorySnapshot.deserialize(inventory);
				((AbstractContainerDigistoreTerminal<?>) container).syncContentsFromServer(snapshot);
			}
		});
	}
}