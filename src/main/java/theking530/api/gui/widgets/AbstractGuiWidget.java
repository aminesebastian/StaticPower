package theking530.api.gui.widgets;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.WidgetContainer;
import theking530.api.utilities.Vector2D;

public abstract class AbstractGuiWidget {
	public enum EInputResult {
		HANDLED, UNHANDLED
	}

	/**
	 * The owning container of this widget.
	 */
	protected WidgetContainer owningContainer;

	private Vector2D position;
	private Vector2D size;
	private Vector2D ownerPosition;
	private Vector2D ownerSize;
	private boolean isVisible;

	public AbstractGuiWidget(float xPosition, float yPosition, float width, float height) {
		position = new Vector2D(xPosition, yPosition);
		size = new Vector2D(width, height);
		ownerPosition = new Vector2D(0.0f, 0.0f);
		ownerSize = new Vector2D(0.0f, 0.0f);
		isVisible = true;
	}

	/**
	 * Sets the owning conatiner of this widget. This is called straight from the
	 * {@link WidgetContainer} that will contain this widget.
	 * 
	 * @param container
	 */
	public void setOwningContainer(WidgetContainer container) {
		owningContainer = container;
	}

	/**
	 * Indicates if this widget is visible.
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Sets the visibility of the widget.
	 * 
	 * @param isVisible
	 * @return
	 */
	public AbstractGuiWidget setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	/**
	 * Sets the relative position of this widget. This is then transformed to the
	 * space of the owner before drawing.
	 * 
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public AbstractGuiWidget setPosition(float xPos, float yPos) {
		position.setX(xPos);
		position.setY(yPos);
		return this;
	}

	/**
	 * Gets the relative position of this widget.
	 * 
	 * @return
	 */
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * Helper method that returns the position of this widget within screen space
	 * (similar to performing gui.getGuiTop() + widget.getPosition().getX().
	 * 
	 * @return
	 */
	public Vector2D getScreenSpacePosition() {
		return new Vector2D(position.getX() + getOwnerPosition().getX(), position.getY() + getOwnerPosition().getY());
	}

	/**
	 * Sets the size of this widget.
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public AbstractGuiWidget setSize(float width, float height) {
		size.setX(width);
		size.setY(height);
		return this;
	}

	/**
	 * Gets the size of this widget.
	 * 
	 * @return The size of the widget.
	 */
	public Vector2D getSize() {
		return size;
	}

	/**
	 * Gets the position of the owner of this widget.
	 * 
	 * @return
	 */
	public Vector2D getOwnerPosition() {
		return ownerPosition;
	}

	/**
	 * Gets the size of the owner of this widget.
	 * 
	 * @return
	 */
	public Vector2D getOwnerSize() {
		return ownerSize;
	}

	/**
	 * Checks if the provided point is inside this widget.
	 * 
	 * @param point The point (such as the mouse position).
	 * @return True if the point is in size this widget, false otherwise.
	 */
	public boolean isPointInsideBounds(Vector2D point) {
		Vector2D ownerRelativePosition = getScreenSpacePosition();
		return point.getX() >= ownerRelativePosition.getX() && point.getX() < ownerRelativePosition.getX() + size.getX() && point.getY() >= ownerRelativePosition.getY()
				&& point.getY() < ownerRelativePosition.getY() + size.getY();
	}

	/* Render Events */
	/**
	 * Updates this widget with the position and size of it's owner.
	 * 
	 * @param position
	 * @param size
	 */
	public void updateBeforeRender(Vector2D position, Vector2D size) {
		this.ownerPosition = position;
		this.ownerSize = size;
	}

	public abstract void renderBackground(int mouseX, int mouseY, float partialTicks);

	public void renderForeground(int mouseX, int mouseY, float partialTicks) {

	}

	/* Tooltip */
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {

	}

	/* Input Events */
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		return EInputResult.UNHANDLED;
	}

	public void mouseHover(int mouseX, int mouseY) {

	}
}
