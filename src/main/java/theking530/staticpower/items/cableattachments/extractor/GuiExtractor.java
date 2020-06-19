package theking530.staticpower.items.cableattachments.extractor;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.tabs.GuiRedstoneTab;
import theking530.staticpower.items.cableattachments.AbstractCableAttachmentGui;

public class GuiExtractor extends AbstractCableAttachmentGui<ContainerExtractor, ExtractorAttachment> {

	public GuiExtractor(ContainerExtractor container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name);
		getTabManager().registerTab(new GuiRedstoneTab(null));
		getTabManager().registerTab(new GuiInfoTab(100, 60));
	}

}
