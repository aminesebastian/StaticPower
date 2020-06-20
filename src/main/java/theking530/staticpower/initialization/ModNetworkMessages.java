package theking530.staticpower.initialization;

import theking530.api.gui.widgets.tabs.PacketSideConfigTab;
import theking530.api.gui.widgets.tabs.redstonecontrol.PacketCableAttachmentRedstoneSync;
import theking530.api.gui.widgets.tabs.redstonecontrol.PacketRedstoneComponentSync;
import theking530.staticpower.items.itemfilter.PacketItemFilter;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.network.TileEntityBasicSyncPacket;
import theking530.staticpower.tileentities.cables.network.modules.ItemCableAddedPacket;
import theking530.staticpower.tileentities.cables.network.modules.ItemCableRemovedPacket;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.PacketLockDigistore;

public class ModNetworkMessages {
	public static void init() {
		StaticPowerMessageHandler.registerMessage(PacketRedstoneComponentSync.class);
		StaticPowerMessageHandler.registerMessage(PacketCableAttachmentRedstoneSync.class);
		StaticPowerMessageHandler.registerMessage(PacketSideConfigTab.class);
		StaticPowerMessageHandler.registerMessage(PacketItemFilter.class);
		StaticPowerMessageHandler.registerMessage(PacketLockDigistore.class);
		StaticPowerMessageHandler.registerMessage(TileEntityBasicSyncPacket.class);
		StaticPowerMessageHandler.registerMessage(ItemCableAddedPacket.class);
		StaticPowerMessageHandler.registerMessage(ItemCableRemovedPacket.class);
	}
}
