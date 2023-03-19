package theking530.staticpower.cables.attachments.digistore.craftinginterface;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachmentGui;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;

public class GuiDigistoreCraftingInterface extends AbstractCableAttachmentGui<ContainerDigistoreCraftingInterface, DigistoreCraftingInterfaceAttachment> {

	public GuiDigistoreCraftingInterface(ContainerDigistoreCraftingInterface container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 151);
		getTabManager().registerTab(new GuiInfoTab(100));
	}
}
