package theking530.staticcore.gui.widgets;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.WidgetContainer;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

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
	private Vector2D ownerSize;
	private boolean isVisible;
	private boolean isEnabled;
	private boolean tooltipsDisabled;
	private boolean autoHandleTooltipBounds;
	private RectangleBounds cachedBounds;
	private PoseStack lastMatrixStack;
	private final WidgetContainer internalContainer;

	public AbstractGuiWidget(float xPosition, float yPosition, float width, float height) {
		cachedBounds = new RectangleBounds(0.0f, 0.0f, 0.0f, 0.0f); // Must be initially set to 0.
		position = new Vector2D(xPosition, yPosition);
		size = new Vector2D(width, height);
		ownerSize = new Vector2D(0.0f, 0.0f);
		isVisible = true;
		isEnabled = true;
		autoHandleTooltipBounds = true;
		internalContainer = new WidgetContainer();
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
	 * This method is raised when this widget is added to a container that exists on
	 * a ContainerScreen. This will NOT be called on any other object that defines a
	 * WidgetContainer.
	 * 
	 * @param gui
	 */
	public void addedToGui(@Nullable StaticPowerContainerGui<?> gui) {

	}

	/**
	 * This method is raised when this widget is removed from a container that
	 * exists on a ContainerScreen. This will NOT be called on any other object that
	 * defines a WidgetContainer.
	 * 
	 * @param gui
	 */
	public void removedFromGui(@Nullable StaticPowerContainerGui<?> gui) {

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
	 * Check if the widget is enabled.
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return isEnabled;
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
	 * Sets the enabled state of this widget.
	 * 
	 * @param isEnabled
	 * @return
	 */
	public AbstractGuiWidget setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
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
	 * Gets the overall bounds of this widget. X and Y are the minimum X and Y
	 * coordinates, Z and W and the maximum x and y coordinates. This value is in
	 * screen space
	 * 
	 * @return
	 */
	public RectangleBounds getBounds() {
		return cachedBounds;
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
	 * Gets the last matrix that was used to render this widget. This is useful to
	 * translate a position from local space of this widget, to screen space. This
	 * matrix should NOT be modified.
	 * 
	 * @return
	 */
	public PoseStack getLastRenderMatrix() {
		return lastMatrixStack;
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
		return getBounds().isPointInBounds(point);
	}

	/**
	 * This tick method runs every 1/20th of a second (once per game tick). This is
	 * a good place to perform any calculations that don't have to happen per frame
	 * but still have to occur rapidly.
	 */
	public void updateData() {

	}

	/* Render Events */
	/**
	 * Updates this widget with the position and size of it's owner.
	 * 
	 * @param ownerPosition
	 * @param ownerSize
	 * @param partialTicks
	 * @param mouseX
	 * @param mouseY
	 */
	public void updateBeforeRender(PoseStack matrixStack, Vector2D ownerSize, float partialTicks, int mouseX, int mouseY) {
		this.ownerSize = ownerSize;

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrixStack, getPosition());
		internalContainer.update(matrixStack, screenSpacePosition, ownerSize, partialTicks, mouseX, mouseY);

		// Make a NEW matrix that translates from local space to screen space. We make a
		// new matrix so that it's owned by this widget, and not modified by any
		// external references.
		lastMatrixStack = new PoseStack();
		lastMatrixStack.translate(screenSpacePosition.getX() - getPosition().getX(), screenSpacePosition.getY() - getPosition().getY(), 0);

		cachedBounds.update(screenSpacePosition.getX(), screenSpacePosition.getY(), this.size.getX(), this.size.getY());
	}

	/**
	 * This method should be overriden to draw anything that should appear behind
	 * slots/items/anything else.
	 * 
	 * @param matrix
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	public void renderBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		matrix.pushPose();
		matrix.translate(getPosition().getX(), getPosition().getY(), 0);
		internalContainer.renderBackground(matrix, mouseX, mouseY, partialTicks);
		renderWidgetBackground(matrix, mouseX, mouseY, partialTicks);
		matrix.popPose();
	}

	/**
	 * This method should be overriden to render anything that should appear above
	 * the background but behind any slots/items.
	 * 
	 * @param matrix
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		matrix.pushPose();
		matrix.translate(getPosition().getX(), getPosition().getY(), 0);
		internalContainer.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
		renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);
		matrix.popPose();
	}

	public void renderForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		matrix.pushPose();
		matrix.translate(getPosition().getX(), getPosition().getY(), 0);
		internalContainer.renderForegound(matrix, mouseX, mouseY, partialTicks);
		renderWidgetForeground(matrix, mouseX, mouseY, partialTicks);
		matrix.popPose();
	}

	/**
	 * This method should be overriden to draw anything that should appear behind
	 * slots/items/anything else.
	 * 
	 * @param matrix
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	protected void renderWidgetBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

	}

	/**
	 * This method should be overriden to render anything that should appear above
	 * the background but behind any slots/items.
	 * 
	 * @param matrix
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	protected void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

	}

	protected void renderWidgetForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

	}

	public void registerWidget(AbstractGuiWidget widget) {
		internalContainer.registerWidget(widget);
	}

	/* Tooltip */
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		internalContainer.getTooltips(mousePosition, tooltips, showAdvanced);
	}

	public boolean getTooltipsDisabled() {
		return tooltipsDisabled;
	}

	public AbstractGuiWidget setTooltipsDisabled(boolean disabled) {
		tooltipsDisabled = disabled;
		return this;
	}

	/**
	 * Indicates whether or not the container will check the bounds of this widget
	 * against the mouse to see if tooltips should be rendered. If false, the
	 * implementer must perform a similar caluclation.
	 * 
	 * @return
	 */
	public boolean getShouldAutoCalculateTooltipBounds() {
		return autoHandleTooltipBounds;
	}

	/**
	 * Sets whether or not the owning container should check the widget's bounds
	 * before calling the {@link #getTooltips(Vector2D, List, boolean)} method.
	 * 
	 * @return
	 */
	public AbstractGuiWidget setShouldAutoCalculateTooltipBounds(boolean value) {
		this.autoHandleTooltipBounds = value;
		return this;
	}

	/* Input Events */
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		return internalContainer.handleMouseClick(mouseX, mouseY, button);
	}

	public void mouseMove(int mouseX, int mouseY) {
		internalContainer.handleMouseMove(mouseX, mouseY);
	}

	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		return internalContainer.handleMouseScrolled(mouseX, mouseY, scrollDelta);
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		return internalContainer.handleMouseDragged(mouseX, mouseY, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
	}

	public EInputResult characterTyped(char character, int p_charTyped_2_) {
		return internalContainer.characterTyped(character, p_charTyped_2_);
	}

	public EInputResult keyPressed(int key, int scanCode, int modifiers) {
		return internalContainer.handleKeyPressed(key, scanCode, modifiers);
	}
}
