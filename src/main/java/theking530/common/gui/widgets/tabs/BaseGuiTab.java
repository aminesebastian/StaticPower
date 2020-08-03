package theking530.common.gui.widgets.tabs;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.WidgetContainer;
import theking530.common.gui.drawables.IDrawable;
import theking530.common.gui.drawables.ItemDrawable;
import theking530.common.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.common.utilities.RectangleBounds;
import theking530.common.utilities.StaticVertexBuffer;
import theking530.common.utilities.Vector2D;

/**
 * Base class for any GUI tabs.
 * 
 * @author Amine Sebastian
 *
 */
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

	protected int tabWidth;
	protected int tabHeight;
	protected int xPosition;
	protected int yPosition;
	protected int guiXOffset;
	protected int guiYOffset;
	protected final WidgetContainer widgetContainer;
	protected final FontRenderer fontRenderer;

	private float currentWidth;
	private float currentHeight;

	private float animationTimer = 0;
	private float animationTime = 4.0f;
	private IDrawable icon;
	private ResourceLocation tabTexture;
	private TabState tabState;
	private TabSide tabSide;
	private boolean initialPositionSet;
	protected GuiTabManager owningManager;

	/**
	 * Creates a {@link BaseGuiTab}.
	 * 
	 * @param tabWidth  The width of the tab.
	 * @param tabHeight The height of the tab.
	 * @param texture   The background texture of the tab.
	 * @param item      The item that should render as the icon for the tab.
	 */
	public BaseGuiTab(int tabWidth, int tabHeight, ResourceLocation texture, IDrawable icon) {
		this.tabWidth = tabWidth;
		this.tabHeight = tabHeight;
		this.icon = icon;
		tabTexture = texture;
		tabState = TabState.CLOSED;
		tabSide = TabSide.RIGHT;
		widgetContainer = new WidgetContainer();
		fontRenderer = Minecraft.getInstance().fontRenderer;
		initialPositionSet = false;
	}

	/**
	 * Creates a {@link BaseGuiTab}.
	 * 
	 * @param tabWidth  The width of the tab.
	 * @param tabHeight The height of the tab.
	 * @param texture   The background texture of the tab.
	 * @param item      The item that should render as the icon for the tab.
	 */
	public BaseGuiTab(int tabWidth, int tabHeight, ResourceLocation texture, Item item) {
		this(tabWidth, tabHeight, texture, new ItemDrawable(item));
	}

	/**
	 * Creates a {@link BaseGuiTab}.
	 * 
	 * @param tabWidth  The width of the tab.
	 * @param tabHeight The height of the tab.
	 * @param texture   The background texture of the tab.
	 * @param block     The block that should render as the icon for the tab.
	 */
	public BaseGuiTab(int tabWidth, int tabHeight, ResourceLocation texture, Block block) {
		this(tabWidth, tabHeight, texture, new ItemDrawable(block));
	}

	/**
	 * Updates this tab's position data in the case where a tab above it may be
	 * opening or closing.
	 * 
	 * @param tabXPosition The new x position.
	 * @param tabYPosition The new y position.
	 * @param partialTicks The partial ticks (delta time).
	 */
	public void updateTabPosition(int tabXPosition, int tabYPosition, float partialTicks, int mouseX, int mouseY) {
		updateAnimation(partialTicks);

		xPosition = getTabSide() == TabSide.RIGHT ? tabXPosition : tabXPosition - tabWidth;
		yPosition = tabYPosition;

		guiXOffset = xPosition - this.owningManager.getOwningGui().getGuiLeft();
		guiYOffset = yPosition - this.owningManager.getOwningGui().getGuiTop();

		widgetContainer.update(new Vector2D(xPosition, yPosition), new Vector2D(tabWidth, tabHeight), partialTicks, mouseX, mouseY);
		if (!initialPositionSet) {
			initialPositionSet = true;
			initialized(xPosition, yPosition);
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
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		int adjustedXPosition = tabSide == TabSide.RIGHT ? xPosition : xPosition + tabWidth;
		if (mouseX > adjustedXPosition && mouseX < adjustedXPosition + 24) {
			if (mouseY > yPosition && mouseY < yPosition + 24) {
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
			}
		} else {
			if (tabState == TabState.OPEN && widgetContainer.handleMouseClick(mouseX, mouseY, button) == EInputResult.HANDLED) {
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

	@Deprecated
	protected void drawDarkBackground(int xPos, int yPos, int width, int height) {
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_BG);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xPos, yPos, 0).tex(0, 1).endVertex();
		vertexbuffer.pos(xPos, yPos + height, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(xPos + width, yPos + height, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(xPos + width, yPos, 0).tex(1, 1).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
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
	 * Override this method to draw any tab contents that should appear BEHIND
	 * items.
	 * 
	 * @param mouseX       The x position of the mouse.
	 * @param mouseY       The y position of the mouse.
	 * @param partialTicks The partial ticks (delta time).
	 */
	protected void renderBackground(int mouseX, int mouseY, float partialTicks) {
		widgetContainer.renderBackground(mouseX, mouseY, partialTicks);
	}

	protected void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		widgetContainer.renderBehindItems(mouseX, mouseY, partialTicks);
		widgetContainer.renderTooltips(mouseX, mouseY);
	}

	/**
	 * Override this method to draw any tab contents that should appear INFRONT
	 * items.
	 * 
	 * @param xPos         The x position of the mouse.
	 * @param yPos         The y position of the mouse.
	 * @param partialTicks The partial ticks (delta time).
	 */
	protected void renderForeground(int mouseX, int mouseY, float partialTicks) {

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
	public void drawTabPanel(float partialTicks) {
		drawTabBackground(partialTicks);
		drawButtonIcon(partialTicks);
	}

	/**
	 * Draws the tab's button icon.
	 * 
	 * @param xPos
	 * @param yPos
	 * @param partialTicks
	 */
	protected void drawButtonIcon(float partialTicks) {
		if (icon != null) {
			icon.draw(getTabSide() == TabSide.RIGHT ? xPosition + 3 : xPosition + tabWidth + 4.5f, yPosition + 4);
		}
	}

	/**
	 * Draws the tab background (the empty panel that opens when the tab is clicked.
	 * 
	 * @param xPos
	 * @param yPos
	 * @param partialTicks
	 */
	protected void drawTabBackground(float partialTicks) {
		float xPixel = 1.0f / 130.0f;
		float yPixel = 1.0f / 83.0f;
		int tabLeft = xPosition - (getTabSide() == TabSide.RIGHT ? 0 : tabWidth + 24);
		int tabTop = yPosition;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder tes = tessellator.getBuffer();
		tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Minecraft.getInstance().getTextureManager().bindTexture(tabTexture);

		GlStateManager.enableBlend();
		currentWidth = ((tabWidth * animationTimer / animationTime));
		currentHeight = ((tabHeight * animationTimer / animationTime));

		GL11.glPushMatrix();

		GL11.glEnable(GL11.GL_BLEND);
		if (getTabSide() == TabSide.LEFT) {
			GL11.glTranslatef(xPosition, yPosition, 0.0f);
			GL11.glScalef(-1.0f, 1.0f, 1.0f);
			GL11.glTranslatef(-xPosition, -yPosition, 0.0f);
		}
		// Top
		StaticVertexBuffer.pos(tabLeft + 1, tabTop, 0, 0.0f, 0.0f);
		StaticVertexBuffer.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop, 0, 1.0f - xPixel * 4.0f, 0.0f);
		StaticVertexBuffer.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop + 3, 0, 1.0f - xPixel * 4.0f, yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 1, tabTop + 3, 0, 0, yPixel * 4.0f);

		// Bottom
		StaticVertexBuffer.pos(tabLeft + 1, tabTop + 24 + (tabHeight * animationTimer / animationTime), 0, 0.0f, 1.0f);
		StaticVertexBuffer.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 24 + (tabHeight * animationTimer / animationTime), 0, 1.0f - xPixel * 3.0f, 1.0f);
		StaticVertexBuffer.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 21 + (tabHeight * animationTimer / animationTime), 0, 1.0f - xPixel * 3.0f, 1.0f - yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 1, tabTop + 21 + (tabHeight * animationTimer / animationTime), 0, 0.0f, 1.0f - yPixel * 4.0f);

		// Right Side
		StaticVertexBuffer.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop + 21.4 + (tabHeight * animationTimer / animationTime), 0, 1.0f, yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop + 4, 0, 1.0f, 1.0f - yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 4, 0, 1.0f - xPixel * 3.0f, 1.0f - yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 21.4 + (tabHeight * animationTimer / animationTime), 0, 1.0f - xPixel * 3.0f, yPixel * 4.0f);

		// Body
		StaticVertexBuffer.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 21 + (tabHeight * animationTimer / animationTime), 0, 1.0f - xPixel * 4.0f, yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 21 + (tabWidth * animationTimer / animationTime), tabTop + 3, 0, 1.0f - xPixel * 4.0f, 1.0f - yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 1, tabTop + 3, 0, 0, 1.0f - yPixel * 4.0f);
		StaticVertexBuffer.pos(tabLeft + 1, tabTop + 21 + (tabHeight * animationTimer / animationTime), 0, 0, yPixel * 4.0f);

		// Bottom Corner
		StaticVertexBuffer.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop + 24 + (tabHeight * animationTimer / animationTime), 0, 1, 1);
		StaticVertexBuffer.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop + 20 + (tabHeight * animationTimer / animationTime), 0, 1, 1.0f - yPixel * 5.0f);
		StaticVertexBuffer.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop + 20 + (tabHeight * animationTimer / animationTime), 0, 1.0f - xPixel * 4.0f, 1.0f - yPixel * 5.0f);
		StaticVertexBuffer.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop + 24 + (tabHeight * animationTimer / animationTime), 0, 1.0f - xPixel * 4.0f, 1);

		// Top Corner
		StaticVertexBuffer.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop + 4, 0, 1.0f, yPixel * 5.0f);
		StaticVertexBuffer.pos(tabLeft + 23.5 + (tabWidth * animationTimer / animationTime), tabTop, 0, 1.0f, 0);
		StaticVertexBuffer.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop, 0, 1.0f - xPixel * 4.0f, 0);
		StaticVertexBuffer.pos(tabLeft + 20 + (tabWidth * animationTimer / animationTime), tabTop + 4, 0, 1.0f - xPixel * 4.0f, yPixel * 5.0f);

		tessellator.draw();
		GL11.glPopMatrix();
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
		if (this.tabSide == TabSide.LEFT) {
			return new RectangleBounds(xPosition + tabWidth - currentWidth, yPosition, (int) currentWidth + 24, (int) currentHeight + 24);
		} else {
			return new RectangleBounds(xPosition, yPosition, (int) currentWidth + 24, (int) currentHeight + 24);
		}
	}
}
