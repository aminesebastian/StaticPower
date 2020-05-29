package theking530.staticpower.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import theking530.api.container.StaticPowerContainerSlot;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.gui.IInteractableGui;
import theking530.api.gui.button.BaseButton;
import theking530.api.gui.button.BaseButton.ClickedState;
import theking530.api.gui.button.ButtonManager;
import theking530.api.gui.widgets.GuiDrawItem;
import theking530.api.gui.widgets.IGuiWidget;
import theking530.api.gui.widgets.tabs.GuiTabManager;
import theking530.api.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentity.SideModeList.Mode;
import theking530.staticpower.tileentity.TileEntityBase;
import theking530.staticpower.tileentity.TileEntityInventory;

/**
 * Base GUI class containing useful features including tabs, button management,
 * procedural UI generation (render slots and background dynamically), etc.
 * 
 * @author Amine Sebastian
 *
 * @param <T> The container type.
 */
public abstract class StaticPowerContainerGui<T extends Container> extends ContainerScreen<T> implements IInteractableGui {
	protected final FontRenderer fontRenderer;
	protected final GuiTabManager tabManager;
	protected final ButtonManager buttonManager;
	protected final List<IGuiWidget> widgets;
	protected final GuiDrawItem itemDrawer;

	protected int xSizeTarget;
	protected int ySizeTarget;
	protected int outputSlotSize;
	protected int inputSlotSize;

	public StaticPowerContainerGui(T container, final PlayerInventory playerInventory, ITextComponent title, int guiXSize, int guiYSize) {
		super(container, playerInventory, title);
		xSize = guiXSize;
		ySize = guiYSize;
		xSizeTarget = guiXSize;
		ySizeTarget = guiYSize;
		tabManager = new GuiTabManager(this);
		buttonManager = new ButtonManager(this);
		widgets = new ArrayList<IGuiWidget>();
		fontRenderer = Minecraft.getInstance().fontRenderer;
		outputSlotSize = 24;
		inputSlotSize = 16;
		itemDrawer = new GuiDrawItem(true);
	}

	/**
	 * This method is raised after all the constructors and should be used by the
	 * implementer to initialize the GUI (register widgets, etc).
	 */
	public abstract void initializeGui();

	/**
	 * This method's only responsibility is to make a super call and then draw the
	 * container title. Override the method {@link #drawContainerTitle(int, int)} to
	 * draw the title at a custom location.
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		drawContainerTitle(mouseX, mouseY);
	}

	/**
	 * Draws the background layers of the GUI. This includes the default dark tint,
	 * the widget backgrounds, the tabs, and buttons.
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// Renders the dark background.
		renderBackground();

		drawBackgroundExtras(partialTicks, mouseX, mouseY);

		for (IGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderBackground(mouseX, mouseY, partialTicks);
			}
		}

		tabManager.drawTabs(guiLeft + xSize - 1, guiTop + 10, xSize, ySize, partialTicks);
		buttonManager.drawButtons(mouseX, mouseY);

		// Animations the screensize if the target sizes have changed.
		animateScreenSize();
	}

	/**
	 * Renders the UI.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		// Renders any hovered tooltips.
		renderHoveredToolTip(mouseX, mouseY);

		// Raise the mouse hovered event for all the widgets,
		for (IGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.mouseHover(mouseX, mouseY);
			}
		}

		// Render the foreground of all the widgets.
		for (IGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.renderForeground(mouseX, mouseY, partialTicks);
			}
		}

		// Draws the tooltips for all the widgets.
		for (IGuiWidget widget : widgets) {
			if (widget.isVisible() && widget.shouldDrawTooltip(mouseX, mouseY)) {
				renderTooltip(widget.getTooltip(), mouseX, mouseY, Minecraft.getInstance().fontRenderer);
			}
		}

		// Let the tab manager handle any mouse over events.
		tabManager.handleMouseMoveInteraction(mouseX, mouseY);

		// Let the button manager handle any mouse over events.
		buttonManager.handleMouseMoveInteraction(mouseX, mouseY);

		// Draw any additional foreground elements.
		drawForegroundExtras(partialTicks, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean superCallResult = super.mouseClicked(mouseX, mouseY, button);

		for (IGuiWidget widget : widgets) {
			if (widget.isVisible()) {
				widget.mouseClick((int) mouseX, (int) mouseY, button);
			}
		}

		// Handle click events for tabs and buttons.
		tabManager.handleMouseInteraction((int) mouseX, (int) mouseY, button);
		buttonManager.handleMouseInteraction((int) mouseX, (int) mouseY, button);

		return superCallResult;
	}

	/**
	 * Draws the container's title at the top center of the UI.
	 * 
	 * @param mouseX The mouse's x position.
	 * @param mouseY The mouse's y position.
	 */
	protected void drawContainerTitle(int mouseX, int mouseY) {
		ITextComponent containerName = getTitle();
		String containerString = containerName.getString();
		fontRenderer.drawString(containerString, xSize / 2 - fontRenderer.getStringWidth(containerString) / 2, 6, 4210752);
	}

	/**
	 * Override this method to draw any additional background features (features
	 * that should appear behind items).
	 * 
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawBackgroundExtras(float partialTicks, int mouseX, int mouseY) {
	}

	/**
	 * Override this method to draw any additional foreground features (features
	 * that should appear infront of items).
	 * 
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawForegroundExtras(float partialTicks, int mouseX, int mouseY) {
	}

	/**
	 * Draws the default Minecraft UI background using the xSize and ySize of the UI
	 * and screen center for the position.
	 */
	public void drawGenericBackground() {
		drawGenericBackground(xSize, ySize);
	}

	/**
	 * Draws the default Minecraft UI background using the provided values as the
	 * size using the center of the screen as the location.
	 * 
	 * @param width  The width of the background.
	 * @param height The height of the background.
	 */
	public void drawGenericBackground(int width, int height) {
		GuiDrawUtilities.drawGenericBackground(width, height, guiLeft, guiTop);
	}

	/**
	 * Draws the default Minecraft UI background using the provided values as the
	 * size and location.
	 * 
	 * @param xPos   The x position of the background.
	 * @param yPos   The y position of the background.
	 * @param width  The width of the background.
	 * @param height The height of the background.
	 */
	public void drawGenericBackground(int xPos, int yPos, int width, int height) {
		GuiDrawUtilities.drawGenericBackground(width, height, xPos + guiLeft, yPos + guiTop);
	}

	/**
	 * Draws the default Minecraft UI background using the provided values as the
	 * size and location. Offers the option to change the background color and tint
	 * the borders.
	 * 
	 * @param xPos            The x position of the background.
	 * @param yPos            The y position of the background.
	 * @param width           The width of the background.
	 * @param height          The height of the background.
	 * @param backgroundColor The color of the background (the main grey area).
	 * @param borderTint      The tint to apply to the border (the two pixel rounded
	 *                        corner border).
	 */
	public void drawGenericBackground(int xPos, int yPos, int width, int height, Color backgroundColor, Color borderTint) {
		GuiDrawUtilities.drawGenericBackground(width, height, xPos + guiLeft, yPos + guiTop, backgroundColor, borderTint);
	}

	/**
	 * Draws the player's inventory slots in the default location.
	 */
	public void drawPlayerInventorySlots() {
		drawPlayerInventorySlots(guiLeft + (xSize - 162) / 2 + 1, guiTop + ySize - 83);
	}

	/**
	 * Draws the player's inventory slots at the provided location.
	 * 
	 * @param xPos The x position.
	 * @param yPos The y position.
	 */
	public void drawPlayerInventorySlots(int xPos, int yPos) {
		GuiDrawUtilities.drawPlayerInventorySlots(xPos, yPos);
	}

	/**
	 * Draws a slot using the provided values. NOTE: The actual position of the slot
	 * will be appear to be one pixel to the left and one pixel down due to the
	 * border of the slot being rendered OUTSIDE the slot. Meaning at 16x16 slot
	 * will use 17x17 pixels.
	 * 
	 * @param xPos     The x position of the slot.
	 * @param yPos     The y position of the slot.
	 * @param width    The width of the slot.
	 * @param height   The height of the slot.
	 * @param slotMode The mode of the slot (this dictates the potential color
	 *                 border).
	 */
	public void drawSlot(int xPos, int yPos, int width, int height, Mode slotMode) {
		if (slotMode == Mode.Regular) {
			GuiDrawUtilities.drawSlot(xPos, yPos, width, height);
		} else {
			GuiDrawUtilities.drawSlot(xPos, yPos, width, height, slotMode.getBorderColor());
		}
	}

	/**
	 * Draws a slot using the provided values. NOTE: The actual position of the slot
	 * will be appear to be one pixel to the left and one pixel down due to the
	 * border of the slot being rendered OUTSIDE the slot. Meaning at 16x16 slot
	 * will use 17x17 pixels.
	 * 
	 * @param xPos   The x position of the slot.
	 * @param yPos   The y position of the slot.
	 * @param width  The width of the slot.
	 * @param height The height of the slot.
	 */
	public void drawSlot(int xPos, int yPos, int width, int height) {
		GuiDrawUtilities.drawSlot(xPos, yPos, width, height);
	}

	public void drawStringWithSize(String text, int xPos, int yPos, float scale, int color, boolean withShadow) {
		GuiDrawUtilities.drawStringWithSize(text, xPos, yPos, scale, color, withShadow);
	}

	public void drawContainerSlots(TileEntityBase te, List<Slot> slots) {
		if (te == null) {
			StaticPower.LOGGER.error("Encountered null tile entity when attempting to draw container slots.");
			return;
		}
		for (Slot slot : slots) {
			// Skip null slots
			if (slot == null) {
				StaticPower.LOGGER.error(String.format("Encountered an invalid slot in tile entity: %1$s at location: %2$s.", te.getDisplayName().getString(), te.getPos()));
				continue;
			}

			if (slot instanceof StaticPowerContainerSlot) {
				StaticPowerContainerSlot handlerSlot = (StaticPowerContainerSlot) slot;
				if (handlerSlot.getItemHandler() == null) {
					StaticPower.LOGGER.error(String.format("Encountered an invalid item handler for a slot in tile entity: %1$s at location: %2$s.", te.getDisplayName().getString(), te.getPos()));
					continue;
				}

				// Check the intended mode of the handler slot. If it ends up being null, just
				// assume the mode is regular.
				Mode intendedMode = handlerSlot.getMode();
				if (handlerSlot.getItemHandler() instanceof TileEntityInventory) {
					intendedMode = ((TileEntityInventory) handlerSlot.getItemHandler()).getMode();
				}
				intendedMode = intendedMode == null ? Mode.Regular : intendedMode;

				// If the slot is an output slot, increase the size of the slot.
				int slotSize = intendedMode.isOutputMode() ? outputSlotSize : inputSlotSize;
				int adjustment = (slotSize - 16) / 2;
				if (intendedMode != Mode.Regular) {
					drawSlot(slot.xPos + guiLeft - adjustment, slot.yPos + guiTop - adjustment, slotSize, slotSize, te.getSideWithModeCount(intendedMode) > 0 ? intendedMode : Mode.Regular);
				} else {
					drawSlot(slot.xPos + guiLeft, slot.yPos + guiTop, 16, 16);
				}
				if (!handlerSlot.getPreviewItem().isEmpty()) {
					itemDrawer.drawItem(handlerSlot.getPreviewItem(), guiLeft, guiTop, slot.xPos, slot.yPos, handlerSlot.getPreviewAlpha());
				}
			}
		}
	}

	public void setInputSlotSize(int size) {
		inputSlotSize = size;
	}

	public void setOutputSlotSize(int size) {
		outputSlotSize = size;
	}

	public void setGuiSizeTarget(int xSizeTarget, int ySizeTarget) {
		this.xSizeTarget = xSizeTarget;
		this.ySizeTarget = ySizeTarget;
	}

	public void setDesieredGuiSize(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.xSizeTarget = xSize;
		this.ySizeTarget = ySize;
	}

	protected void mouseClicked(int x, int y, int button) throws IOException {
		// super.mouseClicked(x, y, button);
		tabManager.handleMouseInteraction(x, y, button);
		buttonManager.handleMouseInteraction(x, y, button);
		for (int i = 0; i < widgets.size(); i++) {
			widgets.get(i).mouseClick(x, y, button);
		}
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		// super.keyTyped(par1, par2);
		tabManager.handleKeyboardInteraction(par1, par2);
	}

	public void registerWidget(IGuiWidget widget) {
		widgets.add(widget);
		widget.setOwningGui(this);
	}

	@Override
	public void buttonPressed(BaseButton button, ClickedState mouseButton) {

	}

	private void animateScreenSize() {
		if (Math.abs(xSize - xSizeTarget) > 0) {
			int minimumAnimationVal = xSizeTarget - xSize > 0 ? 1 : -1;
			if (minimumAnimationVal == 1) {
				xSize = xSize + Math.max(minimumAnimationVal, (xSizeTarget - xSize) / 20);
			} else {
				xSize = xSize + Math.min(minimumAnimationVal, (xSizeTarget - xSize) / 20);
			}
		}
		if (Math.abs(ySize - ySizeTarget) > 0) {
			int minimumAnimationVal = ySizeTarget - ySize > 0 ? 1 : -1;
			if (minimumAnimationVal == 1) {
				ySize = ySize + Math.max(minimumAnimationVal, (ySizeTarget - ySize) / 20);
			} else {
				ySize = ySize + Math.min(minimumAnimationVal, (ySizeTarget - ySize) / 20);
			}
		}
	}

	public GuiTabManager getTabManager() {
		return tabManager;
	}
}
