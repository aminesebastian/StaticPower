package theking530.staticpower.items.cableattachments.extractor;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.items.ItemStackInventoryCapabilityProvider;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.utilities.ItemUtilities;

public class ExtractorAttachment extends AbstractCableAttachment {
	public static final String EXTRACTION_TIMER_TAG = "extraction_timer";
	private final int ExtractionRate;
	private final int ExtractionStackSize;
	private final ResourceLocation Model;

	public ExtractorAttachment(String name, int extractionRate, int extractionStackSize, ResourceLocation model) {
		super(name);
		ExtractionRate = extractionRate;
		ExtractionStackSize = extractionStackSize;
		Model = model;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackInventoryCapabilityProvider(stack, 7, nbt);
	}

	public void onAddedToCable(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, cableComponent);
		attachment.getTag().putInt(EXTRACTION_TIMER_TAG, 0);
	}

	public boolean doesItemPassExtractionFilter(ItemStack attachment, ItemStack itemToTest, AbstractCableProviderComponent cableComponent) {
		// Get the filter inventory (if there is a null value, do not handle it, throw
		// an exception).
		IItemHandler filterItems = attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Encounetered an extractor attachment without a valid filter inventory."));

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
		return ItemUtilities.filterItems(filterItemList, itemToTest, true, false, false, false, false);
	}

	public void openAttachmentGui(ItemStack attachment) {

	}

	public int getExtractionRate() {
		return ExtractionRate;
	}

	public int getExtractionStackSize() {
		return ExtractionStackSize;
	}

	@Override
	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment) {
		return new ExtractorContainerProvider(attachment);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return Model;
	}

	protected class ExtractorContainerProvider extends AbstractCableAttachmentContainerProvider {
		private Direction attachmentSide;
		private AbstractCableProviderComponent cableComponent;

		public ExtractorContainerProvider(ItemStack stack) {
			super(stack);
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerExtractor(windowId, playerInv, targetItemStack, attachmentSide, cableComponent);
		}
	}
}
