package theking530.staticpower.cables.attachments.digistore;

import net.minecraft.item.ItemStack;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;

public abstract class AbstractDigistoreCableAttachment extends AbstractCableAttachment {

	public AbstractDigistoreCableAttachment(String name) {
		super(name);
	}

	public abstract long getPowerUsage(ItemStack attachment, DigistoreCableProviderComponent cableComponent);

}
