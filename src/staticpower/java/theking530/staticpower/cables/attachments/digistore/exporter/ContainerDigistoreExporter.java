package theking530.staticpower.cables.attachments.digistore.exporter;

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
import theking530.staticcore.container.slots.PhantomSlot;
import theking530.staticcore.container.slots.UpgradeItemSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.math.SDMath;

public class ContainerDigistoreExporter extends AbstractCableAttachmentContainer<DigistoreExporterAttachment> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreExporter, GuiDigistoreExporter> TYPE = new ContainerTypeAllocator<>("cable_attachment_digistore_exporter",
			ContainerDigistoreExporter::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreExporter::new);
		}
	}

	private ItemStackHandler filterInventory;
	private ItemStackHandler upgradeInventory;

	public ContainerDigistoreExporter(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreExporter(int windowId, Inventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// Create the upgrade inventory.
		getAttachment().getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent((handler) -> {
			upgradeInventory = (ItemStackHandler) handler;
		});

		addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, true).renderFluidContainerAsFluid());

		addSlot(new UpgradeItemSlot(upgradeInventory, 0, -19, 14));
		addSlot(new UpgradeItemSlot(upgradeInventory, 1, -19, 32));
		addSlot(new UpgradeItemSlot(upgradeInventory, 2, -19, 50));

		addAllPlayerSlots();
	}

	public ItemStackHandler getExtractorInventory() {
		return filterInventory;
	}
}
