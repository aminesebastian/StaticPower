package theking530.staticpower.tileentities.powered.treefarmer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.ItemDrawable;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticpower.client.container.StaticPowerContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiAxeTab extends BaseGuiTab {
	private final TileEntityTreeFarm treeFarmer;
	private final StaticPowerContainer container;
	private int slotIndex;

	public GuiAxeTab(StaticPowerContainer container, TileEntityTreeFarm treeFarmer) {
		super("Lumber Axe", 0, 0, GuiTextures.ORANGE_TAB, Items.IRON_AXE);
		this.container = container;
		this.treeFarmer = treeFarmer;
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Add the slots.
		container.addSlotGeneric(new StaticPowerContainerSlot(new ItemStack(Items.IRON_AXE), 0.3f, treeFarmer.inputInventory, 0, -18, 4 + guiYOffset));
		slotIndex = container.inventorySlots.size() - 1;

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.windowId);
		msg.addSlot(treeFarmer.inputInventory, 0, -17, 59);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Set the is initial state.
		StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(slotIndex);
		slot.setEnabledState(false);

		// Update the icon if it should be updated.
		if (slot.getHasStack()) {
			this.icon = new ItemDrawable(slot.getStack());
		}
	}

	@Override
	protected void onTabOpened() {
		StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(slotIndex);
		slot.setEnabledState(true);
	}

	@Override
	protected void onTabClosing() {
		StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(slotIndex);
		slot.setEnabledState(false);

		// Update the icon to either the default if the slot is empty, or the slot's
		// containted item.
		if (slot.getHasStack()) {
			this.icon = new ItemDrawable(slot.getStack());
		} else {
			this.icon = new ItemDrawable(new ItemStack(Items.IRON_AXE));
		}
	}
}
