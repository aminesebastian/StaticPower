package theking530.common.gui.widgets.scrollbar;

import net.minecraft.client.Minecraft;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.drawables.SpriteDrawable;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.utilities.SDMath;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;

public class ScrollBarWidget extends AbstractGuiWidget {
	private int scrollAmount;
	private int maxScroll;
	private final SpriteDrawable scrollHandleDrawable;

	public ScrollBarWidget(float xPosition, float yPosition, float height) {
		super(xPosition, yPosition, 12.0f, height);
		scrollAmount = 0;
		maxScroll = 0;
		scrollHandleDrawable = new SpriteDrawable(StaticPowerSprites.SCROLL_HANDLE, 12, 15);
	}

	public int getScrollAmount() {
		return scrollAmount;
	}

	public void setScrollAmount(int scrollAmount) {
		this.scrollAmount = scrollAmount;
	}

	public int getMaxScroll() {
		return maxScroll;
	}

	public void setMaxScroll(int maxScroll) {
		this.maxScroll = maxScroll;
		if (maxScroll == 0) {
			scrollHandleDrawable.setSprite(StaticPowerSprites.SCROLL_HANDLE_DISABLED);
		} else {
			scrollHandleDrawable.setSprite(StaticPowerSprites.SCROLL_HANDLE);
		}
	}

	public float getScrollPercent() {
		return (float) scrollAmount / Math.max(1, maxScroll);
	}

	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		if (isPointInsideBounds(new Vector2D((float) mouseX, (float) mouseY))) {
			int offset = (int) mouseY - (int) getScrollHandleLocation().getY();
			offset /= Math.max((Minecraft.getInstance().gameSettings.guiScale - 1), 1);
			scrollAmount = (int) SDMath.clamp(scrollAmount + offset, 0, maxScroll);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isPointInsideBounds(new Vector2D((float) mouseX, (float) mouseY))) {
			int offset = (int) mouseY - (int) getScrollHandleLocation().getY();
			offset /= Math.max((Minecraft.getInstance().gameSettings.guiScale - 1), 1);
			scrollAmount = (int) SDMath.clamp(scrollAmount + offset, 0, maxScroll);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		scrollAmount = (int) SDMath.clamp(scrollAmount - scrollDelta, 0, maxScroll);
		return EInputResult.HANDLED;
	}

	public Vector2D getScrollHandleLocation() {
		Vector2D position = getScreenSpacePosition();
		float adjustedScale = getSize().getY() - scrollHandleDrawable.getSize().getY();
		float yLocation = position.getY() + (getScrollPercent() * adjustedScale);
		return new Vector2D(position.getX(), yLocation);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		Vector2D position = this.getScreenSpacePosition();
		GuiDrawUtilities.drawSlot(position.getX(), position.getY(), getSize().getX(), getSize().getY());

		Vector2D scrollHandlePosition = getScrollHandleLocation();
		scrollHandleDrawable.draw(scrollHandlePosition.getX(), scrollHandlePosition.getY());
	}
}