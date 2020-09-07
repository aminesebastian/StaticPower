package theking530.staticpower.cables.attachments.digistore.craftinginterface;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentGui;

public class GuiDigistoreCraftingInterface extends AbstractCableAttachmentGui<ContainerDigistoreCraftingInterface, DigistoreCraftingInterfaceAttachment> {

	public GuiDigistoreCraftingInterface(ContainerDigistoreCraftingInterface container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 151);
		getTabManager().registerTab(new GuiInfoTab(100));
	}
}
