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
import theking530.staticpower.cables.digistore.DigistoreInventorySortType;
import theking530.staticpower.cables.digistore.DigistoreInventoryWrapper;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.DigistoreSlot;
import theking530.staticpower.client.container.slots.DummySlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class ContainerDigistoreManager extends StaticPowerTileEntityContainer<TileEntityDigistoreManager> {
	public static final int ITEMS_PER_ROW = 9;
	public static final int MAX_ROWS_ON_SCREEN = 8;
	public static final int INVENTORY_START_X = 8;
	public static final int INVENTORY_START_Y = 20;

	private DigistoreSimulatedItemStackHandler clientSimulatedInventory;
	private String filter;
	private DigistoreInventorySortType sortType;
	private boolean sortDescending;
	private int scrollOffset;
	private GuiDigistoreManager clientGui;

	public ContainerDigistoreManager(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreManager) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreManager(int windowId, PlayerInventory playerInventory, TileEntityDigistoreManager owner) {
		super(ModContainerTypes.DIGISTORE_MANAGER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		filter = "";
		sortType = DigistoreInventorySortType.COUNT;
		sortDescending = true;

		// On both the client and the server, add the player slots.
		addPlayerHotbar(getPlayerInventory(), 8, 246);
		addPlayerInventory(getPlayerInventory(), 8, 188);

		// If on the server, populate the digistore container slots.
		if (!getTileEntity().getWorld().isRemote) {
			getDigistoreNetwork().ifPresent(digistoreModule -> {
				DigistoreInventoryWrapper digistoreInv = digistoreModule.getNetworkInventory(filter, sortType, sortDescending);
				addDigistoreSlots(digistoreInv, ITEMS_PER_ROW, INVENTORY_START_X, INVENTORY_START_Y);
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
							// If left click, insert the whole stack, if right click, only one at a time.
							int insertAmount = dragType == 0 ? playerMouseHeldItem.getCount() : 1;
							ItemStack insertStack = ItemHandlerHelper.copyStackWithSize(playerMouseHeldItem, insertAmount);
							ItemStack remaining = digistoreModule.insertItem(insertStack, false);
							getPlayerInventory().getItemStack().shrink(insertStack.getCount() - remaining.getCount());
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

	/**
	 * Sends all inventory changes to the client.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : getListeners()) {
			if (listener instanceof ServerPlayerEntity) {
				syncContainerSlots((ServerPlayerEntity) listener);
			}
		}
	}

	/**
	 * CLIENT ONLY. Sets all the container contents from the server.
	 */
	@OnlyIn(Dist.CLIENT)
	public void setAll(List<ItemStack> items) {
		// Remove all the digistore slots.
		for (int i = inventorySlots.size() - 1; i >= getFirstDigistoreSlotIndex(); i--) {
			inventorySlots.remove(i);
		}

		// Capture how many digistore slots we need.
		int digistoreItems = items.size() - getFirstDigistoreSlotIndex();

		// Create a simulated inventory and add the slots.
		clientSimulatedInventory = new DigistoreSimulatedItemStackHandler(digistoreItems);
		addDigistoreSlots(clientSimulatedInventory, ITEMS_PER_ROW, INVENTORY_START_X, INVENTORY_START_Y);

		// Update the max scroll on the client.
		int overflow = Math.max(0, (clientSimulatedInventory.getSlots() / ITEMS_PER_ROW) - MAX_ROWS_ON_SCREEN);
		clientGui.scrollBar.setMaxScroll(overflow);

		// Let the parent handle populating the slots with items.
		super.setAll(items);
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

	/**
	 * Sets the string to filter the items by. If on the client, a packet is sent to
	 * the server to do the same.
	 */
	public void setFilterString(String filter) {
		this.filter = filter;
		if (getTileEntity().getWorld().isRemote) {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketDigistoreManagerFilters(windowId, filter, sortType, sortDescending));
		}
	}

	public void setSortType(DigistoreInventorySortType sortType, boolean sortDescending) {
		this.sortType = sortType;
		this.sortDescending = sortDescending;
		if (getTileEntity().getWorld().isRemote) {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketDigistoreManagerFilters(windowId, filter, sortType, sortDescending));
		}
	}

	/**
	 * Sets the owning client GUI for this container. WARNING: This value is only
	 * available on the client.
	 * 
	 * @param clientGui
	 */
	public void setClientGui(GuiDigistoreManager clientGui) {
		this.clientGui = clientGui;
	}

	/**
	 * Sets the position of the scroll bar from the client.
	 * 
	 * @param offset
	 */
	public void setScrollOffset(int offset) {
		scrollOffset = offset;
	}

	public String getFilter() {
		return filter;
	}

	public DigistoreInventorySortType getSortType() {
		return sortType;
	}

	public boolean isSortDescending() {
		return sortDescending;
	}

	/**
	 * Determines if the provided slot is a digistore slot or a slot belonging to
	 * some other purpose (ie. Upgrade slot, etc).
	 * 
	 * @param slot
	 * @return
	 */
	protected boolean isDigistoreSlot(int slot) {
		return slot >= getFirstDigistoreSlotIndex();
	}

	/**
	 * Indicates the first index belonging to a digistore.
	 * 
	 * @return
	 */
	protected int getFirstDigistoreSlotIndex() {
		return 36;
	}

	/**
	 * Sends an update packet from the server to the client that synchronizes the
	 * containers.
	 * 
	 * @param player
	 */
	protected void syncContainerSlots(PlayerEntity player) {
		// If being called on the client, throw an exception.
		if (getTileEntity().getWorld().isRemote) {
			throw new RuntimeException("Containers should only be synced from the server!");
		}

		getDigistoreNetwork().ifPresent(digistoreModule -> {
			// Clear the digistore slots.
			for (int i = inventorySlots.size() - 1; i >= getFirstDigistoreSlotIndex(); i--) {
				inventorySlots.remove(i);
			}

			// Get the network inventory.
			DigistoreInventoryWrapper digistoreInv = digistoreModule.getNetworkInventory(filter, sortType, sortDescending);
			addDigistoreSlots(digistoreInv, ITEMS_PER_ROW, INVENTORY_START_X, INVENTORY_START_Y);

			// Sync the whole thing.
			((ServerPlayerEntity) player).sendAllContents(this, this.getInventory());
		});
	}

	/**
	 * Adds the digistore slots to the container.
	 * 
	 * @param digistoreInv
	 * @param rowSize
	 * @param xPos
	 * @param yPos
	 */
	protected void addDigistoreSlots(IItemHandler digistoreInv, int rowSize, int xPos, int yPos) {
		// This is just a constant used to indicate the size of a slot (16 + 2(1) for
		// each border).
		final int adjustedSlotSize = 18;

		// Keep track of how many slots were enabled.
		int enabledSlots = 0;

		// Iterate through all the digistore inventory slots.
		for (int i = 0; i < digistoreInv.getSlots(); i++) {
			// Get the row the slot should render on.
			int row = i / rowSize;

			// Create the slot at the proper x and y positions in the grid (accounting for
			// the scroll offset).
			DigistoreSlot output = new DigistoreSlot(digistoreInv, i, xPos + ((i % rowSize) * adjustedSlotSize), yPos + ((row - scrollOffset) * adjustedSlotSize));

			// We then update the enabled state if this slot should be enabled. A slot is
			// enabled IF it's row is beyond the row that the scroll bar is set to AND it is
			// added before we run out of slots we can display on screen. This only has an
			// effect on the client, only reason we dont do a remote check is to save a some
			// minor performance.
			output.setEnabledState(row >= this.scrollOffset);
			if (output.isEnabled()) {
				output.setEnabledState(i < ITEMS_PER_ROW * MAX_ROWS_ON_SCREEN);
			}

			// If the slot is enabled, increment the enabled slots count.
			enabledSlots += output.isEnabled() ? 1 : 0;

			// Add the slot.
			addSlot(output);
		}

		// Get the count of missing slots (ie. if we only have 20 items in the network
		// but we can render up to 50, we must render 30 blank slots).
		int missingSlots = (ITEMS_PER_ROW * MAX_ROWS_ON_SCREEN) - enabledSlots;

		// Add a dummy slot for each of the missing slots.
		for (int i = 0; i < missingSlots; i++) {
			int index = enabledSlots + i;
			int row = index / rowSize;
			Slot output = new DummySlot(index, xPos + (((i + missingSlots) % rowSize) * adjustedSlotSize), yPos + (row * adjustedSlotSize));
			addSlot(output);
		}
	}

	/**
	 * Attempts to get the digistore network module if it is available.
	 * 
	 * @return
	 */
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
