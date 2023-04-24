package theking530.staticcore.init;

import theking530.staticcore.blockentity.components.control.ProcesingComponentSyncPacket;
import theking530.staticcore.blockentity.components.control.processing.machine.MachineProcessingComponentSyncPacket;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponentSyncPacket;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponentSyncPacket;
import theking530.staticcore.blockentity.components.fluids.PacketFluidContainerComponent;
import theking530.staticcore.blockentity.components.fluids.PacketFluidTankComponent;
import theking530.staticcore.blockentity.components.heat.PacketHeatStorageComponent;
import theking530.staticcore.blockentity.components.items.PacketLockInventorySlot;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundPacketStart;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundPacketStop;
import theking530.staticcore.blockentity.components.team.PacketSetTeamComponentTeam;
import theking530.staticcore.cablenetwork.CableStateSyncPacket;
import theking530.staticcore.cablenetwork.CableStateSyncRequestPacket;
import theking530.staticcore.container.PacketCloseCurrentContainer;
import theking530.staticcore.container.PacketRevertToParentContainer;
import theking530.staticcore.data.gamedata.StaticPowerGameDataSyncPacket;
import theking530.staticcore.gui.widgets.tabs.PacketSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.PacketCableAttachmentRedstoneSync;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.PacketRedstoneComponentSync;
import theking530.staticcore.gui.widgets.tabs.slottabs.PacketGuiTabAddSlots;
import theking530.staticcore.network.BlockEntityBasicSyncPacket;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.metrics.PacketRecieveProductionMetrics;
import theking530.staticcore.productivity.metrics.PacketRecieveProductionTimeline;
import theking530.staticcore.productivity.metrics.PacketRequestProductionMetrics;
import theking530.staticcore.productivity.metrics.PacketRequestProductionTimeline;
import theking530.staticcore.research.network.PacketSetSelectedResearch;

public class StaticCoreNetworkMessages {
	public static void init() {
		StaticCoreMessageHandler.registerMessage(MachineProcessingComponentSyncPacket.class);
		StaticCoreMessageHandler.registerMessage(RecipeProcessingComponentSyncPacket.class);
		
		StaticCoreMessageHandler.registerMessage(PacketRedstoneComponentSync.class);
		StaticCoreMessageHandler.registerMessage(PacketCableAttachmentRedstoneSync.class);
		StaticCoreMessageHandler.registerMessage(PacketSideConfigTab.class);
		StaticCoreMessageHandler.registerMessage(BlockEntityBasicSyncPacket.class);
		StaticCoreMessageHandler.registerMessage(PacketHeatStorageComponent.class);
		StaticCoreMessageHandler.registerMessage(PacketFluidTankComponent.class);
		StaticCoreMessageHandler.registerMessage(PacketLockInventorySlot.class);
		StaticCoreMessageHandler.registerMessage(PacketGuiTabAddSlots.class);
		StaticCoreMessageHandler.registerMessage(PacketFluidContainerComponent.class);
		StaticCoreMessageHandler.registerMessage(LoopingSoundPacketStart.class);
		StaticCoreMessageHandler.registerMessage(LoopingSoundPacketStop.class);
		StaticCoreMessageHandler.registerMessage(PacketCloseCurrentContainer.class);
		StaticCoreMessageHandler.registerMessage(PacketRevertToParentContainer.class);

		StaticCoreMessageHandler.registerMessage(ProcesingComponentSyncPacket.class);

		StaticCoreMessageHandler.registerMessage(PowerStorageComponentSyncPacket.class);

		StaticCoreMessageHandler.registerMessage(StaticPowerGameDataSyncPacket.class);
		StaticCoreMessageHandler.registerMessage(PacketSetTeamComponentTeam.class);
		StaticCoreMessageHandler.registerMessage(PacketSetSelectedResearch.class);

		StaticCoreMessageHandler.registerMessage(CableStateSyncPacket.class);
		StaticCoreMessageHandler.registerMessage(CableStateSyncRequestPacket.class);

		StaticCoreMessageHandler.registerMessage(PacketRequestProductionMetrics.class);
		StaticCoreMessageHandler.registerMessage(PacketRecieveProductionMetrics.class);

		StaticCoreMessageHandler.registerMessage(PacketRequestProductionTimeline.class);
		StaticCoreMessageHandler.registerMessage(PacketRecieveProductionTimeline.class);
	}
}
