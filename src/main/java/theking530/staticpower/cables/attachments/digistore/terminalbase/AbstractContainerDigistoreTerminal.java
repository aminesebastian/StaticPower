package theking530.staticpower.cables.attachments.digistore.terminalbase;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.StringTextComponent;
import theking530.staticcore.container.ContainerOpener;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentContainer;
import theking530.staticpower.cables.attachments.digistore.terminal.DigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractGuiDigistoreTerminal.TerminalViewType;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.ContainerCraftingAmount;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketDigistoreFakeSlotClicked;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketDigistoreTerminalFilters;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketGetCurrentCraftingQueue;
import theking530.staticpower.cables.attachments.digistore.terminalbase.network.PacketSyncDigistoreInventory;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot.DigistoreItemCraftableState;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.network.PacketCancelDigistoreCraftingRequest;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle.CraftingStepsBundleContainer;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.PlayerArmorItemSlot;
import theking530.staticpower.items.tools.DigistoreWirelessTerminal;
import theking530.staticpower.network.StaticPowerMessageHandler;

public abstract class AbstractContainerDigistoreTerminal<T extends Item> extends AbstractCableAttachmentContainer<T> {

	/**
	 * Indicates whether or not the manager was present last tick.
	 */
	protected boolean managerPresentLastState;
	/**
	 * Client side simulated digistore inventory.
	 */
	private DigistoreInventorySnapshot clientInventory;
	/**
	 * The view type of the terminal (Items, Crafting Reqeusts, etc).
	 */
	private TerminalViewType viewType;
	/**
	 * List of active crafting requests.
	 */
	private List<CraftingRequestResponse> craftingRequests;
	/**
	 * The string filter to use on the digistore items.
	 */
	private String filter;
	/**
	 * The sort type (alpha, count, etc).
	 */
	private DigistoreInventorySortType sortType;
	/**
	 * The sorting order for the digistore items.
	 */
	private boolean sortDescending;

	public AbstractContainerDigistoreTerminal(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int windowId, PlayerInventory playerInventory, ItemStack attachment,
			Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(allocator, windowId, playerInventory, attachment, attachmentSide, cableComponent);
		sortDescending = true;
		filter = "";
		sortType = DigistoreInventorySortType.COUNT;
	}

	@Override
	public void preInitializeContainer() {
		craftingRequests = new LinkedList<CraftingRequestResponse>();
		clientInventory = DigistoreInventorySnapshot.createEmpty();
	}

	@Override
	public void initializeContainer() {
		viewType = TerminalViewType.ITEMS;
		managerPresentLastState = getCableComponent().isManagerPresent();

		// Armor
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 39, -18, 113, EquipmentSlotType.HEAD));
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 38, -18, 131, EquipmentSlotType.CHEST));
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 37, -18, 149, EquipmentSlotType.LEGS));
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 36, -18, 167, EquipmentSlotType.FEET));

		// On both the client and the server, add the player slots.
		addPlayerHotbar(getPlayerInventory(), 8, 246);
		addPlayerInventory(getPlayerInventory(), 8, 188);
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		// Edge case for wireless terminals, don't let players modify their held item.
		if (slotId >= 0 && getSlot(slotId) != null && !getSlot(slotId).getStack().isEmpty() && getSlot(slotId).getStack() == player.getHeldItemMainhand()
				&& (player.getHeldItemMainhand().getItem() instanceof DigistoreWirelessTerminal)) {
			return ItemStack.EMPTY;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	/**
	 * Sends all inventory changes to the client.
	 */
	@Override
	public void detectAndSendChanges() {
		// This probably won't do anything, but keep it for compatability reasons.
		super.detectAndSendChanges();

		// Check for the manager state.
		if (managerPresentLastState != getCableComponent().isManagerPresent()) {
			managerPresentLastState = getCableComponent().isManagerPresent();
			onManagerStateChanged(managerPresentLastState);
		}

		// Sync the snapshots. Only do this on the server. This check shouldn't be
		// required but vanilla has a bug. Only do so if the inventory actually changed.
		if (!getPlayerInventory().player.getEntityWorld().isRemote()) {
			getDigistoreNetwork().ifPresent((network) -> {
				// Hold on to the old inventory and get a new snapshot.
				DigistoreInventorySnapshot oldSnapshot = clientInventory;
				clientInventory = network.getNetworkInventorySnapshotForDisplay(filter, sortType, sortDescending);

				// Only do the following if the inventory has changed.
				if (!clientInventory.equals(oldSnapshot)) {
					for (IContainerListener listener : this.listeners) {
						if (listener instanceof ServerPlayerEntity) {
							syncContentsToClient(clientInventory, (ServerPlayerEntity) listener);
						}
					}
				}
			});
		}

		// Because of the way the shift click goes between a slot and a fake slot, we
		// have to manually do this.
		// There is a way to optimize this, TO-DO.
		for (IContainerListener icontainerlistener : this.listeners) {
			if (icontainerlistener instanceof ServerPlayerEntity) {
				((ServerPlayerEntity) icontainerlistener).sendContainerToPlayer(this);
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
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
				if (slotIndex <= this.playerInventoryEnd) {
					// Attempt to insert it into the digistore network.
					ItemStack remaining = digistoreModule.insertItem(targetItem, false);

					// Shrink the stack in the player's inventory by the amount that was inserted.
					targetItem.shrink(targetItem.getCount() - remaining.getCount());

					// Return the remaining stack.
					output.set(remaining);
					return;
				}
			});

		}

		// We have to return empty, do NOT call super.
		return ItemStack.EMPTY;
	}

	public void digistoreFakeSlotClickedOnClient(ItemStack stack, MouseButton button, boolean shiftHeld, boolean controlHeld, boolean altHeld) {
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketDigistoreFakeSlotClicked(windowId, stack, button, shiftHeld, controlHeld, altHeld));
	}

	public void digistoreFakeSlotClickedOnServer(ItemStack stack, MouseButton button, boolean shiftHeld, boolean controlHeld, boolean altHeld) {
		if (!getPlayerInventory().player.getEntityWorld().isRemote()) {
			getDigistoreNetwork().ifPresent((network) -> {
				// If the player is holding an item, attempt to insert it.
				// Otherwise, attempt to extract items/trigger a crafting job.
				if (!getPlayerInventory().getItemStack().isEmpty()) {
					// Get the item to insert. When right clicking, only insert a single item.
					ItemStack stackToInsert = getPlayerInventory().getItemStack().copy();
					if (button == MouseButton.RIGHT) {
						stackToInsert.setCount(1);
					}

					// Insert the item and capture the amount inserted.
					ItemStack remaining = network.insertItem(stackToInsert, false);
					int inserted = stackToInsert.getCount() - remaining.getCount();

					// Update the player's held item.
					getPlayerInventory().getItemStack().shrink(inserted);
					((ServerPlayerEntity) (getPlayerInventory().player)).updateHeldItem();
				} else if (!stack.isEmpty()) {
					// Get its craftable state.
					DigistoreItemCraftableState itemCraftableState = DigistoreInventorySnapshot.getCraftableStateOfItem(stack);

					// Check if we should craft it. This is true if the item is ONLY craftable, or
					// if the player held the craft button.
					boolean shouldCraft = itemCraftableState == DigistoreItemCraftableState.ONLY_CRAFTABLE;
					shouldCraft |= itemCraftableState == DigistoreItemCraftableState.CRAFTABLE && controlHeld;

					// If we should craft it, attempt to add a request for it. IF not, just pull it
					// out like usual.
					if (shouldCraft) {
						// Calculate the max craftable.
						CraftingStepsBundleContainer newBundles = network.getCraftingManager().calculateAllPossibleCraftingTrees(stack, 1);

						// Open prompt for crafting if we can actually craft some.
						// Create the container opener.
						ContainerOpener<?> requestUi = new ContainerOpener<>(new StringTextComponent("Crafting Request"), (id, inv, data) -> {
							return new ContainerCraftingAmount(id, inv, newBundles, network.getNetwork().getId());
						}).fromParent(this);

						// Open the UI.
						requestUi.open((ServerPlayerEntity) getPlayerInventory().player, buff -> {
							buff.writeString(newBundles.serializeCompressed());
							buff.writeLong(network.getNetwork().getId());
						});
					} else {
						// Get the amount of item to actually extract.
						int extract = stack.getMaxStackSize();

						// Simulate an extract to make sure we have the item on the server side and the
						// correct number.
						ItemStack adjustedStack = network.extractItem(stack, extract, true);

						// If shift is held, add up to a whole stack to the player's inventory.
						if (shiftHeld) {
							// If we fail to do so, return early.
							if (!getPlayerInventory().addItemStackToInventory(adjustedStack)) {
								return;
							}
						} else if (button == MouseButton.MIDDLE) {
							// If we're scrolling, just grab a single item.
							extract = 1;
							adjustedStack.setCount(extract);

							// If we fail to do so, return early.
							if (!getPlayerInventory().addItemStackToInventory(adjustedStack)) {
								return;
							}
						} else {
							// If the mouse button used was the right mouse button, split the stack.
							if (button == MouseButton.RIGHT) {
								extract = adjustedStack.getCount() / 2;
								adjustedStack.setCount(extract);
							}

							// Set the player's held item.
							getPlayerInventory().setItemStack(adjustedStack);
							((ServerPlayerEntity) (getPlayerInventory().player)).updateHeldItem();
						}

						// Extract the item for real now.
						network.extractItem(stack, extract, false);
					}
				}
			});
		}
	}

	public void updateSortAndFilter(String filter, DigistoreSyncedSearchMode mode, DigistoreInventorySortType sortType, boolean sortDescending) {
		this.filter = filter;
		this.sortType = sortType;
		this.sortDescending = sortDescending;
		DigistoreTerminal.setSortType(getAttachment(), sortType);
		DigistoreTerminal.setSortDescending(getAttachment(), sortDescending);
		DigistoreTerminal.setSearchMode(getAttachment(), mode);

		// If on the client, send an update to the server to update these values too.
		if (getCableComponent().getWorld().isRemote) {
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketDigistoreTerminalFilters(windowId, filter, mode, sortType, sortDescending));
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

	public void setViewType(TerminalViewType type) {
		this.viewType = type;
	}

	public TerminalViewType getViewType() {
		return this.viewType;
	}

	public List<CraftingRequestResponse> getCurrentCraftingQueue() {
		return craftingRequests;
	}

	public void updateCraftingQueue(List<CraftingRequestResponse> queue) {
		craftingRequests = queue;
	}

	public void refreshCraftingQueue() {
		PacketGetCurrentCraftingQueue newCraftingRequest = new PacketGetCurrentCraftingQueue(windowId);
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, newCraftingRequest);
	}

	public void cancelCraftingRequest(long id) {
		PacketCancelDigistoreCraftingRequest cancelRequest = new PacketCancelDigistoreCraftingRequest(windowId, id);
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, cancelRequest);
		refreshCraftingQueue();
	}

	public boolean isManagerOnline() {
		return managerPresentLastState;
	}

	public void syncContentsToClient(DigistoreInventorySnapshot digistoreInv, ServerPlayerEntity player) {
		if (!player.getEntityWorld().isRemote()) {
			StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, player, new PacketSyncDigistoreInventory(windowId, digistoreInv));
		}
	}

	public void syncContentsFromServer(DigistoreInventorySnapshot snapshot) {
		clientInventory = snapshot;
	}

	public DigistoreInventorySnapshot getDigistoreClientInventory() {
		return this.clientInventory;
	}

	@Override
	public DigistoreCableProviderComponent getCableComponent() {
		return (DigistoreCableProviderComponent) super.getCableComponent();
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

	protected void onManagerStateChanged(boolean isPresent) {

	}

	protected void markForResync() {

	}
}
