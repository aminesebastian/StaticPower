package theking530.staticpower.cables.attachments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public abstract class AbstractCableAttachmentGui<T extends AbstractCableAttachmentContainer<K>, K extends AbstractCableAttachment> extends StaticPowerContainerGui<T> {
	public AbstractCableAttachmentGui(T container, Inventory invPlayer, Component name, int width, int height) {
		super(container, invPlayer, name, width, height);
	}

	public AbstractCableProviderComponent getCableComponent() {
		return getMenu().getCableComponent();
	}
}
