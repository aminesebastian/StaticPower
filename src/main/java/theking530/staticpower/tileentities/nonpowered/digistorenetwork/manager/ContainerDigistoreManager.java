package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.cables.digistore.DigistoreInventoryWrapper;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.DigistoreSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerDigistoreManager extends StaticPowerTileEntityContainer<TileEntityDigistoreManager> {
	private DigistoreSimulatedItemStackHandler clientSimulatedInventory;
	private String filter;

	public ContainerDigistoreManager(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreManager) resolveTileEntityFromDataPacket(inv, data));
		filter = "";
	}

	public ContainerDigistoreManager(int windowId, PlayerInventory playerInventory, TileEntityDigistoreManager owner) {
		super(ModContainerTypes.DIGISTORE_MANAGER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// On both the client and the server, add the player slots.
		addPlayerHotbar(getPlayerInventory(), 8, 246);
		addPlayerInventory(getPlayerInventory(), 8, 188);

		// If on the server, populate the digistore container slots.
		if (!getTileEntity().getWorld().isRemote) {
			getDigistoreNetwork().ifPresent(digistoreModule -> {
				DigistoreInventoryWrapper digistoreInv = digistoreModule.getNetworkInventory();
				addDigistoreSlots(digistoreInv);
			});
		}
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		// If we clicked on a digistore network slot, perform the appropriate action.
		if (isDigistoreSlot(slotId)) {
			// If we're picking up (or inserting).
			if (clickTypeIn == ClickType.PICKUP) {
				// Get the contents in the slot and the stack the player is currently holding on
				// their mouse.
				ItemStack actualSlotContents = inventorySlots.get(slotId).getStack();
				ItemStack playerMouseHeldItem = getPlayerInventory().getItemStack();

				// Only perform on the server.
				if (!getTileEntity().getWorld().isRemote) {
					getDigistoreNetwork().ifPresent(digistoreModule -> {
						// If on the server, attempt to either insert into the network if the held stack
						// is empty, otherwise, attempt to extract. Extract up to a full stack if left
						// clicking, or a half stack if not.
						if (playerMouseHeldItem.isEmpty()) {
							int takeAmount = dragType == 0 ? actualSlotContents.getMaxStackSize() : (actualSlotContents.getMaxStackSize() + 1) / 2;
							ItemStack actuallyExtracted = digistoreModule.extractItem(actualSlotContents, takeAmount, false);
							getPlayerInventory().setItemStack(actuallyExtracted);
						} else {
							ItemStack remaining = digistoreModule.insertItem(playerMouseHeldItem.copy(), false);
							getPlayerInventory().setItemStack(remaining);
						}

						// Update the held item.
						((ServerPlayerEntity) player).updateHeldItem();
					});
				}
				return actualSlotContents;
			} else if (clickTypeIn == ClickType.QUICK_MOVE) {
				// If we're handling a quick move (shift click), hand that off to the
				// #transferStaclInSlot method.
				return transferStackInSlot(player, slotId);
			} else {
				return ItemStack.EMPTY;
			}
		} else {
			return super.slotClick(slotId, dragType, clickTypeIn, player);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : getListeners()) {
			if (listener instanceof ServerPlayerEntity) {
				syncContainerSlots((ServerPlayerEntity) listener);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void setAll(List<ItemStack> items) {
		// Remove all the digistore slots.
		for (int i = inventorySlots.size() - 1; i >= getStartingDigistoreSlot(); i--) {
			inventorySlots.remove(i);
		}

		// Capture how many digistore slots we need.
		int digistoreItems = items.size() - getStartingDigistoreSlot();

		// Create a simulated inventory and add the slots.
		clientSimulatedInventory = new DigistoreSimulatedItemStackHandler(digistoreItems);
		addDigistoreSlots(clientSimulatedInventory);

		// Let the parent handle populating the slots with items.
		super.setAll(items);

		if(filter.length() > 0) {
			for (Slot slot : inventorySlots) {
				if (slot instanceof StaticPowerContainerSlot) {
					boolean enabled = slot.getHasStack() && slot.getStack().getDisplayName().getFormattedText().toLowerCase().contains(this.filter.toLowerCase());
					((StaticPowerContainerSlot) slot).setEnabledState(enabled);
				}
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		if (!getTileEntity().getWorld().isRemote) {
			AtomicReference<ItemStack> output = new AtomicReference<ItemStack>(ItemStack.EMPTY);
			getDigistoreNetwork().ifPresent(digistoreModule -> {
				// Get the targeted item.
				ItemStack targetItem = inventorySlots.get(slotIndex).getStack();

				// If the slot is empty, do nothing.
				if (targetItem.isEmpty()) {
					output.set(targetItem);
					return;
				}

				// If this slot is in the players inventory.
				if (slotIndex <= 35) {
					// Attempt to insert it into the digistore network.
					ItemStack remaining = digistoreModule.insertItem(targetItem, false);

					// Shrink the stack in the player's inventory by the amount that was inserted.
					targetItem.shrink(targetItem.getCount() - remaining.getCount());

					// Return the remaining stack.
					output.set(remaining);
					return;
				} else {
					// Get the max amount to pull (either the amount there is, or the max stack size
					// if we have more than the max stack size in the network).
					ItemStack actualSizeTargetItem = ItemHandlerHelper.copyStackWithSize(targetItem, Math.min(targetItem.getCount(), targetItem.getMaxStackSize()));

					// Simulate extracting that amount.
					ItemStack simulatedExtract = digistoreModule.extractItem(actualSizeTargetItem, actualSizeTargetItem.getCount(), true);

					// Allocate a new copy of the extract to use when inserting into the player's
					// inventory.
					ItemStack insertAttempt = simulatedExtract.copy();

					// Attempt the insert.
					player.inventory.addItemStackToInventory(insertAttempt);

					// If the extracted amount is different than the amount remaining after the
					// attempt to insert into the player's inventory, that means at least one item
					// was inserted.
					if (simulatedExtract.getCount() != actualSizeTargetItem.getCount()) {
						// Perform the extract for real now with the amount we were able to insert into
						// the player's inventory.
						ItemStack extracted = digistoreModule.extractItem(simulatedExtract, simulatedExtract.getCount() - insertAttempt.getCount(), false);
						output.set(extracted);
						return;
					}
				}
			});

		}
		return ItemStack.EMPTY;
	}

	public void setFilterString(String filter) {
		this.filter = filter;
	}

	protected boolean isDigistoreSlot(int slot) {
		return slot >= getStartingDigistoreSlot();
	}

	protected int getStartingDigistoreSlot() {
		return 36;
	}

	protected void syncContainerSlots(PlayerEntity player) {
		// If being called on the client, throw an exception.
		if (getTileEntity().getWorld().isRemote) {
			throw new RuntimeException("Containers should only be synced from the server!");
		}

		getDigistoreNetwork().ifPresent(digistoreModule -> {
			// Clear the digistore slots.
			for (int i = inventorySlots.size() - 1; i >= getStartingDigistoreSlot(); i--) {
				inventorySlots.remove(i);
			}

			// Get the network inventory.
			DigistoreInventoryWrapper digistoreInv = digistoreModule.getNetworkInventory();
			addDigistoreSlots(digistoreInv);

			// Sync the whole thing.
			((ServerPlayerEntity) player).sendAllContents(this, this.getInventory());
		});
	}

	protected void addDigistoreSlots(IItemHandler digistoreInv) {
		// Add slots for the network inventory.
		addSlotsInPerfectSquare(digistoreInv, 0, 89, 36, 9, 16, (index, x, y) -> new DigistoreSlot(digistoreInv, index, x, y));
	}

	protected Optional<DigistoreNetworkModule> getDigistoreNetwork() {
		// Get the server cable for this manager.
		ServerCable cable = CableNetworkManager.get(getTileEntity().getWorld()).getCable(getTileEntity().getPos());

		// If it or it's network are null, return null.
		if (cable == null || cable.getNetwork() == null) {
			return Optional.empty();
		}

		// Return the module.
		return Optional.of(cable.getNetwork().getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE));
	}

}
