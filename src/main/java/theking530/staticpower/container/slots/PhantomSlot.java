package theking530.staticpower.container.slots;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import theking530.staticpower.utilities.ItemUtilities;

public class PhantomSlot extends StaticPowerContainerSlot {
	private boolean limitToOnePerItem;
	private IItemHandlerModifiable itemHandler;

	public PhantomSlot(IItemHandlerModifiable itemHandler, int index, int x, int y, boolean limitToOnePerItem) {
		super(itemHandler, index, x, y);
		this.limitToOnePerItem = limitToOnePerItem;
		this.itemHandler = itemHandler;
	}

	public void insertPhantomItem(ItemStack stack, int amount) {
		if (itemHandler.getStackInSlot(slotNumber).isEmpty()) {
			ItemStack copy = stack.copy();
			if (limitToOnePerItem) {
				copy.setCount(1);
			} else {
				copy.setCount(amount);
			}

			itemHandler.setStackInSlot(slotNumber, copy);
		} else if (ItemUtilities.areItemStacksStackable(stack, itemHandler.getStackInSlot(slotNumber))) {
			if (!limitToOnePerItem) {
				itemHandler.getStackInSlot(slotNumber).grow(amount);
			}
		} else {
			clearPhantom();
		}
	}

	public void decreasePhantomCount(int amount) {
		itemHandler.getStackInSlot(slotNumber).shrink(amount);
	}

	public void clearPhantom() {
		itemHandler.setStackInSlot(slotNumber, ItemStack.EMPTY);
	}

	@Override
	public int getSlotStackLimit() {
		return Integer.MAX_VALUE;
	}
}
