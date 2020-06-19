package theking530.staticpower.items.cableattachments;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		attachment.getTag().putInt(EXTRACTION_TIMER_TAG, 0);
	}

	public void onRemovedFromCable(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		if (attachment.hasTag()) {
			attachment.getTag().remove(EXTRACTION_TIMER_TAG);
		}
	}

	public int getExtractionRate() {
		return ExtractionRate;
	}

	public int getExtractionStackSize() {
		return ExtractionStackSize;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return Model;
	}
}
