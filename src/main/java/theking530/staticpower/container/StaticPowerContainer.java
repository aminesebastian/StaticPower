package theking530.staticpower.container;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.container.ContainerOpener;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.PacketLockInventorySlot;
import theking530.staticpower.container.slots.DummySlot;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.network.StaticPowerMessageHandler;

public abstract class StaticPowerContainer extends AbstractContainerMenu {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerContainer.class);
	public static final int INVENTORY_COMPONENT_LOCK_MOUSE_BUTTON = 69;

	public final ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator;

	protected int playerInventoryStart;
	protected int playerHotbarStart;
	protected int playerInventoryEnd;
	protected int playerHotbarEnd;
	protected int clientHeight;
	protected int clientWidth;
	private final Inventory playerInventory;
	private final List<Slot> playerInventorySlots;
	private final List<Slot> playerHotBarSlots;
	private ContainerOpener<?> opener;
	private Component name;

	protected StaticPowerContainer(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int id, Inventory inv) {
		super(allocator.getType(), id);
		this.allocator = allocator;
		this.playerInventorySlots = new ArrayList<Slot>();
		this.playerHotBarSlots = new ArrayList<Slot>();
		playerInventory = inv;
		preInitializeContainer();
	}

	/**
	 * Updates the GUI screen size. This should not be called manually and instead
	 * left to the StaticPowerContainerGui to call it. This will automatically move
	 * the player slots to stay pinned at the bottom.
	 * 
	 * @param clientWidth
	 * @param clientHeight
	 */
	public void setDimensions(int clientWidth, int clientHeight) {
		this.clientWidth = clientWidth;
		this.clientHeight = clientHeight;

		for (int i = 0; i < playerInventorySlots.size(); i++) {
			Slot slot = playerInventorySlots.get(i);
			int row = i / 9;
			int col = i % 9;
			slot.x = (col * 18) + (clientWidth / 2) - 80;
			slot.y = clientHeight - (row * 18) - 46;

		}
		for (int i = 0; i < playerHotBarSlots.size(); i++) {
			Slot hotbarSlot = playerHotBarSlots.get(i);
			hotbarSlot.x = (18 * i) + (clientWidth / 2) - 80;
			hotbarSlot.y = clientHeight - 24;
		}
	}

	/**
	 * Gets the inventory of the player that is using this container.
	 * 
	 * @return The inventory of the player using this container.
	 */
	public Inventory getPlayerInventory() {
		return playerInventory;
	}

	@SuppressWarnings("unchecked")
	public <T extends Slot> T addSlotGeneric(T slotIn) {
		return (T) super.addSlot(slotIn);
	}

	public FriendlyByteBuf getRevertDataPacket() {
		return null;
	}

	public StaticPowerContainer duplicateForRevert(int windowId, Inventory inv, FriendlyByteBuf data) {
		try {
			Constructor<? extends StaticPowerContainer> constructor = getClass().getConstructor(new Class[] { int.class, Inventory.class, FriendlyByteBuf.class });
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
		ContainerOpener<?> requestUi = new ContainerOpener<>(opener.getParent().getName(),
				(x, y, z) -> opener.getParent().duplicateForRevert(x, y, opener.getParent().getRevertDataPacket()));

		// Open the UI.
		NetworkGUI.openScreen((ServerPlayer) getPlayerInventory().player, requestUi, buff -> {
			FriendlyByteBuf parent = opener.getParent().getRevertDataPacket();
			if (parent != null) {
				parent.resetReaderIndex();
				buff.writeBytes(parent);
			}
		});
	}

	public void setName(Component name) {
		this.name = name;
	}

	public Component getName() {
		return this.name;
	}

	protected void addAllPlayerSlots() {
		this.addPlayerInventory(getPlayerInventory(), 8, -82);
		this.addPlayerHotbar(getPlayerInventory(), 8, -24);
	}

	protected void addPlayerInventory(Inventory invPlayer, int xPosition, int yPosition) {
		playerInventorySlots.clear();
		playerInventoryStart = this.slots.size();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				playerInventorySlots.add(addSlot(new Slot(invPlayer, j + i * 9 + 9, xPosition + j * 18, yPosition + i * 18)));
			}
		}
		playerInventoryEnd = this.slots.size() - 1;
	}

	protected void addPlayerHotbar(Inventory invPlayer, int xPosition, int yPosition) {
		playerHotBarSlots.clear();
		playerHotbarStart = this.slots.size();
		for (int i = 0; i < 9; i++) {
			playerHotBarSlots.add(addSlot(new Slot(invPlayer, i, xPosition + i * 18, yPosition)));
		}
		playerHotbarEnd = this.slots.size() - 1;
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
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		return false;
	}

	protected boolean containerSlotShiftClicked(ItemStack stack, Player player, StaticPowerContainerSlot slot, int slotIndex) {
		if (moveItemStackTo(stack, playerHotbarStart, playerHotbarEnd + 1, false)) {
			return true;
		} else if (!moveItemStackTo(stack, playerInventoryStart, playerInventoryEnd + 1, false)) {
			return true;
		}
		return false;
	}

	protected List<Slot> addSlotsInGrid(Container inventory, int startingIndex, int xPos, int yPos, int maxPerRow, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		return addSlotsInGrid(inventory, startingIndex, xPos, yPos, maxPerRow, 16, slotFactory);
	}

	protected List<Slot> addSlotsInGrid(IItemHandler inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize,
			TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		return addSlotsInGrid(inventory, startingIndex, inventory.getSlots(), xPos, yPos, maxPerRow, slotSize, slotFactory);
	}

	protected List<Slot> addSlotsInGrid(IItemHandler inventory, int startingIndex, int xPos, int yPos, int maxPerRow, TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		return addSlotsInGrid(inventory, startingIndex, inventory.getSlots(), xPos, yPos, maxPerRow, 16, slotFactory);
	}

	protected List<Slot> addSlotsInGrid(IItemHandler inventory, int startingIndex, int slotCount, int xPos, int yPos, int maxPerRow, int slotSize,
			TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		List<Slot> outputs = new ArrayList<Slot>();
		maxPerRow = Math.min(slotCount, maxPerRow);
		int adjustedSlotSize = slotSize + 2;
		int offset = (maxPerRow * adjustedSlotSize) / 2;
		for (int i = 0; i < slotCount; i++) {
			int row = i / maxPerRow;
			Slot output = slotFactory.apply(startingIndex + i, xPos + ((i % maxPerRow) * adjustedSlotSize) - offset, yPos + (row * adjustedSlotSize));
			outputs.add(addSlot(output));
		}
		return outputs;
	}

	protected List<Slot> addSlotsInGrid(Container inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize,
			TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
		List<Slot> outputs = new ArrayList<Slot>();
		maxPerRow = Math.min(inventory.getContainerSize(), maxPerRow);
		int adjustedSlotSize = slotSize + 2;
		int offset = (maxPerRow * adjustedSlotSize) / 2;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			int row = i / maxPerRow;
			Slot output = slotFactory.apply(startingIndex + i, xPos + ((i % maxPerRow) * adjustedSlotSize) - offset, yPos + (row * adjustedSlotSize));
			outputs.add(addSlot(output));
		}
		return outputs;
	}

	protected void addSlotsInPerfectSquare(IItemHandler inventory, int startingIndex, int xPos, int yPos, int maxPerRow, int slotSize,
			TriFunction<Integer, Integer, Integer, Slot> slotFactory) {
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
		return moveItemStackTo(stack, index, index + 1, false);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int invSlot) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(invSlot);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (slot instanceof StaticPowerContainerSlot) {
				containerSlotShiftClicked(itemstack1, player, (StaticPowerContainerSlot) this.slots.get(invSlot), invSlot);
				slot.onQuickCraft(itemstack1, itemstack);
			} else {
				if (!playerItemShiftClicked(itemstack1, player, slot, invSlot)) {
					if (isInventorySlot(invSlot) && !moveItemStackTo(itemstack1, playerHotbarStart, playerHotbarEnd + 1, false)) {
						return ItemStack.EMPTY;
					} else if (isHotbarSlot(invSlot) && !moveItemStackTo(itemstack1, playerInventoryStart, playerInventoryEnd + 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				slot.onQuickCraft(itemstack1, itemstack);
			}
			if (itemstack1.getCount() == 0) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}

	@SuppressWarnings("resource")
	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		// If the mouse button was INVENTORY_COMPONENT_FILTER_MOUSE_BUTTON, then this is
		// an attempt to lock a slot.
		if (dragType == INVENTORY_COMPONENT_LOCK_MOUSE_BUTTON && slotId >= 0) {
			Slot slot = slots.get(slotId);
			if (slot instanceof StaticPowerContainerSlot && ((StaticPowerContainerSlot) slot).getItemHandler() instanceof InventoryComponent) {
				if (player.getCommandSenderWorld().isClientSide) {
					// Get the inventory component.
					InventoryComponent invComponent = ((InventoryComponent) ((StaticPowerContainerSlot) slot).getItemHandler());

					// If they held control, toggle the locked state of the slot.
					if (invComponent.areSlotsLockable()) {
						if (ModKeyBindings.SLOT_LOCK.isDown()) {
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
			}

		} else if (slotId >= 0 && slots.get(slotId) instanceof PhantomSlot) {
			// Get the phantom slot.
			PhantomSlot phantSlot = (PhantomSlot) slots.get(slotId);

			// If the mouse item is empty, if shift is held, clear the slot. If regular
			// click, decrease the stack size. Otherwise, attempt to insert the
			// phantom item.
			if (getCarried().isEmpty()) {
				if (clickTypeIn == ClickType.QUICK_MOVE) {
					phantSlot.clearPhantom();
				} else {
					phantSlot.decreasePhantomCount(1);
				}
			} else {
				if (clickTypeIn == ClickType.QUICK_MOVE) {
					phantSlot.insertPhantomItem(getCarried(), 64);
				} else {
					phantSlot.insertPhantomItem(getCarried(), 1);
				}
			}
			slots.get(slotId).setChanged();

		} else {
			super.clicked(slotId, dragType, clickTypeIn, player);
		}
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
}
