package theking530.staticpower.network;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.util.NonNullList;
import theking530.staticpower.utilities.ItemUtilities;

public class SWindowLargeItemsPacket extends SWindowItemsPacket {
	public SWindowLargeItemsPacket() {
	}

	public SWindowLargeItemsPacket(int p_i47317_1_, NonNullList<ItemStack> p_i47317_2_) {
		this.windowId = p_i47317_1_;
		this.itemStacks = NonNullList.withSize(p_i47317_2_.size(), ItemStack.EMPTY);

		for (int i = 0; i < this.itemStacks.size(); ++i) {
			this.itemStacks.set(i, p_i47317_2_.get(i).copy());
		}

	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.windowId = buf.readUnsignedByte();
		int i = buf.readShort();
		this.itemStacks = NonNullList.withSize(i, ItemStack.EMPTY);

		for (int j = 0; j < i; ++j) {
			this.itemStacks.set(j, ItemUtilities.readLargeStackItemFromBuffer(buf));
		}

	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeByte(this.windowId);
		buf.writeShort(this.itemStacks.size());

		for (ItemStack itemstack : this.itemStacks) {
			ItemUtilities.writeLargeStackItemToBuffer(itemstack, true, buf);
		}

	}
}
