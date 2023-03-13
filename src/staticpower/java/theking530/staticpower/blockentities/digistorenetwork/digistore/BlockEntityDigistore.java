package theking530.staticpower.blockentities.digistorenetwork.digistore;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandler;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.blockentities.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.blockentities.digistorenetwork.BlockEntityDigistoreBase;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderDigistore;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.utilities.WorldUtilities;

public class BlockEntityDigistore extends BlockEntityDigistoreBase implements IItemHandler {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityDigistore> TYPE = new BlockEntityTypeAllocator<BlockEntityDigistore>("digistore",
			(type, pos, state) -> new BlockEntityDigistore(pos, state), ModBlocks.Digistore);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderDigistore::new);
		}
	}

	public final DigistoreInventoryComponent inventory;
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<DigistoreRenderingState> RENDERING_STATE = new ModelProperty<DigistoreRenderingState>();
	private static final float RENDER_UPDATE_THRESHOLD = 0.05f;

	@UpdateSerialize
	private float lastRenderUpdateFilledRatio;
	@UpdateSerialize
	private boolean locked;

	public BlockEntityDigistore(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, 1);
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

		// If the inventory changes (new card), re-render the block.
		inventory.setModifiedCallback((type, stack, comp) -> {
			if (type != InventoryChangeType.MODIFIED) {
				if (!getLevel().isClientSide()) {
					float filledRatio = inventory.getFilledRatio();
					lastRenderUpdateFilledRatio = filledRatio;
					addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(true), true);
					this.addRenderingUpdateRequest();
				}
			}
		});

		// If the digistore inventory contents change (new card), re-render the block.
		inventory.setDigistoreModifiedCallback((type, stack) -> {
			if (!getLevel().isClientSide()) {
				addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(true), true);
			}
		});
	}

	@Override
	public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
		// If the hand is not empty and the held itemstack matches the item stored in
		// this digistore, attempt to insert it.
		if (!player.getItemInHand(hand).isEmpty()) {
			if (!level.isClientSide) {
				ItemStack heldItem = player.getItemInHand(hand);

				// If the player is holding a card, attempt to insert it.
				if (heldItem.getItem() instanceof DigistoreMonoCard && inventory.getStackInSlot(0).isEmpty()) {

					ItemStack remaining = inventory.insertItem(0, heldItem.copy(), false);
					if (remaining.getCount() != heldItem.getCount()) {
						if (!player.isCreative()) {
							heldItem.setCount(remaining.getCount());
						}

						level.playSound(null, worldPosition, SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 0.1f, 1.8f);
						level.playSound(null, worldPosition, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.8f, 1.0f);
						addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
						return InteractionResult.SUCCESS;
					}
				} else {
					// If not, attempt to insert the item.
					int initialCount = player.getItemInHand(hand).getCount();
					ItemStack remaining = inventory.insertItem(player.getItemInHand(hand), false);
					player.setItemSlot(EquipmentSlot.MAINHAND, remaining);

					if (initialCount != remaining.getCount()) {
						level.playSound(null, worldPosition, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.8f, 1.0f);
						addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
						return InteractionResult.SUCCESS;
					} else {
						return InteractionResult.PASS;
					}
				}
			}
			return InteractionResult.SUCCESS;
		} else {
			if (!level.isClientSide) {
				// Keep track of if any items changed.
				boolean itemInserted = false;

				// Loop through the whole inventory.
				for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
					// Get the item in the slot.
					ItemStack currentItem = player.getInventory().getItem(i).copy();

					// Skip empty slots.
					if (player.getInventory().getItem(i).isEmpty()) {
						continue;
					}
					if (inventory.canAcceptItem(currentItem)) {
						currentItem = inventory.insertItem(currentItem, false);
						if (currentItem.getCount() != player.getInventory().getItem(i).getCount()) {
							itemInserted = true;
							player.getInventory().setItem(i, currentItem);
						}
					}
				}

				if (itemInserted) {
					level.playSound(null, worldPosition, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0f, 1.0f);
					addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return super.onBlockActivated(state, player, hand, hit);
	}

	@Override
	public void onBlockLeftClicked(BlockState state, Player player) {
		super.onBlockLeftClicked(state, player);

		// If the digistore is not empty, extract either a stack or a single item.
		if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
			if (player.isShiftKeyDown()) {
				extractItemInWorld(false);
			} else {
				extractItemInWorld(true);
			}
			addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
		}
	}

	@Nonnull
	@Override
	public ModelData getModelData() {
		ModelData.Builder builder = ModelData.builder();
		return builder.with(RENDERING_STATE, new DigistoreRenderingState(inventory.getStackInSlot(0), inventory.getFilledRatio())).build();
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
		if (level.isClientSide) {
			return;
		}

		// Do nothing if there is no card.
		if (inventory.getStackInSlot(0).isEmpty()) {
			return;
		}

		// If somehow the card in the slot is not a monostore card, return early.
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
		if (!output.isEmpty()) {
			Direction facingDirection = getFacingDirection();
			WorldUtilities.dropItem(getLevel(), facingDirection, getBlockPos().relative(facingDirection), output, output.getCount());
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
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> {
				return this;
			}).cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerDigistore(windowId, inventory, this);
	}

}
