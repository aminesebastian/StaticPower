package theking530.staticpower.cables.attachments.digistore.exporter;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.attachments.digistore.AbstractDigistoreCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.items.upgrades.AcceleratorUpgrade;
import theking530.staticpower.items.upgrades.CraftingUpgrade;
import theking530.staticpower.items.upgrades.StackUpgrade;

public class DigistoreExporterAttachment extends AbstractDigistoreCableAttachment {
	public static final String CURRENT_CRAFTING_ID_TAG = "current_crafting_request";
	public static final String EXPORT_TIMER_TAG = "export_timer";

	public DigistoreExporterAttachment() {
		super();
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		int slots = !StaticPowerConfig.SERVER_SPEC.isLoaded() ? 0 : StaticPowerConfig.SERVER.digistoreExporterSlots.get();
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, slots))
				.addCapability(new ItemStackCapabilityInventory("upgrades", stack, 3));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		getAttachmentTag(attachment).putInt(EXPORT_TIMER_TAG, 0);
		getAttachmentTag(attachment).putLong(CURRENT_CRAFTING_ID_TAG, -1);
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getLevel().isClientSide() || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Get the tile entity on the pulling side, return if it is null.
		BlockEntity te = cable.getLevel().getBlockEntity(cable.getPos().relative(side));
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
	public double getPowerUsage(ItemStack attachment) {
		return 1;
	}

	public boolean increaseSupplierTimer(ItemStack attachment) {
		// Get the current timer and the extraction rate.
		int currentTimer = getAttachmentTag(attachment).getInt(EXPORT_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= getExportRate(attachment)) {
			getAttachmentTag(attachment).putInt(EXPORT_TIMER_TAG, 0);
			return true;
		} else {
			getAttachmentTag(attachment).putInt(EXPORT_TIMER_TAG, currentTimer);
			return false;
		}
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new ExporterContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		return StaticPowerAdditionalModels.CABLE_DIGISTORE_EXPORTER_ATTACHMENT;
	}

	protected void supplyFromNetwork(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, BlockEntity targetTe) {
		targetTe.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).ifPresent(target -> {
			cable.<DigistoreNetworkModule>getNetworkModule(ModCableModules.Digistore.get()).ifPresent(module -> {
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
				IItemHandler filterItems = attachment.getCapability(ForgeCapabilities.ITEM_HANDLER)
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
								cable.getBlockEntity().setChanged();
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
		getAttachmentTag(attachment).putLong(CURRENT_CRAFTING_ID_TAG, id);
	}

	protected long getCurrentCraftingId(ItemStack attachment) {
		return getAttachmentTag(attachment).getLong(CURRENT_CRAFTING_ID_TAG);
	}

	protected void markCurrentCraftingIdComplete(ItemStack attachment) {
		getAttachmentTag(attachment).putLong(CURRENT_CRAFTING_ID_TAG, -1);
	}

	protected boolean isWaitingOnCraftingRequest(ItemStack attachment) {
		return getAttachmentTag(attachment).getLong(CURRENT_CRAFTING_ID_TAG) >= 0;
	}

	@SuppressWarnings("deprecation")
	protected int getExportRate(ItemStack attachment) {
		float acceleratorCardCount = getUpgradeCount(attachment, AcceleratorUpgrade.class);
		if (acceleratorCardCount > 0) {
			double accelerationAmount = StaticPowerConfig.SERVER.acceleratorCardImprovment.get() * (acceleratorCardCount / ModItems.AcceleratorUpgrade.get().getMaxStackSize());
			return (int) (StaticPowerConfig.SERVER.digistoreExporterRate.get() / accelerationAmount);
		} else {
			return StaticPowerConfig.SERVER.digistoreExporterRate.get();
		}
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.translatable("gui.staticpower.exporter_tooltip"));
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.SERVER.digistoreExporterSlots.get(), tooltip);
	}

	protected class ExporterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public ExporterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerDigistoreExporter(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
