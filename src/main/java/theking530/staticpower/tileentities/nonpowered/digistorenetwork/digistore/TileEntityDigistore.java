package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import theking530.staticpower.StaticPower;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.initialization.ModUpgrades;
import theking530.staticpower.items.upgrades.BaseDigistoreCapacityUpgrade;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.InventoryComponent.InventoryChangeType;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.ItemUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityDigistore extends BaseDigistoreTileEntity {
	public static final int DEFAULT_CAPACITY = 1024;

	public final InventoryComponent upgradesInventory;
	public final DigistoreInventoryComponent outputInventory;

	private ItemStack storedItem;
	private int storedAmount;
	private int maximumStorage;

	private boolean shouldVoid;
	private boolean locked;

	public TileEntityDigistore() {
		super(ModTileEntityTypes.DIGISTORE);
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never).setModifiedCallback(this::onUpgradesInventoryModifiedCallback));
		registerComponent(outputInventory = new DigistoreInventoryComponent("OutputInventory"));
		storedItem = ItemStack.EMPTY.copy();
		storedAmount = 0;
		maximumStorage = DEFAULT_CAPACITY;
		shouldVoid = false;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		// If the hand is not empty and the held itemstack matches the item stored in
		// this digistore, attempt to insert it.
		if (!player.getHeldItem(hand).isEmpty()) {
			ItemStack heldItem = player.getHeldItem(hand);

			// If the player is holding an upgrade, attempt to insert the upgrade.
			if (heldItem.getItem() instanceof BaseUpgrade) {
				ItemStack remaining = InventoryUtilities.insertItemIntoInventory(upgradesInventory, heldItem, false);
				if (remaining.getCount() != heldItem.getCount()) {
					if (!player.isCreative()) {
						heldItem.setCount(remaining.getCount());
					}

					world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.2f, 1.5f);
					return ActionResultType.SUCCESS;
				}
			}

			// If not, attempt to insert the item.
			if (canAcceptItem(heldItem)) {
				ItemStack remaining = insertItem(player.getHeldItem(hand), false);
				player.setItemStackToSlot(EquipmentSlotType.MAINHAND, remaining);
				world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.8f, 1.0f);
				return ActionResultType.SUCCESS;
			}
		} else {
			// Keep track of if any items changed.
			boolean itemInserted = false;

			// Loop through the whole inventory.
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				// Get the item in the slot.
				ItemStack currentItem = player.inventory.getStackInSlot(i).copy();

				// Skip empty slots.
				if (player.inventory.getStackInSlot(i).isEmpty()) {
					continue;
				}
				if (doesItemMatchStoredItem(currentItem)) {
					currentItem = insertItem(currentItem, false);
					if (currentItem.getCount() != player.inventory.getStackInSlot(i).getCount()) {
						itemInserted = true;
						player.inventory.setInventorySlotContents(i, currentItem);
					}
				}
			}

			if (itemInserted) {
				world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0f, 1.0f);
				return ActionResultType.SUCCESS;
			}
		}
		return super.onBlockActivated(state, player, hand, hit);
	}

	@Override
	public void onBlockLeftClicked(BlockState state, PlayerEntity player) {
		super.onBlockLeftClicked(state, player);

		// If the digistore is not empty, extract either a stack or a single item.
		if (!isEmpty() && player.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
			if (player.isSneaking()) {
				extractItemInWorld(false);
			} else {
				extractItemInWorld(true);
			}
		}
	}

	@Override
	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
		while (storedAmount > 0) {
			int countToDrop = Math.min(storedItem.getMaxStackSize(), getStoredAmount());
			WorldUtilities.dropItem(getWorld(), getFacingDirection(), getPos(), storedItem, countToDrop);
			storedAmount -= countToDrop;
		}
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, InventoryComponent inventory) {
		maximumStorage = DEFAULT_CAPACITY;
		for (ItemStack stack : inventory) {
			if (stack.getItem() instanceof BaseDigistoreCapacityUpgrade) {
				BaseDigistoreCapacityUpgrade upgrade = (BaseDigistoreCapacityUpgrade) stack.getItem();
				maximumStorage += (upgrade.getTier().getDigistoreItemCapacityAmount() * stack.getCount());
			}
		}
		markTileEntityForSynchronization();
	}

	public ItemStack getStoredItem() {
		return storedItem;
	}

	public boolean isEmpty() {
		return storedItem.isEmpty();
	}

	public int getStoredAmount() {
		return storedAmount;
	}

	public int getMaxStoredAmount() {
		return maximumStorage;
	}

	public boolean isFull() {
		return getStoredAmount() >= getMaxStoredAmount();
	}

	public float getFilledRatio() {
		return (float) getStoredAmount() / (float) getMaxStoredAmount();
	}

	public boolean isVoidUpgradeInstalled() {
		for (ItemStack upgrade : this.upgradesInventory) {
			if (upgrade.getItem() == ModUpgrades.DigistoreVoidUpgrade) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the remaining amount of items this digistore can take.
	 * 
	 * @param checkForVoidUpgrade If true, then if this digistore has a void
	 *                            upgrade, it will always return Integer.MAX_VALUE
	 *                            as its remaining storage. If false, the regular
	 *                            remaining storage will be calculated.
	 * @return
	 */
	public int getRemainingStorage(boolean checkForVoidUpgrade) {
		return checkForVoidUpgrade ? shouldVoid ? Integer.MAX_VALUE : getStoredAmount() : getStoredAmount();
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		if (!locked && getStoredAmount() <= 0) {
			storedItem = ItemStack.EMPTY;
		}
	}

	public boolean doesItemMatchStoredItem(ItemStack stack) {
		return ItemStack.areItemsEqual(storedItem, stack) && ItemStack.areItemStackTagsEqual(storedItem, stack);
	}

	public boolean canAcceptItem(ItemStack stack) {
		return storedItem.isEmpty() || doesItemMatchStoredItem(stack);
	}

	/**
	 * 
	 * Does not modify the provided stack if simulating.
	 * 
	 * @param stack
	 * @param simulate
	 * @return
	 */
	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		// Do nothing if empty.
		if (stack.isEmpty()) {
			return stack;
		}

		// Calculate the remaining storage space and the insertable amount.
		int remainingStorage = maximumStorage - getStoredAmount();
		int insertableAmount = Math.min(remainingStorage, stack.getCount());

		// Then, attempt to insert the item.
		if (storedItem.isEmpty() || ItemUtilities.areItemStacksStackable(storedItem, stack)) {
			if (!simulate) {
				// If we aren't storing anything, set the stored item. Check the remaining
				// storage (Even though it isn't necessary, its a good edge case check).
				if (storedItem.isEmpty() && remainingStorage > 0) {
					setStoredItem(stack);
				}
				storedAmount += insertableAmount;
			}
			ItemStack output = stack.copy();
			output.setCount(stack.getCount() - insertableAmount);
			markTileEntityForSynchronization();

			// If we are voiding, ALWAYS consume the input.
			if (isVoidUpgradeInstalled()) {
				return ItemStack.EMPTY;
			} else {
				return output;
			}
		} else {
			return stack;
		}
	}

	public ItemStack extractItem(int count, boolean simulate) {
		// If empty, do nothing.
		if (isEmpty()) {
			return ItemStack.EMPTY.copy();
		}

		// Calculate the actual amount that can be extracted.
		int outputCount = Math.min(count, getStoredAmount());

		// Create a new result stack.
		ItemStack output = storedItem.copy();
		output.setCount(outputCount);

		// If not simulating, actually update the amount.
		if (!simulate) {
			storedAmount -= outputCount;
			updateEmptyState();
		}

		markTileEntityForSynchronization();
		return output;
	}

	public void extractItemInWorld(boolean singleItem) {
		// Only perform on the server.
		if (world.isRemote) {
			return;
		}

		// Do nothing if we store nothing.
		if (storedItem.isEmpty()) {
			return;
		}

		// Set the amount to drop to 1 if a single item is requested, otherwise set it
		// up
		// to a whole stack.
		int countToDrop = 1;
		if (!singleItem) {
			countToDrop = Math.min(storedItem.getMaxStackSize(), getStoredAmount());
		}

		// Extract the item.
		ItemStack output = extractItem(countToDrop, false);

		// Drop the item. Check the count just in case again (edge case coverage).
		if (countToDrop > 0) {
			WorldUtilities.dropItem(getWorld(), getFacingDirection(), getPos(), output, output.getCount());
		}
	}

	protected void updateEmptyState() {
		// If we're locked, we should keep the stored item set, even if the digistore is
		// empty.
		if (locked) {
			return;
		}
		// If not locked, clear the digistore icon if there are no items.
		if (storedAmount <= 0) {
			storedItem = ItemStack.EMPTY.copy();
		}
	}

	protected void setStoredItem(ItemStack stack) {
		// Set the stored item if it is currently empty.
		if (storedItem.isEmpty()) {
			storedItem = stack.copy();
			storedItem.setCount(1);
		} else {
			StaticPower.LOGGER.error("Attempted to set the stored item of a digistore that already has a stored item set. Clear the existing stored item before attempting to set a new one.");
		}
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		CompoundNBT itemNbt = new CompoundNBT();
		storedItem.write(itemNbt);
		nbt.put("StoredItem", itemNbt);
		nbt.putInt("StoredAmount", storedAmount);
		nbt.putInt("MaximumStorage", maximumStorage);
		nbt.putBoolean("Locked", locked);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		storedItem = ItemStack.read(nbt.getCompound("StoredItem"));
		storedAmount = nbt.getInt("StoredAmount");
		maximumStorage = nbt.getInt("MaximumStorage");
		locked = nbt.getBoolean("Locked");
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistore(windowId, inventory, this);
	}
}
