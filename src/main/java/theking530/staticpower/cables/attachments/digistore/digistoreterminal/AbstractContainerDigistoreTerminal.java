package theking530.staticpower.cables.attachments.digistore.digistoreterminal;

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
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentContainer;
import theking530.staticpower.cables.attachments.digistore.iobus.ContainerDigistoreIOBus;
import theking530.staticpower.cables.attachments.digistore.iobus.GuiDigistoreIOBus;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.container.slots.DigistoreSlot;
import theking530.staticpower.container.slots.DummySlot;
import theking530.staticpower.network.StaticPowerMessageHandler;

public abstract class AbstractContainerDigistoreTerminal<T extends AbstractCableAttachment> extends AbstractCableAttachmentContainer<T> {
	public static final ContainerTypeAllocator<ContainerDigistoreIOBus, GuiDigistoreIOBus> TYPE = new ContainerTypeAllocator<>("cable_attachment_digistore_io_bus", ContainerDigistoreIOBus::new,
			GuiDigistoreIOBus::new);

	public static final int DEFAULT_ITEMS_PER_ROW = 9;
	public static final int DEFAULT_MAX_ROWS_ON_SCREEN = 8;
	public static final Vector2D DEFAULT_INVENTORY_START = new Vector2D(8, 22);

	protected int itemsPerRow;
	protected int maxRows;
	protected Vector2D digistoreInventoryPosition;
	protected boolean resyncInv;
	protected boolean managerPresentLastState;

	private int scrollOffset;
	private boolean sortDescending;
	private String filter;
	private DigistoreInventorySortType sortType;
	private DigistoreSimulatedItemStackHandler clientSimulatedInventory;

	public AbstractContainerDigistoreTerminal(ContainerTypeAllocator<?, ?> allocator, int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide,
			AbstractCableProviderComponent cableComponent) {
		super(allocator, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void preInitializeContainer() {
		itemsPerRow = DEFAULT_ITEMS_PER_ROW;
		maxRows = DEFAULT_MAX_ROWS_ON_SCREEN;
		digistoreInventoryPosition = DEFAULT_INVENTORY_START;
		scrollOffset = 0;
		sortDescending = true;
		filter = "";
		resyncInv = false;
		sortType = DigistoreInventorySortType.COUNT;
	}

	@Override
	public void initializeContainer() {
		managerPresentLastState = getCableComponent().isManagerPresent();

		// On both the client and the server, add the player slots.
		addPlayerHotbar(getPlayerInventory(), 8, 246);
		addPlayerInventory(getPlayerInventory(), 8, 188);

		// If on the server, populate the digistore container slots.
		if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
			getDigistoreNetwork().ifPresent(digistoreModule -> {
				DigistoreInventorySnapshot digistoreInv = digistoreModule.getNetworkInventorySnapshot(filter, sortType, sortDescending);
				addDigistoreSlots(digistoreInv, itemsPerRow, digistoreInventoryPosition.getXi(), digistoreInventoryPosition.getYi());
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
				// their mouse. Check to make sure the slot is a valid one and not a dummy.
				final ItemStack actualSlotContents = slotId < inventorySlots.size() ? inventorySlots.get(slotId).getStack() : ItemStack.EMPTY;
				ItemStack playerMouseHeldItem = getPlayerInventory().getItemStack();

				// Only perform on the server.
				if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
					getDigistoreNetwork().ifPresent(digistoreModule -> {
						// If on the server, attempt to either insert into the network if the held stack
						// is not empty, otherwise, attempt to extract. Extract up to a full stack if
						// left
						// clicking, or a half stack if not.
						if (playerMouseHeldItem.isEmpty()) {
							int halfStackSize = actualSlotContents.getCount() >= actualSlotContents.getMaxStackSize() ? (actualSlotContents.getMaxStackSize() + 1) / 2
									: (actualSlotContents.getCount() + 1) / 2;
							int takeAmount = dragType == 0 ? actualSlotContents.getMaxStackSize() : halfStackSize;
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
		if (managerPresentLastState != getCableComponent().isManagerPresent()) {
			managerPresentLastState = getCableComponent().isManagerPresent();
			onManagerStateChanged(managerPresentLastState);
		}

		for (IContainerListener listener : getListeners()) {
			if (listener instanceof ServerPlayerEntity) {
				syncContainerSlots((ServerPlayerEntity) listener);
			}
		}
	}

	protected void onManagerStateChanged(boolean isPresent) {

	}

	/**
	 * CLIENT ONLY. Sets all the container contents from the server.
	 */
	@OnlyIn(Dist.CLIENT)
	public void setAll(List<ItemStack> items) {
		// Capture how many digistore slots we need.
		int digistoreItems = items.size() - getFirstDigistoreSlotIndex();

		// Create a simulated inventory and add the slots.
		clientSimulatedInventory = new DigistoreSimulatedItemStackHandler(digistoreItems);
		addDigistoreSlots(clientSimulatedInventory, itemsPerRow, digistoreInventoryPosition.getXi(), digistoreInventoryPosition.getYi());

		// Let the parent handle populating the slots with items.
		super.setAll(items);
	}

	@Override
	public DigistoreCableProviderComponent getCableComponent() {
		return (DigistoreCableProviderComponent) super.getCableComponent();
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
			AtomicReference<ItemStack> output = new AtomicReference<ItemStack>(ItemStack.EMPTY);
			getDigistoreNetwork().ifPresent(digistoreModule -> {
				resyncInv = true;
				// Get the targeted item.
				ItemStack targetItem = inventorySlots.get(slotIndex).getStack();

				// If the slot is empty, do nothing.
				if (targetItem.isEmpty()) {
					output.set(targetItem);
					return;
				}

				// If this slot is in the players inventory.
				if (slotIndex <= this.playerInventoryEnd) {
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
					if (simulatedExtract.getCount() != insertAttempt.getCount()) {
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

	public void updateSortAndFilter(String filter, DigistoreSearchMode mode, DigistoreInventorySortType sortType, boolean sortDescending) {
		this.filter = filter;
		this.sortType = sortType;
		this.sortDescending = sortDescending;
		DigistoreTerminal.setSortType(getAttachment(), sortType);
		DigistoreTerminal.setSortDescending(getAttachment(), sortDescending);
		resyncInv = true;
		if (getCableComponent().getWorld().isRemote) {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketDigistoreTerminalFilters(windowId, filter, mode, sortType, sortDescending));
		}
	}

	/**
	 * Sets the position of the scroll bar from the client.
	 * 
	 * @param offset
	 */
	public void setScrollOffset(int offset) {
		if (scrollOffset != offset) {
			scrollOffset = offset;
			addDigistoreSlots(clientSimulatedInventory, itemsPerRow, digistoreInventoryPosition.getXi(), digistoreInventoryPosition.getYi());
		}
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

	public int getItemsPerRow() {
		return itemsPerRow;
	}

	protected void setItemsPerRow(int itemsPerRow) {
		this.itemsPerRow = itemsPerRow;
		resyncInv = true;
	}

	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Returns how many digistore slots are on screen.
	 * 
	 * @return
	 */
	public int getDigistoreSlotCount() {
		return itemsPerRow * maxRows;
	}

	/**
	 * Gets the maximum amount of scroll that should be available to the user.
	 * 
	 * @return
	 */
	public int getMaxScroll() {
		return (int) Math.max(0, Math.ceil((double) clientSimulatedInventory.getSlots() / itemsPerRow) - maxRows);
	}

	protected void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
		resyncInv = true;
	}

	public Vector2D getDigistoreInventoryPosition() {
		return digistoreInventoryPosition;
	}

	protected void setDigistoreInventoryPosition(Vector2D digistoreInventoryPosition) {
		this.digistoreInventoryPosition = digistoreInventoryPosition;
		resyncInv = true;
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
		return playerInventoryEnd + 1;
	}

	/**
	 * Returns true if any digistore slots are in this container. Always true except
	 * the first time the container is opened.
	 * 
	 * @return
	 */
	protected boolean hasDigistoreSlots() {
		return getFirstDigistoreSlotIndex() < inventorySlots.size();
	}

	/**
	 * Sends an update packet from the server to the client that synchronizes the
	 * containers.
	 * 
	 * @param player
	 */
	protected void syncContainerSlots(PlayerEntity player) {
		// If being called on the client, throw an exception.
		if (getCableComponent().getWorld().isRemote) {
			throw new RuntimeException("Containers should only be synced from the server!");
		}

		getDigistoreNetwork().ifPresent(digistoreModule -> {
			// Get the old inventory if we have initialized this container and there is a
			// atleast one digistore slot.
			DigistoreInventorySnapshot oldInv = null;
			if (hasDigistoreSlots() && inventorySlots.get(getFirstDigistoreSlotIndex()) instanceof DigistoreSlot) {
				oldInv = (DigistoreInventorySnapshot) ((DigistoreSlot) inventorySlots.get(getFirstDigistoreSlotIndex())).getItemHandler();
			}

			// Clear the digistore slots.
			for (int i = inventorySlots.size() - 1; i >= getFirstDigistoreSlotIndex(); i--) {
				inventorySlots.remove(i);
			}

			// Get the network inventory.
			DigistoreInventorySnapshot digistoreInv = digistoreModule.getNetworkInventorySnapshot(filter, sortType, sortDescending);
			addDigistoreSlots(digistoreInv, itemsPerRow, digistoreInventoryPosition.getXi(), digistoreInventoryPosition.getYi());

			if (oldInv != null && !resyncInv) {
				if (oldInv.getSlots() == digistoreInv.getSlots()) {
					for (int i = 0; i < oldInv.getSlots(); i++) {
						ItemStack oldStack = oldInv.getStackInSlot(i);
						if (!ItemStack.areItemStacksEqual(oldStack, digistoreInv.getStackInSlot(i))) {
							// System.out.println("Syncing Slot: " + i + " Item:" + oldStack);
							((ServerPlayerEntity) player).sendSlotContents(this, getFirstDigistoreSlotIndex() + i, digistoreInv.getStackInSlot(i));
						}
					}
				} else {
					// Sync the whole thing.
					((ServerPlayerEntity) player).sendAllContents(this, this.getInventory());
				}
			} else {
				// Sync the whole thing.
				((ServerPlayerEntity) player).sendAllContents(this, this.getInventory());
				// System.out.println("WHOLE INV SYNC");
				resyncInv = false;
			}
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
		// Remove all the digistore slots.
		for (int i = inventorySlots.size() - 1; i >= getFirstDigistoreSlotIndex(); i--) {
			inventorySlots.remove(i);
		}

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
			DigistoreSlot output = new DigistoreSlot(digistoreInv, i, xPos + ((i % rowSize) * adjustedSlotSize), yPos + ((row - scrollOffset) * adjustedSlotSize),
					getCableComponent().isManagerPresent());

			// We then update the enabled state if this slot should be enabled. A slot is
			// enabled IF it's row is beyond the row that the scroll bar is set to AND it is
			// added before we run out of slots we can display on screen. This only has an
			// effect on the client, only reason we dont do a remote check is to save a some
			// minor performance.
			output.setEnabledState(row >= this.scrollOffset);

			if (output.isEnabled()) {
				output.setEnabledState((i - (scrollOffset * rowSize)) < itemsPerRow * maxRows);
			}

			// If the slot is enabled, increment the enabled slots count.
			enabledSlots += output.isEnabled() ? 1 : 0;

			// Add the slot.
			addSlot(output);
		}

		// Get the count of missing slots (ie. if we only have 20 items in the network
		// but we can render up to 50, we must render 30 blank slots).
		int missingSlots = (itemsPerRow * maxRows) - enabledSlots;

		// Add a dummy slot for each of the missing slots.
		for (int i = 0; i < missingSlots; i++) {
			int index = enabledSlots + i;
			int row = index / rowSize;
			Slot output = new DummySlot(index, xPos + ((i + (rowSize - (missingSlots % rowSize))) * adjustedSlotSize), yPos + (row * adjustedSlotSize));
			addSlot(output);
		}
	}

	/**
	 * Attempts to get the digistore network module if it is available.
	 * 
	 * @return
	 */
	public Optional<DigistoreNetworkModule> getDigistoreNetwork() {
		// Make sure we only call this on the server.
		if (getCableComponent().getWorld().isRemote) {
			throw new RuntimeException("Attempted to get the Digistore Network from client code.");
		}

		// Get the server cable for this manager.
		ServerCable cable = CableNetworkManager.get(getCableComponent().getWorld()).getCable(getCableComponent().getPos());

		// If it or it's network are null, return null.
		if (cable == null || cable.getNetwork() == null) {
			return Optional.empty();
		}

		// Return the module.
		return Optional.of(cable.getNetwork().getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE));
	}

}
