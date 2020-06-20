package theking530.staticpower.items.cableattachments;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerItemStackGui;

public class AbstractCableAttachmentGui<T extends AbstractCableAttachmentContainer<K>, K extends AbstractCableAttachment> extends StaticPowerItemStackGui<T, K> {
	
	public GuiInfoTab infoTab;

	public AbstractCableAttachmentGui(T container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
	}

	@Override
	public void initializeGui() {
		guiLeft = (this.width - this.xSize) / 2;
		guiTop = (this.height - this.ySize) / 2;

	}

	@Override
	protected void drawBackgroundExtras(float f, int i, int j) {
		drawGenericBackground();
		drawPlayerInventorySlots();
	}
}
