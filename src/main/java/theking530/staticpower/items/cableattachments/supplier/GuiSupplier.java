package theking530.staticpower.items.cableattachments.supplier;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.widgets.tabs.GuiInfoTab;
import theking530.common.gui.widgets.tabs.redstonecontrol.GuiCableAttachmentRedstoneTab;
import theking530.staticpower.items.cableattachments.AbstractCableAttachmentGui;

public class GuiSupplier extends AbstractCableAttachmentGui<ContainerSupplier, SupplierAttachment> {

	public GuiSupplier(ContainerSupplier container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
		GuiCableAttachmentRedstoneTab redstoneTab;
		getTabManager().registerTab(redstoneTab = new GuiCableAttachmentRedstoneTab(container.getAttachment(), container.getAttachmentSide(), container.getCableComponent()));
		getTabManager().registerTab(new GuiInfoTab(100, 60));
		getTabManager().setInitiallyOpenTab(redstoneTab);
	}
}
