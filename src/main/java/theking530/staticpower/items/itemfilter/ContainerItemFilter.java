package theking530.staticpower.items.itemfilter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.api.gui.widgets.SlotPhantom;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.container.StaticPowerItemContainer;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerItemFilter extends StaticPowerItemContainer<ItemFilter> {

	private InventoryItemFilter filterInventory;

	public ContainerItemFilter(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerItemFilter(int windowId, PlayerInventory playerInventory, ItemStack owner) {
		super(ModContainerTypes.ITEM_FILTER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((IItemHandler handler) -> {
			filterInventory = (InventoryItemFilter) handler;
		});

		// If the item filter is null, then return early and log the error.
		if (filterInventory == null) {
			StaticPower.LOGGER.error(String.format("Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.", getItemStack().getDisplayName()));
			return;
		}

		int slotOffset = filterInventory.getFilterTier() == FilterTier.BASIC ? 3 : filterInventory.getFilterTier() == FilterTier.UPGRADED ? 1 : 0;
		for (int i = 0; i < filterInventory.getFilterTier().getSlotCount(); i++) {
			this.addSlot(new SlotPhantom(filterInventory, i, 8 + (i + slotOffset) * 18, 19));
		}

		this.addPlayerInventory(getPlayerInventory(), 8, 69);
		this.addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return false;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
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
