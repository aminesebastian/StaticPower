package theking530.common.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import theking530.common.gui.GuiTextures;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticpower.client.container.StaticPowerContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModUpgrades;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.InventoryComponent;

public class GuiUpgradeTab extends BaseGuiTab {
	private final InventoryComponent upgradesInventory;
	private final List<Integer> slotIndecies;
	private final StaticPowerContainer container;

	public GuiUpgradeTab(StaticPowerContainer container, InventoryComponent upgradesInventory) {
		super(1, 57, GuiTextures.YELLOW_TAB, ModUpgrades.BasicSpeedUpgrade);
		this.container = container;
		this.slotIndecies = new ArrayList<Integer>();
		this.upgradesInventory = upgradesInventory;
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Add the slots.
		container.addSlot(new UpgradeItemSlot(upgradesInventory, 0, -18, 24 + this.guiYOffset));
		slotIndecies.add(container.inventorySlots.size() - 1);

		container.addSlot(new UpgradeItemSlot(upgradesInventory, 1, -18, 42 + this.guiYOffset));
		slotIndecies.add(container.inventorySlots.size() - 1);

		container.addSlot(new UpgradeItemSlot(upgradesInventory, 2, -18, 60 + this.guiYOffset));
		slotIndecies.add(container.inventorySlots.size() - 1);

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(upgradesInventory, container.windowId);
		msg.addSlot(0, -18, 59);
		msg.addSlot(1, -18, 77);
		msg.addSlot(2, -18, 95);

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
	}

	@Override
	protected void onTabClosing() {
		for (int index : slotIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(false);
		}
	}
}
