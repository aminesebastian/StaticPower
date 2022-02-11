package theking530.staticcore.gui.widgets.scrollbar;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class ScrollBarWidget extends AbstractGuiWidget {
	private int scrollAmount;
	private int maxScroll;
	private final SpriteDrawable scrollHandleDrawable;

	public ScrollBarWidget(float xPosition, float yPosition, float height) {
		super(xPosition, yPosition, 12.0f, height);
		scrollAmount = 0;
		scrollHandleDrawable = new SpriteDrawable(GuiTextures.SCROLL_HANDLE, 12, 15);
		setMaxScroll(0);
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
		this.maxScroll = Math.max(0, maxScroll);

		// If the scroll amount is equal to zero, disable the scroll bar.
		if (maxScroll == 0) {
			scrollHandleDrawable.setSprite(GuiTextures.SCROLL_HANDLE_DISABLED);
		} else {
			scrollHandleDrawable.setSprite(GuiTextures.SCROLL_HANDLE);
		}

		// Bring back the scroll amount if we are over scrolled.
		if (scrollAmount > maxScroll) {
			scrollAmount = maxScroll;
		}
	}

	public float getScrollPercent() {
		return (float) scrollAmount / Math.max(1, maxScroll);
	}

	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		if (isPointInsideBounds(new Vector2D((float) mouseX, (float) mouseY))) {
			int offset = (int) mouseY - (int) getScrollHandleLocation().getY();
			int scrollSpaceOffset = Math.round(((offset * maxScroll) / getSize().getY()));
			scrollAmount = (int) SDMath.clamp(scrollAmount + scrollSpaceOffset, 0, maxScroll);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isPointInsideBounds(new Vector2D((float) mouseX, (float) mouseY))) {
			int offset = (int) mouseY - (int) getScrollHandleLocation().getY();
			int scrollSpaceOffset = Math.round(((offset * maxScroll) / getSize().getY()));
			scrollAmount = (int) SDMath.clamp(scrollAmount + scrollSpaceOffset, 0, maxScroll);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		scrollAmount = (int) SDMath.clamp(scrollAmount - scrollDelta, 0, maxScroll);
		return EInputResult.HANDLED;
	}

	public Vector2D getScrollHandleLocation() {
		Vector2D position = GuiDrawUtilities.translatePositionByMatrix(getLastRenderMatrix(), getPosition());
		float adjustedScale = getSize().getY() - scrollHandleDrawable.getSize().getY();
		float yLocation = position.getY() + (getScrollPercent() * adjustedScale);
		return new Vector2D(position.getX(), yLocation);
	}

	@Override
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		Vector2D position = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		GuiDrawUtilities.drawSlot(null, position.getX(), position.getY(), getSize().getX(), getSize().getY(), 0);

		Vector2D scrollHandlePosition = getScrollHandleLocation();
		scrollHandleDrawable.draw(scrollHandlePosition.getX(), scrollHandlePosition.getY());
	}
}
