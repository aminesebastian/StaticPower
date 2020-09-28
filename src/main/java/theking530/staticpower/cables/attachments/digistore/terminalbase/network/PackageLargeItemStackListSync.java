package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.utilities.ItemUtilities;

public class PackageLargeItemStackListSync extends NetworkMessage {
	protected int windowId;
	private List<ItemStack> itemStacks;

	public PackageLargeItemStackListSync() {

	}

	public PackageLargeItemStackListSync(int windowId, List<ItemStack> itemStacks) {
		this.windowId = windowId;
		this.itemStacks = itemStacks;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeByte(this.windowId);
		buffer.writeShort(this.itemStacks.size());

		for (ItemStack itemstack : this.itemStacks) {
			ItemUtilities.writeLargeStackItemToBuffer(itemstack, false, buffer);
		}

	}

	@Override
	public void decode(PacketBuffer buffer) {
		this.windowId = buffer.readUnsignedByte();
		int i = buffer.readShort();
		this.itemStacks = NonNullList.withSize(i, ItemStack.EMPTY);

		for (int j = 0; j < i; ++j) {
			this.itemStacks.set(j, ItemUtilities.readLargeStackItemFromBuffer(buffer));
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player.openContainer.windowId == windowId) {
				player.openContainer.setAll(itemStacks);
			}
		});
	}
}
