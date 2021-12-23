package theking530.staticpower.cables.attachments.digistore.craftinginterface;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;

public class GuiDigistoreCraftingInterface extends AbstractCableAttachmentGui<ContainerDigistoreCraftingInterface, DigistoreCraftingInterfaceAttachment> {

	public GuiDigistoreCraftingInterface(ContainerDigistoreCraftingInterface container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 151);
		getTabManager().registerTab(new GuiInfoTab(100));
	}
}
