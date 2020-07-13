package theking530.staticpower.tileentities.components;

import java.util.Iterator;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.util.TriConsumer;

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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class InventoryComponent extends AbstractTileEntityComponent implements Iterable<ItemStack>, IItemHandler, IItemHandlerModifiable {
	public enum InventoryChangeType {
		ADDED, REMOVED, MODIFIED
	}

	protected NonNullList<ItemStack> stacks;
	private ItemStackHandlerFilter filter;
	private MachineSideMode inventoryMode;
	private boolean capabilityInsertEnabled;
	private boolean capabilityExtractEnabled;
	private final InventoryComponentCapabilityInterface capabilityInterface;
	private TriConsumer<InventoryChangeType, ItemStack, InventoryComponent> changeCallback;
	private boolean shouldDropContentsOnBreak;

	public InventoryComponent(String name, int size) {
		this(name, size, MachineSideMode.Never);
	}

	public InventoryComponent(String name, int size, MachineSideMode mode) {
		super(name);
		this.inventoryMode = mode;
		this.capabilityInsertEnabled = true;
		this.capabilityExtractEnabled = true;
		this.shouldDropContentsOnBreak = true;
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
		this.capabilityInterface = new InventoryComponentCapabilityInterface();
		if (mode.isOutputMode()) {
			setCapabilityInsertEnabled(false);
		} else if (mode.isInputMode()) {
			setCapabilityExtractEnabled(false);
		}
	}

	/**
	 * If true, when the block representing this tile entity is broken, the contents
	 * of this inventory are dropped as items.
	 * 
	 * @return
	 */
	public boolean shouldDropContentsOnBreak() {
		return shouldDropContentsOnBreak;
	}

	/**
	 * Indicates whether or not this inventory should drop its contents when broken.
	 * 
	 * @param shouldDropContentsOnBreak
	 */
	public void setShouldDropContentsOnBreak(boolean shouldDropContentsOnBreak) {
		this.shouldDropContentsOnBreak = shouldDropContentsOnBreak;
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

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		ListNBT nbtTagList = new ListNBT();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				CompoundNBT itemTag = new CompoundNBT();
				itemTag.putInt("Slot", i);
				stacks.get(i).write(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", stacks.size());
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
		ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				stacks.set(slot, ItemStack.read(itemTags));
			}
		}
		onLoad();
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new TileEntityInventoryIterator();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventoryMode != MachineSideMode.Never) {
			// Check if the owner is side configurable. If it is, check to make sure it's
			// not disabled, if not, return the inventory.
			Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
			if (side == null || !sideConfig.isPresent() || sideConfig.get().getWorldSpaceDirectionConfiguration(side) == inventoryMode) {
				return LazyOptional.of(() -> capabilityInterface).cast();
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
			return currentIndex <= getSlots() - 1;
		}

		public ItemStack next() {
			ItemStack stackInSlot = getStackInSlot(currentIndex);
			currentIndex++;
			return stackInSlot;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public void setSize(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		ItemStack oldStack = getStackInSlot(slot);
		this.stacks.set(slot, stack);

		if (!oldStack.equals(stack, false)) {
			onItemStackAdded(stack);
		}
	}

	@Override
	public int getSlots() {
		return stacks.size();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return this.stacks.get(slot);
	}

	public boolean isCapabilityInsertEnabled() {
		return capabilityInsertEnabled;
	}

	public InventoryComponent setCapabilityInsertEnabled(boolean insertEnabled) {
		this.capabilityInsertEnabled = insertEnabled;
		return this;
	}

	public boolean isCapabilityExtractEnabled() {
		return capabilityExtractEnabled;
	}

	public InventoryComponent setCapabilityExtractEnabled(boolean extractEnabled) {
		this.capabilityExtractEnabled = extractEnabled;
		return this;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		if (filter != null) {
			if (!filter.canInsertItem(slot, stack)) {
				return stack;
			}
		}

		if (!isItemValid(slot, stack)) {
			return stack;
		}

		validateSlotIndex(slot);

		ItemStack existing = this.stacks.get(slot);

		int limit = getStackLimit(slot, stack);

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty()) {
				this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				onItemStackAdded(getStackInSlot(slot));
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
				onItemStackModified(getStackInSlot(slot));
			}
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		if (filter != null) {
			if (!filter.canExtractItem(slot, amount)) {
				return ItemStack.EMPTY;
			}
		}

		validateSlotIndex(slot);

		ItemStack existing = this.stacks.get(slot);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				ItemStack removedItem = getStackInSlot(slot);
				this.stacks.set(slot, ItemStack.EMPTY);
				onItemStackRemoved(removedItem);
				return existing;
			} else {
				return existing.copy();
			}
		} else {
			if (!simulate) {
				this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				onItemStackModified(getStackInSlot(slot));
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return true;
	}

	public InventoryComponent setModifiedCallback(TriConsumer<InventoryChangeType, ItemStack, InventoryComponent> callback) {
		changeCallback = callback;
		return this;
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.size())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	protected void onLoad() {

	}

	protected void onItemStackAdded(ItemStack stack) {
		if (changeCallback != null) {
			changeCallback.accept(InventoryChangeType.ADDED, stack, this);
		}
		getTileEntity().markDirty();
	}

	protected void onItemStackRemoved(ItemStack stack) {
		if (changeCallback != null) {
			changeCallback.accept(InventoryChangeType.REMOVED, stack, this);
		}
		getTileEntity().markDirty();
	}

	protected void onItemStackModified(ItemStack stack) {
		if (changeCallback != null) {
			changeCallback.accept(InventoryChangeType.MODIFIED, stack, this);
		}
		getTileEntity().markDirty();
	}

	public class InventoryComponentCapabilityInterface implements IItemHandler {

		@Override
		public int getSlots() {
			return InventoryComponent.this.getSlots();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return InventoryComponent.this.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (InventoryComponent.this.isCapabilityInsertEnabled()) {
				return InventoryComponent.this.insertItem(slot, stack, simulate);
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (InventoryComponent.this.isCapabilityExtractEnabled()) {
				return InventoryComponent.this.extractItem(slot, amount, simulate);
			}
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return InventoryComponent.this.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return InventoryComponent.this.isItemValid(slot, stack);
		}

	}
}
