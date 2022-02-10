package theking530.staticcore.gui.widgets.tabs;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.WidgetContainer;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.StaticVertexBuffer;
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
public abstract class BaseGuiTab {
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
	protected int tabWidth;
	protected int tabHeight;
	protected int xPosition;
	protected int yPosition;
	protected int guiXOffset;
	protected int guiYOffset;
	protected int titleColor;
	protected RectangleBounds cachedIconBounds;
	protected RectangleBounds cachedTabBounds;
	protected final WidgetContainer widgetContainer;
	protected final Font fontRenderer;

	protected IDrawable icon;

	protected float currentWidth;
	protected float currentHeight;
	private int tabIndex;
	private float animationTimer = 0;
	private float animationTime = 4.0f;
	private ResourceLocation tabTexture;
	private TabState tabState;
	private TabSide tabSide;
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
	public BaseGuiTab(String title, Color titleColor, int tabWidth, int tabHeight, ResourceLocation texture,
			IDrawable icon) {
		this.tabWidth = tabWidth;
		this.tabHeight = tabHeight;
		this.icon = icon;
		this.title = title;
		this.titleColor = titleColor.encodeInInteger();
		cachedIconBounds = new RectangleBounds(0.0f, 0.0f, 0.0f, 0.0f); // Must be initially set to 0.
		cachedTabBounds = new RectangleBounds(0.0f, 0.0f, 0.0f, 0.0f); // Must be initially set to 0.
		tabTexture = texture;
		tabState = TabState.CLOSED;
		tabSide = TabSide.RIGHT;
		widgetContainer = new WidgetContainer();
		fontRenderer = Minecraft.getInstance().font;
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
	public BaseGuiTab(String title, Color titleColor, int tabWidth, int tabHeight, ResourceLocation texture,
			Item item) {
		this(title, titleColor, tabWidth, tabHeight, texture, new ItemDrawable(item));
	}

	/**
	 * Creates a {@link BaseGuiTab}.
	 * 
	 * @param tabWidth  The width of the tab.
	 * @param tabHeight The height of the tab.
	 * @param texture   The background texture of the tab.
	 * @param block     The block that should render as the icon for the tab.
	 */
	public BaseGuiTab(String title, Color titleColor, int tabWidth, int tabHeight, ResourceLocation texture,
			Block block) {
		this(title, titleColor, tabWidth, tabHeight, texture, new ItemDrawable(block));
	}

	/**
	 * Updates this tab's position data in the case where a tab above it may be
	 * opening or closing.
	 * 
	 * @param tabXPosition The new x position.
	 * @param tabYPosition The new y position.
	 * @param partialTicks The partial ticks (delta time).
	 */
	public void updateTabPosition(PoseStack stack, int tabXPosition, int tabYPosition, float partialTicks, int mouseX,
			int mouseY, int index) {
		updateAnimation(partialTicks);

		tabIndex = index;
		this.xPosition = tabXPosition;
		this.yPosition = tabYPosition;

		widgetContainer.update(stack, new Vector2D(0, 0), new Vector2D(currentWidth, currentHeight), partialTicks,
				mouseX, mouseY);
		if (!initialPositionSet) {
			initialPositionSet = true;
			initialized(xPosition, yPosition);
		}

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(stack, new Vector2D(0, 0));
		if (this.tabSide == TabSide.LEFT) {
			cachedIconBounds.update(screenSpacePosition.getX() + this.tabWidth, screenSpacePosition.getY(), 24, 24);
			cachedTabBounds.update(screenSpacePosition.getX() + (tabWidth - currentWidth), screenSpacePosition.getY(),
					currentWidth + 24, currentHeight + 24);
		} else {
			cachedIconBounds.update(screenSpacePosition.getX(), screenSpacePosition.getY(), 24, 24);
			cachedTabBounds.update(screenSpacePosition.getX(), screenSpacePosition.getY(), currentWidth + 24,
					currentHeight + 24);
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
	public EInputResult mouseClick(PoseStack matrixStack, int mouseX, int mouseY, int button) {
		if (!Minecraft.getInstance().player.containerMenu.getCarried().isEmpty()) { // TO-DO: Check if this works.
			return EInputResult.UNHANDLED;
		}

		// Check if we clicked on the icon bounds.
		if (getIconBounds().isPointInBounds(new Vector2D(mouseX, mouseY))) {
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
			if (tabState == TabState.OPEN
					&& widgetContainer.handleMouseClick(mouseX, mouseY, button) == EInputResult.HANDLED) {
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
	public void mouseHover(int x, int y) {
		if (tabState == TabState.OPEN) {
			widgetContainer.handleMouseMove(x, y);
		}
	}

	/**
	 * Handles keyboard interation.
	 * 
	 * @param par1
	 * @param par2
	 */
	public void keyboardInteraction(char par1, int par2) {
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
	 * Renders any tooltips that should be rendered.
	 * 
	 * @param mousePosition
	 * @param tooltips
	 * @param showAdvanced
	 */
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		widgetContainer.getTooltips(mousePosition, tooltips, showAdvanced);
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
	 * This value indicates how far down the tab is on the particular that it is
	 * being rendered on. The higher the number, the lower it is.
	 * 
	 * @return
	 */
	public int getTabIndex() {
		return tabIndex;
	}

	public void updateBeforeRender(PoseStack matrixStack, Vector2D ownerSize, float partialTicks, int mouseX,
			int mouseY) {

	}

	/**
	 * Override this method to draw any tab contents that should appear BEHIND
	 * items.
	 * 
	 * @param matrix       TODO
	 * @param mouseX       The x position of the mouse.
	 * @param mouseY       The y position of the mouse.
	 * @param partialTicks The partial ticks (delta time).
	 */
	protected void renderBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		widgetContainer.renderBackground(matrix, mouseX, mouseY, partialTicks);
		if (isOpen()) {
			if (drawTitle) {
				drawTitle(matrix);
			}
		}
	}

	protected void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		widgetContainer.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
	}

	/**
	 * Override this method to draw any tab contents that should appear INFRONT
	 * items.
	 * 
	 * @param matrix       TODO
	 * @param partialTicks The partial ticks (delta time).
	 * @param xPos         The x position of the mouse.
	 * @param yPos         The y position of the mouse.
	 */
	protected void renderForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

	}

	/**
	 * This method is called every TICK (20 times a second), and can be used to
	 * update any data in the UI that does not need to be updated per frame (for
	 * example, the text in an info tab.)
	 */
	protected void updateData() {

	}

	/**
	 * Updates the animation state of this tab.
	 * 
	 * @param partialTicks
	 */
	private void updateAnimation(float partialTicks) {
		if (tabState == TabState.OPENING && animationTimer < animationTime) {
			animationTimer = Math.min(animationTime, animationTimer + partialTicks * 1.75f);
			if (animationTimer == animationTime) {
				tabState = TabState.OPEN;
				onTabOpened();
			}
		} else if (tabState == TabState.CLOSING && animationTimer > 0) {
			animationTimer = Math.max(0, animationTimer - partialTicks * 2);
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
	}

	/**
	 * Draws the base tab (the background and the tab icon).
	 * 
	 * @param xPos
	 * @param yPos
	 * @param partialTicks
	 */
	public void drawTabPanel(PoseStack stack, float partialTicks) {
		drawTabBackground(stack, partialTicks);
		drawButtonIcon(stack, partialTicks);
	}

	/**
	 * Draws the tab's button icon.
	 * 
	 * @param xPos
	 * @param yPos
	 * @param partialTicks
	 */
	protected void drawButtonIcon(PoseStack stack, float partialTicks) {
		Vector2D position = GuiDrawUtilities.translatePositionByMatrix(stack, 0, 0);
		// GlStateManager._disableDepthTest();
		if (icon != null) {
			icon.draw(getTabSide() == TabSide.RIGHT ? position.getX() + 3 : position.getX() + tabWidth + 4.0f,
					position.getY() + 4, tabIndex);
			if (showNotificationBadge && tabState == TabState.CLOSED) {
				notifictionBadge.draw(
						getTabSide() == TabSide.RIGHT ? position.getX() + 17 : position.getX() + tabWidth - 4.0f,
						position.getY() - 2.0f, tabIndex);
			}
		}
		// GlStateManager._enableDepthTest();
	}

	/**
	 * Draws the title of the tab.
	 * 
	 * @param stack
	 */
	protected void drawTitle(PoseStack stack) {
		// Draw title.
		fontRenderer.drawShadow(stack, getTitle(), (getTabSide() == TabSide.LEFT ? 11 : 24), 8, titleColor);
	}

	/**
	 * Draws the tab background (the empty panel that opens when the tab is clicked.
	 * 
	 * @param xPos
	 * @param yPos
	 * @param partialTicks
	 */
	protected void drawTabBackground(PoseStack stack, float partialTicks) {
		float xPixel = 1.0f / 130.0f;
		float yPixel = 1.0f / 83.0f;
		float zLevel = tabIndex + 100;

		Vector2D position = GuiDrawUtilities.translatePositionByMatrix(stack, 0, 0);
		int tabLeft = position.getXi() - (getTabSide() == TabSide.RIGHT ? 0 : tabWidth + 23);
		int tabTop = position.getYi();

		currentWidth = ((tabWidth * animationTimer / animationTime));
		currentHeight = ((tabHeight * animationTimer / animationTime));

		stack = RenderSystem.getModelViewStack();
		stack.pushPose();

		if (getTabSide() == TabSide.LEFT) {
			stack.translate(position.getXi(), position.getYi(), 0.0f);
			stack.scale(-1.001f, 1.0f, -1.0f);
			stack.translate(-position.getXi(), -position.getYi(), 0.0f);
		}
		
		// TO-DO: Redesign rendering system to use the incoming matrix.
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, tabTexture);
		RenderSystem.disableCull();
		
		StaticVertexBuffer buff = StaticVertexBuffer.quads(DefaultVertexFormat.POSITION_TEX);
		
		// Top
		buff.pos(tabLeft + 1, tabTop, zLevel, 0.0f, 0.0f);
		buff.pos(tabLeft + 1, tabTop + 3, zLevel, 0.0f, yPixel * 4.0f);
		buff.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop + 3, zLevel, 1.0f - xPixel * 4.0f,
				yPixel * 4.0f);
		buff.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop, zLevel, 1.0f - xPixel * 4.0f,
				0.0f);

		// Bottom
		buff.pos(tabLeft + 1, tabTop + 24 + (tabHeight * animationTimer / animationTime), zLevel, 0.0f, 1.0f);
		buff.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime),
				tabTop + 24 + (tabHeight * animationTimer / animationTime), zLevel, 1.0f - xPixel * 3.0f, 1.0f);
		buff.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime),
				tabTop + 21 + (tabHeight * animationTimer / animationTime), zLevel, 1.0f - xPixel * 3.0f,
				1.0f - yPixel * 4.0f);
		buff.pos(tabLeft + 1, tabTop + 21 + (tabHeight * animationTimer / animationTime), zLevel, 0.0f,
				1.0f - yPixel * 4.0f);

		// Right Side
		buff.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime),
				tabTop + 21.4 + (tabHeight * animationTimer / animationTime), zLevel, 1.0f, yPixel * 4.0f);
		buff.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop + 4, zLevel, 1.0f,
				1.0f - yPixel * 4.0f);
		buff.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 4, zLevel, 1.0f - xPixel * 3.0f,
				1.0f - yPixel * 4.0f);
		buff.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime),
				tabTop + 21.4 + (tabHeight * animationTimer / animationTime), zLevel, 1.0f - xPixel * 3.0f,
				yPixel * 4.0f);

		// Body
		buff.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime),
				tabTop + 21 + (tabHeight * animationTimer / animationTime), zLevel, 1.0f - xPixel * 4.0f,
				yPixel * 4.0f);
		buff.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 3, zLevel, 1.0f - xPixel * 4.0f,
				1.0f - yPixel * 4.0f);
		buff.pos(tabLeft + 1, tabTop + 3, zLevel, 0, 1.0f - yPixel * 4.0f);
		buff.pos(tabLeft + 1, tabTop + 21 + (tabHeight * animationTimer / animationTime), zLevel, 0, yPixel * 4.0f);

		// Bottom Corner
		buff.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime),
				tabTop + 24 + (tabHeight * animationTimer / animationTime), zLevel, 1, 1);
		buff.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime),
				tabTop + 20 + (tabHeight * animationTimer / animationTime), zLevel, 1, 1.0f - yPixel * 5.0f);
		buff.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime),
				tabTop + 20 + (tabHeight * animationTimer / animationTime), zLevel, 1.0f - xPixel * 4.0f,
				1.0f - yPixel * 5.0f);
		buff.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime),
				tabTop + 24 + (tabHeight * animationTimer / animationTime), zLevel, 1.0f - xPixel * 4.0f, 1);

		// Top Corner
		buff.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop + 4, zLevel, 1.0f, yPixel * 5.0f);
		buff.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop, zLevel, 1.0f, 0);
		buff.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop, zLevel, 1.0f - xPixel * 4.0f, 0);
		buff.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop + 4, zLevel, 1.0f - xPixel * 4.0f,
				yPixel * 5.0f);

		buff.end();
		stack.popPose();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}

	protected void initialized(int tabXPosition, int tabYPosition) {

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
	public RectangleBounds getBounds() {
		return cachedTabBounds;
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
