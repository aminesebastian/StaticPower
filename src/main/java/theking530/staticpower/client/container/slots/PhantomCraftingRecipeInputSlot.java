package theking530.staticpower.client.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PhantomCraftingRecipeInputSlot extends CraftingRecipeInputSlot {

	public PhantomCraftingRecipeInputSlot(IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean canTakeStack(PlayerEntity player) {
		super.canTakeStack(player);
		inventory.setInventorySlotContents(slotNumber, ItemStack.EMPTY);
		return false;
	}

	public boolean isItemValid(ItemStack itemStack) {
		ItemStack tempItemStack = itemStack.copy();
		tempItemStack.setCount(1);
		inventory.setInventorySlotContents(slotNumber, tempItemStack);
		return false;
	}
}
