package theking530.staticpower.tileentity;

import java.util.Iterator;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.tileentity.SideModeList.Mode;

public class TileEntityInventory extends ItemStackHandler implements Iterable<ItemStack> {

	private ItemStackHandlerFilter filter;
	private Mode inventoryMode;

	public TileEntityInventory(int size, Mode mode) {
		super(size);
		inventoryMode = mode;
	}

	public Mode getMode() {
		return inventoryMode;
	}

	public ItemStack insertItemToSide(Mode sideMode, int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (filter != null) {
			if (!filter.canInsertItem(sideMode, slot, stack)) {
				return stack;
			}
		}
		return insertItem(slot, stack, simulate);
	}

	public ItemStack extractItemFromSide(Mode sideMode, int slot, int amount, boolean simulate) {
		if (filter != null) {
			if (!filter.canExtractItem(sideMode, slot, amount)) {
				return ItemStack.EMPTY;
			}
		}
		return extractItem(slot, amount, simulate);
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new TileEntityInventoryIterator(this);
	}

	protected class TileEntityInventoryIterator implements Iterator<ItemStack> {
		private TileEntityInventory teInventory;
		private int currentIndex;

		TileEntityInventoryIterator(TileEntityInventory inventory) {
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
