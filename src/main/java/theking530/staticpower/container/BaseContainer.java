package theking530.staticpower.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

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
}
