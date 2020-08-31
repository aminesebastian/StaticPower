package theking530.staticpower.cables.attachments;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public abstract class AbstractCableAttachmentGui<T extends AbstractCableAttachmentContainer<K>, K extends AbstractCableAttachment> extends StaticPowerContainerGui<T> {
	public AbstractCableAttachmentGui(T container, PlayerInventory invPlayer, ITextComponent name, int width, int height) {
		super(container, invPlayer, name, width, height);
	}

	public AbstractCableProviderComponent getCableComponent() {
		return getContainer().getCableComponent();
	}
}
