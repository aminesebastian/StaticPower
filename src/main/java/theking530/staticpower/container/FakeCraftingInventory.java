package theking530.staticpower.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class FakeCraftingInventory extends CraftingContainer {
	private final NonNullList<ItemStack> stackListOverride;

	public FakeCraftingInventory(int width, int height) {
		super(null, width, height);
		this.stackListOverride = NonNullList.withSize(width * height, ItemStack.EMPTY);
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getContainerSize() {
		return this.stackListOverride.size();
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.stackListOverride) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getItem(int index) {
		return index >= this.getContainerSize() ? ItemStack.EMPTY : this.stackListOverride.get(index);
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.stackListOverride, index);
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns
	 * them in a new stack.
	 */
	public ItemStack removeItem(int index, int count) {
		ItemStack itemstack = ContainerHelper.removeItem(this.stackListOverride, index, count);
		return itemstack;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setItem(int index, ItemStack stack) {
		this.stackListOverride.set(index, stack);
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to
	 * disk later - the game won't think it hasn't changed and skip it.
	 */
	public void setChanged() {
	}

	/**
	 * Don't rename this method to canInteractWith due to conflicts with Container
	 */
	public boolean stillValid(Player player) {
		return true;
	}

	public void clearContent() {
		this.stackListOverride.clear();
	}

	public void fillStackedContents(StackedContents helper) {
		for (ItemStack itemstack : this.stackListOverride) {
			helper.accountSimpleStack(itemstack);
		}

	}
}