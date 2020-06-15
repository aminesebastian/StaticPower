package theking530.staticpower.items.cableattachments;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;

public class BasicExtractorAttachment extends AbstractCableAttachment {

	public BasicExtractorAttachment(String name) {
		super(name);
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return StaticPowerAdditionalModels.CABLE_ITEM_EXTRACTOR_ATTACHMENT;
	}
}
