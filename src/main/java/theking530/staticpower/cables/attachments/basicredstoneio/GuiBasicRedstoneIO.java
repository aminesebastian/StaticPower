package theking530.staticpower.cables.attachments.basicredstoneio;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiCableAttachmentRedstoneTab;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;

public class GuiBasicRedstoneIO extends AbstractCableAttachmentGui<ContainerBasicRedstoneIO, BasicRedstoneIO> {

	public GuiBasicRedstoneIO(ContainerBasicRedstoneIO container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
		GuiCableAttachmentRedstoneTab redstoneTab;
		getTabManager().registerTab(redstoneTab = new GuiCableAttachmentRedstoneTab(container.getAttachment(), container.getAttachmentSide(), container.getCableComponent()));
		getTabManager().registerTab(new GuiInfoTab(100));
		getTabManager().setInitiallyOpenTab(redstoneTab);
	}
}
