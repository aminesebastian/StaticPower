package theking530.staticpower.network;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import theking530.staticpower.utilities.ItemUtilities;

public class SSetSlotLargeItemStackPacket extends SSetSlotPacket {

	public SSetSlotLargeItemStackPacket() {
	}

	public SSetSlotLargeItemStackPacket(int windowIdIn, int slotIn, ItemStack itemIn) {
		this.windowId = windowIdIn;
		this.slot = slotIn;
		this.item = itemIn.copy();
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.windowId = buf.readByte();
		this.slot = buf.readShort();
		this.item = ItemUtilities.readLargeStackItemFromBuffer(buf);
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeByte(this.windowId);
		buf.writeShort(this.slot);
		ItemUtilities.writeLargeStackItemToBuffer(this.item, false, buf);
	}
}
