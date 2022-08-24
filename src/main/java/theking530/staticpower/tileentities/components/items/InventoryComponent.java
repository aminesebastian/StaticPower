package theking530.staticpower.tileentities.components.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
import theking530.staticpower.utilities.ItemUtilities;

public class InventoryComponent extends AbstractTileEntityComponent implements Iterable<ItemStack>, IItemHandlerModifiable {
	public enum InventoryChangeType {
		ADDED, REMOVED, MODIFIED
	}

	protected NonNullList<ItemStack> stacks;
	protected NonNullList<ItemStack> lastStacks;
	protected List<ItemStack> lockedSlots;
	private ItemStackHandlerFilter filter;
	private MachineSideMode inventoryMode;
	private boolean capabilityInsertEnabled;
	private boolean capabilityExtractEnabled;
	private final InventoryComponentCapabilityInterface capabilityInterface;
	private TriConsumer<InventoryChangeType, ItemStack, Integer> changeCallback;
	private boolean shouldDropContentsOnBreak;
	private boolean areSlotsLockable;

	@UpdateSerialize
	private boolean isShiftClickEnabled;
	@UpdateSerialize
	private int shiftClickPriority;
	@UpdateSerialize
	private boolean exposeAsCapability;

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
		this.lastStacks = NonNullList.withSize(size, ItemStack.EMPTY);
		this.areSlotsLockable = false;
		this.shiftClickPriority = 0;
		this.lockedSlots = new ArrayList<ItemStack>();
		for (int i = 0; i < size; i++) {
			lockedSlots.add(null);
		}

		this.capabilityInterface = new InventoryComponentCapabilityInterface();
		if (mode.isOutputMode()) {
			setCapabilityInsertEnabled(false);
		} else if (mode.isInputMode()) {
			setCapabilityExtractEnabled(false);
		}

		exposeAsCapability = true;
	}

	/**
	 * Checks for inventory modification.
	 */
	public void preProcessUpdate() {
		for (int i = 0; i < Math.min(stacks.size(), lastStacks.size()); i++) {
			ItemStack currentStack = stacks.get(i);
			ItemStack lastStack = lastStacks.get(i);

			// If both are empty, skip/
			if (currentStack.isEmpty() && lastStack.isEmpty()) {
				continue;
			}

			// If the items are not equal, then something has changed.
			if (!lastStack.equals(currentStack, false)) {
				if (lastStack.isEmpty()) {
					onItemStackAdded(currentStack, i);
				} else if (currentStack.isEmpty()) {
					onItemStackRemoved(lastStack, i);
				} else {
					onItemStackModified(currentStack, i);
				}
				lastStacks.set(i, currentStack.copy());
			}
		}
	}

	/**
	 * Gets the priority when inserting into this inventory through a shift click.
	 * The order is evaluated highest to lowest priority.
	 * 
	 * @return
	 */
	public int getShiftClickPriority() {
		return shiftClickPriority;
	}

	/**
	 * Sets the priority when inserting into this inventory through a shift click.
	 * The order is evaluated highest to lowest priority.
	 * 
	 * @param priority
	 * @return
	 */
	public InventoryComponent setShiftClickPriority(int priority) {
		this.shiftClickPriority = priority;
		return this;
	}

	/**
	 * Indicates that this component should receive shift click items in the GUI.
	 * 
	 * @return
	 */
	public boolean isShiftClickEnabled() {
		return isShiftClickEnabled;
	}

	/**
	 * Indicates whether or not this component should receive shift click items in
	 * the GUI.
	 * 
	 * @return
	 */

	public InventoryComponent setShiftClickEnabled(boolean isShiftClickEnabled) {
		this.isShiftClickEnabled = isShiftClickEnabled;
		return this;
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
	 * @return
	 */
	public InventoryComponent setShouldDropContentsOnBreak(boolean shouldDropContentsOnBreak) {
		this.shouldDropContentsOnBreak = shouldDropContentsOnBreak;
		return this;
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

	/**
	 * Sets the input/output mode for this inventory.
	 * 
	 * @param mode
	 * @return
	 */
	public InventoryComponent setMode(MachineSideMode mode) {
		this.inventoryMode = mode;
		return this;
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		ListTag itemTagList = new ListTag();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				stacks.get(i).save(itemTag);
				itemTagList.add(itemTag);
			}
		}
		nbt.put("Items", itemTagList);
		nbt.putInt("Size", stacks.size());

		// Put the locked slot filters. Skip null slots.
		for (int i = 0; i < lockedSlots.size(); i++) {
			if (lockedSlots.get(i) != null) {
				CompoundTag filterItemTag = new CompoundTag();
				lockedSlots.get(i).save(filterItemTag);
				nbt.put("filter_slot#" + i, filterItemTag);
			}
		}
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTagCompound = tagList.getCompound(i);
			int slot = itemTagCompound.getInt("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				stacks.set(slot, ItemStack.of(itemTagCompound));
			}
		}

		// Deserialize the slot filters.
		for (int i = 0; i < lockedSlots.size(); i++) {
			if (nbt.contains("filter_slot#" + i)) {
				lockedSlots.set(i, ItemStack.of(nbt.getCompound("filter_slot#" + i)));
			} else {
				lockedSlots.set(i, null);
			}
		}
		onLoad();
	}

	@Override
	public Iterator<ItemStack> iterator() {
		return new TileEntityInventoryIterator();
	}

	public boolean isExposedAsCapability() {
		return exposeAsCapability;
	}

	public InventoryComponent setExposedAsCapability(boolean exposeAsCapability) {
		this.exposeAsCapability = exposeAsCapability;
		return this;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled()) {
			if (inventoryMode != MachineSideMode.Never) {
				// Check if the owner is side configurable. If it is not, just return this.
				// Otherwise, check to make sure this inventory's mode is equal to the
				// configured side's mode.
				Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
				if (!sideConfig.isPresent() || (side != null && sideConfig.get().getWorldSpaceDirectionConfiguration(side) == inventoryMode)) {
					return manuallyProvideCapability(cap, side);
				}
			}
		}
		return LazyOptional.empty();
	}

	public <T> LazyOptional<T> manuallyProvideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> capabilityInterface).cast();
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
		// Set the last stacks to the new size.
		lastStacks = NonNullList.withSize(size, ItemStack.EMPTY);

		// Then update the items.
		for (int i = 0; i < Math.min(stacks.size(), size); i++) {
			lastStacks.set(i, stacks.get(i));
		}

		// Finally, set the actual stacks array to the new list.
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);

		// Update the locked slots as well.
		List<ItemStack> temp = new ArrayList<ItemStack>(size);
		for (int i = 0; i < Math.min(size, lockedSlots.size()); i++) {
			temp.add(lockedSlots.get(i));
		}
		if (size > temp.size()) {
			for (int j = lockedSlots.size(); j < size; j++) {
				temp.add(null);
			}
		}
		lockedSlots = temp;
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		this.stacks.set(slot, stack);
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

		// Check if this slot is locked.
		if (lockedSlots.get(slot) != null) {
			if (lockedSlots.get(slot).isEmpty() || !ItemUtilities.areItemStacksStackable(stack, lockedSlots.get(slot))) {
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
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
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
				this.stacks.set(slot, ItemStack.EMPTY);
				return existing;
			} else {
				return existing.copy();
			}
		} else {
			if (!simulate) {
				this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	public InventoryComponent setSlotsLockable(boolean lockable) {
		this.areSlotsLockable = lockable;
		return this;
	}

	public boolean areSlotsLockable() {
		return areSlotsLockable;
	}

	public boolean isSlotLocked(int slot) {
		return lockedSlots.get(slot) != null;
	}

	/**
	 * Prevents items that don't match the provided ItemStack from entering the
	 * slot. If the filteredItem is empty, no items are allowed to enter the slot.
	 * 
	 * @param slot
	 * @param filteredItem
	 * @return
	 */
	public InventoryComponent lockSlot(int slot, @Nonnull ItemStack filteredItem) {
		if (areSlotsLockable) {
			lockedSlots.set(slot, filteredItem);
		}
		return this;
	}

	public ItemStack getLockedSlotFilter(int slot) {
		return lockedSlots.get(slot);
	}

	public InventoryComponent unlockSlot(int slot) {
		if (areSlotsLockable) {
			lockedSlots.set(slot, null);
		}
		return this;
	}

	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return true;
	}

	public InventoryComponent setModifiedCallback(TriConsumer<InventoryChangeType, ItemStack, Integer> callback) {
		changeCallback = callback;
		return this;
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.size())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	protected void onLoad() {

	}

	protected void onItemStackAdded(ItemStack stack, int slot) {
		if (changeCallback != null) {
			changeCallback.accept(InventoryChangeType.ADDED, stack, slot);
		}
		getTileEntity().setChanged();
	}

	protected void onItemStackRemoved(ItemStack stack, int slot) {
		if (changeCallback != null) {
			changeCallback.accept(InventoryChangeType.REMOVED, stack, slot);
		}
		getTileEntity().setChanged();
	}

	protected void onItemStackModified(ItemStack stack, int slot) {
		if (changeCallback != null) {
			changeCallback.accept(InventoryChangeType.MODIFIED, stack, slot);
		}
		getTileEntity().setChanged();
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