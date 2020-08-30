package theking530.staticpower.tileentities.digistorenetwork.digistore;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityDigistore extends BaseDigistoreTileEntity implements IItemHandler {
	public final DigistoreInventoryComponent inventory;
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<DigistoreRenderingState> RENDERING_STATE = new ModelProperty<DigistoreRenderingState>();

	@UpdateSerialize
	private boolean locked;

	public TileEntityDigistore() {
		super(ModTileEntityTypes.DIGISTORE);
		registerComponent(inventory = (DigistoreInventoryComponent) new DigistoreInventoryComponent("Inventory", 1).setShiftClickEnabled(true));
		inventory.setFilter(new ItemStackHandlerFilter() {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				IDigistoreInventory inventory = stack.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY).orElse(null);
				if (inventory == null) {
					return false;
				}

				return inventory.getUniqueItemCapacity() == 1;
			}
		});
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		// If the hand is not empty and the held itemstack matches the item stored in
		// this digistore, attempt to insert it.
		if (!player.getHeldItem(hand).isEmpty()) {
			if (!world.isRemote) {
				ItemStack heldItem = player.getHeldItem(hand);

				// If the player is holding a card, attempt to insert it.
				if (heldItem.getItem() instanceof DigistoreMonoCard && inventory.getStackInSlot(0).isEmpty()) {

					ItemStack remaining = inventory.insertItem(0, heldItem.copy(), false);
					if (remaining.getCount() != heldItem.getCount()) {
						if (!player.isCreative()) {
							heldItem.setCount(remaining.getCount());
						}

						world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.1f, 1.8f);
						world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.8f, 1.0f);
						markTileEntityForSynchronization();
						return ActionResultType.SUCCESS;
					}
				} else {
					// If not, attempt to insert the item.
					int initialCount = player.getHeldItem(hand).getCount();
					ItemStack remaining = inventory.insertItem(player.getHeldItem(hand), false);
					player.setItemStackToSlot(EquipmentSlotType.MAINHAND, remaining);

					if (initialCount != remaining.getCount()) {
						world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.8f, 1.0f);
						markTileEntityForSynchronization();
						return ActionResultType.SUCCESS;
					} else {
						return ActionResultType.PASS;
					}
				}
			}
		} else {
			if (!world.isRemote) {
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
					if (inventory.canAcceptItem(currentItem)) {
						currentItem = inventory.insertItem(currentItem, false);
						if (currentItem.getCount() != player.inventory.getStackInSlot(i).getCount()) {
							itemInserted = true;
							player.inventory.setInventorySlotContents(i, currentItem);
						}
					}
				}

				if (itemInserted) {
					world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0f, 1.0f);
					markTileEntityForSynchronization();
					return ActionResultType.SUCCESS;
				}
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
				extractItemInWorld(false);
			} else {
				extractItemInWorld(true);
			}
		}
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		return builder.withInitial(RENDERING_STATE, new DigistoreRenderingState(inventory.getStackInSlot(0), inventory.getFilledRatio())).build();
	}

	public boolean isVoidUpgradeInstalled() {
		return inventory.shouldVoidExcess();
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		inventory.setLockState(locked);
	}

	public void extractItemInWorld(boolean singleItem) {
		// Only perform on the server.
		if (world.isRemote) {
			return;
		}

		// Do nothing if there is no card.
		if (inventory.getStackInSlot(0).isEmpty()) {
			return;
		}

		// If someone the card in the slot is not a monostore card, return early.
		if (!(inventory.getStackInSlot(0).getItem() instanceof DigistoreMonoCard)) {
			return;
		}

		// Get the digistore inventory.
		IDigistoreInventory inv = DigistoreCard.getInventory(inventory.getStackInSlot(0));

		// If there are no items in the card, return.
		if (inv.getTotalContainedCount() == 0) {
			return;
		}

		// Set the amount to drop to 1 if a single item is requested, otherwise set it
		// up
		// to a whole stack.
		int countToDrop = 1;
		if (!singleItem) {
			countToDrop = Math.min(inv.getDigistoreStack(0).getStoredItem().getMaxStackSize(), inv.getTotalContainedCount());
		}

		// Extract the item.
		ItemStack output = inv.extractItem(inv.getDigistoreStack(0).getStoredItem(), countToDrop, false);

		// Drop the item. Check the count just in case again (edge case coverage).
		if (countToDrop > 0) {
			WorldUtilities.dropItem(getWorld(), getFacingDirection(), getPos(), output, output.getCount());
		}
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (inventory.getUniqueItemCapacity() > 0) {
			return inventory.getDigistoreStack(0).getStoredItem();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return inventory.insertItem(stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return inventory.extractItem(getStackInSlot(slot), amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return inventory.getItemCapacity();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return inventory.canAcceptItem(stack);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> {
				return this;
			}).cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistore(windowId, inventory, this);
	}

}
