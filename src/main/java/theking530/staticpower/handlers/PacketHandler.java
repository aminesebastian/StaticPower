package theking530.staticpower.handlers;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.gui.widgets.PacketPowerControlTab;
import theking530.staticpower.client.gui.widgets.PacketRedstoneTab;
import theking530.staticpower.client.gui.widgets.PacketSideConfigTab;
import theking530.staticpower.items.itemfilter.PacketItemFilter;
import theking530.staticpower.machines.batteries.PacketGuiBattery;
import theking530.staticpower.machines.signalmultiplier.PacketSignalMultiplier;

public class PacketHandler {
	public static SimpleNetworkWrapper net;
	
	public static void initPackets() {
		net = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
		registerMessage(PacketSignalMultiplier.Message.class, PacketSignalMultiplier.class);
		registerMessage(PacketGuiBattery.Message.class, PacketGuiBattery.class);
		registerMessage(PacketRedstoneTab.Message.class, PacketRedstoneTab.class);
		registerMessage(PacketPowerControlTab.Message.class, PacketPowerControlTab.class);
		registerMessage(PacketSideConfigTab.Message.class, PacketSideConfigTab.class);
		registerMessage(PacketItemFilter.Message.class, PacketItemFilter.class);
	}
	private static int nextPacketId = 0;
	  
	private static void registerMessage(Class packet, Class message) {
	    net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
	    net.registerMessage(packet, message, nextPacketId, Side.SERVER);
	    nextPacketId++;
	  }
}

