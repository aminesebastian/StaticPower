package theking530.staticpower.cables.attachments.digistore.regulator;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.widgets.GuiIslandWidget;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiCableAttachmentRedstoneTab;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;

public class GuiDigistoreRegulator extends AbstractCableAttachmentGui<ContainerDigistoreRegulator, DigistoreRegulatorAttachment> {

	public GuiDigistoreRegulator(ContainerDigistoreRegulator container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 151);
		GuiCableAttachmentRedstoneTab redstoneTab;
		getTabManager().registerTab(redstoneTab = new GuiCableAttachmentRedstoneTab(container.getAttachment(), container.getAttachmentSide(), container.getCableComponent()));
		getTabManager().registerTab(new GuiInfoTab(100));
		getTabManager().setInitiallyOpenTab(redstoneTab);

		// Add island for the upgrades.
		registerWidget(new GuiIslandWidget(-25, 8, 30, 64));
	}
}
