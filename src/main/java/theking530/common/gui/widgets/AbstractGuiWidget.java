package theking530.common.gui.widgets;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.WidgetContainer;
import theking530.common.utilities.RectangleBounds;
import theking530.common.utilities.Vector2D;

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
	 * Gets the overall bounds of this widget. X and Y are the minimum X and Y
	 * coordinates, Z and W and the maximum x and y coordinates. This value is in
	 * screen space
	 * 
	 * @return
	 */
	public RectangleBounds getBounds() {
		Vector2D screenSpacePosition = getScreenSpacePosition();
		return new RectangleBounds(screenSpacePosition.getX(), screenSpacePosition.getY(), size.getX(), size.getY());
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
		return point.getX() >= ownerRelativePosition.getX() && point.getX() < ownerRelativePosition.getX() + size.getX() && point.getY() >= ownerRelativePosition.getY() && point.getY() < ownerRelativePosition.getY() + size.getY();
	}

	/**
	 * This tick method runs every 1/20th of a second (once per game tick). This is
	 * a good place to perform any calculations that don't have to happen per frame
	 * but still have to occur rapidly.
	 */
	public void tick() {

	}

	/* Render Events */
	/**
	 * Updates this widget with the position and size of it's owner.
	 * 
	 * @param position
	 * @param size
	 * @param partialTicks TODO
	 * @param mouseX       TODO
	 * @param mouseY       TODO
	 */
	public void updateBeforeRender(Vector2D position, Vector2D size, float partialTicks, int mouseX, int mouseY) {
		this.ownerPosition = position;
		this.ownerSize = size;
	}

	/**
	 * This method should be overriden to draw anything that should appear behind
	 * slots/items/anything else.
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {

	}

	/**
	 * This method should be overriden to render anything that should appear above
	 * the background but behind any slots/items.
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {

	}

	public void renderForeground(int mouseX, int mouseY, float partialTicks) {

	}

	/* Tooltip */
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {

	}

	/* Input Events */
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		return EInputResult.UNHANDLED;
	}

	public void mouseMove(int mouseX, int mouseY) {

	}

	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		return EInputResult.UNHANDLED;
	}

	public EInputResult characterTyped(char character, int p_charTyped_2_) {
		return EInputResult.UNHANDLED;
	}

	public EInputResult keyPressed(int key, int scanCode, int modifiers) {
		return EInputResult.UNHANDLED;
	}
}
