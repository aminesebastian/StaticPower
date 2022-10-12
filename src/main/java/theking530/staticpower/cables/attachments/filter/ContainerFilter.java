package theking530.staticpower.cables.attachments.filter;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentContainer;
import theking530.staticpower.container.slots.PhantomSlot;

public class ContainerFilter extends AbstractCableAttachmentContainer<FilterAttachment> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFilter, GuiFilter> TYPE = new ContainerTypeAllocator<>("cable_attachment_filter", ContainerFilter::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFilter::new);
		}
	}

	private ItemStackHandler filterInventory;

	public ContainerFilter(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerFilter(int windowId, Inventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, true).renderFluidContainerAsFluid());
		addAllPlayerSlots();
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
