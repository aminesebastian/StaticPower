package theking530.staticpower.cables.attachments.digistore.importer;

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
import theking530.staticpower.cables.attachments.AttachmentUpgradeInventory;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerDigistoreImporter extends AbstractCableAttachmentContainer<DigistoreImporterAttachment> {
	private ItemStackHandler filterInventory;
	private AttachmentUpgradeInventory upgradeInventory;

	public ContainerDigistoreImporter(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreImporter(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(ModContainerTypes.IMPORTER_CONTAINER, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// Create the upgrade inventory.
		upgradeInventory = new AttachmentUpgradeInventory(getAttachment());

		// If the item filter is null, then return early and log the error.
		if (filterInventory == null) {
			StaticPower.LOGGER.error(String.format("Received capability for Importer: %1$s that did not inherit from InventoryItemFilter.", getAttachment().getDisplayName()));
			return;
		}

		addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, true).renderFluidContainerAsFluid());

		addSlot(new UpgradeItemSlot(upgradeInventory, 0, -19, 14));
		addSlot(new UpgradeItemSlot(upgradeInventory, 1, -19, 32));
		addSlot(new UpgradeItemSlot(upgradeInventory, 2, -19, 50));

		addPlayerInventory(getPlayerInventory(), 8, 69);
		addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
