package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerDigistoreManager extends StaticPowerTileEntityContainer<TileEntityDigistoreManager> {

	public ContainerDigistoreManager(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreManager) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreManager(int windowId, PlayerInventory playerInventory, TileEntityDigistoreManager owner) {
		super(ModContainerTypes.DIGISTORE_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addPlayerHotbar(getPlayerInventory(), 8, 126);
		addPlayerInventory(getPlayerInventory(), 8, 68);
	}

	// Shift Click Functionality
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex >= 3) {
				if (!mergeItemStack(itemstack1, 0, 3, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!mergeItemStack(itemstack1, 3, inventorySlots.size(), false)) {
					return ItemStack.EMPTY;
				}
			}
			slot.onSlotChanged();
		}

		return itemstack;
	}
}
