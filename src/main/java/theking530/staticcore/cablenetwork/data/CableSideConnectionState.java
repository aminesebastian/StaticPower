package theking530.staticcore.cablenetwork.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CableSideConnectionState {
	public enum CableConnectionType {
		NONE, CABLE, TILE_ENTITY
	}

	private CableConnectionType connectionType;
	private ItemStack attachment;
	private ItemStack cover;
	private boolean disabled;

	public CableSideConnectionState(CableConnectionType connectionType, ItemStack attachment, ItemStack cover, boolean disabled) {
		this.connectionType = connectionType;
		this.attachment = attachment;
		this.cover = cover;
		this.disabled = disabled;
	}

	public CableConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(CableConnectionType connectionType) {
		this.connectionType = connectionType;
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

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putByte("t", (byte) connectionType.ordinal());
		output.put("a", attachment.serializeNBT());
		output.put("c", cover.serializeNBT());
		output.putBoolean("d", disabled);
		return output;
	}

	public static CableSideConnectionState deserialize(CompoundTag tag) {
		return new CableSideConnectionState(CableConnectionType.values()[tag.getByte("t")], ItemStack.of(tag.getCompound("a")), ItemStack.of(tag.getCompound("c")),
				tag.getBoolean("d"));
	}

	public static CableSideConnectionState createEmpty() {
		return new CableSideConnectionState(CableConnectionType.NONE, ItemStack.EMPTY, ItemStack.EMPTY, false);
	}

	@Override
	public String toString() {
		return "CableSideConnectionState [connectionType=" + connectionType + ", attachment=" + attachment + ", cover=" + cover + ", disabled=" + disabled + "]";
	}
}
