package theking530.staticpower.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class BaseContainer extends Container{

	protected void addPlayerInventory(InventoryPlayer invPlayer, int xPosition, int yPosition) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, xPosition + j * 18, yPosition + i * 18));
			}
		}
	}
	protected void addPlayerHotbar(InventoryPlayer invPlayer, int xPosition, int yPosition) {
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(invPlayer, i, xPosition + i * 18, yPosition));
		}
	}
	protected ItemStack handleShiftClickToInventory(ItemStack stack, int maxTileEntitySlot, int slot) {
		 if (!this.mergeItemStack(stack, maxTileEntitySlot+28, maxTileEntitySlot+28+8, false)) {
             return ItemStack.EMPTY;
         }
         if (!this.mergeItemStack(stack, maxTileEntitySlot, maxTileEntitySlot+28, false)) {
             return ItemStack.EMPTY;
         }
		return stack;
	}
}
