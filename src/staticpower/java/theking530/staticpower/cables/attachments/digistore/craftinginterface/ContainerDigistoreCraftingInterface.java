package theking530.staticpower.cables.attachments.digistore.craftinginterface;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachmentContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.container.slots.EncodedPatternSlot;

public class ContainerDigistoreCraftingInterface extends AbstractCableAttachmentContainer<DigistoreCraftingInterfaceAttachment> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreCraftingInterface, GuiDigistoreCraftingInterface> TYPE = new ContainerTypeAllocator<>("cable_attachment_digistore_crafting_interface",
			ContainerDigistoreCraftingInterface::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreCraftingInterface::new);
		}
	}

	private ItemStackHandler filterInventory;

	public ContainerDigistoreCraftingInterface(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreCraftingInterface(int windowId, Inventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16, (index, x, y) -> new EncodedPatternSlot(filterInventory, index, x, y));

		addAllPlayerSlots();
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
