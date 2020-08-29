package theking530.staticpower.client.container.slots;

import net.minecraftforge.items.IItemHandler;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.GuiDrawItem;
import theking530.common.utilities.Color;

public class DigistoreSlot extends StaticPowerContainerSlot {
	private boolean managerPresent;

	public DigistoreSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean managerPresent) {
		super(itemHandler, index, xPosition, yPosition);
		this.managerPresent = managerPresent;
	}

	public void drawBeforeItem(GuiDrawItem itemRenderer, int guiLeft, int guiTop, int slotSize, int slotPosOffset) {
		if (!managerPresent) {
			GuiDrawUtilities.drawColoredRectangle(guiLeft + this.xPos, guiTop + this.yPos, 16, 16, 1.0f, Color.DARK_GREY);
		}
	}
}
