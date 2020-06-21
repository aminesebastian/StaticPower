package theking530.staticpower.items.cableattachments.extractor;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;
import theking530.api.gui.widgets.tabs.GuiInfoTab;
import theking530.api.gui.widgets.tabs.redstonecontrol.GuiCableAttachmentRedstoneTab;
import theking530.staticpower.items.cableattachments.AbstractCableAttachmentGui;

public class GuiExtractor extends AbstractCableAttachmentGui<ContainerExtractor, ExtractorAttachment> {
	private ItemStackHandler filterInventory;

	public GuiExtractor(ContainerExtractor container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name);
		filterInventory = container.getExtractorInventory();
		GuiCableAttachmentRedstoneTab redstoneTab;
		getTabManager().registerTab(redstoneTab = new GuiCableAttachmentRedstoneTab(container.getItemStack(), container.getAttachmentSide(), container.getCableComponent()));
		getTabManager().registerTab(new GuiInfoTab(100, 60));
		getTabManager().setInitiallyOpenTab(redstoneTab);
	}

	@Override
	protected void drawBackgroundExtras(float f, int i, int j) {
		drawGenericBackground();
		drawPlayerInventorySlots();

		// Draw the filter slots.
		for (int k = 0; k < filterInventory.getSlots(); k++) {
			Slot slot = container.inventorySlots.get(k);
			drawSlot(guiLeft + slot.xPos, guiTop + slot.yPos, 16, 16);
		}
	}
}
