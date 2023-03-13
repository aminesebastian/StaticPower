package theking530.staticpower.cables.attachments.digistore.importer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AttachmentTooltipUtilities;
import theking530.staticpower.cables.attachments.digistore.AbstractDigistoreCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.items.upgrades.AcceleratorUpgrade;
import theking530.staticpower.items.upgrades.StackUpgrade;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreImporterAttachment extends AbstractDigistoreCableAttachment {
	public static final String IMPORT_TIMER_TAG = "import_timer";

	public DigistoreImporterAttachment() {
		super();
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		int slots = !StaticPowerConfig.SERVER_SPEC.isLoaded() ? 0 : StaticPowerConfig.SERVER.digistoreImporterSlots.get();
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, slots), (Direction) null)
				.addCapability(new ItemStackCapabilityInventory("upgrades", stack, 3));
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		getAttachmentTag(attachment).putInt(IMPORT_TIMER_TAG, 0);
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (cable.getLevel().isClientSide || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Get the tile entity on the pulling side, return if it is null.
		BlockEntity te = cable.getLevel().getBlockEntity(cable.getPos().relative(side));
		if (te == null || te.isRemoved()) {
			return;
		}

		// Increment the import timer. If it returns true, attempt an item extract.
		if (increaseSupplierTimer(attachment)) {
			// See if we can perform a digistore extract and supply.
			importFromAttached(attachment, side, cable, te);
		}
	}

	@Override
	public double getPowerUsage(ItemStack attachment) {
		return 1;
	}

	public boolean increaseSupplierTimer(ItemStack attachment) {

		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundTag());
		}
		if (!getAttachmentTag(attachment).contains(IMPORT_TIMER_TAG)) {
			getAttachmentTag(attachment).putInt(IMPORT_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = getAttachmentTag(attachment).getInt(IMPORT_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= getImportRate(attachment)) {
			getAttachmentTag(attachment).putInt(IMPORT_TIMER_TAG, 0);
			return true;
		} else {
			getAttachmentTag(attachment).putInt(IMPORT_TIMER_TAG, currentTimer);

			return false;
		}

	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return new ImporterContainerProvider(attachment, cable, attachmentSide);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		return StaticPowerAdditionalModels.CABLE_DIGISTORE_IMPORTER_ATTACHMENT;
	}

	public boolean doesItemPassImportFilter(ItemStack attachment, ItemStack itemToTest) {
		// Get the filter inventory (if there is a null value, do not handle it, throw
		// an exception).
		IItemHandler filterItems = attachment.getCapability(ForgeCapabilities.ITEM_HANDLER)
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

	protected boolean importFromAttached(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, BlockEntity targetTe) {
		AtomicBoolean output = new AtomicBoolean(false);
		targetTe.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).ifPresent(target -> {
			cable.<DigistoreNetworkModule>getNetworkModule(ModCableModules.Digistore.get()).ifPresent(module -> {
				// Return early if there is no manager.
				if (!module.isManagerPresent()) {
					return;
				}

				for (int i = 0; i < target.getSlots(); i++) {
					// Simulate an extract.
					int countToExtract = StaticPowerConfig.SERVER.digistoreImporterStackSize.get();
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
						cable.getBlockEntity().setChanged();
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
			double accelerationAmount = StaticPowerConfig.SERVER.acceleratorCardImprovment.get() * (acceleratorCardCount / ModItems.AcceleratorUpgrade.get().getMaxStackSize());
			return (int) (StaticPowerConfig.SERVER.digistoreImporterRate.get() / accelerationAmount);
		} else {
			return StaticPowerConfig.SERVER.digistoreImporterRate.get();
		}
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.translatable("gui.staticpower.importer_tooltip"));
		AttachmentTooltipUtilities.addSlotsCountTooltip("gui.staticpower.slots", StaticPowerConfig.SERVER.digistoreImporterSlots.get(), tooltip);
	}

	protected class ImporterContainerProvider extends AbstractCableAttachmentContainerProvider {
		public ImporterContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			super(stack, cable, attachmentSide);
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
			return new ContainerDigistoreImporter(windowId, playerInv, targetItemStack, attachmentSide, cable);
		}
	}
}
