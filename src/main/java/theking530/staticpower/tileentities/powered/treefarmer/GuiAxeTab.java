package theking530.staticpower.tileentities.powered.treefarmer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiAxeTab extends BaseGuiTab {
	private final TileEntityTreeFarm treeFarmer;
	private final StaticPowerContainer container;
	private StaticPowerContainerSlot slot;

	public GuiAxeTab(StaticPowerContainer container, TileEntityTreeFarm treeFarmer) {
		super("Lumber Axe", Color.EIGHT_BIT_WHITE, 0, 0, GuiTextures.ORANGE_TAB, Items.IRON_AXE);
		this.container = container;
		this.treeFarmer = treeFarmer;
		this.drawTitle = false;
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Add the slots.
		slot = new StaticPowerContainerSlot(new ItemStack(Items.IRON_AXE), 0.3f, treeFarmer.inputInventory, 0, this.xPosition + 4, 0);
		container.addSlotGeneric(slot);

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.windowId);
		msg.addSlot(treeFarmer.inputInventory, 0, 0, 0);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Set the is initial state.
		slot.setEnabledState(false);

		// Update the icon if it should be updated.
		if (slot.getHasStack()) {
			this.icon = new ItemDrawable(slot.getStack());
		}
	}

	@Override
	protected void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
		slot.yPos = this.yPosition + 4;
	}

	@Override
	protected void onTabOpened() {
		slot.setEnabledState(true);
		slot.yPos = this.yPosition + 4;
	}

	@Override
	protected void onTabClosing() {
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
