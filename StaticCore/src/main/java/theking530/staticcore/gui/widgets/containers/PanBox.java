package theking530.staticcore.gui.widgets.containers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.RectangleBounds;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticcore.utilities.math.Vector4D;

public class PanBox extends AbstractGuiWidget<PanBox> {
	private SDColor color;
	private boolean drawBackground;
	private float interpolatedZoom;
	private float targetZoom;
	private float maxZoom;

	private Vector2D interpolatedPan;
	private Vector2D targetPan;
	private Vector4D maxBounds;

	public PanBox(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		// this.internalContainer.setTransfomer(this::updateChildLayout);
		interpolatedPan = new Vector2D();
		targetPan = new Vector2D();
		maxBounds = new Vector4D();
		interpolatedZoom = 1;
		targetZoom = 1;
		this.setClipType(WidgetClipType.CLIP);
	}

	@Override
	public void transformPoseBeforeRender(PoseStack pose) {
		pose.translate((getSize().getX() + getPosition().getX()) / 2, (getSize().getY() + getPosition().getY()) / 2, 0);
		pose.scale(1 / interpolatedZoom, 1 / interpolatedZoom, 1 / interpolatedZoom);
		pose.translate((getSize().getX() + getPosition().getX()) / -2, (getSize().getY() + getPosition().getY()) / -2, 0);
		pose.translate(interpolatedPan.getX(), interpolatedPan.getY(), 0);
	}

	public PanBox setBackgroundColor(SDColor color) {
		this.color = color;
		return this;
	}

	public PanBox setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	public float getCurrentZoom() {
		return interpolatedZoom;
	}

	public PanBox setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
		return this;
	}

	public float getMaxZoom() {
		return maxZoom;
	}

	public Vector4D getMaxBounds() {
		return maxBounds;
	}

	public void setMaxBounds(Vector4D maxBounds) {
		this.maxBounds = maxBounds;
	}

	public PanBox setMaxScroll(float maxScroll) {
		if (targetZoom > maxScroll) {
			targetZoom = maxScroll;
		}
		return this;
	}

	public PanBox setTargetPan(Vector2D pan) {
		this.targetPan = pan;
		if (targetPan.getX() < maxBounds.getX()) {
			targetPan.setX(maxBounds.getX());
		} else if (targetPan.getX() > maxBounds.getZ()) {
			targetPan.setX(maxBounds.getZ());
		}

		// Limit the Y.
		if (targetPan.getY() < maxBounds.getY()) {
			targetPan.setY(maxBounds.getY());
		} else if (targetPan.getY() > maxBounds.getW()) {
			targetPan.setY(maxBounds.getW());
		}
		return this;
	}

	public PanBox addPanOffset(float x, float y) {
		Vector2D newTarget = new Vector2D();
		newTarget.setX(targetPan.getX() + (float) x * targetZoom);
		newTarget.setY(targetPan.getY() + (float) y * targetZoom);
		setTargetPan(newTarget);
		return this;
	}

	public void tick() {

	}

	public RectangleBounds getClipBounds(PoseStack matrix) {
		Vector2D resolution = new Vector2D(Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
		Vector2D screenSpace = getScreenSpacePosition();
		Vector2D adjustedSize = getSize();
		RectangleBounds output = new RectangleBounds(screenSpace.getX(), resolution.getY() - adjustedSize.getY() - screenSpace.getY(), adjustedSize.getX(), adjustedSize.getY());
		return output;
	}

	@Override
	protected void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (drawBackground) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), 0, 0, 0, color);
		}

		// Interpolate the zoom target.
		float interpSpeed = Math.max(0, Math.abs(interpolatedZoom - targetZoom)) / 2;
		if (interpolatedZoom >= targetZoom) {
			interpolatedZoom = SDMath.clamp(interpolatedZoom - partialTicks * interpSpeed, targetZoom, maxZoom);
		} else {
			interpolatedZoom = SDMath.clamp(interpolatedZoom + partialTicks * interpSpeed, 0, targetZoom);
		}

		// Interpolate the pan target.
		Vector2D panDelta = targetPan.copy().subtract(interpolatedPan);
		interpolatedPan.add(panDelta.copy().multiply(0.15f));
	}

	@Override
	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (isHovered()) {
			targetZoom = (float) SDMath.clamp(targetZoom + scrollDelta * -0.1f, 1, maxZoom);
		}
		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		addPanOffset((float) p_mouseDragged_6_, (float) p_mouseDragged_8_);
		return super.mouseDragged(mouseX, mouseY, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
	}

}
