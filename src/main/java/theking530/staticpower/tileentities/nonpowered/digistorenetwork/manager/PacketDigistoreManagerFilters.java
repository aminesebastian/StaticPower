package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.digistore.DigistoreInventorySortType;
import theking530.staticpower.network.NetworkMessage;

public class PacketDigistoreManagerFilters extends NetworkMessage {
	protected int windowId;
	protected String searchString;
	protected DigistoreInventorySortType sortType;
	protected boolean sortDescending;

	public PacketDigistoreManagerFilters() {

	}

	public PacketDigistoreManagerFilters(int windowId, String searchString, DigistoreInventorySortType sortType, boolean sortDescending) {
		this.windowId = windowId;
		this.searchString = searchString;
		this.sortType = sortType;
		this.sortDescending = sortDescending;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeString(searchString);
		buffer.writeInt(sortType.ordinal());
		buffer.writeBoolean(sortDescending);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		searchString = buffer.readString();
		sortType = DigistoreInventorySortType.values()[buffer.readInt()];
		sortDescending = buffer.readBoolean();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			if (player.openContainer.windowId == windowId) {
				ContainerDigistoreManager digistoreContainer = (ContainerDigistoreManager) player.openContainer;
				digistoreContainer.setFilterString(searchString);
				digistoreContainer.setSortType(sortType, sortDescending);
			}
		});
	}
}
