package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.network.NetworkMessage;

public class PacketSyncDigistoreInventory extends NetworkMessage {
	protected int windowId;
	private CompoundNBT inventory;

	public PacketSyncDigistoreInventory() {

	}

	public PacketSyncDigistoreInventory(int windowId, DigistoreInventorySnapshot inventory) {
		this.windowId = windowId;
		this.inventory = inventory.serialize();
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(inventory, out);
			buffer.writeByteArray(out.toByteArray());
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to serialize a JEI recipe for display in an IJEIReipceTransferHandler.", e);
		}
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();

		try {
			byte[] compressedData = buffer.readByteArray();
			inventory = CompressedStreamTools.readCompressed(new ByteArrayInputStream(compressedData));
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to deserialize a JEI recipe for display in an IJEIReipceTransferHandler.", e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container container = Minecraft.getInstance().player.openContainer;
			if (container instanceof AbstractContainerDigistoreTerminal && container.windowId == windowId) {
				DigistoreInventorySnapshot snapshot = DigistoreInventorySnapshot.deserialize(inventory);
				((AbstractContainerDigistoreTerminal) container).syncContentsFromServer(snapshot);
			}
		});
	}
}