package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.DigistoreInventorySortType;
import theking530.staticpower.cables.attachments.digistore.terminalbase.DigistoreSearchMode;
import theking530.staticpower.network.NetworkMessage;

public class PacketDigistoreTerminalFilters extends NetworkMessage {
	protected int windowId;
	protected String searchString;
	protected DigistoreInventorySortType sortType;
	protected DigistoreSearchMode searchMode;
	protected boolean sortDescending;

	public PacketDigistoreTerminalFilters() {

	}

	public PacketDigistoreTerminalFilters(int windowId, String searchString, DigistoreSearchMode searchMode, DigistoreInventorySortType sortType, boolean sortDescending) {
		this.windowId = windowId;
		this.searchString = searchString;
		this.searchMode = searchMode;
		this.sortType = sortType;
		this.sortDescending = sortDescending;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		writeStringOnServer(searchString, buffer);
		buffer.writeInt(searchMode.ordinal());
		buffer.writeInt(sortType.ordinal());
		buffer.writeBoolean(sortDescending);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		searchString = readStringOnServer(buffer);
		searchMode = DigistoreSearchMode.values()[buffer.readInt()];
		sortType = DigistoreInventorySortType.values()[buffer.readInt()];
		sortDescending = buffer.readBoolean();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			if (player.openContainer.windowId == windowId) {
				AbstractContainerDigistoreTerminal<?> digistoreContainer = (AbstractContainerDigistoreTerminal<?>) player.openContainer;
				digistoreContainer.updateSortAndFilter(searchString, searchMode, sortType, sortDescending);
			}
		});
	}
}
