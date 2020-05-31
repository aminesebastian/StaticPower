package theking530.staticpower.tileentities.components;

import java.util.Iterator;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class TileEntityInventoryComponent extends ItemStackHandler implements Iterable<ItemStack>, ITileEntityComponent {

	private ItemStackHandlerFilter filter;
	private MachineSideMode inventoryMode;
	private String name;
	private boolean isEnabled;

	public TileEntityInventoryComponent(String name, int size, MachineSideMode mode) {
		super(size);
		this.inventoryMode = mode;
		this.name = name;
		this.isEnabled = true;
	}

	/**
	 * Adds a filter to this inventory.
	 * 
	 * @param filter The filter to use.
	 * @return This component for chaining calls.
	 */
	public TileEntityInventoryComponent setFilter(ItemStackHandlerFilter filter) {
		this.filter = filter;
		return this;
	}

	/**
	 * Gets the mode of this inventory.
	 * 
	 * @return The {@link MachineSideMode} of this inventory.
	 */
	public MachineSideMode getMode() {
		return inventoryMode;
	}

	@Override
	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		nbt.put(name, serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeSaveNbt(CompoundNBT nbt) {
		if (nbt.contains(name)) {
			deserializeNBT(nbt.getCompound(name));
		}
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (filter != null) {
			if (!filter.canInsertItem(slot, stack)) {
				return stack;
			}
		}
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (filter != null) {
			if (!filter.canExtractItem(slot, amount)) {
				return ItemStack.EMPTY;
			}
		}
		return super.extractItem(slot, amount, simulate);
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new TileEntityInventoryIterator(this);
	}

	@Override
	public String getComponentName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Iterator to help iterate through the items in this inventory.
	 */
	protected class TileEntityInventoryIterator implements Iterator<ItemStack> {
		private TileEntityInventoryComponent teInventory;
		private int currentIndex;

		TileEntityInventoryIterator(TileEntityInventoryComponent inventory) {
			teInventory = inventory;
			currentIndex = 0;
		}

		public boolean hasNext() {
			return currentIndex < teInventory.getSlots() - 1;
		}

		public ItemStack next() {
			ItemStack stackInSlot = teInventory.getStackInSlot(currentIndex);
			currentIndex++;
			return stackInSlot;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
