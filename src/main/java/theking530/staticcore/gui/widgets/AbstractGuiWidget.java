package theking530.staticcore.gui.widgets;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.WidgetContainer;
import theking530.staticcore.gui.WidgetContainer.WidgetParent;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.RenderingUtilities;
import theking530.staticcore.utilities.Vector2D;

@SuppressWarnings({ "unchecked" })
public abstract class AbstractGuiWidget<T extends AbstractGuiWidget<?>> {
	public enum EInputResult {
		HANDLED, UNHANDLED
	}

	/**
	 * The owning container of this widget.
	 */
	protected WidgetContainer owningContainer;
	protected final WidgetContainer internalContainer;
	protected boolean DEBUG_HOVER;
	private Vector2D initialPosition;
	private Vector2D position;
	private float zLevel;
	private float ticksHovered;
	private Vector2D size;
	private Vector2D parentSize;
	private boolean isVisible;
	private boolean isEnabled;
	private boolean tooltipsDisabled;
	private boolean autoHandleTooltipBounds;
	private RectangleBounds cachedBounds;
	private PoseStack lastMatrixStack;
	private Vector2D lastMousePosition;
	private final Font fontRenderer;

	@SuppressWarnings("resource")
	public AbstractGuiWidget(float xPosition, float yPosition, float width, float height) {
		cachedBounds = new RectangleBounds(0.0f, 0.0f, 0.0f, 0.0f); // Must be initially set to 0.
		initialPosition = new Vector2D(xPosition, yPosition);
		position = new Vector2D(xPosition, yPosition);
		size = new Vector2D(width, height);
		parentSize = new Vector2D(0.0f, 0.0f);
		lastMousePosition = new Vector2D(0.0f, 0.0f);
		isVisible = true;
		isEnabled = true;
		zLevel = 0.0f;
		autoHandleTooltipBounds = true;
		internalContainer = new WidgetContainer(WidgetParent.fromWidget(this));
		fontRenderer = getMinecraft().font;
	}

	/**
	 * Sets the owning container of this widget. This is called straight from the
	 * {@link WidgetContainer} that will contain this widget.
	 * 
	 * @param container
	 * @param parent    TODO
	 */
	public void setOwningContainer(WidgetContainer container) {
		owningContainer = container;
	}

	/**
	 * Gets the parent for this widget.
	 * 
	 * @return
	 */
	public WidgetParent getParent() {
		return owningContainer.getParent();
	}

	/**
	 * This method is raised when this widget is added to a parent.
	 * 
	 * @param gui
	 */
	public void addedToParent(@Nullable WidgetParent parent) {

	}

	/**
	 * This method is raised when this widget is removed from a parent.
	 * 
	 * @param gui
	 */
	public void removedFromParent(@Nullable WidgetParent parent) {

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
	public T setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return (T) this;
	}

	/**
	 * Sets the enabled state of this widget.
	 * 
	 * @param isEnabled
	 * @return
	 */
	public T setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		return (T) this;
	}

	/**
	 * Sets the relative position of this widget. This is then transformed to the
	 * space of the owner before drawing.
	 * 
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public T setPosition(float xPos, float yPos) {
		position.setX(xPos);
		position.setY(yPos);
		return (T) this;
	}

	/**
	 * Sets the zLevel for this widget. All matricies will be translated by this
	 * value in the z direction.
	 * 
	 * @param zLevel
	 * @return
	 */
	public T setZLevel(float zLevel) {
		this.zLevel = zLevel;
		return (T) this;
	}

	/**
	 * Returns the zLevel for this widget.
	 * 
	 * @return
	 */
	public float getZLevel() {
		return zLevel;
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
	 * Gets the first position that this widget was rendered;
	 * 
	 * @return
	 */
	public Vector2D getInitialPosition() {
		return initialPosition;
	}

	/**
	 * Gets the last screen space absolute position that this widget was rendered
	 * at.
	 * 
	 * @return
	 */
	public Vector2D getScreenSpacePosition() {
		return GuiDrawUtilities.translatePositionByMatrix(lastMatrixStack, getPosition());
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
	public T setSize(float width, float height) {
		size.setX(width);
		size.setY(height);
		return (T) this;
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
	 * Gets the last known mouse position.
	 * 
	 * @return
	 */
	public Vector2D getLastMousePosition() {
		return lastMousePosition;
	}

	public boolean isHovered() {
		return (DEBUG_HOVER || isPointInsideBounds(getLastMousePosition())) && isEnabled();
	}

	public float getTicksHovered() {
		return ticksHovered;
	}

	/**
	 * Gets the size of the parent of this widget.
	 * 
	 * @return
	 */
	public Vector2D getParentSize() {
		return parentSize;
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
	 * @param parentSize
	 * @param partialTicks
	 * @param mouseX
	 * @param mouseY
	 */
	public void updateBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		this.parentSize = parentSize;
		lastMousePosition = new Vector2D(mouseX, mouseY);

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrixStack, getPosition());
		matrixStack.pushPose();
		transformPoseBeforeRender(matrixStack);
		internalContainer.update(matrixStack, parentSize, partialTicks, mouseX, mouseY);
		matrixStack.popPose();

		// Make a NEW matrix that translates from local space to screen space. We make a
		// new matrix so that it's owned by this widget, and not modified by any
		// external references.
		lastMatrixStack = new PoseStack();
		lastMatrixStack.translate(screenSpacePosition.getX() - getPosition().getX(), screenSpacePosition.getY() - getPosition().getY(), 0);

		cachedBounds.update(screenSpacePosition.getX(), screenSpacePosition.getY(), this.size.getX(), this.size.getY());

		if (isHovered()) {
			ticksHovered += partialTicks;
		} else {
			ticksHovered = 0;
		}
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
		transformPoseBeforeRender(matrix);
		RectangleBounds clip = getClipBounds(matrix);
		if (clip != null) {
			RenderingUtilities.applyScissorMask(clip);
		}

		internalContainer.renderBackground(matrix, mouseX, mouseY, partialTicks);
		renderWidgetBackground(matrix, mouseX, mouseY, partialTicks);
		matrix.popPose();

		if (clip != null) {
			RenderingUtilities.clearScissorMask();
		}
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
		transformPoseBeforeRender(matrix);
		RectangleBounds clip = getClipBounds(matrix);
		if (clip != null) {
			RenderingUtilities.applyScissorMask(clip);
		}

		renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);
		internalContainer.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
		matrix.popPose();

		if (clip != null) {
			RenderingUtilities.clearScissorMask();
			;
		}
	}

	public void renderForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		matrix.pushPose();
		transformPoseBeforeRender(matrix);
		RectangleBounds clip = getClipBounds(matrix);
		if (clip != null) {
			RenderingUtilities.applyScissorMask(clip);
		}

		internalContainer.renderForegound(matrix, mouseX, mouseY, partialTicks);
		renderWidgetForeground(matrix, mouseX, mouseY, partialTicks);
		matrix.popPose();

		if (clip != null) {
			RenderingUtilities.clearScissorMask();
		}
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

	/**
	 * Performs the translation from parent space to this widget's space for
	 * rendering. Careful when overidding this method as the bounds aren't updated
	 * until the next render request.
	 * 
	 * @param matrix
	 */
	protected void transformPoseBeforeRender(PoseStack matrix) {
		matrix.translate(getPosition().getX(), getPosition().getY(), zLevel);
	}

	public RectangleBounds getClipBounds(PoseStack matrix) {
		return null;
	}

	/**
	 * Gets the current runing instance of minecraft.
	 * 
	 * @return
	 */
	protected Minecraft getMinecraft() {
		return Minecraft.getInstance();
	}

	/**
	 * Gets the font renderer.
	 * 
	 * @return
	 */
	protected Font getFontRenderer() {
		return fontRenderer;
	}

	/**
	 * Gets the local player.
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	protected Player getLocalPlayer() {
		return getMinecraft().player;
	}

	protected void playSoundLocally(SoundEvent sound, float volume, float pitch) {
		getLocalPlayer().level.playSound(getLocalPlayer(), getLocalPlayer().blockPosition(), sound, SoundSource.MASTER, volume, pitch);
	}

	public void registerWidget(@SuppressWarnings("rawtypes") AbstractGuiWidget widget) {
		internalContainer.registerWidget(widget);
	}

	public void removeWidget(@SuppressWarnings("rawtypes") AbstractGuiWidget widget) {
		internalContainer.removeWidget(widget);
	}

	public void clearChildren() {
		internalContainer.clearWidgets();
	}

	/* Tooltip */
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		internalContainer.getTooltips(mousePosition, tooltips, showAdvanced);
	}

	public boolean getTooltipsDisabled() {
		return tooltipsDisabled;
	}

	public AbstractGuiWidget<T> setTooltipsDisabled(boolean disabled) {
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
	public AbstractGuiWidget<T> setShouldAutoCalculateTooltipBounds(boolean value) {
		this.autoHandleTooltipBounds = value;
		return this;
	}

	/**
	 * Returns all the child widgets for this widget.
	 * 
	 * @return
	 */
	public List<AbstractGuiWidget<?>> getChildren() {
		return internalContainer.getWidgets();
	}

	/* Input Events */
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		return internalContainer.handleMouseClick(mouseX, mouseY, button);
	}

	public EInputResult mouseMove(int mouseX, int mouseY) {
		return internalContainer.handleMouseMove(mouseX, mouseY);
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
