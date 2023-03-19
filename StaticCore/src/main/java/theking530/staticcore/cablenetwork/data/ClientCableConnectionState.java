package theking530.staticcore.cablenetwork.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ClientCableConnectionState {
	private ItemStack attachment;
	private ItemStack cover;

	public ClientCableConnectionState(ItemStack attachment, ItemStack cover) {
		this.attachment = attachment;
		this.cover = cover;
	}

	public ItemStack getAttachment() {
		return attachment;
	}

	public boolean hasAttachment() {
		return !attachment.isEmpty();
	}

	public void setAttachment(ItemStack attachment) {
		this.attachment = attachment;
	}

	public ItemStack removeAttachment() {
		ItemStack existing = attachment;
		this.attachment = ItemStack.EMPTY;
		return existing;
	}

	public ItemStack getCover() {
		return cover;
	}

	public void setCover(ItemStack cover) {
		this.cover = cover;
	}

	public ItemStack removeCover() {
		ItemStack existing = cover;
		this.cover = ItemStack.EMPTY;
		return existing;
	}

	public boolean hasCover() {
		return !cover.isEmpty();
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.put("a", attachment.serializeNBT());
		output.put("c", cover.serializeNBT());
		return output;
	}

	public static ClientCableConnectionState deserialize(CompoundTag tag) {
		return new ClientCableConnectionState(ItemStack.of(tag.getCompound("a")), ItemStack.of(tag.getCompound("c")));
	}

	public static ClientCableConnectionState createEmpty() {
		return new ClientCableConnectionState(ItemStack.EMPTY, ItemStack.EMPTY);
	}

	@Override
	public String toString() {
		return "ClientCableSideConnectionState [attachment=" + attachment + ", cover=" + cover + "]";
	}
}
