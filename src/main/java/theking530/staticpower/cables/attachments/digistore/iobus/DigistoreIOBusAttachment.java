package theking530.staticpower.cables.attachments.digistore.iobus;

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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.init.ModUpgrades;
import theking530.staticpower.items.CableAttachmentInventoryCapabilityProvider;
import theking530.staticpower.items.upgrades.AcceleratorUpgrade;
import theking530.staticpower.items.upgrades.StackUpgrade;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreIOBusAttachment extends AbstractCableAttachment {
	public static final String REGULATOR_TIMER_TAG = "regulator_timer";

	public DigistoreIOBusAttachment(String name) {
		super(name);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new CableAttachmentInventoryCapabilityProvider(stack, StaticPowerConfig.digistoreIOBusSlots * 2, 3, nbt);
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		attachment.getTag().putInt(REGULATOR_TIMER_TAG, 0);
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
			// See if we can perform a digistore import and supply.
			importFromAttached(attachment, side, cable, te);
			exportFromNetwork(attachment, side, cable, te);
		}
	}

	public boolean increaseSupplierTimer(ItemStack attachment) {
		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		if (!attachment.getTag().contains(REGULATOR_TIMER_TAG)) {
			attachment.getTag().putInt(REGULATOR_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(REGULATOR_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= getIOBusRate(attachment)) {
			attachment.getTag().putInt(REGULATOR_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(REGULATOR_TIMER_TAG, currentTimer);
			return false;
		}

	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new RegulatorContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return StaticPowerAdditionalModels.CABLE_DIGISTORE_IO_BUS_ATTACHMENT;
	}

	public boolean doesItemPassImportFilter(ItemStack attachment, ItemStack itemToTest) {
		// Get the filter inventory (if there is a null value, do not handle it, throw
		// an exception).
		IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Encounetered an importer attachment without a valid filter inventory."));

		// Get the list of filter items.
		List<ItemStack> filterItemList = new LinkedList<ItemStack>();
		for (int i = filterSlotsStart(); i < filterSlotsLastIndex(); i++) {
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
					int countToExtract = StaticPowerConfig.digistoreIOBusStackSize;
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

	protected boolean exportFromNetwork(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		AtomicBoolean output = new AtomicBoolean(false);
		targetTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(target -> {
			cable.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(module -> {
				// Return early if there is no manager.
				if (!module.isManagerPresent()) {
					return;
				}
				// Get the filter inventory (if there is a null value, do not handle it, throw
				// an exception).
				IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
						.orElseThrow(() -> new RuntimeException("Encounetered a supplier attachment without a valid filter inventory."));

				// We do this to ensure we randomly extract.
				int startingSlot = SDMath.getRandomIntInRange(exportSlotsStart(), exportSlotsLastIndex());
				int currentSlot = startingSlot;

				// Get the list of filter items.
				for (int i = exportSlotsStart(); i < exportSlotsLastIndex(); i++) {
					if (!filterItems.getStackInSlot(currentSlot).isEmpty()) {
						// Get the amount to extract.
						ItemStack filterItem = filterItems.getStackInSlot(currentSlot);
						int countToExtract = Math.min(StaticPowerConfig.digistoreIOBusStackSize, filterItem.getMaxStackSize());
						countToExtract = hasUpgradeOfClass(attachment, StackUpgrade.class) ? filterItem.getMaxStackSize() : countToExtract;

						// Simulate an extract.
						ItemStack extractedItem = module.extractItem(filterItem, countToExtract, true);

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
					if (currentSlot > exportSlotsLastIndex()) {
						currentSlot = exportSlotsStart();
					}
				}
			});
		});
		return output.get();
	}

	protected int filterSlotsStart() {
		return 0;
	}

	protected int filterSlotsLastIndex() {
		return StaticPowerConfig.digistoreIOBusSlots - 1;
	}

	protected int exportSlotsStart() {
		return StaticPowerConfig.digistoreIOBusSlots;
	}

	protected int exportSlotsLastIndex() {
		return (StaticPowerConfig.digistoreIOBusSlots * 2) - 1;
	}

	@SuppressWarnings("deprecation")
	protected int getIOBusRate(ItemStack attachment) {
		float acceleratorCardCount = getUpgradeCount(attachment, AcceleratorUpgrade.class);
		if (acceleratorCardCount > 0) {
			float accelerationAmount = StaticPowerConfig.acceleratorCardMaxImprovment * (acceleratorCardCount / ModUpgrades.AcceleratorUpgrade.getMaxStackSize());
			return (int) (StaticPowerConfig.digistoreIOBusRate / accelerationAmount);
		} else {
			return StaticPowerConfig.digistoreIOBusRate;
		}
	}

	protected class RegulatorContainerProvider extends AbstractCableAttachmentContainerProvider {
		public RegulatorContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerDigistoreIOBus(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
