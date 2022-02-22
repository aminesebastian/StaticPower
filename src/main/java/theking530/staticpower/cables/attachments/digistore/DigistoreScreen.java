package theking530.staticpower.cables.attachments.digistore;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreScreen extends AbstractDigistoreCableAttachment {
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

	@Override
	public long getPowerUsage(ItemStack attachment) {
		return 0;
	}
}
