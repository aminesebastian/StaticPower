package theking530.staticpower.init;

import theking530.staticcore.data.StaticPowerGameDataLoadPacket;
import theking530.staticcore.data.StaticPowerGameDataSyncPacket;
import theking530.staticcore.gui.widgets.tabs.PacketSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.PacketCableAttachmentRedstoneSync;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.PacketRedstoneComponentSync;
import theking530.staticcore.gui.widgets.tabs.slottabs.PacketGuiTabAddSlots;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponentSyncPacket;
import theking530.staticpower.blockentities.components.fluids.PacketFluidContainerComponent;
import theking530.staticpower.blockentities.components.fluids.PacketFluidTankComponent;
import theking530.staticpower.blockentities.components.heat.PacketHeatStorageComponent;
import theking530.staticpower.blockentities.components.items.PacketLockInventorySlot;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundPacketStart;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundPacketStop;
import theking530.staticpower.blockentities.components.team.PacketSetTeamComponentTeam;
import theking530.staticpower.blockentities.digistorenetwork.digistore.PacketLockDigistore;
import theking530.staticpower.blockentities.machines.packager.PacketPackagerSizeChange;
import theking530.staticpower.blockentities.nonpowered.solderingtable.PacketSyncSolderingFakeSlotRecipe;
import theking530.staticpower.blockentities.power.powermonitor.PacketPowerMonitorSync;
import theking530.staticpower.blockentities.power.transformer.TransformerControlSyncPacket;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.PacketClearDigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.PacketRestorePreviousCraftingRecipe;
import theking530.staticpower.cables.attachments.digistore.patternencoder.PacketPatternEncoderClearRecipe;
import theking530.staticpower.cables.attachments.digistore.patternencoder.PacketPatternEncoderEncode;
import theking530.staticpower.cables.attachments.digistore.patternencoder.PacketPatternEncoderRecipeTypeChange;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketDigistoreFakeSlotClicked;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketDigistoreTerminalFilters;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketGetCurrentCraftingQueue;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketReturnCurrentCraftingQueue;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketSyncDigistoreInventory;
import theking530.staticpower.cables.digistore.crafting.network.PacketCancelDigistoreCraftingRequest;
import theking530.staticpower.cables.digistore.crafting.network.PacketMakeDigistoreCraftingRequest;
import theking530.staticpower.cables.digistore.crafting.network.PacketRequestDigistoreCraftRecalculation;
import theking530.staticpower.cables.digistore.crafting.network.PacketSimulateDigistoreCraftingRequestResponse;
import theking530.staticpower.cables.fluid.FluidCableUpdatePacket;
import theking530.staticpower.cables.heat.HeatCableUpdatePacket;
import theking530.staticpower.cables.item.ItemCableAddedPacket;
import theking530.staticpower.cables.item.ItemCableRemovedPacket;
import theking530.staticpower.cables.redstone.network.PacketUpdateRedstoneCableConfiguration;
import theking530.staticpower.container.PacketCloseCurrentContainer;
import theking530.staticpower.container.PacketRevertToParentContainer;
import theking530.staticpower.entities.player.datacapability.PacketSyncStaticPowerPlayerDataCapability;
import theking530.staticpower.integration.JEI.JEIRecipeTransferPacket;
import theking530.staticpower.items.itemfilter.PacketItemFilter;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.network.TileEntityBasicSyncPacket;
import theking530.staticpower.teams.research.network.PacketSetSelectedResearch;

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
		StaticPowerMessageHandler.registerMessage(TransformerControlSyncPacket.class);
		StaticPowerMessageHandler.registerMessage(PacketDigistoreTerminalFilters.class);
		StaticPowerMessageHandler.registerMessage(JEIRecipeTransferPacket.class);
		StaticPowerMessageHandler.registerMessage(PacketHeatStorageComponent.class);
		StaticPowerMessageHandler.registerMessage(PacketFluidTankComponent.class);
		StaticPowerMessageHandler.registerMessage(PacketLockInventorySlot.class);
		StaticPowerMessageHandler.registerMessage(PacketGuiTabAddSlots.class);
		StaticPowerMessageHandler.registerMessage(PacketFluidContainerComponent.class);
		StaticPowerMessageHandler.registerMessage(LoopingSoundPacketStart.class);
		StaticPowerMessageHandler.registerMessage(LoopingSoundPacketStop.class);
		StaticPowerMessageHandler.registerMessage(PacketPatternEncoderRecipeTypeChange.class);
		StaticPowerMessageHandler.registerMessage(PacketPatternEncoderEncode.class);
		StaticPowerMessageHandler.registerMessage(PacketPatternEncoderClearRecipe.class);
		StaticPowerMessageHandler.registerMessage(PacketRequestDigistoreCraftRecalculation.class);
		StaticPowerMessageHandler.registerMessage(PacketSimulateDigistoreCraftingRequestResponse.class);
		StaticPowerMessageHandler.registerMessage(PacketMakeDigistoreCraftingRequest.class);
		StaticPowerMessageHandler.registerMessage(PacketCloseCurrentContainer.class);
		StaticPowerMessageHandler.registerMessage(PacketRevertToParentContainer.class);
		StaticPowerMessageHandler.registerMessage(PacketGetCurrentCraftingQueue.class);
		StaticPowerMessageHandler.registerMessage(PacketReturnCurrentCraftingQueue.class);
		StaticPowerMessageHandler.registerMessage(PacketCancelDigistoreCraftingRequest.class);
		StaticPowerMessageHandler.registerMessage(PacketRestorePreviousCraftingRecipe.class);
		StaticPowerMessageHandler.registerMessage(PacketPackagerSizeChange.class);
		StaticPowerMessageHandler.registerMessage(PacketSyncStaticPowerPlayerDataCapability.class);
		StaticPowerMessageHandler.registerMessage(PacketClearDigistoreCraftingTerminal.class);
		StaticPowerMessageHandler.registerMessage(PacketUpdateRedstoneCableConfiguration.class);
		StaticPowerMessageHandler.registerMessage(PacketSyncSolderingFakeSlotRecipe.class);

		StaticPowerMessageHandler.registerMessage(PacketSyncDigistoreInventory.class);
		StaticPowerMessageHandler.registerMessage(PacketDigistoreFakeSlotClicked.class);

		StaticPowerMessageHandler.registerMessage(PacketPowerMonitorSync.class);
		StaticPowerMessageHandler.registerMessage(PowerStorageComponentSyncPacket.class);

		StaticPowerMessageHandler.registerMessage(StaticPowerGameDataSyncPacket.class);
		StaticPowerMessageHandler.registerMessage(StaticPowerGameDataLoadPacket.class);
		StaticPowerMessageHandler.registerMessage(PacketSetTeamComponentTeam.class);
		StaticPowerMessageHandler.registerMessage(PacketSetSelectedResearch.class);
	}
}
