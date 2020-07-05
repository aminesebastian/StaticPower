package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreItemTracker {
	private ItemStack storedItem;
	private int count;
	private boolean locked;

	public DigistoreItemTracker() {
		storedItem = ItemStack.EMPTY;
		count = 0;
	}

	public ItemStack getStoredItem() {
		return storedItem;
	}

	public void setStoredItem(ItemStack storedItem) {
		this.storedItem = storedItem;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
		if (count < 0) {
			count = 0;
		}
		updateEmptyState();
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public boolean canAcceptItem(ItemStack stack) {
		return ItemUtilities.areItemStacksStackable(stack, storedItem) || storedItem.isEmpty();
	}

	public void grow(int amount) {
		setCount(count + amount);
	}

	public void shrink(int amount) {
		setCount(count - amount);
	}

	protected void updateEmptyState() {
		if (count == 0 && !locked) {
			storedItem = ItemStack.EMPTY;
		}
	}

	public void writeToNbt(CompoundNBT nbt) {
		nbt.putInt("count", count);
		CompoundNBT itemTag = new CompoundNBT();
		storedItem.write(itemTag);
		nbt.put("item", itemTag);
		nbt.putBoolean("locked", locked);
	}

	public void readFromNbt(CompoundNBT nbt) {
		count = nbt.getInt("count");
		storedItem = ItemStack.read((CompoundNBT) nbt.get("item"));
		locked = nbt.getBoolean("locked");
	}
}
