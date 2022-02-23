package theking530.staticcore.gui.widgets.containers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;

public class ScrollBox extends AbstractGuiWidget<ScrollBox> {
	private Color color;
	private boolean drawBackground;
	private float interpolatedScroll;
	private float targetScroll;
	private float maxScroll;
	private boolean drawScrollBar;
	private boolean drawScrollBarBackground;

	public ScrollBox(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.internalContainer.setTransfomer(this::updateChildLayout);
	}

	protected void updateChildLayout(PoseStack pose, int index) {
		pose.translate(0, -interpolatedScroll, 0);

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

	public void updateData() {
		float requiredHeight = 0;
		for (AbstractGuiWidget<?> child : getChildren()) {
			if (child.getPosition().getY() > requiredHeight) {
				requiredHeight = child.getPosition().getY() + child.getSize().getY();
			}
		}

		float maxScroll = Math.max(0, requiredHeight - getSize().getY() + 20);
		setMaxScroll(maxScroll);
	}

	public RectangleBounds getClipBounds(PoseStack matrix) {
		float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();
		Vector2D resolution = new Vector2D(Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
		Vector2D screenSpace = getScreenSpacePosition();
		Vector2D adjustedSize = getSize();
		RectangleBounds output = new RectangleBounds(screenSpace.getX() * guiScale, resolution.getY() - adjustedSize.getY() - screenSpace.getY(), adjustedSize.getX() * guiScale,
				adjustedSize.getY() * guiScale);
		return output;
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

		if (drawScrollBar) {
			int topPosition = 5;
			int bottomPosition = (int) (getSize().getY() - 5);
			float handlePosition = SDMath.lerp(topPosition, bottomPosition, (float) targetScroll / maxScroll) - 6;

			if (drawScrollBarBackground) {
				GuiDrawUtilities.drawSlot(pose, 10, getSize().getY() - 2, getSize().getX() - 10, 0, 0);
			}

			GuiDrawUtilities.drawTexture(pose, GuiTextures.SCROLL_HANDLE, 10, 15, getSize().getX() - 10, handlePosition, 0, 0, 0, 1, 1, Color.WHITE);
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
