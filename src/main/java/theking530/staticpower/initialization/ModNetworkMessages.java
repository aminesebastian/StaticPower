package theking530.staticpower.initialization;

import theking530.api.gui.widgets.tabs.PacketRedstoneTab;
import theking530.api.gui.widgets.tabs.PacketSideConfigTab;
import theking530.staticpower.items.itemfilter.PacketItemFilter;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.network.TileEntityBasicSyncPacket;

public class ModNetworkMessages {
	public static void init() {
		StaticPowerMessageHandler.registerMessage(PacketRedstoneTab.class);
		StaticPowerMessageHandler.registerMessage(PacketSideConfigTab.class);
		StaticPowerMessageHandler.registerMessage(PacketItemFilter.class);
		StaticPowerMessageHandler.registerMessage(TileEntityBasicSyncPacket.class);
	}
}
