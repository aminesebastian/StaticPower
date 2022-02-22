package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.DigistoreInventorySortType;
import theking530.staticpower.cables.attachments.digistore.terminalbase.DigistoreSyncedSearchMode;

public class PacketDigistoreTerminalFilters extends NetworkMessage {
	protected int windowId;
	protected String searchString;
	protected DigistoreInventorySortType sortType;
	protected DigistoreSyncedSearchMode searchMode;
	protected boolean sortDescending;

	public PacketDigistoreTerminalFilters() {

	}

	public PacketDigistoreTerminalFilters(int windowId, String searchString, DigistoreSyncedSearchMode searchMode, DigistoreInventorySortType sortType, boolean sortDescending) {
		this.windowId = windowId;
		this.searchString = searchString;
		this.searchMode = searchMode;
		this.sortType = sortType;
		this.sortDescending = sortDescending;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		writeStringOnServer(searchString, buffer);
		buffer.writeInt(searchMode.ordinal());
		buffer.writeInt(sortType.ordinal());
		buffer.writeBoolean(sortDescending);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		searchString = readStringOnServer(buffer);
		searchMode = DigistoreSyncedSearchMode.values()[buffer.readInt()];
		sortType = DigistoreInventorySortType.values()[buffer.readInt()];
		sortDescending = buffer.readBoolean();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player.containerMenu.containerId == windowId) {
				AbstractContainerDigistoreTerminal<?> digistoreContainer = (AbstractContainerDigistoreTerminal<?>) player.containerMenu;
				digistoreContainer.updateSortAndFilter(searchString, searchMode, sortType, sortDescending);
			}
		});
	}
}
