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
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.initialization.ModUpgrades;
import theking530.staticpower.items.upgrades.BaseDigistoreCapacityUpgrade;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.InventoryComponent.InventoryChangeType;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityDigistore extends BaseDigistoreTileEntity {
	public static final int DEFAULT_CAPACITY = 1024;

	public final InventoryComponent upgradesInventory;
	public final DigistoreInventoryComponent inventory;
	private boolean locked;

	public TileEntityDigistore() {
		super(ModTileEntityTypes.DIGISTORE);
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never).setModifiedCallback(this::onUpgradesInventoryModifiedCallback));
		registerComponent(inventory = new DigistoreInventoryComponent("Inventory", 1, DEFAULT_CAPACITY));
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
			if (inventory.getInventory().canAcceptItem(heldItem)) {
				ItemStack remaining = inventory.getInventory().insertItem(player.getHeldItem(hand), false);
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
				if (inventory.getInventory().canAcceptItem(currentItem)) {
					currentItem = inventory.getInventory().insertItem(currentItem, false);
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
		if (player.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
			if (player.isSneaking()) {
				extractItemInWorld(0, false);
			} else {
				extractItemInWorld(0, true);
			}
		}
	}

	@Override
	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
		inventory.dropAllContentsInWorld();
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, InventoryComponent upgradeInv) {
		inventory.getInventory().setMaximumStorage(DEFAULT_CAPACITY);
		inventory.getInventory().setVoidExcess(false);
		for (ItemStack stack : upgradeInv) {
			if (stack.getItem() instanceof BaseDigistoreCapacityUpgrade) {
				BaseDigistoreCapacityUpgrade upgrade = (BaseDigistoreCapacityUpgrade) stack.getItem();
				int upgradeAmount = (upgrade.getTier().getDigistoreItemCapacityAmount() * stack.getCount());
				inventory.getInventory().setMaximumStorage(inventory.getInventory().getMaxStoredAmount() + upgradeAmount);
			} else if (stack.getItem() == ModUpgrades.DigistoreVoidUpgrade) {
				inventory.getInventory().setVoidExcess(true);
			}
		}
		markTileEntityForSynchronization();
	}

	public boolean isVoidUpgradeInstalled() {
		for (ItemStack upgrade : this.upgradesInventory) {
			if (upgrade.getItem() == ModUpgrades.DigistoreVoidUpgrade) {
				return true;
			}
		}
		return false;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		inventory.getInventory().setLockState(locked);
	}

	public void extractItemInWorld(int slot, boolean singleItem) {
		// Only perform on the server.
		if (world.isRemote) {
			return;
		}

		// Do nothing if we store nothing.
		if (inventory.getInventory().getStackInSlot(slot).isEmpty()) {
			return;
		}

		// Set the amount to drop to 1 if a single item is requested, otherwise set it
		// up
		// to a whole stack.
		int countToDrop = 1;
		if (!singleItem) {
			countToDrop = Math.min(inventory.getInventory().getStackInSlot(slot).getMaxStackSize(), inventory.getInventory().getCountForItem(inventory.getInventory().getStackInSlot(slot)));
		}

		// Extract the item.
		ItemStack output = inventory.getInventory().extractItem(slot, countToDrop, false);

		// Drop the item. Check the count just in case again (edge case coverage).
		if (countToDrop > 0) {
			WorldUtilities.dropItem(getWorld(), getFacingDirection(), getPos(), output, output.getCount());
		}
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putBoolean("Locked", locked);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		locked = nbt.getBoolean("Locked");
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistore(windowId, inventory, this);
	}
}
