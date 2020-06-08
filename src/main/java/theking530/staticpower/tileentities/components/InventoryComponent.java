package theking530.staticpower.tileentities.components;

import java.util.Iterator;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class InventoryComponent extends AbstractTileEntityComponent implements Iterable<ItemStack> {
	private ItemStackHandler inventory;
	private ItemStackHandlerFilter filter;
	private MachineSideMode inventoryMode;

	public InventoryComponent(String name, int size, MachineSideMode mode) {
		super(name);
		this.inventory = new ItemStackHandler(size);
		this.inventoryMode = mode;
	}

	/**
	 * Adds a filter to this inventory.
	 * 
	 * @param filter The filter to use.
	 * @return This component for chaining calls.
	 */
	public InventoryComponent setFilter(ItemStackHandlerFilter filter) {
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

	public ItemStackHandler getInventory() {
		return inventory;
	}

	@Override
	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		nbt.put(getComponentName(), inventory.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeSaveNbt(CompoundNBT nbt) {
		if (nbt.contains(getComponentName())) {
			inventory.deserializeNBT(nbt.getCompound(getComponentName()));
		}
	}

	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return inventory.insertItem(slot, stack, simulate);
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (filter != null) {
			if (!filter.canExtractItem(slot, amount)) {
				return ItemStack.EMPTY;
			}
		}
		return inventory.extractItem(slot, amount, simulate);
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		inventory.setStackInSlot(slot, stack);
	}

	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	public int getSlotCount() {
		return inventory.getSlots();
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new TileEntityInventoryIterator();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			// Check if the owner is side configurable. If it is, check to make sure it's
			// not disabled, if not, return the inventory.
			Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
			if (side == null || !sideConfig.isPresent() || !sideConfig.get().getWorldSpaceDirectionConfiguration(side).isDisabledMode()) {
				return LazyOptional.of(() -> {
					return inventory;
				}).cast();
			}
		}
		return LazyOptional.empty();
	}

	/**
	 * Iterator to help iterate through the items in this inventory.
	 */
	protected class TileEntityInventoryIterator implements Iterator<ItemStack> {
		private int currentIndex;

		TileEntityInventoryIterator() {
			currentIndex = 0;
		}

		public boolean hasNext() {
			return currentIndex < getInventory().getSlots() - 1;
		}

		public ItemStack next() {
			ItemStack stackInSlot = getInventory().getStackInSlot(currentIndex);
			currentIndex++;
			return stackInSlot;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
