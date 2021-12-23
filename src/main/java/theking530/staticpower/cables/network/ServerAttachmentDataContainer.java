package theking530.staticpower.cables.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class ServerAttachmentDataContainer {
	private final ResourceLocation id;
	private final Direction side;
	private final CompoundTag dataTag;

	public ServerAttachmentDataContainer(Direction side) {
		this(new ResourceLocation("empty"), side, new CompoundTag());
	}

	public ServerAttachmentDataContainer(ResourceLocation id, Direction side) {
		this(id, side, new CompoundTag());
	}

	public ServerAttachmentDataContainer(ResourceLocation id, Direction side, CompoundTag dataTag) {
		this.id = id;
		this.side = side;
		this.dataTag = dataTag;
	}

	public void addProperty(String key, String value) {
		dataTag.putString(key, value);
	}

	public boolean hasProperty(String key) {
		return dataTag.contains(key);
	}

	public void addProperty(String key, boolean value) {
		dataTag.putBoolean(key, value);
	}

	public boolean getBooleanProperty(String key) {
		return dataTag.getBoolean(key);
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("id", id.toString());
		output.putByte("side", (byte) side.ordinal());
		output.put("data", dataTag);
		return output;
	}

	public static ServerAttachmentDataContainer createFromTag(CompoundTag tag) {
		ResourceLocation id = new ResourceLocation(tag.getString("id"));
		Direction side = Direction.values()[tag.getByte("side")];
		CompoundTag data = tag.getCompound("data");
		return new ServerAttachmentDataContainer(id, side, data);
	}
}
