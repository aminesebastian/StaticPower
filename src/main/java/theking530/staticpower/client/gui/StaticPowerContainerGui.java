package theking530.staticpower.client.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.SlotItemHandler;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.WidgetContainer;
import theking530.common.gui.drawables.SpriteDrawable;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.common.gui.widgets.GuiDrawItem;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.GuiTabManager;
import theking530.common.utilities.Color;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.container.StaticPowerContainer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.DigistoreCraftingOutputSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

/**
 * Base GUI class containing useful features including tabs, button management,
 * procedural UI generation (render slots and background dynamically), etc.
 * 
 * @author Amine Sebastian
 *
 * @param <T> The container type.
 */
public abstract class StaticPowerContainerGui<T extends Container> extends ContainerScreen<T> {
	/** The default location to render the inventory label. */
	public static final Vector2D DEFAULT_INVENTORY_LABEL_LOCATION = new Vector2D(8, 97);

	/** The container responsible for managing all the widget. */
	protected final WidgetContainer widgetContainer;
	/** The tab manager widget. */
	protected final GuiTabManager tabManager;
	/** The item renderer. */
	protected final GuiDrawItem itemRenderer;

	protected int xSizeTarget;
	protected int ySizeTarget;
	protected int outputSlotSize;
	protected int inputSlotSize;
	protected boolean isInitialized;
	protected float partialTicks;
	
	private final SpriteDrawable lockedSprite;

	/**
	 * Creates a new Gui.
	 * 
	 * @param container       The container that this GUI represents.
	 * @param playerInventory The player's inventory.
	 * @param title           The title of this GUI.
	 * @param guiXSize        The gui's xSize.
	 * @param guiYSize        The gui's ySize;
	 */
	public StaticPowerContainerGui(T container, final PlayerInventory playerInventory, ITextComponent title, int guiXSize, int guiYSize) {
		super(container, playerInventory, title);
		widgetContainer = new WidgetContainer();
		xSize = guiXSize;
		ySize = guiYSize;
		xSizeTarget = guiXSize;
		ySizeTarget = guiYSize;
		outputSlotSize = 24;
		inputSlotSize = 16;
		itemRenderer = new GuiDrawItem();
		partialTicks = 0.0f;
		lockedSprite = new SpriteDrawable(StaticPowerSprites.DIGISTORE_LOCKED_INDICATOR, 8, 8);
		lockedSprite.setTint(new Color(1.0f, 1.0f, 1.0f, 0.95f));
		registerWidget(tabManager = new GuiTabManager(this));
	}

	@Override()
	public void init() {
		super.init();
		if (!isInitialized) {
			initializeGui();
			isInitialized = true;
		}
	}

	@Override
	public void tick() {
		super.tick();
		updateData();
		widgetContainer.tick();
	}

	/**
	 * This method is raised after all the constructors and should be used by the
	 * implementer to initialize the GUI (register widgets, etc).
	 */
	public void initializeGui() {

	}

	/**
	 * This method is called every TICK (20 times a second), and can be used to
	 * update any data in the UI that does not need to be updated per frame (for
	 * example, the text in an info tab.)
	 */
	public void updateData() {

	}

	/**
	 * This method's only responsibility is to make a super call and then draw the
	 * container title. Override the method {@link #drawContainerTitle(int, int)} to
	 * draw the title at a custom location.
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// Draw the title.
		drawContainerTitle(mouseX, mouseY);

		// Draw the locks for lock slots. We do this in a single pass to avoid having
		// the flip the GL states a lot.
		GlStateManager.disableDepthTest();
		for (Slot slot : getContainer().inventorySlots) {
			if (slot instanceof SlotItemHandler) {
				SlotItemHandler itemHandlerSlot = (SlotItemHandler) slot;
				if (itemHandlerSlot.getItemHandler() instanceof InventoryComponent) {
					InventoryComponent component = (InventoryComponent) itemHandlerSlot.getItemHandler();
					if (component.isSlotLocked(slot.getSlotIndex())) {
						lockedSprite.draw(slot.xPos + 4, slot.yPos + 4);
					}
				}
			}
		}
		GlStateManager.enableDepthTest();
	}

	/**
	 * Draws the background layers of the GUI. This includes the default dark tint,
	 * the widget backgrounds, the tabs, and buttons.
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// Renders the dark background.
		renderBackground();

		// Update the widgets and then draw the background.
		widgetContainer.update(new Vector2D(getGuiLeft(), getGuiTop()), new Vector2D(getXSize(), getYSize()), partialTicks, mouseX, mouseY);
		widgetContainer.renderBackground(mouseX, mouseY, partialTicks);

		// Draw the container background.
		drawGenericBackground();

		// Draw any extras.
		drawBackgroundExtras(partialTicks, mouseX, mouseY);

		// Draw the slots.
		if (container instanceof StaticPowerTileEntityContainer) {
			drawContainerSlots(container.inventorySlots, ((StaticPowerTileEntityContainer<?>) container).getTileEntity().getComponent(SideConfigurationComponent.class));
		} else {
			drawContainerSlots(container.inventorySlots);
		}

		// Draw any widgets that need to appear above slots/items.
		widgetContainer.renderBehindItems(mouseX, mouseY, partialTicks);

		// Draw anything infront of the background but behind the items.
		drawBehindItems(partialTicks, mouseX, mouseY);

		// Animations the screensize if the target sizes have changed.
		animateScreenSize();
	}

	/**
	 * Renders the UI.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		// Cache the partial ticks.
		this.partialTicks = partialTicks;

		// Raise the mouse hovered event for all the widgets,
		widgetContainer.handleMouseMove(mouseX, mouseY);

		// Render the foreground of all the widgets.
		widgetContainer.renderForegound(mouseX, mouseY, partialTicks);

		drawSlotOverlays(container.inventorySlots);

		// Draw any additional foreground elements.
		drawForegroundExtras(partialTicks, mouseX, mouseY);

		// Renders any hovered tooltips.
		renderHoveredToolTip(mouseX, mouseY);

		// Render the widget tooltips as needed.
		widgetContainer.renderTooltips(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// If we clicked on an input container slot and we held control, don't raise the
		// regular clicked chain. Use the SWAP just as a placeholder.
		if (hoveredSlot instanceof StaticPowerContainerSlot && ((StaticPowerContainerSlot) hoveredSlot).getItemHandler() instanceof InventoryComponent && Screen.hasControlDown()) {
			StaticPowerContainerSlot spSlot = (StaticPowerContainerSlot) hoveredSlot;
			if (((InventoryComponent) spSlot.getItemHandler()).areSlotsLockable()) {
				handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, StaticPowerContainer.INVENTORY_COMPONENT_FILTER_MOUSE_BUTTON, ClickType.SWAP);
				return true;
			}
		}

		boolean superCallResult = super.mouseClicked(mouseX, mouseY, button);
		widgetContainer.handleMouseClick(mouseX, mouseY, button);
		return superCallResult;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		EInputResult result = widgetContainer.handleMouseScrolled(mouseX, mouseY, scrollDelta);
		if (result != EInputResult.HANDLED) {
			return super.mouseScrolled(mouseX, mouseY, scrollDelta);
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		EInputResult result = widgetContainer.handleMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		if (result != EInputResult.HANDLED) {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		return true;
	}

	@Override
	public boolean charTyped(char character, int p_charTyped_2_) {
		EInputResult result = widgetContainer.characterTyped(character, p_charTyped_2_);
		if (result == EInputResult.UNHANDLED) {
			return super.charTyped(character, p_charTyped_2_);
		}
		return true;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		EInputResult result = widgetContainer.handleKeyPressed(key, scanCode, modifiers);
		if (result == EInputResult.UNHANDLED) {
			return super.keyPressed(key, scanCode, modifiers);
		}
		return true;
	}

	public List<Rectangle2d> getGuiBounds() {
		List<Rectangle2d> tabBoxes = new ArrayList<>();

		for (BaseGuiTab tab : getTabManager().getRegisteredTabs()) {
			tabBoxes.add(tab.getBounds().toRectange2d());
		}

		for (AbstractGuiWidget widget : this.widgetContainer.getWidgets()) {
			tabBoxes.add(widget.getBounds().toRectange2d());
		}

		return tabBoxes;
	}

	/**
	 * Draws the container's title at the top center of the UI.
	 * 
	 * @param mouseX The mouse's x position.
	 * @param mouseY The mouse's y position.
	 */
	protected void drawContainerTitle(int mouseX, int mouseY) {
		// Draw the container title if requested at the designated location.
		if (shouldDrawContainerLabel()) {
			Vector2D containerLabelLocation = getContainerLabelDrawLocation();
			ITextComponent containerName = getTitle();
			String containerString = containerName.getString();
			font.drawString(containerString, containerLabelLocation.getX(), containerLabelLocation.getY(), 4210752);
		}

		// Draw the inventory label if requested at the designated location.
		if (shouldDrawInventoryLabel()) {
			Vector2D inventoryLabelLocation = getInventoryLabelDrawLocation();
			font.drawString(playerInventory.getDisplayName().getFormattedText(), inventoryLabelLocation.getX(), inventoryLabelLocation.getY(), 4210752);
		}
	}

	/**
	 * This method should return a vector (2D) where the "Inventory" label should be
	 * rendered in the UI. To disable the rendering all together, override the
	 * {@link #shouldDrawInventoryLabel()} method.
	 * 
	 * @return A vector indicating the position where the inventory label should
	 *         render.
	 */
	protected Vector2D getInventoryLabelDrawLocation() {
		return DEFAULT_INVENTORY_LABEL_LOCATION;
	}

	/**
	 * This method should return whether or not the container label should be drawn.
	 * 
	 * @return True if the container label should be drawn, false otherwise.
	 */
	protected boolean shouldDrawContainerLabel() {
		return true;
	}

	/**
	 * This method should return a vector (2D) where the container label should be
	 * rendered in the UI. To disable the rendering all together, override the
	 * {@link #shouldDrawContainerLabel()} method.
	 * 
	 * @return A vector indicating the position where the container label should
	 *         render.
	 */
	protected Vector2D getContainerLabelDrawLocation() {
		ITextComponent containerName = getTitle();
		String containerString = containerName.getString();
		return new Vector2D(xSize / 2 - font.getStringWidth(containerString) / 2, 6);
	}

	/**
	 * This method should return whether or not the Inventory label should be drawn.
	 * 
	 * @return True if the inventory label should be drawn, false otherwise.
	 */
	protected boolean shouldDrawInventoryLabel() {
		return false;
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
	 * Override this method to draw any additional background features (features
	 * that should appear infront of the background, but behind items.).
	 * 
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawBehindItems(float partialTicks, int mouseX, int mouseY) {

	}

	/**
	 * Override this method to draw any additional foreground features (features
	 * that should appear in front of items).
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
		GuiDrawUtilities.drawGenericBackground(width, height, xPos + guiLeft, yPos + guiTop, backgroundColor, borderTint, true, true, true, true);
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
	 * Draws an item from the provided slot. This does NOT render the actual slot
	 * background (unfortunate name overlap with vanilla code).
	 */
	@Override
	public void drawSlot(Slot slotIn) {
		super.drawSlot(slotIn);
	}

	/**
	 * Draws a slot BACKGROUND using the provided values. NOTE: The actual position
	 * of the slot will be appear to be one pixel to the left and one pixel down due
	 * to the border of the slot being rendered OUTSIDE the slot. Meaning at 16x16
	 * slot will use 17x17 pixels.
	 * 
	 * @param xPos     The x position of the slot.
	 * @param yPos     The y position of the slot.
	 * @param width    The width of the slot.
	 * @param height   The height of the slot.
	 * @param slotMode The mode of the slot (this dictates the potential color
	 *                 border).
	 */
	public void drawSlot(int xPos, int yPos, int width, int height, MachineSideMode slotMode) {
		if (slotMode == MachineSideMode.Regular) {
			GuiDrawUtilities.drawSlot(xPos, yPos, width, height);
		} else {
			GuiDrawUtilities.drawSlot(xPos, yPos, width, height, slotMode.getColor());
		}
	}

	/**
	 * Draws a slot BACKGROUND using the provided values. NOTE: The actual position
	 * of the slot will be appear to be one pixel to the left and one pixel down due
	 * to the border of the slot being rendered OUTSIDE the slot. Meaning at 16x16
	 * slot will use 17x17 pixels.
	 * 
	 * @param xPos   The x position of the slot.
	 * @param yPos   The y position of the slot.
	 * @param width  The width of the slot.
	 * @param height The height of the slot.
	 */
	public void drawSlot(int xPos, int yPos, int width, int height) {
		GuiDrawUtilities.drawSlot(xPos, yPos, width, height);
	}

	/**
	 * Renders a string with the provided scale.
	 * 
	 * @param text
	 * @param xPos
	 * @param yPos
	 * @param scale
	 * @param color
	 * @param withShadow
	 */
	public void drawStringWithSize(String text, int xPos, int yPos, float scale, Color color, boolean withShadow) {
		GuiDrawUtilities.drawStringWithSize(text, xPos, yPos, scale, color, withShadow);
	}

	public void drawSlotOverlays(List<Slot> slots) {
		for (Slot slot : slots) {
			if (slot instanceof StaticPowerContainerSlot) {
				StaticPowerContainerSlot handlerSlot = (StaticPowerContainerSlot) slot;
				int slotSize = handlerSlot.getMode().isOutputMode() ? outputSlotSize : handlerSlot.getMode().isInputMode() ? inputSlotSize : 16;
				int sizePosOffset = (slotSize - 16) / 2;
				handlerSlot.drawSlotOverlay(itemRenderer, guiLeft, guiTop, slotSize, sizePosOffset);
			}
		}
	}

	public void drawContainerSlots(List<Slot> slots) {
		drawContainerSlots(slots, null);
	}

	public void drawContainerSlots(List<Slot> slots, @Nullable SideConfigurationComponent sideConfiguration) {
		for (Slot slot : slots) {
			// Skip null slots
			if (slot == null) {
				continue;
			}

			// Skip disabled slots.
			if (!slot.isEnabled()) {
				continue;
			}

			// If the slot is a static power container slot, perform some additional checks
			// before drawing, otheriwse just draw the slot.
			if (slot instanceof StaticPowerContainerSlot) {
				StaticPowerContainerSlot handlerSlot = (StaticPowerContainerSlot) slot;
				if (handlerSlot.getItemHandler() == null) {
					continue;
				}

				// Check the intended mode of the handler slot. If it ends up being null, just
				// assume the mode is regular.
				MachineSideMode intendedMode = handlerSlot.getMode();

				// If the slot is an output slot, increase the size of the slot.
				int slotSize = intendedMode.isOutputMode() || slot instanceof OutputSlot ? outputSlotSize : handlerSlot.getMode().isInputMode() ? inputSlotSize : 16;
				int sizePosOffset = (slotSize - 16) / 2;

				// If side configuration is present, draw the slow with a border.
				if (sideConfiguration != null) {
					if (intendedMode != MachineSideMode.Regular && intendedMode != MachineSideMode.Never) {
						// Get the side mode to draw with.
						MachineSideMode drawnSideMode = sideConfiguration.getCountOfSidesWithMode(intendedMode) > 0 ? intendedMode : MachineSideMode.Regular;

						// If the drawn side is regular, check to see if we can render one of the two
						// general output or input modes.
						if (drawnSideMode == MachineSideMode.Regular) {
							if (intendedMode.isOutputMode() && sideConfiguration.getCountOfSidesWithMode(MachineSideMode.Output) > 0) {
								drawnSideMode = MachineSideMode.Output;
							} else if (intendedMode.isInputMode() && sideConfiguration.getCountOfSidesWithMode(MachineSideMode.Input) > 0) {
								drawnSideMode = MachineSideMode.Input;
							}
						}

						drawSlot(slot.xPos + guiLeft - sizePosOffset, slot.yPos + guiTop - sizePosOffset, slotSize, slotSize, drawnSideMode);
					} else {
						drawSlot(slot.xPos + guiLeft - sizePosOffset, slot.yPos + guiTop - sizePosOffset, slotSize, slotSize);
					}
				} else {
					drawSlot(slot.xPos + guiLeft - sizePosOffset, slot.yPos + guiTop - sizePosOffset, slotSize, slotSize);
				}

				// If the slot is locked, render the phantom item.
				if (handlerSlot.getItemHandler() instanceof InventoryComponent) {
					InventoryComponent component = (InventoryComponent) handlerSlot.getItemHandler();
					if (component.isSlotLocked(slot.getSlotIndex())) {
						itemRenderer.drawItem(component.getLockedSlotFilter(slot.getSlotIndex()), guiLeft, guiTop, slot.xPos, slot.yPos, 0.5f);
					}
				}

				// Draw the item.
				handlerSlot.drawExtras(itemRenderer, guiLeft, guiTop, slotSize, sizePosOffset);
			} else if (slot instanceof DigistoreCraftingOutputSlot) {
				drawSlot(slot.xPos + guiLeft - 4, slot.yPos - 4 + guiTop, 24, 24);
			} else {
				drawSlot(slot.xPos + guiLeft, slot.yPos + guiTop, 16, 16);
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

	public void registerWidget(AbstractGuiWidget widget) {
		widgetContainer.registerWidget(widget);
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
