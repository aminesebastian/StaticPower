package theking530.staticpower.cables.attachments.digistore;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreScreen extends AbstractCableAttachment {
	private static final Vector3D BOUNDS = new Vector3D(6.0f, 6.0f, 2.0f);

	public DigistoreScreen(String name) {
		super(name);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return false;
	}

	@Override
	public Vector3D getBounds() {
		return BOUNDS;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		DigistoreCableProviderComponent digistoreCable = (DigistoreCableProviderComponent) cableComponent;
		return digistoreCable.isManagerPresent() ? StaticPowerAdditionalModels.CABLE_DIGISTORE_SCREEN_ATTACHMENT_ON : StaticPowerAdditionalModels.CABLE_DIGISTORE_SCREEN_ATTACHMENT;
	}
}
