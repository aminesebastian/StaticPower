package theking530.staticcore.gui.widgets.containers;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreGuiTextures;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.RectangleBounds;
import theking530.staticcore.utilities.math.SDMath;

public class ScrollBox extends AbstractGuiWidget<ScrollBox> {
	private SDColor color;
	private boolean drawBackground;
	private float interpolatedScroll;
	private float targetScroll;
	private float maxScroll;
	private boolean drawScrollBar;
	private boolean drawScrollBarBackground;

	public ScrollBox(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.setClipType(WidgetClipType.CLIP);
	}

	@Override
	protected void transformPoseBeforeChildRender(PoseStack matrix, AbstractGuiWidget<?> child, int index) {
		super.transformPoseBeforeChildRender(matrix, child, index);
		matrix.translate(0, -interpolatedScroll, 0);

	}

	protected void onWidgetAdded(AbstractGuiWidget<?> widget) {
		recreateWidgets();
	}

	protected void onWidgetRemoved(AbstractGuiWidget<?> widget) {
		recreateWidgets();
	}

	protected void recreateWidgets() {
		for (int i = 0; i < getChildren().size(); i++) {
			AbstractGuiWidget<?> widget = getChildren().get(i);
			if (i == 0) {
				widget.setPosition(0, 0);
			} else {
				widget.setPosition(0, getChildren().get(i - 1).getYPosition() + getChildren().get(i - 1).getSize().getY());
			}
		}
	}

	public ScrollBox setBackgroundColor(SDColor color) {
		this.color = color;
		return this;
	}

	public ScrollBox setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	public float getCurrentScroll() {
		return interpolatedScroll;
	}

	public ScrollBox setTargetScroll(float targetScroll) {
		this.targetScroll = targetScroll;
		return this;
	}

	public float getMaxScroll() {
		return maxScroll;
	}

	public ScrollBox setMaxScroll(float maxScroll) {
		this.maxScroll = maxScroll;
		if (targetScroll > maxScroll) {
			targetScroll = maxScroll;
		}
		return this;
	}

	public boolean isDrawScrollBar() {
		return drawScrollBar;
	}

	public ScrollBox setDrawScrollBar(boolean drawScrollBar) {
		this.drawScrollBar = drawScrollBar;
		return this;
	}

	public boolean isDrawScrollBarBackground() {
		return drawScrollBarBackground;
	}

	public ScrollBox setDrawScrollBarBackground(boolean drawScrollBarBackground) {
		this.drawScrollBarBackground = drawScrollBarBackground;
		return this;
	}

	public void tick() {
		float requiredHeight = 0;
		for (AbstractGuiWidget<?> child : getChildren()) {
			if (child.isVisible()) {
				requiredHeight += child.getHeight();
				if (this.drawScrollBar) {
					child.setWidth(this.getWidth() - 10);
				} else {
					child.setWidth(this.getWidth());
				}
			}
		}

		float maxScroll = Math.max(0, requiredHeight - getSize().getY());
		setMaxScroll(maxScroll);
	}

	@Override
	public RectangleBounds getClipBounds(PoseStack matrix) {
		return super.getClipBounds(matrix);
	}

	@Override
	protected void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (drawBackground) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), 0, 0, 0, color);
		}

		// Interpolate the scroll target.
		float interpSpeed = Math.max(0, Math.abs(interpolatedScroll - targetScroll)) / 2;
		if (interpolatedScroll >= targetScroll) {
			interpolatedScroll = SDMath.clamp(interpolatedScroll - partialTicks * interpSpeed, targetScroll, maxScroll);
		} else {
			interpolatedScroll = SDMath.clamp(interpolatedScroll + partialTicks * interpSpeed, 0, targetScroll);
		}

	}

	@Override
	protected void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (drawScrollBar) {
			int topPosition = 5;
			int bottomPosition = (int) (getSize().getY() - 5);
			float handlePosition = SDMath.lerp(topPosition, bottomPosition, (float) interpolatedScroll / maxScroll) - 6;

			if (drawScrollBarBackground) {
				GuiDrawUtilities.drawSlot(pose, 10, getSize().getY(), getSize().getX() - 10, 0, 0);
			}

			if (this.maxScroll > 0) {
				GuiDrawUtilities.drawTexture(pose, StaticCoreGuiTextures.SCROLL_HANDLE, 10, 15, getSize().getX() - 10, handlePosition, 0, 0, 0, 1, 1, SDColor.WHITE);
			}

		}
	}

	@Override
	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (isHovered()) {
			targetScroll = (float) SDMath.clamp(targetScroll + scrollDelta * -10, 0, maxScroll);
		}
		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}
}
