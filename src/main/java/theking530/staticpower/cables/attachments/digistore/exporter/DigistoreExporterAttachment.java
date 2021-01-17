package theking530.staticpower.cables.attachments.digistore.exporter;

import java.util.List;

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
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.attachments.digistore.AbstractDigistoreCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.init.ModUpgrades;
import theking530.staticpower.items.upgrades.AcceleratorUpgrade;
import theking530.staticpower.items.upgrades.CraftingUpgrade;
import theking530.staticpower.items.upgrades.StackUpgrade;
import theking530.staticpower.utilities.InventoryUtilities;

public class DigistoreExporterAttachment extends AbstractDigistoreCableAttachment {
	public static final String CURRENT_CRAFTING_ID_TAG = "current_crafting_request";
	public static final String EXPORT_TIMER_TAG = "export_timer";

	public DigistoreExporterAttachment(String name) {
		super(name);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt)
				.addCapability(new ItemStackCapabilityInventory("default", stack, StaticPowerConfig.SERVER.digistoreExporterSlots.get()), (Direction) null)
				.addCapability(new ItemStackCapabilityInventory("upgrades", stack, 3));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		attachment.getTag().putInt(EXPORT_TIMER_TAG, 0);
		attachment.getTag().putLong(CURRENT_CRAFTING_ID_TAG, -1);
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

	@Override
	public long getPowerUsage(ItemStack attachment, DigistoreCableProviderComponent cableComponent) {
		return 1000;
	}

	public boolean increaseSupplierTimer(ItemStack attachment) {
		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(EXPORT_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= getExportRate(attachment)) {
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
		return StaticPowerAdditionalModels.CABLE_DIGISTORE_EXPORTER_ATTACHMENT;
	}

	protected void supplyFromNetwork(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, TileEntity targetTe) {
		targetTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(target -> {
			cable.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(module -> {
				// Return early if there is no manager.
				if (!module.isManagerPresent()) {
					return;
				}

				// If we're waiting on a craft, check if the crafting is completed.
				if (isWaitingOnCraftingRequest(attachment)) {
					if (module.getCraftingManager().isCraftingIdStillProcessint(getCurrentCraftingId(attachment))) {
						return;
					}
				}

				// Get the filter inventory (if there is a null value, do not handle it, throw
				// an exception).
				IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
						.orElseThrow(() -> new RuntimeException("Encounetered a supplier attachment without a valid filter inventory."));

				// We do this to ensure we randomly extract.
				int startingSlot = SDMath.getRandomIntInRange(0, filterItems.getSlots() - 1);
				int currentSlot = startingSlot;

				// Get the list of filter items.
				for (int i = 0; i < filterItems.getSlots(); i++) {
					if (!filterItems.getStackInSlot(currentSlot).isEmpty()) {
						// Get the amount to extract.
						ItemStack filterItem = filterItems.getStackInSlot(currentSlot);
						int countToExtract = Math.min(StaticPowerConfig.SERVER.digistoreExporterStackSize.get(), filterItem.getMaxStackSize());
						countToExtract = hasUpgradeOfClass(attachment, StackUpgrade.class) ? filterItem.getMaxStackSize() : countToExtract;

						// Attempt an extract.
						ItemStack extractedItem = module.extractItem(filterItem, countToExtract, true);

						// If the extracted item not empty, see if we can craft it. If we can, mark it
						// for crafting. If not, continue checking.
						if (!extractedItem.isEmpty()) {
							// Attempt to transfer the itemstack through the cable network.
							ItemStack remainingAmount = InventoryUtilities.insertItemIntoInventory(target, extractedItem.copy(), false);

							// If we actually extracted something, now we perform the real extract.
							if (remainingAmount.getCount() < extractedItem.getCount()) {
								module.extractItem(extractedItem, extractedItem.getCount() - remainingAmount.getCount(), false);
								cable.getTileEntity().markDirty();
								break;
							}
						} else if (hasUpgradeOfClass(attachment, CraftingUpgrade.class)) {
							long craftingId = module.getCraftingManager().addAutomationCraftingRequest(filterItem, countToExtract).getId();
							if (craftingId >= 0) {
								setCurrentCraftingId(attachment, craftingId);
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
	}

	protected void setCurrentCraftingId(ItemStack attachment, long id) {
		attachment.getTag().putLong(CURRENT_CRAFTING_ID_TAG, id);
	}

	protected long getCurrentCraftingId(ItemStack attachment) {
		return attachment.getTag().getLong(CURRENT_CRAFTING_ID_TAG);
	}

	protected void markCurrentCraftingIdComplete(ItemStack attachment) {
		attachment.getTag().putLong(CURRENT_CRAFTING_ID_TAG, -1);
	}

	protected boolean isWaitingOnCraftingRequest(ItemStack attachment) {
		return attachment.getTag().getLong(CURRENT_CRAFTING_ID_TAG) >= 0;
	}

	@SuppressWarnings("deprecation")
	protected int getExportRate(ItemStack attachment) {
		float acceleratorCardCount = getUpgradeCount(attachment, AcceleratorUpgrade.class);
		if (acceleratorCardCount > 0) {
			double accelerationAmount = StaticPowerConfig.SERVER.acceleratorCardImprovment.get() * (acceleratorCardCount / ModUpgrades.AcceleratorUpgrade.getMaxStackSize());
			return (int) (StaticPowerConfig.SERVER.digistoreExporterRate.get() / accelerationAmount);
		} else {
			return StaticPowerConfig.SERVER.digistoreExporterRate.get();
		}
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.SERVER.digistoreExporterSlots.get(), tooltip);
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
