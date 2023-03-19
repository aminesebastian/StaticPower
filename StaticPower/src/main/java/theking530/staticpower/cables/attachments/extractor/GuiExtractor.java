package theking530.staticpower.cables.attachments.extractor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachmentGui;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiCableAttachmentRedstoneTab;

public class GuiExtractor extends AbstractCableAttachmentGui<ContainerExtractor, ExtractorAttachment> {

	public GuiExtractor(ContainerExtractor container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 151);
		GuiCableAttachmentRedstoneTab redstoneTab;
		getTabManager().registerTab(redstoneTab = new GuiCableAttachmentRedstoneTab(container.getAttachment(), container.getAttachmentSide(), container.getCableComponent()));
		getTabManager().registerTab(new GuiInfoTab(100));
		getTabManager().setInitiallyOpenTab(redstoneTab);
	}
}
