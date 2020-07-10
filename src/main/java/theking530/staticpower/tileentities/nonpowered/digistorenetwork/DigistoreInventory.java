package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.DigistoreItemTracker;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreInventory implements Iterable<DigistoreItemTracker>, IDigistoreInventory, IItemHandler {
	private List<DigistoreItemTracker> slots;
	private boolean voidExcess;
	private int maximumStorage;

	public DigistoreInventory(int maxUniqueItems, int maximumStorage) {
		this.maximumStorage = maximumStorage;
		slots = new ArrayList<DigistoreItemTracker>();
		for (int i = 0; i < maxUniqueItems; i++) {
			slots.add(new DigistoreItemTracker());
		}
	}

	@Override
	public void setLockState(boolean locked) {
		for (DigistoreItemTracker slot : slots) {
			slot.setLocked(locked);
		}
	}

	@Override
	public int getCountForItem(ItemStack stack) {
		int slot = getSlotForItemStack(stack);
		if (slot >= 0) {
			return slots.get(slot).getCount();
		}
		return 0;
	}

	@Override
	public DigistoreItemTracker getItemTracker(int index) {
		return slots.get(index);
	}

	@Override
	public int getCurrentUniqueItemTypeCount() {
		int emptySlots = 0;
		for (DigistoreItemTracker slot : slots) {
			if (slot.isEmpty()) {
				emptySlots++;
			}
		}
		return slots.size() - emptySlots;
	}

	@Override
	public int getMaximumUniqueItemTypeCount() {
		return slots.size();
	}

	@Override
	public int getSlots() {
		return slots.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slots.get(slot).getStoredItem();
	}

	@Override
	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		int slot = getSlotForItemStack(stack);
		if (slot == -1) {
			for (int i = 0; i < slots.size(); i++) {
				if (slots.get(i).isEmpty()) {
					slot = i;
					break;
				}
			}
			if (slot == -1) {
				return stack;
			}
		}
		return insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (isFull()) {
			if (shouldVoidExcess()) {
				return ItemStack.EMPTY;
			} else {
				return stack;
			}
		}
		// Get the tracker.
		DigistoreItemTracker tracker = slots.get(slot);

		// Do nothing if empty.
		if (stack.isEmpty()) {
			return stack;
		}

		// Calculate the remaining storage space and the insertable amount.
		int remainingStorage = getRemainingStorage(true);
		int insertableAmount = Math.min(remainingStorage, stack.getCount());

		// Then, attempt to insert the item.
		if (tracker.getStoredItem().isEmpty() || ItemUtilities.areItemStacksStackable(tracker.getStoredItem(), stack)) {
			if (!simulate) {
				// If we aren't storing anything, set the stored item. Check the remaining
				// storage (Even though it isn't necessary, its a good edge case check).
				if (tracker.getStoredItem().isEmpty() && remainingStorage > 0) {
					tracker.setStoredItem(ItemHandlerHelper.copyStackWithSize(stack, 1));
				}
				tracker.grow(insertableAmount);
				onContentsChanged();
			}
			ItemStack output = stack.copy();
			output.setCount(stack.getCount() - insertableAmount);

			// If we are voiding, ALWAYS consume the input.
			if (voidExcess) {
				return ItemStack.EMPTY;
			} else {
				return output;
			}
		} else {
			return stack;
		}
	}

	@Override
	public ItemStack extractItem(ItemStack stack, int count, boolean simulate) {
		int slot = getSlotForItemStack(stack);
		if (slot == -1) {
			return ItemStack.EMPTY;
		}
		return extractItem(slot, count, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		// Get the tracker.
		DigistoreItemTracker tracker = slots.get(slot);

		// If empty, do nothing.
		if (tracker.isEmpty()) {
			return ItemStack.EMPTY.copy();
		}

		// Calculate the actual amount that can be extracted.
		int outputCount = Math.min(amount, tracker.getCount());

		// Create a new result stack.
		ItemStack output = tracker.getStoredItem().copy();
		output.setCount(outputCount);

		// If not simulating, actually update the amount.
		if (!simulate) {
			tracker.shrink(outputCount);
			onContentsChanged();
		}

		return output;
	}

	@Override
	public int getMaxStoredAmount() {
		return maximumStorage;
	}

	@Override
	public boolean shouldVoidExcess() {
		return voidExcess;
	}

	@Override
	public int getTotalContainedCount() {
		int amount = 0;
		for (DigistoreItemTracker tracker : slots) {
			amount += tracker.getCount();
		}
		return amount;
	}

	@Override
	public int getRemainingStorage(boolean ignoreVoidUpgrade) {
		return ignoreVoidUpgrade ? getMaxStoredAmount() - getTotalContainedCount() : voidExcess ? Integer.MAX_VALUE : getMaxStoredAmount() - getTotalContainedCount();
	}

	@Override
	public boolean canAcceptItem(ItemStack item) {
		return getSlotForItemStack(item) >= 0;
	}

	@Override
	public int getSlotLimit(int slot) {
		return maximumStorage;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return slots.get(slot).canAcceptItem(stack);
	}

	public boolean isFull() {
		return getTotalContainedCount() >= getMaxStoredAmount();
	}

	public void setVoidExcess(boolean voidExcess) {
		this.voidExcess = voidExcess;
	}

	protected void onContentsChanged() {

	}

	public void setMaximumStorage(int maxStorage) {
		maximumStorage = maxStorage;
	}

	public float getFilledRatio() {
		return (float) getTotalContainedCount() / (float) getMaxStoredAmount();
	}

	public void setSupportedItemTypeCount(int itemCount) {
		for (int i = getMaximumUniqueItemTypeCount(); i < itemCount; i++) {
			slots.add(new DigistoreItemTracker());
		}
	}

	protected int getSlotForItemStack(ItemStack item) {
		for (int i = 0; i < slots.size(); i++) {
			if (ItemUtilities.areItemStacksStackable(item, slots.get(i).getStoredItem())) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();
		output.putInt("MaximumStorage", maximumStorage);
		output.putBoolean("void_excess", voidExcess);
		ListNBT digistoreSlots = new ListNBT();
		slots.forEach(network -> {
			CompoundNBT networkTag = new CompoundNBT();
			network.writeToNbt(networkTag);
			digistoreSlots.add(networkTag);
		});
		output.put("slots", digistoreSlots);

		return output;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains("maximum_storage")) {
			maximumStorage = nbt.getInt("maximum_storage");
		}
		if (nbt.contains("void_excess")) {
			voidExcess = nbt.getBoolean("void_excess");
		}

		if (nbt.contains("slots")) {
			ListNBT digistoreSlots = nbt.getList("slots", Constants.NBT.TAG_COMPOUND);
			slots.clear();
			for (int i = 0; i < digistoreSlots.size(); i++) {
				CompoundNBT slotTagComponent = (CompoundNBT) digistoreSlots.get(i);
				DigistoreItemTracker newTracker = new DigistoreItemTracker();
				newTracker.readFromNbt(slotTagComponent);
				slots.add(newTracker);
			}
		}
	}

	@Override
	public Iterator<DigistoreItemTracker> iterator() {
		return new DigistoreInventoryIterator();
	}

	/**
	 * Iterator to help iterate through the items in this digistore inventory.
	 */
	protected class DigistoreInventoryIterator implements Iterator<DigistoreItemTracker> {
		private int currentIndex;

		DigistoreInventoryIterator() {
			currentIndex = 0;
		}

		public boolean hasNext() {
			return currentIndex <= slots.size() - 1;
		}

		public DigistoreItemTracker next() {
			DigistoreItemTracker stackInSlot = slots.get(currentIndex);
			currentIndex++;
			return stackInSlot;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
