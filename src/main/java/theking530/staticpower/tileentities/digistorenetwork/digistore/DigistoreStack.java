package theking530.staticpower.tileentities.digistorenetwork.digistore;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreStack {
	private ItemStack storedItem;
	private int count;
	private boolean locked;

	public DigistoreStack() {
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

	/**
	 * Indicates if this stack is locked. If locked, then the stored item will
	 * remain non-empty even if the count is 0.
	 * 
	 * @return
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Locks this stack to the currently contained item.
	 * 
	 * @param locked
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * This method indicates that this stack's count is 0. It does NOT mean that the
	 * stack has no item assigned, it could still have one if it is locked. To check
	 * if there is no item assigned to this stack at all, check to see if the stored
	 * item is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * Checks if the provided item can be inserted into this tracker. Does NOT check
	 * for counts, only if the item is acceptable.
	 * 
	 * @param stack
	 * @return
	 */
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

	public void writeToNbt(CompoundTag nbt) {
		nbt.putInt("count", count);
		CompoundTag itemTag = new CompoundTag();
		storedItem.save(itemTag);
		nbt.put("item", itemTag);
		nbt.putBoolean("locked", locked);
	}

	public void readFromNbt(CompoundTag nbt) {
		count = nbt.getInt("count");
		storedItem = ItemStack.of((CompoundTag) nbt.get("item"));
		locked = nbt.getBoolean("locked");
	}
}
