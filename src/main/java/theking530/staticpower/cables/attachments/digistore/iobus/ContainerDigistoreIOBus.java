package theking530.staticpower.cables.attachments.digistore.iobus;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentContainer;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerDigistoreIOBus extends AbstractCableAttachmentContainer<DigistoreIOBusAttachment> {
	private ItemStackHandler filterInventory;

	public ContainerDigistoreIOBus(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreIOBus(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(ModContainerTypes.IO_CONTAINER, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// If the item filter is null, then return early and log the error.
		if (filterInventory == null) {
			StaticPower.LOGGER.error(String.format("Received capability for Importer: %1$s that did not inherit from InventoryItemFilter.", getAttachment().getDisplayName()));
			return;
		}

		addSlotsInGrid(filterInventory, 0, filterInventory.getSlots() / 2, 98, 20, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, true).renderFluidContainerAsFluid());

		addSlotsInGrid(filterInventory, (filterInventory.getSlots() / 2), filterInventory.getSlots() / 2, 98, 44, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, true).renderFluidContainerAsFluid());

		addPlayerInventory(getPlayerInventory(), 8, 69);
		addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
