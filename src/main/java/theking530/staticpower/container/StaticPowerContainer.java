package theking530.staticpower.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.gui.ContainerOpener;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticpower.container.slots.DummySlot;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.powered.autocrafter.PacketLockInventorySlot;

public abstract class StaticPowerContainer extends Container {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerContainer.class);
	public static final int INVENTORY_COMPONENT_LOCK_MOUSE_BUTTON = 69;

	public final ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator;

	protected int playerInventoryStart;
	protected int playerHotbarStart;
	protected int playerInventoryEnd;
	protected int playerHotbarEnd;
	private final PlayerInventory playerInventory;
	private final Field listenersField;
	private ContainerOpener<?> opener;
	private ITextComponent name;

	protected StaticPowerContainer(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int id, PlayerInventory inv) {
		super(allocator.getType(), id);
		this.allocator = allocator;
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

	@SuppressWarnings("unchecked")
	public <T extends Slot> T addSlotGeneric(T slotIn) {
		return (T) super.addSlot(slotIn);
	}

	public PacketBuffer getRevertDataPacket() {
		return null;
	}

	public StaticPowerContainer duplicateForRevert(int windowId, PlayerInventory inv, PacketBuffer data) {
		try {
			Constructor<? extends StaticPowerContainer> constructor = getClass().getConstructor(new Class[] { int.class, PlayerInventory.class, PacketBuffer.class });
			return constructor.newInstance(windowId, inv, data);
		} catch (Exception e) {
			LOGGER.error(
					"An error occured when attempting to duplicate the current container for reverting. You may either need to implement getRevertDataPacket() or may need to provide a custom implementation for duplicateForRevert().",
					e);
		}
		return null;
	}

	public StaticPowerContainer setOpener(ContainerOpener<?> opener) {
		this.opener = opener;
		return this;
	}

	public boolean canRevert() {
		return opener != null;
	}

	public void revertToParent() {
		// Open prompt for crafting if we can actually craft some.
		// Create the container opener.
		ContainerOpener<?> requestUi = new ContainerOpener<>(opener.getParent().getName(), (x, y, z) -> opener.getParent().duplicateForRevert(x, y, opener.getParent().getRevertDataPacket()));

		// Open the UI.
		NetworkGUI.openGui((ServerPlayerEntity) getPlayerInventory().player, requestUi, buff -> {
			PacketBuffer parent = opener.getParent().getRevertDataPacket();
			if (parent != null) {
				parent.resetReaderIndex();
				buff.writeBytes(parent);
			}
		});
	}

	public void setName(ITextComponent name) {
		this.name = name;
	}

	public ITextComponent getName() {
		return this.name;
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

	protected List<Slot> addSlotsInGrid(IInventory inventory, int startingIndex, int xPos, int yPos, int maxPerRow, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		return addSlotsInGrid(inventory, startingIndex, xPos, yPos, maxPerRow, 16, slotFactory);
	}

	protected List<Slot> addSlotsInGrid(IItemHandler inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		return addSlotsInGrid(inventory, startingIndex, inventory.getSlots(), xPos, yPos, maxPerRow, slotSize, slotFactory);
	}

	protected List<Slot> addSlotsInGrid(IItemHandler inventory, int startingIndex, int xPos, int yPos, int maxPerRow, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		return addSlotsInGrid(inventory, startingIndex, inventory.getSlots(), xPos, yPos, maxPerRow, 16, slotFactory);
	}

	protected List<Slot> addSlotsInGrid(IItemHandler inventory, int startingIndex, int slotCount, int xPos, int yPos, int maxPerRow, int slotSize,
			TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		List<Slot> outputs = new ArrayList<Slot>();
		maxPerRow = Math.min(inventory.getSlots(), maxPerRow);
		int adjustedSlotSize = slotSize + 2;
		int offset = (maxPerRow * adjustedSlotSize) / 2;
		for (int i = 0; i < slotCount; i++) {
			int row = i / maxPerRow;
			Slot output = slotFactory.apply(startingIndex + i, xPos + ((i % maxPerRow) * adjustedSlotSize) - offset, yPos + (row * adjustedSlotSize));
			outputs.add(addSlot(output));
		}
		return outputs;
	}

	protected List<Slot> addSlotsInGrid(IInventory inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		List<Slot> outputs = new ArrayList<Slot>();
		maxPerRow = Math.min(inventory.getSizeInventory(), maxPerRow);
		int adjustedSlotSize = slotSize + 2;
		int offset = (maxPerRow * adjustedSlotSize) / 2;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			int row = i / maxPerRow;
			Slot output = slotFactory.apply(startingIndex + i, xPos + ((i % maxPerRow) * adjustedSlotSize) - offset, yPos + (row * adjustedSlotSize));
			outputs.add(addSlot(output));
		}
		return outputs;
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
		if (dragType == INVENTORY_COMPONENT_LOCK_MOUSE_BUTTON && slotId >= 0) {
			Slot slot = inventorySlots.get(slotId);
			if (slot instanceof StaticPowerContainerSlot && ((StaticPowerContainerSlot) slot).getItemHandler() instanceof InventoryComponent) {
				if (player.getEntityWorld().isRemote) {
					// Get the inventory component.
					InventoryComponent invComponent = ((InventoryComponent) ((StaticPowerContainerSlot) slot).getItemHandler());

					// If they held control, toggle the locked state of the slot.
					if (Screen.hasControlDown()) {
						if (invComponent.isSlotLocked(slot.getSlotIndex())) {
							invComponent.unlockSlot(slot.getSlotIndex());
						} else {
							invComponent.lockSlot(slot.getSlotIndex(), invComponent.getStackInSlot(slot.getSlotIndex()));
						}

						// Send a packet to the server with the updated values.
						NetworkMessage msg = new PacketLockInventorySlot(invComponent, slot.getSlotIndex(), invComponent.isSlotLocked(slot.getSlotIndex()),
								invComponent.getStackInSlot(slot.getSlotIndex()));
						StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

					}
				}
			}
			// Return as we don't want them to modify the container in this case.
			return inventorySlots.get(slotId).getStack();
		} else if (slotId >= 0 && inventorySlots.get(slotId) instanceof PhantomSlot) {
			// Get the phantom slot.
			PhantomSlot phantSlot = (PhantomSlot) inventorySlots.get(slotId);

			// If the mouse item is empty, if shift is held, clear the slot. If regular
			// click, decrease the stack size. Otherwise, attempt to insert the
			// phantom item.
			if (player.inventory.getItemStack().isEmpty()) {
				if (clickTypeIn == ClickType.QUICK_MOVE) {
					phantSlot.clearPhantom();
				} else {
					phantSlot.decreasePhantomCount(1);
				}
			} else {
				if (clickTypeIn == ClickType.QUICK_MOVE) {
					phantSlot.insertPhantomItem(player.inventory.getItemStack(), 64);
				} else {
					phantSlot.insertPhantomItem(player.inventory.getItemStack(), 1);
				}
			}
			inventorySlots.get(slotId).onSlotChanged();

			// Return as we don't want them to modify the container in this case.
			return inventorySlots.get(slotId).getStack();
		} else {
			return super.slotClick(slotId, dragType, clickTypeIn, player);
		}
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
			LOGGER.error(String.format("An error occured when attempting to access (through reflection) the listeners to this container: %1$s.", this.toString()), e);
		}
		return null;
	}

	/**
	 * Gets the private listeners field.
	 * 
	 * @return
	 */
	private Field getListnersField() {
		return ObfuscationReflectionHelper.findField(Container.class, "field_75149_d");
	}
}
