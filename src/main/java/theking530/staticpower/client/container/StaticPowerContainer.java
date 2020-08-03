package theking530.staticpower.client.container;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import theking530.common.utilities.TriFunction;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.container.slots.DummySlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.powered.autocrafter.PacketLockInventorySlot;

public abstract class StaticPowerContainer extends Container {
	public static final int INVENTORY_COMPONENT_FILTER_MOUSE_BUTTON = 69;
	protected int playerInventoryStart;
	protected int playerHotbarStart;
	protected int playerInventoryEnd;
	protected int playerHotbarEnd;
	private final PlayerInventory playerInventory;
	private final Field listenersField;

	protected StaticPowerContainer(ContainerType<?> type, int id, PlayerInventory inv) {
		super(type, id);
		playerInventory = inv;
		listenersField = getListnersField();
		preInitializeContainer();
	}

	/**
	 * Gets the inventory of the player that is using this container.
	 * 
	 * @return The inventory of the player using this container.
	 */
	public PlayerInventory getPlayerInventory() {
		return playerInventory;
	}

	@Override
	public Slot addSlot(Slot slotIn) {
		return super.addSlot(slotIn);
	}

	protected void addPlayerInventory(PlayerInventory invPlayer, int xPosition, int yPosition) {
		playerInventoryStart = this.inventorySlots.size();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(invPlayer, j + i * 9 + 9, xPosition + j * 18, yPosition + i * 18));
			}
		}
		playerInventoryEnd = this.inventorySlots.size() - 1;
	}

	protected void addPlayerHotbar(PlayerInventory invPlayer, int xPosition, int yPosition) {
		playerHotbarStart = this.inventorySlots.size();
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(invPlayer, i, xPosition + i * 18, yPosition));
		}
		playerHotbarEnd = this.inventorySlots.size() - 1;
	}

	/**
	 * This method is called BEFORE all the constructor calls have been made. This
	 * is where code that should be called before any container methods are called
	 * should be placed. Should only be used to initialize values.
	 */
	public void preInitializeContainer() {

	}

	/**
	 * This method is raised AFTER all the constructor calls have been made. This is
	 * where the implementer can initialize the container (set up slots, etc). Any
	 * inheritors from this class specifically (and not from any of the provided
	 * implementations) must call this method at the end of their constructor.
	 */
	public abstract void initializeContainer();

	/**
	 * Returns false if no conditions were met, otherwise returns true. If false,
	 * container handles moving the item between the inventory and the hotbar.
	 */
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		return false;
	}

	protected boolean containerSlotShiftClicked(ItemStack stack, PlayerEntity player, StaticPowerContainerSlot slot, int slotIndex) {
		if (mergeItemStack(stack, playerHotbarStart, playerHotbarEnd + 1, false)) {
			return true;
		} else if (!mergeItemStack(stack, playerInventoryStart, playerInventoryEnd + 1, false)) {
			return true;
		}
		return false;
	}

	protected void addSlotsInGrid(IInventory inventory, int startingIndex, int xPos, int yPos, int maxPerRow, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		addSlotsInGrid(inventory, startingIndex, xPos, yPos, maxPerRow, 16, slotFactory);
	}

	protected void addSlotsInGrid(IItemHandler inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		maxPerRow = Math.min(inventory.getSlots(), maxPerRow);
		int adjustedSlotSize = slotSize + 2;
		int offset = (maxPerRow * adjustedSlotSize) / 2;
		for (int i = 0; i < inventory.getSlots(); i++) {
			int row = i / maxPerRow;
			Slot output = slotFactory.apply(startingIndex + i, xPos + ((i % maxPerRow) * adjustedSlotSize) - offset, yPos + (row * adjustedSlotSize));
			addSlot(output);
		}
	}

	protected void addSlotsInGrid(IInventory inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		maxPerRow = Math.min(inventory.getSizeInventory(), maxPerRow);
		int adjustedSlotSize = slotSize + 2;
		int offset = (maxPerRow * adjustedSlotSize) / 2;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			int row = i / maxPerRow;
			Slot output = slotFactory.apply(startingIndex + i, xPos + ((i % maxPerRow) * adjustedSlotSize) - offset, yPos + (row * adjustedSlotSize));
			addSlot(output);
		}
	}

	protected void addSlotsInPerfectSquare(IItemHandler inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		addSlotsInGrid(inventory, startingIndex, xPos, yPos, maxPerRow, slotSize, slotFactory);
		maxPerRow = Math.min(inventory.getSlots(), maxPerRow);
		int adjustedSlotSize = slotSize + 2;
		int offset = (maxPerRow * adjustedSlotSize) / 2;
		int lastValidSlotIndex = inventory.getSlots() - 1;
		int row = lastValidSlotIndex / maxPerRow;

		int missingSlots = maxPerRow - Math.floorMod(inventory.getSlots(), maxPerRow);

		if (missingSlots % maxPerRow != 0) {
			for (int i = 0; i < missingSlots; i++) {
				int index = inventory.getSlots() - 1 + i;
				Slot output = new DummySlot(index, xPos + (((i + missingSlots) % maxPerRow) * adjustedSlotSize) - offset, yPos + (row * adjustedSlotSize));
				addSlot(output);
			}
		}
	}

	protected boolean isInventorySlot(int slot) {
		return slot >= playerInventoryStart && slot <= playerInventoryEnd;
	}

	protected boolean isHotbarSlot(int slot) {
		return slot >= playerHotbarStart && slot <= playerHotbarEnd;
	}

	protected boolean mergeItemStack(ItemStack stack, int index) {
		return mergeItemStack(stack, index, index + 1, false);
	}

	public ItemStack transferStackInSlot(PlayerEntity player, int invSlot) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(invSlot);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (slot instanceof StaticPowerContainerSlot) {
				containerSlotShiftClicked(itemstack1, player, (StaticPowerContainerSlot) this.inventorySlots.get(invSlot), invSlot);
				slot.onSlotChange(itemstack1, itemstack);
			} else {
				if (!playerItemShiftClicked(itemstack1, player, slot, invSlot)) {
					if (isInventorySlot(invSlot) && !mergeItemStack(itemstack1, playerHotbarStart, playerHotbarEnd + 1, false)) {
						return ItemStack.EMPTY;
					} else if (isHotbarSlot(invSlot) && !mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd + 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				slot.onSlotChange(itemstack1, itemstack);
			}
			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		// If the mouse button was INVENTORY_COMPONENT_FILTER_MOUSE_BUTTON, then this is
		// an attempt to lock a slot.
		if (dragType == INVENTORY_COMPONENT_FILTER_MOUSE_BUTTON) {
			Slot slot = inventorySlots.get(slotId);
			if (slot instanceof StaticPowerContainerSlot && ((StaticPowerContainerSlot) slot).getItemHandler() instanceof InventoryComponent) {
				if (player.getEntityWorld().isRemote) {
					// Get the inventory component.
					InventoryComponent invComponent = ((InventoryComponent) ((StaticPowerContainerSlot) slot).getItemHandler());

					// Get the relative slot index for the input inventory.
					int inputInventorySlot = slotId - 9;
					// If they held control, toggle the locked state of the slot.
					if (Screen.hasControlDown()) {
						if (invComponent.isSlotLocked(inputInventorySlot)) {
							invComponent.unlockSlot(inputInventorySlot);
						} else {
							invComponent.lockSlot(inputInventorySlot, invComponent.getStackInSlot(inputInventorySlot));
						}

						// Send a packet to the server with the updated values.
						NetworkMessage msg = new PacketLockInventorySlot(invComponent, inputInventorySlot, invComponent.isSlotLocked(inputInventorySlot),
								invComponent.getStackInSlot(inputInventorySlot));
						StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

					}
				}
			}
			// Return as we don't want them to modify the container in this case.
			return inventorySlots.get(slotId).getStack();
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	/**
	 * Gets the list of all listeners to this container. Access the private
	 * "listeners" field through cached reflection.
	 * 
	 * @return The list of all listeners to this container.
	 */
	@SuppressWarnings("unchecked")
	public List<IContainerListener> getListeners() {
		try {
			return (List<IContainerListener>) listenersField.get(this);
		} catch (Exception e) {
			StaticPower.LOGGER.error(String.format("An error occured when attempting to access (through reflection) the listeners to this container: %1$s.", this.toString()), e);
		}
		return null;
	}

	/**
	 * Gets the private listeners field.
	 * 
	 * @return
	 */
	private Field getListnersField() {
		return ObfuscationReflectionHelper.findField(Container.class, "field_177758_a");
	}
}
