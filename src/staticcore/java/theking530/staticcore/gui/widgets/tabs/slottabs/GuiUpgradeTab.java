package theking530.staticcore.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.utilities.InventoryUtilities;

@OnlyIn(Dist.CLIENT)
public class GuiUpgradeTab extends BaseGuiTab {
	private final InventoryComponent upgradesInventory;
	private final List<StaticPowerContainerSlot> slots;
	private final StaticPowerContainer container;

	public GuiUpgradeTab(StaticPowerContainer container, InventoryComponent upgradesInventory) {
		this(container, upgradesInventory, ModItems.BasicSpeedUpgrade.get());
	}

	public GuiUpgradeTab(StaticPowerContainer container, InventoryComponent upgradesInventory, Item icon) {
		super("Upgrades", SDColor.EIGHT_BIT_WHITE, 26, 83, new SDColor(1f, 1.0f, 0.1f), icon);
		this.container = container;
		this.slots = new ArrayList<StaticPowerContainerSlot>();
		this.upgradesInventory = upgradesInventory;
		this.showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
		this.drawTitle = false;
	}

	@Override
	protected void initialized() {
		// Allocate the packet.
		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.containerId);

		// Add the slots.
		for (int i = 0; i < upgradesInventory.getSlots(); i++) {
			StaticPowerContainerSlot slot;
			container.addSlotGeneric(slot = new UpgradeItemSlot(upgradesInventory, i, 0, 0));
			slots.add(slot);
			msg.addSlot(upgradesInventory, i, 0, 0);
		}

		// Change the shape for a 4 slot upgrade inventory.
		if (upgradesInventory.getSlots() == 1) {
			this.setHeight(18);
		} else if (upgradesInventory.getSlots() == 4) {
			this.setSize(18, 38);
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
	protected void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);
		positionSlots();
	}

	@Override
	protected void onTabOpened() {
		for (StaticPowerContainerSlot slot : slots) {
			slot.setEnabledState(true);
		}
		positionSlots();
		showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
	}

	@Override
	protected void onTabClosing() {
		for (StaticPowerContainerSlot slot : slots) {
			slot.setEnabledState(false);
		}
		showNotificationBadge = !InventoryUtilities.isInventoryEmpty(upgradesInventory);
	}

	protected void positionSlots() {
		if (slots.size() == 1) {
			setExpandedSize(26, 45);
			slots.get(0).x = (int) (this.getXPosition() + 6);
			slots.get(0).y = (int) (this.getYPosition() + 23);
		} else if (slots.size() == 3) {
			setExpandedSize(26, 83);
			for (int i = 0; i < slots.size(); i++) {
				if (getTabSide() == TabSide.RIGHT) {
					slots.get(i).x = (int) (this.getXPosition() + 5);
				} else {
					slots.get(i).x = (int) (this.getXPosition() + 6);
				}
				slots.get(i).y = (int) (this.getYPosition() + 24 + (i * 18));
			}
		} else if (slots.size() == 4) {
			setExpandedSize(44, 64);
			int xOffset = -18;
			for (int i = 0; i < slots.size(); i++) {
				slots.get(i).x = (int) (this.getXPosition() + 24 + ((i / 2) * xOffset));
				slots.get(i).y = (int) (this.getYPosition() + 24 + ((i % 2) * 18));
			}
		}
	}
}
