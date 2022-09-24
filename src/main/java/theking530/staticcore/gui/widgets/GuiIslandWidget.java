package theking530.staticcore.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;

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
	public void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		GuiDrawUtilities.drawGenericBackground(pose, getSize().getX(),  getSize().getY());
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
