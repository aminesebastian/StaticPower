package theking530.staticpower.items.cableattachments.extractor;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;

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

	public void onAddedToCable(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, cableComponent);
		attachment.getTag().putInt(EXTRACTION_TIMER_TAG, 0);
	}

	public void onRemovedFromCable(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		if (attachment.hasTag()) {
			attachment.getTag().remove(EXTRACTION_TIMER_TAG);
		}
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
