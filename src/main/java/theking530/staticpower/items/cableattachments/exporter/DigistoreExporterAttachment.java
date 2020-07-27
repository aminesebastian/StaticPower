package theking530.staticpower.items.cableattachments.exporter;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.common.utilities.SDMath;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.ItemStackInventoryCapabilityProvider;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.utilities.InventoryUtilities;

public class DigistoreExporterAttachment extends AbstractCableAttachment {
	public static final String EXPORT_TIMER_TAG = "extraction_timer";
	private final ResourceLocation tierType;
	private final ResourceLocation model;

	public DigistoreExporterAttachment(String name, ResourceLocation model) {
		super(name);
		this.tierType = StaticPowerTiers.STATIC;
		this.model = model;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackInventoryCapabilityProvider(stack, StaticPowerDataRegistry.getTier(tierType).getCableExtractionFilterSize(), nbt);
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		attachment.getTag().putInt(EXPORT_TIMER_TAG, 0);
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getWorld().isRemote || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Get the tile entity on the pulling side, return if it is null.
		TileEntity te = cable.getWorld().getTileEntity(cable.getPos().offset(side));
		if (te == null || te.isRemoved()) {
			return;
		}

		// Increment the extraction timer. If it returns true, attempt an item extract.
		if (increaseSupplierTimer(attachment)) {
			// See if we can perform a digistore extract and supply.
			supplyFromNetwork(attachment, side, cable, te);
		}
	}

	public boolean increaseSupplierTimer(ItemStack attachment) {

		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		if (!attachment.getTag().contains(EXPORT_TIMER_TAG)) {
			attachment.getTag().putInt(EXPORT_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(EXPORT_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= StaticPowerDataRegistry.getTier(tierType).getCableExtractorRate()) {
			attachment.getTag().putInt(EXPORT_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(EXPORT_TIMER_TAG, currentTimer);

			return false;
		}

	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new ExporterContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return model;
	}

	protected boolean supplyFromNetwork(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		AtomicBoolean output = new AtomicBoolean(false);
		targetTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(target -> {
			cable.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(module -> {
				// Get the filter inventory (if there is a null value, do not handle it, throw
				// an exception).
				IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(() -> new RuntimeException("Encounetered a supplier attachment without a valid filter inventory."));

				// We do this to ensure we randomly extract.
				int startingSlot = SDMath.getRandomIntInRange(0, filterItems.getSlots() - 1);
				int currentSlot = startingSlot;

				// Get the list of filter items.
				for (int i = 0; i < filterItems.getSlots(); i++) {
					if (!filterItems.getStackInSlot(currentSlot).isEmpty()) {
						// Simulate an extract.
						ItemStack extractedItem = module.extractItem(filterItems.getStackInSlot(currentSlot), StaticPowerDataRegistry.getTier(tierType).getCableExtractionStackSize(), true);

						// If the extracted item not empty, continue.
						if (!extractedItem.isEmpty()) {
							// Attempt to transfer the itemstack through the cable network.
							ItemStack remainingAmount = InventoryUtilities.insertItemIntoInventory(target, extractedItem.copy(), false);

							// If we actually extracted something, now we perform the real extract.
							if (remainingAmount.getCount() < extractedItem.getCount()) {
								module.extractItem(extractedItem, extractedItem.getCount() - remainingAmount.getCount(), false);
								cable.getTileEntity().markDirty();
								output.set(true);
								break;
							}
						}
					}
					// Increment the current slot and make sure we wrap around.
					currentSlot++;
					if (currentSlot > filterItems.getSlots() - 1) {
						currentSlot = 0;
					}
				}
			});
		});
		return output.get();
	}

	protected class ExporterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public ExporterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerDigistoreExporter(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
