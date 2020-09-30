package theking530.staticcore.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModUpgrades;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

@OnlyIn(Dist.CLIENT)
public class GuiUpgradeTab extends BaseGuiTab {
	private final InventoryComponent upgradesInventory;
	private final List<StaticPowerContainerSlot> slots;
	private final StaticPowerContainer container;

	public GuiUpgradeTab(StaticPowerContainer container, InventoryComponent upgradesInventory) {
		super("Upgrades", Color.EIGHT_BIT_WHITE, 0, 57, GuiTextures.YELLOW_TAB, ModUpgrades.BasicSpeedUpgrade);
		this.container = container;
		this.slots = new ArrayList<StaticPowerContainerSlot>();
		this.upgradesInventory = upgradesInventory;
		this.showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
		this.drawTitle = false;
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Allocate the packet.
		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.windowId);

		// Add the slots.
		for (int i = 0; i < 3; i++) {
			StaticPowerContainerSlot slot;
			container.addSlotGeneric(slot = new UpgradeItemSlot(upgradesInventory, i, this.xPosition + 4, 0));
			slots.add(slot);
			msg.addSlot(upgradesInventory, i, 0, 0);
		}

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Set the is initial state.
		for (StaticPowerContainerSlot slot : slots) {
			slot.setEnabledState(false);
		}

		// Initialize as closed.
		onTabClosing();
	}

	@Override
	protected void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
		for (int i = 0; i < slots.size(); i++) {
			slots.get(i).yPos = this.yPosition + 24 + (i * 18);
		}
	}

	@Override
	protected void onTabOpened() {
		for (int i = 0; i < slots.size(); i++) {
			slots.get(i).yPos = this.yPosition + 24 + (i * 18);
			slots.get(i).setEnabledState(true);
		}
		showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
	}

	@Override
	protected void onTabClosing() {
		for (StaticPowerContainerSlot slot : slots) {
			slot.setEnabledState(false);
		}
		showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
	}
}
