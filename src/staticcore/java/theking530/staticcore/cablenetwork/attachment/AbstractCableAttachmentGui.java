package theking530.staticcore.cablenetwork.attachment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.gui.screens.StaticPowerContainerScreen;

public abstract class AbstractCableAttachmentGui<T extends AbstractCableAttachmentContainer<K>, K extends AbstractCableAttachment> extends StaticPowerContainerScreen<T> {
	public AbstractCableAttachmentGui(T container, Inventory invPlayer, Component name, int width, int height) {
		super(container, invPlayer, name, width, height);
	}

	public AbstractCableProviderComponent getCableComponent() {
		return getMenu().getCableComponent();
	}
}
