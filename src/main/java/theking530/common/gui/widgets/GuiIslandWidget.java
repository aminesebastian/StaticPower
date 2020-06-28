package theking530.common.gui.widgets;

import theking530.common.gui.GuiDrawUtilities;
import theking530.common.utilities.Vector2D;

public class GuiIslandWidget extends AbstractGuiWidget {

	public GuiIslandWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		Vector2D screenSpace = getScreenSpacePosition();
		GuiDrawUtilities.drawGenericBackground((int) getSize().getX(), (int) getSize().getY(), (int) screenSpace.getX(), (int) screenSpace.getY());
	}
}
