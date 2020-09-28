package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.utilities.ItemUtilities;

public class PackageLargeItemStackSync extends NetworkMessage {
	protected int windowId;
	protected int slot;
	private ItemStack itemstack;

	public PackageLargeItemStackSync() {

	}

	public PackageLargeItemStackSync(int windowId, int slot, ItemStack itemstack) {
		this.windowId = windowId;
		this.slot = slot;
		this.itemstack = itemstack;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeByte(this.windowId);
		buffer.writeInt(this.slot);
		ItemUtilities.writeLargeStackItemToBuffer(itemstack, false, buffer);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		this.windowId = buffer.readUnsignedByte();
		this.slot = buffer.readInt();
		this.itemstack = ItemUtilities.readLargeStackItemFromBuffer(buffer);
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player.openContainer.windowId == windowId) {
				Minecraft.getInstance().getTutorial().handleSetSlot(itemstack);
				player.openContainer.putStackInSlot(slot, itemstack);
			}
		});
	}
}
