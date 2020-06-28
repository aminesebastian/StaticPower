package theking530.staticpower.items.cableattachments;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerItemStackGui;

public abstract class AbstractCableAttachmentGui<T extends AbstractCableAttachmentContainer<K>, K extends AbstractCableAttachment> extends StaticPowerItemStackGui<T, K> {

	public GuiInfoTab infoTab;

	public AbstractCableAttachmentGui(T container, PlayerInventory invPlayer, ITextComponent name, int width, int height) {
		super(container, invPlayer, name, width, height);
	}
}
