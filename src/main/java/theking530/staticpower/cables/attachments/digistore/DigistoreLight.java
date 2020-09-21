package theking530.staticpower.cables.attachments.digistore;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreLight extends DigistoreScreen {

	public DigistoreLight(String name) {
		super(name);
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		DigistoreCableProviderComponent digistoreCable = (DigistoreCableProviderComponent) cableComponent;
		return digistoreCable.isManagerPresent() ? StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT_ON : StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT;
	}
}
