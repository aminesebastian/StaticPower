package theking530.staticpower.items.cableattachments.filter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.common.utilities.SDMath;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.client.container.slots.PhantomSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.items.cableattachments.AbstractCableAttachmentContainer;

public class ContainerFilter extends AbstractCableAttachmentContainer<FilterAttachment> {
	private ItemStackHandler filterInventory;

	public ContainerFilter(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerFilter(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(ModContainerTypes.FILTER_CONTAINER, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// If the item filter is null, then return early and log the error.
		if (filterInventory == null) {
			StaticPower.LOGGER.error(String.format("Received capability for ItemFilter: %1$s that did not inherit from InventoryItemFilter.", getAttachment().getDisplayName()));
			return;
		}

		this.addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16, (index, x, y) -> new PhantomSlot(filterInventory, index, x, y).renderFluidContainerAsFluid());
		this.addPlayerInventory(getPlayerInventory(), 8, 69);
		this.addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
