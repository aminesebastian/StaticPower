package theking530.api.digistore;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticpower.tileentities.digistorenetwork.digistore.DigistoreStack;

/**
 * Interface used by any digistore accessible containers.
 * 
 * @author Amine Sebastian
 *
 */
public interface IDigistoreInventory extends INBTSerializable<CompoundTag> {
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
	 * Gets the digistore stack at the provided index. Make a call to
	 * {@link #getUniqueItemCapacity()} to ensure the returned value is > 0 before
	 * making a call to this method. The returned value should NEVER be modified.
	 * This should be treated as a pure const getter. Modifying the result will
	 * break everything.
	 * 
	 * @param index The index of the stack to get. These values range from 0 to the
	 *              values returned by {@link #getUniqueItemCapacity()} exclusive.
	 * 
	 * @return
	 */
	public DigistoreStack getDigistoreStack(int index);

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
	public int getUniqueItemCapacity();

	/**
	 * Get the maximum amount of items that can be contained in this inventory. If
	 * this number is zero, then this inventory can never accept items.
	 * 
	 * @return
	 */
	public int getItemCapacity();

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
