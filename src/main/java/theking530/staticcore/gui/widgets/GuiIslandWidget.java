package theking530.staticcore.gui.widgets;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;

public class GuiIslandWidget extends AbstractGuiWidget {
	private boolean topEnabled;
	private boolean bottomEnabled;
	private boolean leftEnabled;
	private boolean rightEnabled;

	public GuiIslandWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		topEnabled = true;
		bottomEnabled = true;
		leftEnabled = true;
		rightEnabled = true;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		Vector2D screenSpace = getScreenSpacePosition();
		GuiDrawUtilities.drawGenericBackground((int) getSize().getX(), (int) getSize().getY(), (int) screenSpace.getX(), (int) screenSpace.getY(), GuiDrawUtilities.DEFAULT_BACKGROUND_COLOR, GuiDrawUtilities.DEFAULT_BACKGROUND_EDGE_TINT, leftEnabled,
				rightEnabled, topEnabled, bottomEnabled);
	}

	public GuiIslandWidget setTopEnabledState(boolean state) {
		topEnabled = state;
		return this;
	}

	public GuiIslandWidget setBottomEnabledState(boolean state) {
		bottomEnabled = state;
		return this;
	}

	public GuiIslandWidget setLeftEnabledState(boolean state) {
		leftEnabled = state;
		return this;
	}

	public GuiIslandWidget setRightEnabledState(boolean state) {
		rightEnabled = state;
		return this;
	}
}
