package theking530.staticpower.cables.attachments.digistore.regulator;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
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
import theking530.staticpower.container.slots.UpgradeItemSlot;

public class ContainerDigistoreRegulator extends AbstractCableAttachmentContainer<DigistoreRegulatorAttachment> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreRegulator, GuiDigistoreRegulator> TYPE = new ContainerTypeAllocator<>("cable_attachment_digistore_regulator",
			ContainerDigistoreRegulator::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreRegulator::new);
		}
	}

	private ItemStackHandler filterInventory;
	private ItemStackHandler upgradeInventory;

	public ContainerDigistoreRegulator(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreRegulator(int windowId, Inventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			filterInventory = (ItemStackHandler) handler;
		});

		// Create the upgrade inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent((handler) -> {
			upgradeInventory = (ItemStackHandler) handler;
		});

		addSlotsInGrid(filterInventory, 0, 88, 24, SDMath.getSmallestFactor(filterInventory.getSlots(), 6), 16,
				(index, x, y) -> new PhantomSlot(filterInventory, index, x, y, false).renderFluidContainerAsFluid());

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
