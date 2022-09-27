package theking530.staticcore.gui.widgets.containers;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.SDColor;

public class HorizontalBox extends AbstractGuiWidget<HorizontalBox> {
	private SDColor color;
	private boolean drawBackground;

	public HorizontalBox(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
	}

	protected void transformPoseBeforeChildRender(PoseStack pose, AbstractGuiWidget<?> child, int index) {
		float distanceBetween = getSize().getX() / (getChildren().size() + 1);
		pose.translate(((index + 1) * distanceBetween), 0, 0);
	}

	public HorizontalBox setBackgroundColor(SDColor color) {
		this.color = color;
		return this;
	}

	public HorizontalBox setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	@Override
	protected void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (drawBackground) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), 0, 0, 0, color);
		}
	}
}
