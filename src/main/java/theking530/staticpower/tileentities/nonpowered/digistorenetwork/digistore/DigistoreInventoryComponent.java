package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.utilities.ItemUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class DigistoreInventoryComponent extends AbstractTileEntityComponent implements IItemHandler {

	private List<DigistoreItemTracker> slots;
	private boolean voidExcess;
	private int maximumStorage;

	public DigistoreInventoryComponent(String name, int slotCount, int maximumStorage) {
		super(name);
		this.maximumStorage = maximumStorage;
		slots = NonNullList.withSize(slotCount, new DigistoreItemTracker());
	}

	public void setLockedStateForAllSlots(boolean locked) {
		for (DigistoreItemTracker slot : slots) {
			slot.setLocked(locked);
		}
	}

	public int getCountInSlot(int slot) {
		return slots.get(slot).getCount();
	}

	@Override
	public int getSlots() {
		return slots.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slots.get(slot).getStoredItem();
	}

	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		int slot = getSlotForItemStack(stack);
		if (slot == -1) {
			return stack;
		}
		return insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		// Get the tracker.
		DigistoreItemTracker tracker = slots.get(slot);

		// Do nothing if empty.
		if (stack.isEmpty()) {
			return stack;
		}

		// Calculate the remaining storage space and the insertable amount.
		int remainingStorage = maximumStorage - tracker.getCount();
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
			}
			ItemStack output = stack.copy();
			output.setCount(stack.getCount() - insertableAmount);

			// Sync the tile entity.
			getTileEntity().markTileEntityForSynchronization();

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
		}

		// Sync the tile entity.
		getTileEntity().markTileEntityForSynchronization();
		return output;
	}

	public void setMaximumStorage(int maxStorage) {
		maximumStorage = maxStorage;
	}

	public int getMaxStoredAmount() {
		return maximumStorage;
	}

	public boolean shouldVoidExcess() {
		return voidExcess;
	}

	public void setVoidExcess(boolean voidExcess) {
		this.voidExcess = voidExcess;
	}

	public int getTotalContainedCount() {
		int amount = 0;
		for (DigistoreItemTracker tracker : slots) {
			amount += tracker.getCount();
		}
		return amount;
	}

	public boolean isFull() {
		return getTotalContainedCount() >= getMaxStoredAmount();
	}

	public int getRemainingStorage(boolean ignoreVoidUpgrade) {
		return ignoreVoidUpgrade ? getMaxStoredAmount() - getTotalContainedCount() : voidExcess ? Integer.MAX_VALUE : getMaxStoredAmount() - getTotalContainedCount();
	}

	public float getFilledRatio() {
		return (float) getTotalContainedCount() / (float) getMaxStoredAmount();
	}

	public int getSlotForItemStack(ItemStack item) {
		for (int i = 0; i < slots.size(); i++) {
			if (slots.get(i).canAcceptItem(item)) {
				return i;
			}
		}
		return -1;
	}

	public boolean canAcceptItem(ItemStack item) {
		return getSlotForItemStack(item) >= 0;
	}

	public void dropAllContentsInWorld() {
		for (DigistoreItemTracker tracker : slots) {
			while (tracker.getCount() > 0) {
				int countToDrop = Math.min(tracker.getStoredItem().getMaxStackSize(), tracker.getCount());
				WorldUtilities.dropItem(getWorld(), getTileEntity().getFacingDirection(), getPos(), tracker.getStoredItem(), countToDrop);
				tracker.shrink(countToDrop);
			}
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return maximumStorage;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return slots.get(slot).canAcceptItem(stack);
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("MaximumStorage", maximumStorage);

		ListNBT digistoreSlots = new ListNBT();
		slots.forEach(network -> {
			CompoundNBT networkTag = new CompoundNBT();
			network.writeToNbt(networkTag);
			digistoreSlots.add(networkTag);
		});
		nbt.put("slots", digistoreSlots);

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		maximumStorage = nbt.getInt("MaximumStorage");

		ListNBT digistoreSlots = nbt.getList("slots", Constants.NBT.TAG_COMPOUND);
		slots = NonNullList.withSize(digistoreSlots.size(), new DigistoreItemTracker());
		for (int i = 0; i < digistoreSlots.size(); i++) {
			CompoundNBT slotTagComponent = (CompoundNBT) digistoreSlots.get(i);
			slots.get(i).readFromNbt(slotTagComponent);
		}
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> {
				return this;
			}).cast();
		}
		return LazyOptional.empty();
	}
}
