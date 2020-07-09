package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.DigistoreItemTracker;

/**
 * Interface used by any digistore accessible containers.
 * 
 * @author Amine Sebastian
 *
 */
public interface IDigistoreInventory extends INBTSerializable<CompoundNBT> {
	/**
	 * Locks the state of this inventory to not accept any other new item types.
	 * 
	 * @param locked True if this inventory should no longer accept any other item
	 *               types, false if it is open to accepting more.
	 */
	public void setLockState(boolean locked);

	/**
	 * Gets the count of the provided item in the inventory.
	 * 
	 * @param stack The stack to check for.
	 * @return The count of the provided item in this inventory.
	 */
	public int getCountForItem(ItemStack stack);

	/**
	 * Gets the item tracker at the provided index.
	 * 
	 * @param index The index of the tracker to get. These values range from 0 to
	 *              the values returned by {@link #getMaximumUniqueItemTypeCount()}
	 *              exclusive.
	 * 
	 * @return
	 */
	public DigistoreItemTracker getItemTracker(int index);

	/**
	 * Gets the number of unique items stored in this inventory.
	 * 
	 * @return
	 */
	public int getCurrentUniqueItemTypeCount();

	/**
	 * Get the maximum amount of unique item types that are supported by this
	 * inventory.
	 * 
	 * @return
	 */
	public int getMaximumUniqueItemTypeCount();

	/**
	 * Get the maximum amount of items that can be contained in this inventory.
	 * 
	 * @return
	 */
	public int getMaxStoredAmount();

	/**
	 * Gets the total amount of items contained in this inventory.
	 * 
	 * @return
	 */
	public int getTotalContainedCount();

	/**
	 * Gets how many more items can be inserted into this inventory.
	 * 
	 * @param ignoreVoid If true, this should return the actual amount of items that
	 *                   can be stored (so if this inventory is full, it can return
	 *                   0). If false and this inventory is set to void excess, it
	 *                   should return Integer.MAX_VALUE.
	 * @return
	 */
	public int getRemainingStorage(boolean ignoreVoid);

	/**
	 * Inserts an item into this digistore inventory. Behaves very similar to the
	 * same method on the {@link IItemStackHandler} except this one doesn't care
	 * about slot.
	 * 
	 * @param stack    The stack to insert.
	 * @param simulate If true, the insert will only be simulated.
	 * @return Returns the remaining items after the insert. If the returned stack
	 *         is empty, then all of the provided stack was inserted.
	 */
	public ItemStack insertItem(ItemStack stack, boolean simulate);

	/**
	 * Extract an item from this digistore inventory. Behaves very similiar to the
	 * same method on {@link IItemStackHandler} except this one' doesn't care about
	 * slot.
	 * 
	 * @param stack    The itemstack to extract (stack size for this doesn't matter,
	 *                 just the itemstack's other unique values).
	 * @param count    The amount of items to extract.
	 * @param simulate If true, the extract will only be simulated.
	 * @return The actual itemstack that was (or was simulated) as extracted.
	 */
	public ItemStack extractItem(ItemStack stack, int count, boolean simulate);

	/**
	 * If true, any extra items will be accepted and voided.
	 * 
	 * @return
	 */
	public boolean shouldVoidExcess();

	/**
	 * Checks to see if we can accept this item.
	 * 
	 * @param item
	 * @return
	 */
	public boolean canAcceptItem(ItemStack item);
}
