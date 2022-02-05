package theking530.staticpower.items.itemfilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.container.StaticPowerItemContainer;
import theking530.staticpower.container.slots.PhantomSlot;

public class ContainerItemFilter extends StaticPowerItemContainer<ItemFilter> {
	public static final Logger LOGGER = LogManager.getLogger(ContainerItemFilter.class);

	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerItemFilter, GuiItemFilter> TYPE = new ContainerTypeAllocator<>(
			"filter_item", ContainerItemFilter::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiItemFilter::new);
		}
	}

	public ItemStackHandler filterInventory;

	public ContainerItemFilter(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerItemFilter(int windowId, Inventory playerInventory, ItemStack owner) {
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
			LOGGER.error(String.format(
					"Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.",
					getItemStack().getHoverName()));
			return;
		}

		addSlotsInGrid(filterInventory, 0, 89, 19, SDMath.getSmallestFactor(filterInventory.getSlots(), 9), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, true).renderFluidContainerAsFluid());

		addPlayerInventory(getPlayerInventory(), 8, 69 + (filterInventory.getSlots() > 9 ? 12 : 0));
		addPlayerHotbar(getPlayerInventory(), 8, 127 + (filterInventory.getSlots() > 9 ? 12 : 0));
	}

	@Override
	public boolean canDragTo(Slot slot) {
		return false;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		// Flag to indicate if we already have this item in the filter.
		boolean alreadyExists = false;

		// Check to see if this item already exists in the filter. If it does, do
		// nothing.
		for (int i = 0; i < filterInventory.getSlots(); i++) {
			if (ItemHandlerHelper.canItemStacksStack(filterInventory.getStackInSlot(i), stack)) {
				alreadyExists = true;
			}
		}

		// If this item doesn't exist, attempt to place it in a slot.
		if (!alreadyExists) {
			for (int i = 0; i < filterInventory.getSlots(); i++) {
				if (filterInventory.getStackInSlot(i).isEmpty()) {
					filterInventory.setStackInSlot(i, stack.copy());
					return true;
				}
			}
		}

		// If we didn't return true earlier, return false now.
		return false;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clicked(int slot, int dragType, ClickType clickTypeIn, Player player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem() == player.getMainHandItem()) {
			return;
		}
		super.clicked(slot, dragType, clickTypeIn, player);
	}
}
