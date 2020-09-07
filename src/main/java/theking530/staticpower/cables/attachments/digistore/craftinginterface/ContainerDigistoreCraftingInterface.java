package theking530.staticpower.cables.attachments.digistore.craftinginterface;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentContainer;
import theking530.staticpower.container.slots.EncodedPatternSlot;

public class ContainerDigistoreCraftingInterface extends AbstractCableAttachmentContainer<DigistoreCraftingInterfaceAttachment> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreCraftingInterface, GuiDigistoreCraftingInterface> TYPE = new ContainerTypeAllocator<>("cable_attachment_digistore_crafting_interface",
			ContainerDigistoreCraftingInterface::new, GuiDigistoreCraftingInterface::new);

	private ItemStackHandler filterInventory;

	public ContainerDigistoreCraftingInterface(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreCraftingInterface(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16, (index, x, y) -> new EncodedPatternSlot(filterInventory, index, x, y));

		addPlayerInventory(getPlayerInventory(), 8, 69);
		addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
