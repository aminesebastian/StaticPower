package theking530.staticcore.cablenetwork.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public class CableConnectionState extends ClientCableConnectionState {
	public enum CableConnectionType implements StringRepresentable {
		NONE("none"), DISABLED("disabled"), CABLE("cable"), DESTINATION("destination");

		private final String serializedName;

		private CableConnectionType(String serializedName) {
			this.serializedName = serializedName;
		}

		@Override
		public String getSerializedName() {
			return serializedName;
		}

		public boolean isConnected() {
			return this == CABLE || this == DESTINATION;
		}
	}

	private CableConnectionType connectionType;

	public CableConnectionState(CableConnectionType connectionType, ItemStack attachment, ItemStack cover) {
		super(attachment, cover);
		this.connectionType = connectionType;
	}

	public CableConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(CableConnectionType connectionType) {
		this.connectionType = connectionType;
	}

	public CompoundTag serialize() {
		CompoundTag output = super.serialize();
		output.putByte("t", (byte) connectionType.ordinal());
		return output;
	}

	public static CableConnectionState deserialize(CompoundTag tag) {
		return new CableConnectionState(CableConnectionType.values()[tag.getByte("t")], ItemStack.of(tag.getCompound("a")), ItemStack.of(tag.getCompound("c")));
	}

	public static CableConnectionState createEmpty() {
		return new CableConnectionState(CableConnectionType.NONE, ItemStack.EMPTY, ItemStack.EMPTY);
	}

	@Override
	public String toString() {
		return "CableSideConnectionState [connectionType=" + connectionType + ", attachment=" + getAttachment() + ", cover=" + getCover() + "]";
	}
}
