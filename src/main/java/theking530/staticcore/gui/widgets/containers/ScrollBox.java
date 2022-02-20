package theking530.staticcore.gui.widgets.containers;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;

public class ScrollBox extends AbstractGuiWidget<ScrollBox> {
	private Color color;
	private boolean drawBackground;
	private float currentScroll;
	private float maxScroll;

	public ScrollBox(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.internalContainer.setTransfomer(this::updateChildLayout);
	}

	public void updateChildLayout(PoseStack pose, int index) {
		pose.translate(0, -currentScroll, 0);
	}

	public ScrollBox setBackgroundColor(Color color) {
		this.color = color;
		return this;
	}

	public ScrollBox setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	public float getCurrentScroll() {
		return currentScroll;
	}

	public void setCurrentScroll(float currentScroll) {
		this.currentScroll = currentScroll;
	}

	public float getMaxScroll() {
		return maxScroll;
	}

	public void setMaxScroll(float maxScroll) {
		this.maxScroll = maxScroll;
		if (currentScroll > maxScroll) {
			currentScroll = maxScroll;
		}
	}

	public void updateData() {
		float requiredHeight = 0;
		for (AbstractGuiWidget<?> child : getChildren()) {
			requiredHeight += child.getSize().getY();
		}

		float maxScroll = Math.max(0, requiredHeight - getSize().getY());
		setMaxScroll(maxScroll);
	}

	@Override
	protected void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (drawBackground) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), 0, 0, 0, color);
		}
	}

	@Override
	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		currentScroll = (float) SDMath.clamp(currentScroll + scrollDelta * -10, 0, maxScroll);
		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}
}
