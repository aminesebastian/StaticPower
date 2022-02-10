package theking530.staticpower.cables.attachments.retirever;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentContainer;
import theking530.staticpower.container.slots.PhantomSlot;

public class ContainerRetriever extends AbstractCableAttachmentContainer<RetrieverAttachment> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRetriever, GuiRetriever> TYPE = new ContainerTypeAllocator<>("cable_attachment_retriever", ContainerRetriever::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRetriever::new);
		}
	}

	private ItemStackHandler filterInventory;

	public ContainerRetriever(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerRetriever(int windowId, Inventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		this.addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, true).renderFluidContainerAsFluid());
		this.addPlayerInventory(getPlayerInventory(), 8, 69);
		this.addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
