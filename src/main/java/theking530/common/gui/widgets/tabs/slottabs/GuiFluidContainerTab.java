package theking530.common.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Items;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticpower.client.container.StaticPowerContainer;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.InventoryComponent;

public class GuiFluidContainerTab extends BaseGuiTab {
	private final InventoryComponent fluidContainerInventory;
	private final List<Integer> fluidConatinerInventoryIndecies;
	private final StaticPowerContainer container;

	public GuiFluidContainerTab(StaticPowerContainer container, InventoryComponent fluidContainerInventory) {
		super(1, 42, GuiTextures.AQUA_TAB, Items.BUCKET);
		this.container = container;
		this.fluidConatinerInventoryIndecies = new ArrayList<Integer>();
		this.fluidContainerInventory = fluidContainerInventory;
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Add the slots.
		container.addSlot(new FluidContainerSlot(fluidContainerInventory, Items.BUCKET, 0, -18, 23 + this.guiYOffset));
		fluidConatinerInventoryIndecies.add(container.inventorySlots.size() - 1);

		container.addSlot(new FluidContainerSlot(fluidContainerInventory, Items.WATER_BUCKET, 1, -18, 45 + this.guiYOffset));
		fluidConatinerInventoryIndecies.add(container.inventorySlots.size() - 1);

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(fluidContainerInventory, container.windowId);
		msg.addSlot(0, -18, 23);
		msg.addSlot(1, -18, 45);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Initialize as closed.
		onTabClosing();
	}

	@Override
	protected void onTabOpened() {
		for (int index : fluidConatinerInventoryIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(true);
		}
	}

	@Override
	protected void onTabClosing() {
		for (int index : fluidConatinerInventoryIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(false);
		}
	}
}
