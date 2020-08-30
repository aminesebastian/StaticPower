package theking530.staticcore.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModUpgrades;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class GuiUpgradeTab extends BaseGuiTab {
	private final InventoryComponent upgradesInventory;
	private final List<Integer> slotIndecies;
	private final StaticPowerContainer container;

	public GuiUpgradeTab(StaticPowerContainer container, InventoryComponent upgradesInventory) {
		super("Upgrades", 0, 57, GuiTextures.YELLOW_TAB, ModUpgrades.BasicSpeedUpgrade);
		this.container = container;
		this.slotIndecies = new ArrayList<Integer>();
		this.upgradesInventory = upgradesInventory;
		showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Add the slots.
		System.out.println(this.guiXOffset);
		container.addSlotGeneric(new UpgradeItemSlot(upgradesInventory, 0, 4 + guiXOffset, 24 + guiYOffset));
		slotIndecies.add(container.inventorySlots.size() - 1);

		container.addSlotGeneric(new UpgradeItemSlot(upgradesInventory, 1, 4 + guiXOffset, 42 + guiYOffset));
		slotIndecies.add(container.inventorySlots.size() - 1);

		container.addSlotGeneric(new UpgradeItemSlot(upgradesInventory, 2, 4 + guiXOffset, 60 + guiYOffset));
		slotIndecies.add(container.inventorySlots.size() - 1);

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.windowId);
		msg.addSlot(upgradesInventory, 0, 4 + guiXOffset, 24);
		msg.addSlot(upgradesInventory, 1, 4 + guiXOffset, 45);
		msg.addSlot(upgradesInventory, 2, 4 + guiXOffset, 60);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Set the is initial state.
		for (int index : slotIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(false);
		}

		// Initialize as closed.
		onTabClosing();
	}

	@Override
	protected void onTabOpened() {
		for (int index : slotIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(true);
		}
		showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
	}

	@Override
	protected void onTabClosing() {
		for (int index : slotIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(false);
		}
		showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
	}
}
