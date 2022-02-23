package theking530.staticcore.gui.widgets.containers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.client.gui.GuiTextures;

public class PanBox extends AbstractGuiWidget<PanBox> {
	private Color color;
	private boolean drawBackground;
	private float interpolatedZoom;
	private float targetZoom;
	private float maxZoom;

	private Vector2D interpolatedPan;
	private Vector2D targetPan;
	private Vector4D maxBounds;

	public PanBox(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.internalContainer.setTransfomer(this::updateChildLayout);
		interpolatedPan = new Vector2D();
		targetPan = new Vector2D();
		maxBounds = new Vector4D();
	}

	protected void updateChildLayout(PoseStack pose, int index) {
		pose.scale(1 / interpolatedZoom, 1 / interpolatedZoom, 1 / interpolatedZoom);
		pose.translate(interpolatedPan.getX(), interpolatedPan.getY(), 0);
	}

	public PanBox setBackgroundColor(Color color) {
		this.color = color;
		return this;
	}

	public PanBox setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	public float getCurrentScroll() {
		return targetZoom;
	}

	public PanBox setCurrentScroll(float currentScroll) {
		this.targetZoom = currentScroll;
		return this;
	}

	public float getMaxScroll() {
		return maxZoom;
	}

	public PanBox setMaxScroll(float maxScroll) {
		this.maxZoom = maxScroll;
		if (targetZoom > maxScroll) {
			targetZoom = maxScroll;
		}
		return this;
	}

	public void updateData() {
		float requiredHeight = 0;
		for (AbstractGuiWidget<?> child : getChildren()) {
			requiredHeight += child.getSize().getY();
		}

		float maxScroll = Math.max(0, requiredHeight - getSize().getY());
		setMaxScroll(maxScroll);
	}

	public RectangleBounds getClipBounds(PoseStack matrix) {
		float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();
		Vector2D resolution = new Vector2D(Minecraft.getInstance().getWindow().getGuiScaledWidth(),
				Minecraft.getInstance().getWindow().getGuiScaledHeight());
		Vector2D screenSpace = getScreenSpacePosition();
		Vector2D adjustedSize = getSize();
		RectangleBounds output = new RectangleBounds(screenSpace.getX() * guiScale,
				resolution.getY() - adjustedSize.getY() - screenSpace.getY(), adjustedSize.getX() * guiScale,
				adjustedSize.getY() * guiScale);
		return output;
	}

	@Override
	protected void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (drawBackground) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), 0, 0, 0, color);
		}

		// Interpolate the scroll target.
		float interpSpeed = Math.max(0, Math.abs(interpolatedZoom - targetZoom)) / 2;
		if (interpolatedZoom >= targetZoom) {
			interpolatedZoom = SDMath.clamp(interpolatedZoom - partialTicks * interpSpeed, targetZoom, maxZoom);
		} else {
			interpolatedZoom = SDMath.clamp(interpolatedZoom + partialTicks * interpSpeed, 0, targetZoom);
		}
	}

	@Override
	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (isHovered()) {
			targetZoom = (float) SDMath.clamp(targetZoom + scrollDelta * -10, 0, maxZoom);
		}
		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_,
			double p_mouseDragged_8_) {
		if (isHovered()) {
			Vector2D delta = getLastMousePosition().copy().subtract((float) mouseX, (float) mouseY);
			this.targetPan.add(delta);

			// Limit the X.
			if (targetPan.getX() < maxBounds.getX()) {
				targetPan.setX(maxBounds.getX());
			} else if (targetPan.getX() > maxBounds.getX()) {
				targetPan.setX(maxBounds.getX());
			}

			// Limit the Y.
			if (targetPan.getY() < maxBounds.getY()) {
				targetPan.setY(maxBounds.getY());
			} else if (targetPan.getY() > maxBounds.getY()) {
				targetPan.setY(maxBounds.getY());
			}
		}
		return super.mouseDragged(mouseX, mouseY, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
	}

}