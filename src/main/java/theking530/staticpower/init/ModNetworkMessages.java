package theking530.staticpower.init;

import theking530.staticcore.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticcore.gui.widgets.tabs.PacketSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.PacketCableAttachmentRedstoneSync;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.PacketRedstoneComponentSync;
import theking530.staticpower.cables.fluid.FluidCableUpdatePacket;
import theking530.staticpower.cables.heat.HeatCableUpdatePacket;
import theking530.staticpower.cables.item.ItemCableAddedPacket;
import theking530.staticpower.cables.item.ItemCableRemovedPacket;
import theking530.staticpower.integration.JEI.JEIRecipeTransferPacket;
import theking530.staticpower.items.cableattachments.digistoreterminal.PacketDigistoreTerminalFilters;
import theking530.staticpower.items.itemfilter.PacketItemFilter;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.network.TileEntityBasicSyncPacket;
import theking530.staticpower.tileentities.components.fluids.PacketFluidContainerComponent;
import theking530.staticpower.tileentities.components.fluids.PacketFluidTankComponent;
import theking530.staticpower.tileentities.components.heat.PacketHeatStorageComponent;
import theking530.staticpower.tileentities.components.loopingsound.LoopingSoundPacketStart;
import theking530.staticpower.tileentities.components.loopingsound.LoopingSoundPacketStop;
import theking530.staticpower.tileentities.components.power.PacketEnergyStorageComponent;
import theking530.staticpower.tileentities.digistorenetwork.digistore.PacketLockDigistore;
import theking530.staticpower.tileentities.powered.autocrafter.PacketLockInventorySlot;
import theking530.staticpower.tileentities.powered.battery.BatteryControlSyncPacket;

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
		StaticPowerMessageHandler.registerMessage(FluidCableUpdatePacket.class);
		StaticPowerMessageHandler.registerMessage(HeatCableUpdatePacket.class);
		StaticPowerMessageHandler.registerMessage(BatteryControlSyncPacket.class);
		StaticPowerMessageHandler.registerMessage(PacketDigistoreTerminalFilters.class);
		StaticPowerMessageHandler.registerMessage(JEIRecipeTransferPacket.class);
		StaticPowerMessageHandler.registerMessage(PacketEnergyStorageComponent.class);
		StaticPowerMessageHandler.registerMessage(PacketHeatStorageComponent.class);
		StaticPowerMessageHandler.registerMessage(PacketFluidTankComponent.class);
		StaticPowerMessageHandler.registerMessage(PacketLockInventorySlot.class);
		StaticPowerMessageHandler.registerMessage(PacketGuiTabAddSlots.class);
		StaticPowerMessageHandler.registerMessage(PacketFluidContainerComponent.class);
		StaticPowerMessageHandler.registerMessage(LoopingSoundPacketStart.class);		
		StaticPowerMessageHandler.registerMessage(LoopingSoundPacketStop.class);	
	}
}
