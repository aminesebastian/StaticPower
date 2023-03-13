package theking530.staticcore.gui.widgets.containers;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;

public class HorizontalBox extends AbstractGuiWidget<HorizontalBox> {
	private SDColor color;
	private boolean drawBackground;
	private boolean evenlyDivideSpace;

	public HorizontalBox(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		evenlyDivideSpace = false;
	}

	protected void transformPoseBeforeChildRender(PoseStack pose, AbstractGuiWidget<?> child, int index) {
		int offset = 0;
		for (int i = 0; i < index; i++) {
			offset += this.getChildren().get(i).getSize().getXi();
		}
		pose.translate(offset, 0, 0);
	}

	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		if (evenlyDivideSpace) {
			for (AbstractGuiWidget<?> widget : this.getChildren()) {
				widget.setWidth(this.getWidth() / this.getChildren().size());
			}
		}
	}

	public HorizontalBox setEvenlyDivideSpace(boolean evenlyDivideSpace) {
		this.evenlyDivideSpace = evenlyDivideSpace;
		return this;
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
