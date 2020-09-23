package theking530.staticpower.items.itemfilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerItemContainer;
import theking530.staticpower.container.slots.PhantomSlot;

public class ContainerItemFilter extends StaticPowerItemContainer<ItemFilter> {
	public static final Logger LOGGER = LogManager.getLogger(ContainerItemFilter.class);

	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerItemFilter, GuiItemFilter> TYPE = new ContainerTypeAllocator<>("filter_item", ContainerItemFilter::new, GuiItemFilter::new);

	private ItemStackHandler filterInventory;

	public ContainerItemFilter(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerItemFilter(int windowId, PlayerInventory playerInventory, ItemStack owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// If the item filter is null, then return early and log the error.
		if (filterInventory == null) {
			LOGGER.error(String.format("Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.", getItemStack().getDisplayName()));
			return;
		}

		int slotOffset = 0;
		for (int i = 0; i < filterInventory.getSlots(); i++) {
			this.addSlot(new PhantomSlot(filterInventory, i, 8 + (i + slotOffset) * 18, 19, true).renderFluidContainerAsFluid());
		}

		this.addPlayerInventory(getPlayerInventory(), 8, 69);
		this.addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return false;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		boolean alreadyExists = false;
		int firstEmptySlot = -1;

		for (int i = 0; i < filterInventory.getSlots(); i++) {
			if (firstEmptySlot == -1 && filterInventory.getStackInSlot(i).isEmpty()) {
				firstEmptySlot = i;
			}
			if (ItemHandlerHelper.canItemStacksStack(filterInventory.getStackInSlot(i), stack)) {
				alreadyExists = true;
			}
		}
		if (!alreadyExists && !mergeItemStack(stack, firstEmptySlot, firstEmptySlot + 1, false)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}
}
