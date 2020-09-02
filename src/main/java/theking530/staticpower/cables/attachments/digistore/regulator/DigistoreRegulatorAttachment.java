package theking530.staticpower.cables.attachments.digistore.regulator;

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
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.items.ItemStackInventoryCapabilityProvider;
import theking530.staticpower.utilities.InventoryUtilities;

public class DigistoreRegulatorAttachment extends AbstractCableAttachment {
	public static final String REGULATOR_TIMER_TAG = "regulator_timer";

	public DigistoreRegulatorAttachment(String name) {
		super(name, 3);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackInventoryCapabilityProvider(stack, StaticPowerConfig.digistoreRegulatorSlots, nbt);
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
		if (increaseReuglationTimer(attachment)) {
			// See if we can perform a digistore extract and supply.
			regulate(attachment, side, cable, te);
		}
	}

	public boolean increaseReuglationTimer(ItemStack attachment) {

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
		if (currentTimer >= StaticPowerConfig.digistoreRegulatorRate) {
			attachment.getTag().putInt(REGULATOR_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(REGULATOR_TIMER_TAG, currentTimer);

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
		return StaticPowerAdditionalModels.CABLE_DIGISTORE_REGULATOR_ATTACHMENT;
	}

	protected boolean regulate(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
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

				// Get the list of filter items.
				for (int i = 0; i < filterItems.getSlots(); i++) {
					// Get the filter item.
					ItemStack filterItem = filterItems.getStackInSlot(i);

					// Skip empty filter slots.
					if (filterItem.isEmpty()) {
						continue;
					}

					// Get current count of the item.
					int targetItemCount = InventoryUtilities.getCountOfItem(filterItem, target);
					int countToTransfer = Math.min(StaticPowerConfig.digistoreRegulatorStackSize, Math.abs(targetItemCount - filterItem.getCount()));

					// If we are at the correct count, skip it.
					if (countToTransfer == 0) {
						continue;
					}

					// If the target inventory has too many items, try to extract some. If it
					// doesn't have enough, try to supply some.
					if (targetItemCount > filterItem.getCount()) {
						// Simulate an extract.
						ItemStack simulateExtract = InventoryUtilities.extractItemFromInventory(target, filterItem, countToTransfer, true);

						// Try to insert the supplied amount.
						ItemStack insertedToNetwork = module.insertItem(simulateExtract, false);

						// Extract the item from the target tile entity.
						InventoryUtilities.extractItemFromInventory(target, filterItem, simulateExtract.getCount() - insertedToNetwork.getCount(), false);

					} else if (targetItemCount < filterItems.getStackInSlot(i).getCount()) {
						// Simulate an extract.
						ItemStack simulatedSupply = module.extractItem(filterItem, countToTransfer, true);

						// Try to insert the supplied amount.
						ItemStack supplied = InventoryUtilities.insertItemIntoInventory(target, simulatedSupply, false);

						// Remove the supplied amount from the network.
						module.extractItem(filterItem, simulatedSupply.getCount() - supplied.getCount(), false);
					}
				}
			});
		});
		return output.get();
	}

	protected class ImporterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public ImporterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerDigistoreRegulator(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
