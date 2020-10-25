package theking530.staticpower.cables.attachments.digistore.importer;

import java.util.LinkedList;
import java.util.List;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.init.ModUpgrades;
import theking530.staticpower.items.upgrades.AcceleratorUpgrade;
import theking530.staticpower.items.upgrades.StackUpgrade;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreImporterAttachment extends AbstractCableAttachment {
	public static final String IMPORT_TIMER_TAG = "import_timer";

	public DigistoreImporterAttachment(String name) {
		super(name);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, StaticPowerConfig.digistoreImporterSlots), (Direction) null)
				.addCapability(new ItemStackCapabilityInventory("upgrades", stack, 3));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		attachment.getTag().putInt(IMPORT_TIMER_TAG, 0);
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

		// Increment the import timer. If it returns true, attempt an item extract.
		if (increaseSupplierTimer(attachment)) {
			// See if we can perform a digistore extract and supply.
			importFromAttached(attachment, side, cable, te);
		}
	}

	public boolean increaseSupplierTimer(ItemStack attachment) {

		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		if (!attachment.getTag().contains(IMPORT_TIMER_TAG)) {
			attachment.getTag().putInt(IMPORT_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(IMPORT_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= getImportRate(attachment)) {
			attachment.getTag().putInt(IMPORT_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(IMPORT_TIMER_TAG, currentTimer);

			return false;
		}

	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new ImporterContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return StaticPowerAdditionalModels.CABLE_DIGISTORE_IMPORTER_ATTACHMENT;
	}

	public boolean doesItemPassImportFilter(ItemStack attachment, ItemStack itemToTest) {
		// Get the filter inventory (if there is a null value, do not handle it, throw
		// an exception).
		IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Encounetered an importer attachment without a valid filter inventory."));

		// Get the list of filter items.
		List<ItemStack> filterItemList = new LinkedList<ItemStack>();
		for (int i = 0; i < filterItems.getSlots(); i++) {
			if (!filterItems.getStackInSlot(i).isEmpty()) {
				filterItemList.add(filterItems.getStackInSlot(i).copy());
			}
		}

		// If there are no items in the filter list, then this attachment is NOT
		// filtering. Allow any items to be extracted.
		if (filterItemList.size() == 0) {
			return true;
		}

		// Check to see if the provided item matches.
		return ItemUtilities.filterItems(filterItemList, itemToTest, true, false, false, false);
	}

	protected boolean importFromAttached(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		AtomicBoolean output = new AtomicBoolean(false);
		targetTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(target -> {
			cable.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(module -> {
				// Return early if there is no manager.
				if (!module.isManagerPresent()) {
					return;
				}

				for (int i = 0; i < target.getSlots(); i++) {
					// Simulate an extract.
					int countToExtract = StaticPowerConfig.digistoreImporterStackSize;
					countToExtract = hasUpgradeOfClass(attachment, StackUpgrade.class) ? 64 : countToExtract;
					ItemStack extractedItem = target.extractItem(i, countToExtract, true);

					// If the extracted item is empty, continue.
					if (extractedItem.isEmpty()) {
						continue;
					}

					// Skip any items that are not supported by the extraction filter.
					if (!doesItemPassImportFilter(attachment, extractedItem)) {
						continue;
					}

					// Attempt to transfer the itemstack through the cable network.
					ItemStack remainingAmount = module.insertItem(extractedItem.copy(), false);
					if (remainingAmount.getCount() < extractedItem.getCount()) {
						target.extractItem(i, extractedItem.getCount() - remainingAmount.getCount(), false);
						cable.getTileEntity().markDirty();
						break;
					}
				}
			});
		});
		return output.get();
	}

	@SuppressWarnings("deprecation")
	protected int getImportRate(ItemStack attachment) {
		float acceleratorCardCount = getUpgradeCount(attachment, AcceleratorUpgrade.class);
		if (acceleratorCardCount > 0) {
			double accelerationAmount = StaticPowerConfig.acceleratorCardMaxImprovment * (acceleratorCardCount / ModUpgrades.AcceleratorUpgrade.getMaxStackSize());
			return (int) (StaticPowerConfig.digistoreImporterRate / accelerationAmount);
		} else {
			return StaticPowerConfig.digistoreImporterRate;
		}
	}

	@Override	
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.digistoreImporterSlots, tooltip);
	}

	protected class ImporterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public ImporterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerDigistoreImporter(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
