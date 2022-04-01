package theking530.staticcore.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.RenderingUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;

@SuppressWarnings({ "unchecked" })
public abstract class AbstractGuiWidget<T extends AbstractGuiWidget<?>> {
	public enum EInputResult {
		HANDLED, UNHANDLED
	}

	protected final List<AbstractGuiWidget<?>> widgets;

	/**
	 * The owning container of this widget.
	 */
	protected AbstractGuiWidget<?> parent;
	protected boolean DEBUG_HOVER;
	private WidgetClipType clipType;
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
	private boolean shouldRenderThisFrame;

	@SuppressWarnings("resource")
	public AbstractGuiWidget(float xPosition, float yPosition, float width, float height) {
		widgets = new ArrayList<AbstractGuiWidget<?>>();
		cachedBounds = new RectangleBounds(0.0f, 0.0f, 0.0f, 0.0f); // Must be initially set to 0.
		initialPosition = new Vector2D(xPosition, yPosition);
		position = new Vector2D(xPosition, yPosition);
		size = new Vector2D(width, height);
		parentSize = new Vector2D(0.0f, 0.0f);
		lastMousePosition = new Vector2D(0.0f, 0.0f);
		isVisible = true;
		isEnabled = true;
		clipType = WidgetClipType.FREE;
		zLevel = 0.0f;
		autoHandleTooltipBounds = true;
		fontRenderer = getMinecraft().font;
	}

	/**
	 * Sets the owning container of this widget. This is called straight from the
	 * {@link WidgetContainer} that will contain this widget.
	 * 
	 * @param parent
	 */
	public final void setParent(AbstractGuiWidget<?> parent) {
		this.parent = parent;
		addedToParent(parent);
	}

	/**
	 * This method is raised when this widget is added to a parent.
	 * 
	 * @param gui
	 */
	protected void addedToParent(@Nullable AbstractGuiWidget<?> parent) {

	}

	/**
	 * This method is raised when this widget is removed from a parent.
	 * 
	 * @param gui
	 */
	public void removedFromParent(@Nullable AbstractGuiWidget<?> parent) {

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
		onPositionChanged(position);
		return (T) this;
	}

	public T setXPosition(float xPos) {
		position.setX(xPos);
		onPositionChanged(position);
		return (T) this;
	}

	public T setYPosition(float yPos) {
		position.setY(yPos);
		onPositionChanged(position);
		return (T) this;
	}

	public float getXPosition() {
		return position.getX();
	}

	public float getYPosition() {
		return position.getY();
	}

	protected void onPositionChanged(Vector2D newPosition) {

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
	 * Gets the clip type of the parent.
	 * 
	 * @return
	 */
	public WidgetClipType getClipType() {
		return this.clipType;
	}

	/**
	 * Sets the clip type of the parent.
	 * 
	 * @param type
	 */
	public void setClipType(WidgetClipType type) {
		this.clipType = type;
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
		onSizeChanged(size);
		return (T) this;
	}

	public T setWidth(float width) {
		size.setX(width);
		onSizeChanged(size);
		return (T) this;
	}

	public T setHeight(float height) {
		size.setY(height);
		onSizeChanged(size);
		return (T) this;
	}

	protected void onSizeChanged(Vector2D newSize) {

	}

	/**
	 * Gets the size of this widget.
	 * 
	 * @return The size of the widget.
	 */
	public Vector2D getSize() {
		return size;
	}

	public float getWidth() {
		return size.getX();
	}

	public float getHeight() {
		return size.getY();
	}

	public AbstractGuiWidget<?> getParent() {
		return this.parent;
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
	 * Returns the time this widget was hovered dialated by the provided scale and
	 * clamped in the range [0, 1]. For example, a scale of 3 means that over three
	 * seconds of being hovered, this value will drive from 0 to 1 and then stay at
	 * 1 until unhovered.
	 * 
	 * @param scale
	 * @return
	 */
	public float getTimeHoveredScaledClamped(float scale) {
		return Math.min(1, ticksHovered / scale);
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
	public void tick() {
		// Tick all the widgets.
		for (AbstractGuiWidget<?> widget : this.getChildren()) {
			widget.tick();
		}
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
	public final void updateBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY, RectangleBounds parentBounds) {
		this.parentSize = parentSize;
		lastMousePosition = new Vector2D(mouseX, mouseY);

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrixStack, getPosition());
		Vector3D scale = GuiDrawUtilities.getScaleFromMatrix(matrixStack);

		matrixStack.pushPose();
		transformPoseBeforeRender(matrixStack);

		RectangleBounds clip = getClipBounds(matrixStack).multiply((float) getMinecraft().getWindow().getGuiScale());
		if (shouldDrawChildren()) {
			for (int i = 0; i < widgets.size(); i++) {
				AbstractGuiWidget<?> widget = widgets.get(i);
				matrixStack.pushPose();
				transformPoseBeforeChildRender(matrixStack, widget, i);
				widget.updateBeforeRender(matrixStack, parentSize, partialTicks, mouseX, mouseY, parentBounds);
				matrixStack.popPose();
			}
		}

		updateWidgetBeforeRender(matrixStack, parentSize, partialTicks, mouseX, mouseY);
		shouldRenderThisFrame = shouldRender(matrixStack);
		matrixStack.popPose();

		// Make a NEW matrix that translates from local space to screen space. We make a
		// new matrix so that it's owned by this widget, and not modified by any
		// external references.
		lastMatrixStack = new PoseStack();
		lastMatrixStack.translate(screenSpacePosition.getX() - getPosition().getX(), screenSpacePosition.getY() - getPosition().getY(), 0);

		cachedBounds.update(screenSpacePosition.getX(), screenSpacePosition.getY(), this.size.getX() * scale.getX(), this.size.getY() * scale.getY());

		if (isVisible() && isEnabled()) {
			if (isHovered()) {
				ticksHovered += partialTicks;
			} else {
				ticksHovered = 0;
			}
		}
	}

	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {

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
	public final void renderBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks, RectangleBounds parentBounds) {
		if (shouldRenderThisFrame) {
			matrix.pushPose();
			transformPoseBeforeRender(matrix);

			// Apply the clip if it was requested by this widget.
			RectangleBounds clip = getClipBounds(matrix).multiply((float) getMinecraft().getWindow().getGuiScale());
			if (getClipType() == WidgetClipType.CLIP) {
				RenderingUtilities.applyScissorMask(clip);
			}

			if (shouldDrawChildren()) {
				// Render the foreground of all the widgets.
				for (int i = 0; i < widgets.size(); i++) {
					AbstractGuiWidget<?> widget = widgets.get(i);
					if (widget.isVisible() && shouldRenderChild(matrix, widget, i)) {
						matrix.pushPose();
						transformPoseBeforeChildRender(matrix, widget, i);
						widget.renderBackground(matrix, mouseX, mouseY, partialTicks, parentBounds);
						matrix.popPose();
					}

//					if (isTopLevel) {
//						RenderingUtilities.clearScissorMask();
//					}
				}
			}
			renderWidgetBackground(matrix, mouseX, mouseY, partialTicks);
			matrix.popPose();
			if (getClipType() == WidgetClipType.CLIP) {
				RenderingUtilities.clearScissorMask();
			}
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
	public final void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks, RectangleBounds parentBounds) {
		if (shouldRenderThisFrame) {
			matrix.pushPose();
			transformPoseBeforeRender(matrix);

			// Apply the clip if it was requested by this widget.
			RectangleBounds clip = getClipBounds(matrix).multiply((float) getMinecraft().getWindow().getGuiScale());
			if (getClipType() == WidgetClipType.CLIP) {
				RenderingUtilities.applyScissorMask(clip);
			}

			renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);

			if (shouldDrawChildren()) {
				// Render the foreground of all the widgets.
				for (int i = 0; i < widgets.size(); i++) {
					AbstractGuiWidget<?> widget = widgets.get(i);
					if (widget.isVisible() && shouldRenderChild(matrix, widget, i)) {
						matrix.pushPose();
						transformPoseBeforeChildRender(matrix, widget, i);
						widget.renderBehindItems(matrix, mouseX, mouseY, partialTicks, parentBounds);
						matrix.popPose();
					}

//					if (isTopLevel) {
//						RenderingUtilities.clearScissorMask();
//					}
				}
			}
			matrix.popPose();
			if (getClipType() == WidgetClipType.CLIP) {
				RenderingUtilities.clearScissorMask();
			}
		}
	}

	public final void renderForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks, RectangleBounds parentBounds) {
		if (shouldRenderThisFrame) {
			matrix.pushPose();
			transformPoseBeforeRender(matrix);

			// Apply the clip if it was requested by this widget.
			RectangleBounds clip = getClipBounds(matrix).multiply((float) getMinecraft().getWindow().getGuiScale());
			if (getClipType() == WidgetClipType.CLIP) {
				RenderingUtilities.applyScissorMask(clip);
			}

			if (shouldDrawChildren()) {
				// Render the foreground of all the widgets.
				for (int i = 0; i < widgets.size(); i++) {
					AbstractGuiWidget<?> widget = widgets.get(i);
					if (widget.isVisible() && shouldRenderChild(matrix, widget, i)) {
						matrix.pushPose();
						transformPoseBeforeChildRender(matrix, widget, i);
						widget.renderForeground(matrix, mouseX, mouseY, partialTicks, parentBounds);
						matrix.popPose();
					}

//					if (isTopLevel) {
//						RenderingUtilities.clearScissorMask();
//					}
				}
			}
			renderWidgetForeground(matrix, mouseX, mouseY, partialTicks);
			matrix.popPose();
			if (getClipType() == WidgetClipType.CLIP) {
				RenderingUtilities.clearScissorMask();
			}
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
	public void transformPoseBeforeRender(PoseStack matrix) {
		matrix.translate(getPosition().getX(), getPosition().getY(), zLevel);
	}

	protected void transformPoseBeforeChildRender(PoseStack matrix, AbstractGuiWidget<?> child, int index) {

	}

	public RectangleBounds getClipBounds(PoseStack matrix) {
		RectangleBounds output = getBounds().copy();
		output.setY(Minecraft.getInstance().getWindow().getGuiScaledHeight() - getBounds().getY() - getSize().getY());
		return output;
	}

	protected final boolean shouldRender(PoseStack matrix) {
		return true && shouldRenderWidget(matrix);
	}

	protected boolean shouldRenderWidget(PoseStack matrix) {
		return true;
	}

	protected boolean shouldRenderChild(PoseStack matrix, AbstractGuiWidget<?> widget, int index) {
		if (clipType == WidgetClipType.CLIP) {
			Vector2D childMin = widget.getScreenSpacePosition();
			Vector2D childMax = childMin.copy().add(widget.getSize());
			Vector2D parentMin = getScreenSpacePosition();
			Vector2D parentMax = parentMin.copy().add(getSize());

			if (childMax.getY() < parentMin.getY() || childMin.getY() > parentMax.getY()) {
				// System.out.println("Parent: " + childMin + " Child: " + parentMax);
				return false;
			}
			if (childMax.getX() < parentMin.getX() || childMin.getX() > parentMax.getX()) {
				// System.out.println("Parent: " + parentMin + " Child: " + parentMax);
				return false;
			}
		}

		return true;
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
		widgets.add(widget);
		widget.setParent(this);
		widget.addedToParent(parent);
		onWidgetAdded(widget);
	}

	protected void onWidgetAdded(AbstractGuiWidget<?> widget) {

	}

	public boolean removeWidget(@SuppressWarnings("rawtypes") AbstractGuiWidget widget) {
		widget.removedFromParent(parent);
		if (widgets.remove(widget)) {
			onWidgetRemoved(widget);
			return true;
		}
		return false;
	}

	protected void onWidgetRemoved(AbstractGuiWidget<?> widget) {

	}

	public void clearChildren() {
		for (AbstractGuiWidget<?> widget : widgets) {
			widget.removedFromParent(parent);
		}
		widgets.clear();
	}

	/* Tooltip */
	@SuppressWarnings("resource")
	public final void renderTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
		// Capture all the tooltips for all the widgets. Skip any invisible widgets or
		// widgets that are not hovered.
		Vector2D mousePosition = new Vector2D(mouseX, mouseY);
		List<Component> tooltips = new ArrayList<Component>();
		getTooltips(mousePosition, tooltips, Screen.hasShiftDown());
		// If there are any tooltips to render, render them.
		if (tooltips.size() > 0) {
			// Format them and then draw them.
			if (Minecraft.getInstance().screen != null) {
				Minecraft.getInstance().screen.renderComponentTooltip(matrixStack, tooltips, mouseX, mouseY);
			}
		}
	}

	public final void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		if (shouldDrawChildren()) {
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible() && !widget.getTooltipsDisabled() && widget.isHovered()) {
					widget.getTooltips(mousePosition, tooltips, showAdvanced);
				}
			}
		}
		getWidgetTooltips(mousePosition, tooltips, showAdvanced);
	}

	protected void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {

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
		return widgets;
	}

	protected boolean shouldDrawChildren() {
		return true;
	}

	/* Input Events */
	public EInputResult mouseClick(double mouseX, double mouseY, int button) {
		if (shouldDrawChildren()) {
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible()) {
					if (widget.mouseClick((int) mouseX, (int) mouseY, button) == EInputResult.HANDLED) {
						return EInputResult.HANDLED;
					}
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseReleased(double mouseX, double mouseY, int button) {
		if (shouldDrawChildren()) {
			// Raise the mouse hovered event for all the widgets,
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible()) {
					if (widget.mouseReleased((int) mouseX, (int) mouseY, button) == EInputResult.HANDLED) {
						return EInputResult.HANDLED;
					}
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseMove(double mouseX, double mouseY) {
		if (shouldDrawChildren()) {
			// Raise the mouse hovered event for all the widgets,
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible()) {
					if (widget.mouseMove((int) mouseX, (int) mouseY) == EInputResult.HANDLED) {
						return EInputResult.HANDLED;
					}
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (shouldDrawChildren()) {
			// Raise the mouse scrolled event for all the widgets,
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible()) {
					if (widget.mouseScrolled(mouseX, mouseY, scrollDelta) == EInputResult.HANDLED) {
						return EInputResult.HANDLED;
					}
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (shouldDrawChildren()) {
			// Raise the mouse scrolled event for all the widgets,
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible()) {
					if (widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) == EInputResult.HANDLED) {
						return EInputResult.HANDLED;
					}
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult characterTyped(char character, int p_charTyped_2_) {
		if (shouldDrawChildren()) {
			// Raise the character typed event for all the widgets,
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible()) {
					if (widget.characterTyped(character, p_charTyped_2_) == EInputResult.HANDLED) {
						return EInputResult.HANDLED;
					}
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public EInputResult keyPressed(int key, int scanCode, int modifiers) {
		if (shouldDrawChildren()) {
			// Raise the key presed event for all the widgets,
			for (AbstractGuiWidget<?> widget : widgets) {
				if (widget.isVisible()) {
					if (widget.keyPressed(key, scanCode, modifiers) == EInputResult.HANDLED) {
						return EInputResult.HANDLED;
					}
				}
			}
		}
		return EInputResult.UNHANDLED;
	}

	public boolean shouldRespondToInput() {
		return shouldRenderThisFrame && isVisible() && isEnabled();
	}

	public enum WidgetClipType {
		CLIP, FREE
	}
}
