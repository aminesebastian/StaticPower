package theking530.staticpower.blockentities.powered.treefarmer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.PacketGuiTabAddSlots;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiAxeTab extends BaseGuiTab {
	private final BlockEntityTreeFarm treeFarmer;
	private final StaticPowerContainer container;
	private StaticPowerContainerSlot slot;

	public GuiAxeTab(StaticPowerContainer container, BlockEntityTreeFarm treeFarmer) {
		super("Lumber Axe", Color.EIGHT_BIT_WHITE, 26, 26, new Color(0.5f, 0.35f, 1.0f, 1.0f), Items.IRON_AXE);
		this.container = container;
		this.treeFarmer = treeFarmer;
		this.drawTitle = false;
	}

	@Override
	protected void initialized() {
		// Add the slots.
		slot = new StaticPowerContainerSlot(new ItemStack(Items.IRON_AXE), 0.3f, treeFarmer.inputInventory, 0, (int) (getXPosition() + 4), 0);
		container.addSlotGeneric(slot);

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.containerId);
		msg.addSlot(treeFarmer.inputInventory, 0, 0, 0);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Set the is initial state.
		slot.setEnabledState(false);

		// Update the icon if it should be updated.
		if (slot.hasItem()) {
			this.icon = new ItemDrawable(slot.getItem());
		}
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);
		slot.y = (int) (this.getYPosition() + 5);
		slot.x = -18;
	}

	@Override
	protected void onTabOpened() {
		slot.setEnabledState(true);
		slot.y = (int) (this.getYPosition() + 5);
		slot.x = -18;
		this.title = "";
	}

	@Override
	protected void onTabClosing() {
		slot.setEnabledState(false);
		this.title = "Lumber Axe";

		// Update the icon to either the default if the slot is empty, or the slot's
		// containted item.
		if (slot.hasItem()) {
			this.icon = new ItemDrawable(slot.getItem());
		} else {
			this.icon = new ItemDrawable(new ItemStack(Items.IRON_AXE));
		}
	}
}
