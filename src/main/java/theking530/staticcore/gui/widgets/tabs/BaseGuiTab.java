package theking530.staticcore.gui.widgets.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.GuiTextures;

/**
 * Base class for any GUI tabs.
 * 
 * @author Amine Sebastian
 *
 */
@OnlyIn(Dist.CLIENT)
public abstract class BaseGuiTab extends AbstractGuiWidget<BaseGuiTab> {
	private static Vector2D COLLAPSED_SIZE = new Vector2D(26, 26);

	/**
	 * Data type for the animation state of the tab.
	 * 
	 * @author Amine Sebastian
	 *
	 */
	public enum TabState {
		CLOSED, OPENING, OPEN, CLOSING; // The order that these are defined in is very important, do not reorder.

		/**
		 * Helper method for incrementing the current animation state of the tab.
		 * Returns a new {@link TabState} that is one state further along than the
		 * current state.
		 * 
		 * @param curr The current tab state.
		 * @return
		 */
		public TabState incrementState() {
			int newIndex = ordinal() + 1;
			newIndex = newIndex % 3;
			return TabState.values()[newIndex];
		}
	}

	/**
	 * Data type to indicate which side of the UI the tab should render on.
	 * 
	 * @author Amine Sebastian
	 *
	 */
	public enum TabSide {
		LEFT, RIGHT;
	}

	protected String title;
	protected Color titleColor;
	protected RectangleBounds cachedIconBounds;
	protected Color tabColor;
	protected IDrawable icon;
	private int tabIndex;
	private float animationTimer = 0;
	private float animationTime = 1.0f;
	private TabState tabState;
	private TabSide tabSide;
	private Vector2D expandedSize;
	private boolean initialPositionSet;
	protected boolean showNotificationBadge;
	protected boolean drawTitle;
	protected SpriteDrawable notifictionBadge;
	protected GuiTabManager owningManager;

	/**
	 * Creates a {@link BaseGuiTab}.
	 * 
	 * @param tabWidth  The width of the tab.
	 * @param tabHeight The height of the tab.
	 * @param texture   The background texture of the tab.
	 * @param item      The item that should render as the icon for the tab.
	 */
	public BaseGuiTab(String title, Color titleColor, int tabWidth, int tabHeight, Color tabColor, IDrawable icon) {
		super(0, 0, COLLAPSED_SIZE.getX(), COLLAPSED_SIZE.getY());
		this.expandedSize = new Vector2D(tabWidth, tabHeight);
		this.tabColor = tabColor;
		this.icon = icon;
		this.title = new TranslatableComponent(title).getString();
		this.titleColor = titleColor;
		cachedIconBounds = new RectangleBounds(0.0f, 0.0f, 0.0f, 0.0f); // Must be initially set to 0.
		tabState = TabState.CLOSED;
		tabSide = TabSide.RIGHT;
		initialPositionSet = false;
		notifictionBadge = new SpriteDrawable(StaticPowerSprites.NOTIFICATION, 9, 9);
		showNotificationBadge = false;
		drawTitle = true;
	}

	/**
	 * Creates a {@link BaseGuiTab}.
	 * 
	 * @param tabWidth  The width of the tab.
	 * @param tabHeight The height of the tab.
	 * @param texture   The background texture of the tab.
	 * @param item      The item that should render as the icon for the tab.
	 */
	public BaseGuiTab(String title, Color titleColor, int tabWidth, int tabHeight, Color tabColor, Item item) {
		this(title, titleColor, tabWidth, tabHeight, tabColor, new ItemDrawable(item));
	}

	/**
	 * Creates a {@link BaseGuiTab}.
	 * 
	 * @param tabWidth  The width of the tab.
	 * @param tabHeight The height of the tab.
	 * @param texture   The background texture of the tab.
	 * @param block     The block that should render as the icon for the tab.
	 */
	public BaseGuiTab(String title, Color titleColor, int tabWidth, int tabHeight, Color tabColor, Block block) {
		this(title, titleColor, tabWidth, tabHeight, tabColor, new ItemDrawable(block));
	}

	/**
	 * Updates this tab's position data in the case where a tab above it may be
	 * opening or closing.
	 * 
	 * @param tabXPosition The new x position.
	 * @param tabYPosition The new y position.
	 * @param partialTicks The partial ticks (delta time).
	 */
	public void updateTabPosition(PoseStack stack, float tabXPosition, float tabYPosition, float partialTicks, int mouseX, int mouseY, int index) {
		tabIndex = index;
		this.setPosition(tabXPosition, tabYPosition);

		// widgetContainer.update(stack, new Vector2D(currentWidth, currentHeight),
		// partialTicks, mouseX, mouseY);
		if (!initialPositionSet) {
			initialPositionSet = true;
			initialized();
		}

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(stack, new Vector2D(0, 0));
		if (this.tabSide == TabSide.LEFT) {
			cachedIconBounds.update(screenSpacePosition.getX() + getWidth() - COLLAPSED_SIZE.getX(), screenSpacePosition.getY(), 24, 24);
		} else {
			cachedIconBounds.update(screenSpacePosition.getX(), screenSpacePosition.getY(), 24, 24);
		}
	}

	/**
	 * This event is raised when the mouse is clicked.
	 * 
	 * @param mouseX The mouse x position.
	 * @param mouseY The mouse y position.
	 * @param button The button that was clicked.
	 * @return True if the event was handled, false otherwise.
	 */
	@SuppressWarnings("resource")
	@Override
	public EInputResult mouseClick(double mouseX, double mouseY, int button) {
		if (!Minecraft.getInstance().player.containerMenu.getCarried().isEmpty()) { // TODO: Check if this works.
			return EInputResult.UNHANDLED;
		}

		// Check if we clicked on the icon bounds.
		if (getIconBounds().isPointInBounds(new Vector2D((float) mouseX, (float) mouseY))) {
			if (tabState == TabState.CLOSED) {
				tabState = TabState.OPENING;
				owningManager.tabOpening(this);
				onTabOpening();
				return EInputResult.HANDLED;
			} else if (tabState == TabState.OPEN) {
				tabState = TabState.CLOSING;
				owningManager.tabClosing(this);
				onTabClosing();
				return EInputResult.HANDLED;
			}
		} else {
			if (tabState == TabState.OPEN && super.mouseClick(mouseX, mouseY, button) == EInputResult.HANDLED) {
				return EInputResult.HANDLED;
			}
		}
		return EInputResult.UNHANDLED;
	}

	/**
	 * This even is raised when the mouse moves.
	 * 
	 * @param x The x position of the mouse.
	 * @param y The y position of the mouse.
	 */
	@Override
	public EInputResult mouseMove(double mouseX, double mouseY) {
		if (tabState == TabState.OPEN) {
			return super.mouseMove(mouseX, mouseY);
		}
		return EInputResult.UNHANDLED;
	}

	@Override
	public EInputResult mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (tabState == TabState.OPEN) {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		return EInputResult.UNHANDLED;
	}

	protected void drawDarkBackground(PoseStack stack, int xPos, int yPos, int width, int height) {
		Vector2D position = GuiDrawUtilities.translatePositionByMatrix(stack, xPos, yPos);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiTextures.BUTTON_BG);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuilder();
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(position.getX(), position.getY(), 0).uv(0, 1).endVertex();
		vertexbuffer.vertex(position.getX(), position.getY() + height, 0).uv(0, 0).endVertex();
		vertexbuffer.vertex(position.getX() + width, position.getY() + height, 0).uv(1, 0).endVertex();
		vertexbuffer.vertex(position.getX() + width, position.getY(), 0).uv(1, 1).endVertex();
		tessellator.end();
		RenderSystem.disableBlend();
	}

	@Override
	protected boolean shouldDrawChildren() {
		return isOpen();
	}

	@Override
	public void transformPoseBeforeRender(PoseStack matrix) {
		super.transformPoseBeforeRender(matrix);
		// matrix.translate(1, 0, 0);
	}

	@Override
	protected void renderWidgetBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		updateAnimation(partialTicks);
		float width = getWidth() + 16;
		float xPos = 0;
		if (tabSide == TabSide.RIGHT) {
			xPos -= 16;
		}
		GuiDrawUtilities.drawGenericBackground(matrix, width, getHeight(), xPos, 0, -10 * tabIndex, tabColor);

		if (icon != null) {
			if (this.tabSide == TabSide.LEFT) {
				icon.draw(getParent().getLastRenderMatrix(), -18.5f, this.getPosition().getY() + 4, -tabIndex * 5);
			} else {
				icon.draw(matrix, 4f, 5f, -tabIndex * 5);
			}

			if (showNotificationBadge && tabState == TabState.CLOSED) {
				notifictionBadge.draw(matrix, getTabSide() == TabSide.RIGHT ? 17 : -2, -2.0f, tabIndex);
			}
		}

		if (isOpen()) {
			if (drawTitle) {
				// Draw title.
				if (this.tabSide == TabSide.LEFT) {
					GuiDrawUtilities.drawStringLeftAligned(matrix, getTitle(), 8, 15, 200, 1.0f, titleColor, true);
				} else {
					GuiDrawUtilities.drawStringLeftAligned(matrix, getTitle(), 22, 16, 200, 1.0f, titleColor, true);
				}
			}
		}
	}

	/**
	 * Returns true if this tab is open.
	 * 
	 * @return
	 */
	public boolean isOpen() {
		return tabState == TabState.OPEN;
	}

	/**
	 * Returns true if this tab is closed.
	 * 
	 * @return
	 */
	public boolean isClosed() {
		return tabState == TabState.CLOSED;
	}

	/**
	 * Returns the {@link TabState} of this tab.
	 * 
	 * @return
	 */
	public TabState getTabState() {
		return tabState;
	}

	/**
	 * Returns the {@link TabSide} of this tab.
	 * 
	 * @return
	 */
	public TabSide getTabSide() {
		return tabSide;
	}

	/**
	 * Gets the title of this tab.
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Updates the side that this tab renders on.
	 * 
	 * @param newSide The new side that the tab should render on.
	 * @return
	 */
	public BaseGuiTab setTabSide(TabSide newSide) {
		this.tabSide = newSide;
		return this;
	}

	/**
	 * Updates the {@link TabState} of this tab. This should only be called by the
	 * {@link GuiTabManager} that own's this tab.
	 * 
	 * @param newState The new state of the tab.
	 * @return
	 */
	public boolean setTabState(TabState newState) {
		if (newState == TabState.CLOSED || newState == TabState.CLOSING) {
			if (getTabState() == TabState.OPEN) {
				tabState = TabState.CLOSING;
				onTabClosing();
				return true;
			}
		} else if (newState == TabState.OPEN || newState == TabState.OPENING) {
			if (getTabState() == TabState.CLOSED) {
				tabState = TabState.OPENING;
				onTabOpening();
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the manager of this tab.
	 * 
	 * @param manager The owning mangager.
	 */
	public void setManager(GuiTabManager manager) {
		owningManager = manager;
	}

	/**
	 * This value indicates how far down the tab is on the particular side that it
	 * is being rendered on. The higher the number, the lower it is.
	 * 
	 * @return
	 */
	public int getTabIndex() {
		return tabIndex;
	}

	public Vector2D getExpandedSize() {
		return expandedSize;
	}

	public void setExpandedSize(float width, float height) {
		this.expandedSize.setX(width);
		this.expandedSize.setY(height);
	}

	public void setExpandedHeight(float height) {
		this.expandedSize.setY(height);
	}

	public void setExpandedWidth(float width) {
		this.expandedSize.setX(width);
	}

	/**
	 * Updates the animation state of this tab.
	 * 
	 * @param partialTicks
	 */
	private void updateAnimation(float partialTicks) {
		if (tabState == TabState.OPENING && animationTimer < animationTime) {
			animationTimer = Math.min(animationTime, animationTimer + partialTicks * 0.45f);
			if (animationTimer == animationTime) {
				tabState = TabState.OPEN;
				onTabOpened();
			}
		} else if (tabState == TabState.CLOSING && animationTimer > 0) {
			animationTimer = Math.max(0, animationTimer - partialTicks * 0.75f);
			if (animationTimer == 0) {
				tabState = TabState.CLOSED;
				onTabClosed();
			}
		} else if (tabState == TabState.CLOSING && animationTimer <= 0) {
			tabState = TabState.CLOSED;
			owningManager.tabClosed(this);
			onTabClosed();
		} else if (tabState == TabState.OPENING && animationTimer >= animationTime) {
			tabState = TabState.OPEN;
			owningManager.tabOpened(this);
			onTabOpened();
		}

		setWidth(SDMath.lerp(COLLAPSED_SIZE.getX(), expandedSize.getX(), animationTimer / animationTime));
		setHeight(SDMath.lerp(COLLAPSED_SIZE.getY(), expandedSize.getY(), animationTimer / animationTime));
	}

	protected void initialized() {

	}

	protected void onTabOpened() {

	}

	protected void onTabClosed() {

	}

	protected void onTabOpening() {

	}

	protected void onTabClosing() {

	}

	/**
	 * Gets the bounds of this tab.
	 * 
	 * @return
	 */
	public RectangleBounds getIconBounds() {
		return cachedIconBounds;
	}
}
