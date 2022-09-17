package theking530.staticpower.container.slots;

import net.minecraft.world.item.ItemStack;
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
		if (itemHandler.getStackInSlot(getSlotIndex()).isEmpty()) {
			ItemStack copy = stack.copy();
			if (limitToOnePerItem) {
				copy.setCount(1);
			} else {
				copy.setCount(amount);
			}

			itemHandler.setStackInSlot(getSlotIndex(), copy);
		} else if (ItemUtilities.areItemStacksStackable(stack, itemHandler.getStackInSlot(getSlotIndex()))) {
			if (!limitToOnePerItem) {
				itemHandler.getStackInSlot(getSlotIndex()).grow(amount);
			}
		} else {
			clearPhantom();
		}
	}

	public PhantomSlot setLimitToSingleItem(boolean limit) {
		limitToOnePerItem = limit;
		if (limit) {
			itemHandler.getStackInSlot(getSlotIndex()).setCount(1);
		}
		return this;
	}

	public void decreasePhantomCount(int amount) {
		itemHandler.getStackInSlot(getSlotIndex()).shrink(amount);
	}

	public void clearPhantom() {
		itemHandler.setStackInSlot(getSlotIndex(), ItemStack.EMPTY);
	}

	@Override
	public int getMaxStackSize() {
		return Integer.MAX_VALUE;
	}
}
