package theking530.staticpower.client.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.WidgetContainer;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.gui.widgets.tabs.GuiTabManager;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.GuiDrawItem;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.DigistoreCraftingOutputSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

/**
 * Base GUI class containing useful features including tabs, button management,
 * procedural UI generation (render slots and background dynamically), etc.
 * 
 * @author Amine Sebastian
 *
 * @param <T> The container type.
 */
public abstract class StaticPowerContainerGui<T extends StaticPowerContainer> extends AbstractContainerScreen<T> {
	/** The default location to render the inventory label. */
	public static final Vector2D DEFAULT_INVENTORY_LABEL_LOCATION = new Vector2D(8, 97);

	/** The container responsible for managing all the widget. */
	protected final WidgetContainer widgetContainer;
	/** The tab manager widget. */
	protected final GuiTabManager tabManager;
	/** The item renderer. */
	protected final GuiDrawItem itemRenderer;
	protected final Inventory inventory;

	protected Vector2D sizeTarget;
	protected Vector2D previousSizeTarget;
	protected boolean isScreenSizeChanging;

	protected int outputSlotSize;
	protected int inputSlotSize;
	protected boolean isInitialized;

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
	public StaticPowerContainerGui(T container, final Inventory playerInventory, Component title, int guiXSize,
			int guiYSize) {
		super(container, playerInventory, title);
		inventory = playerInventory;
		widgetContainer = new WidgetContainer(this);
		imageWidth = guiXSize;
		imageHeight = guiYSize;
		sizeTarget = new Vector2D(imageWidth, imageHeight);
		outputSlotSize = 24;
		inputSlotSize = 16;
		itemRenderer = new GuiDrawItem();
		isScreenSizeChanging = false;
		lockedSprite = new SpriteDrawable(StaticPowerSprites.DIGISTORE_LOCKED_INDICATOR, 8, 8);
		lockedSprite.setTint(new Color(1.0f, 1.0f, 1.0f, 0.95f));
		registerWidget(tabManager = new GuiTabManager());
		container.setName(title);
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
	public void containerTick() {
		super.containerTick();
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
	protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
		// Draw the title.
		drawContainerTitle(stack, mouseX, mouseY);

		// Draw the locks for lock slots. We do this in a single pass to avoid having
		// the flip the GL states a lot.
		GlStateManager._disableDepthTest();
		for (Slot slot : getMenu().slots) {
			if (slot instanceof SlotItemHandler) {
				SlotItemHandler itemHandlerSlot = (SlotItemHandler) slot;
				if (itemHandlerSlot.getItemHandler() instanceof InventoryComponent) {
					InventoryComponent component = (InventoryComponent) itemHandlerSlot.getItemHandler();
					if (component.isSlotLocked(slot.getSlotIndex())) {
						lockedSprite.draw(slot.x + 4, slot.y + 4);
					}
				}
			}
		}
		GlStateManager._enableDepthTest();
	}

	/**
	 * Draws the background layers of the GUI. This includes the default dark tint,
	 * the widget backgrounds, the tabs, and buttons.
	 */
	@Override
	protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		// Renders the dark background.
		renderBackground(stack);

		// Update the widgets and then draw the background.
		widgetContainer.update(stack, new Vector2D(leftPos, topPos), new Vector2D(getXSize(), getYSize()), partialTicks,
				mouseX, mouseY);
		widgetContainer.renderBackground(stack, mouseX, mouseY, partialTicks);

		// Draw the container background.
		drawGenericBackground();

		// Draw any extras.
		stack.pushPose();
		stack.translate(leftPos, topPos, 0);
		drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);
		stack.popPose();

		// Draw the slots.
		if (menu instanceof StaticPowerTileEntityContainer) {
			drawContainerSlots(stack, menu.slots, ((StaticPowerTileEntityContainer<?>) menu).getTileEntity()
					.getComponent(SideConfigurationComponent.class));
		} else {
			drawContainerSlots(stack, menu.slots);
		}

		// Draw any widgets that need to appear above slots/items.
		widgetContainer.renderBehindItems(stack, mouseX, mouseY, partialTicks);

		// Draw anything infront of the background but behind the items.
		stack.pushPose();
		stack.translate(leftPos, topPos, 0);
		drawBehindItems(stack, partialTicks, mouseX, mouseY);
		stack.popPose();

		// Animations the screensize if the target sizes have changed.
		animateScreenSize();
	}

	/**
	 * Renders the UI.
	 */
	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		super.render(stack, mouseX, mouseY, partialTicks);

		// Raise the mouse hovered event for all the widgets,
		widgetContainer.handleMouseMove(mouseX, mouseY);

		// Render the foreground of all the widgets.
		widgetContainer.renderForegound(stack, mouseX, mouseY, partialTicks);

		drawSlotOverlays(menu.slots);

		// Draw any additional foreground elements.
		stack.pushPose();
		stack.translate(leftPos, topPos, 0);
		drawForegroundExtras(stack, partialTicks, mouseX, mouseY);
		stack.popPose();

		// Renders any hovered tooltips.
		renderTooltip(stack, mouseX, mouseY);

		// Render the widget tooltips as needed.
		widgetContainer.renderTooltips(stack, mouseX, mouseY);

		// Render any extra tooltips.
		List<Component> tooltips = new ArrayList<Component>();
		getExtraTooltips(tooltips, stack, mouseX, mouseY);
		if (tooltips.size() > 0) {
			renderComponentTooltip(stack, tooltips, mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// If we clicked on an input container slot and we held control, don't raise the
		// regular clicked chain. Use the SWAP just as a placeholder.
		if (ModKeyBindings.SLOT_LOCK.isDown()) {
			if (hoveredSlot != null) {
				slotClicked(hoveredSlot, hoveredSlot.index, StaticPowerContainer.INVENTORY_COMPONENT_LOCK_MOUSE_BUTTON,
						ClickType.SWAP);
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

	public List<Rect2i> getGuiBounds() {
		List<Rect2i> tabBoxes = new ArrayList<>();

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
	protected void drawContainerTitle(PoseStack stack, int mouseX, int mouseY) {
		// Draw the container title if requested at the designated location.
		if (shouldDrawContainerLabel()) {
			Vector2D containerLabelLocation = getContainerLabelDrawLocation();
			Component containerName = getTitle();
			String containerString = containerName.getString();
			font.draw(stack, containerString, containerLabelLocation.getX(), containerLabelLocation.getY(), 4210752);
		}

		// Draw the inventory label if requested at the designated location.
		if (shouldDrawInventoryLabel()) {
			Vector2D inventoryLabelLocation = getInventoryLabelDrawLocation();
			font.draw(stack, inventory.getDisplayName().getString(), inventoryLabelLocation.getX(),
					inventoryLabelLocation.getY(), 4210752);
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
		Component containerName = getTitle();
		String containerString = containerName.getString();
		return new Vector2D(imageWidth / 2 - font.width(containerString) / 2, 6);
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
	 * @param stack        
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

	}

	/**
	 * Override this method to draw any additional background features (features
	 * that should appear infront of the background, but behind items.).
	 * 
	 * @param stack        
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

	}

	/**
	 * Override this method to draw any additional foreground features (features
	 * that should appear in front of items).
	 * 
	 * @param stack        
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
	}

	/**
	 * Override this method to supply any additional tooltips.
	 * 
	 * @param tooltips
	 * @param stack
	 * @param mouseX
	 * @param mouseY
	 */
	protected void getExtraTooltips(List<Component> tooltips, PoseStack stack, int mouseX, int mouseY) {

	}

	/**
	 * Draws the default Minecraft UI background using the xSize and ySize of the UI
	 * and screen center for the position.
	 */
	public void drawGenericBackground() {
		drawGenericBackground(imageWidth, imageHeight);
	}

	/**
	 * Draws the default Minecraft UI background using the provided values as the
	 * size using the center of the screen as the location.
	 * 
	 * @param width  The width of the background.
	 * @param height The height of the background.
	 */
	public void drawGenericBackground(int width, int height) {
		GuiDrawUtilities.drawGenericBackground(width, height, leftPos, topPos);
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
		GuiDrawUtilities.drawGenericBackground(width, height, xPos + leftPos, yPos + topPos);
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
	public void drawGenericBackground(int xPos, int yPos, int width, int height, Color backgroundColor,
			Color borderTint) {
		GuiDrawUtilities.drawGenericBackground(width, height, xPos + leftPos, yPos + topPos, 0.0f, backgroundColor,
				true, true, true, true);
	}

	/**
	 * Draws the player's inventory slots in the default location.
	 */
	public void drawPlayerInventorySlots() {
		drawPlayerInventorySlots(leftPos + (imageWidth - 162) / 2 + 1, topPos + imageHeight - 83);
	}

	/**
	 * Draws the player's inventory slots at the provided location.
	 * 
	 * @param xPos The x position.
	 * @param yPos The y position.
	 */
	public void drawPlayerInventorySlots(int xPos, int yPos) {
		GuiDrawUtilities.drawPlayerInventorySlots(null, xPos, yPos);
	}

	@Override
	public void renderSlot(PoseStack stack, Slot slotIn) {
		super.renderSlot(stack, slotIn);
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
	public void drawEmptySlot(PoseStack matrixStack, int xPos, int yPos, int width, int height,
			MachineSideMode slotMode) {
		if (slotMode == MachineSideMode.Regular) {
			GuiDrawUtilities.drawSlot(matrixStack, width, height, xPos, yPos, 0);
		} else {
			GuiDrawUtilities.drawSlot(matrixStack, width, height, xPos, yPos, 0, slotMode.getColor());
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
	public void drawEmptySlot(PoseStack matrixStack, int xPos, int yPos, int width, int height) {
		GuiDrawUtilities.drawSlot(matrixStack, width, height, xPos, yPos, 0);
	}

	public void drawSlotOverlays(List<Slot> slots) {
		for (Slot slot : slots) {
			if (slot instanceof StaticPowerContainerSlot) {
				StaticPowerContainerSlot handlerSlot = (StaticPowerContainerSlot) slot;
				int slotSize = handlerSlot.getMode().isOutputMode() ? outputSlotSize
						: handlerSlot.getMode().isInputMode() ? inputSlotSize : 16;
				int sizePosOffset = (slotSize - 16) / 2;
				handlerSlot.drawSlotOverlay(itemRenderer, leftPos, topPos, slotSize, sizePosOffset);
			}
		}
	}

	public void drawContainerSlots(PoseStack matrixStack, List<Slot> slots) {
		drawContainerSlots(matrixStack, slots, null);
	}

	public void drawContainerSlots(PoseStack matrixStack, List<Slot> slots,
			@Nullable SideConfigurationComponent sideConfiguration) {
		for (Slot slot : slots) {
			// Skip null slots
			if (slot == null) {
				continue;
			}

			// Skip disabled slots.
			if (!slot.isActive()) {
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
				int slotSize = intendedMode.isOutputMode() || slot instanceof OutputSlot ? outputSlotSize
						: handlerSlot.getMode().isInputMode() ? inputSlotSize : 16;
				int sizePosOffset = (slotSize - 16) / 2;

				// If side configuration is present, draw the slow with a border.
				if (sideConfiguration != null) {
					if (intendedMode != MachineSideMode.Regular && intendedMode != MachineSideMode.Never) {
						// Get the side mode to draw with.
						MachineSideMode drawnSideMode = sideConfiguration.getCountOfSidesWithMode(intendedMode) > 0
								? intendedMode
								: MachineSideMode.Regular;

						// If the drawn side is regular, check to see if we can render one of the two
						// general output or input modes.
						if (drawnSideMode == MachineSideMode.Regular) {
							if (intendedMode.isOutputMode()
									&& sideConfiguration.getCountOfSidesWithMode(MachineSideMode.Output) > 0) {
								drawnSideMode = MachineSideMode.Output;
							} else if (intendedMode.isInputMode()
									&& sideConfiguration.getCountOfSidesWithMode(MachineSideMode.Input) > 0) {
								drawnSideMode = MachineSideMode.Input;
							}
						}

						drawEmptySlot(matrixStack, slot.x + leftPos - sizePosOffset, slot.y + topPos - sizePosOffset,
								slotSize, slotSize, drawnSideMode);
					} else {
						drawEmptySlot(matrixStack, slot.x + leftPos - sizePosOffset, slot.y + topPos - sizePosOffset,
								slotSize, slotSize);
					}
				} else {
					drawEmptySlot(matrixStack, slot.x + leftPos - sizePosOffset, slot.y + topPos - sizePosOffset,
							slotSize, slotSize);
				}

				// Check if this slot's inventory is an InventoryComponent
				if (handlerSlot.getItemHandler() instanceof InventoryComponent) {
					InventoryComponent component = (InventoryComponent) handlerSlot.getItemHandler();
					// Check if the slot is lockable.
					if (component.areSlotsLockable()) {
						// If the slot is locked, render the phantom item & the lock indicator.
						if (component.isSlotLocked(slot.getSlotIndex())) {
							itemRenderer.drawItem(component.getLockedSlotFilter(slot.getSlotIndex()), leftPos, topPos,
									slot.x, slot.y, 0.5f);
							RenderSystem.enableDepthTest();
						}

						// Draw the yellow line lockable indicator.
						GuiDrawUtilities.drawColoredRectangle(slot.x + leftPos - sizePosOffset,
								slot.y + topPos - sizePosOffset + slotSize, slotSize, 1.0f, 1.0f,
								new Color(0.9f, 0.8f, 0));
					}
				}

				// Draw the item.
				handlerSlot.drawBeforeItem(matrixStack, itemRenderer, leftPos, topPos, slotSize, sizePosOffset);
			} else if (slot instanceof DigistoreCraftingOutputSlot) {
				drawEmptySlot(matrixStack, slot.x + leftPos - 4, slot.y - 4 + topPos, 24, 24);
			} else {
				drawEmptySlot(matrixStack, slot.x + leftPos, slot.y + topPos, 16, 16);
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
		previousSizeTarget = new Vector2D(imageWidth, imageHeight);
		sizeTarget = new Vector2D(xSizeTarget, ySizeTarget);
	}

	public void setDesieredGuiSize(int xSize, int ySize) {
		this.imageWidth = xSize;
		this.imageHeight = ySize;
		previousSizeTarget = new Vector2D(xSize, ySize);
		sizeTarget = previousSizeTarget;
	}

	public void registerWidget(AbstractGuiWidget widget) {
		widgetContainer.registerWidget(widget);
	}

	private void animateScreenSize() {
		// Check if any change occured.
		boolean changeOccured = false;

		// Process the xSize.
		if (Math.abs(imageWidth - sizeTarget.getXi()) > 0) {
			int minimumAnimationVal = sizeTarget.getXi() - imageWidth > 0 ? 1 : -1;
			if (minimumAnimationVal == 1) {
				imageWidth = imageWidth + Math.max(minimumAnimationVal, (sizeTarget.getXi() - imageWidth) / 10);
			} else {
				imageWidth = imageWidth + Math.min(minimumAnimationVal, (sizeTarget.getXi() - imageWidth) / 10);
			}
			isScreenSizeChanging = true;
			changeOccured = true;
		}

		// Process the ySize.
		if (Math.abs(imageHeight - sizeTarget.getYi()) > 0) {
			int minimumAnimationVal = sizeTarget.getYi() - imageHeight > 0 ? 1 : -1;
			if (minimumAnimationVal == 1) {
				imageHeight = imageHeight + Math.max(minimumAnimationVal, (sizeTarget.getYi() - imageHeight) / 15);
			} else {
				imageHeight = imageHeight + Math.min(minimumAnimationVal, (sizeTarget.getYi() - imageHeight) / 15);
			}
			isScreenSizeChanging = true;
			changeOccured = true;
		}

		// If a change occured, raise the changed method. If not, and we were previously
		// changing, raise the change completed method and mark changing as false. THIS
		// CAN BE DONE WAY BETTER - TO DO.
		if (changeOccured) {
			Vector2D expectedDifference = previousSizeTarget.clone().subtract(sizeTarget);
			Vector2D currentDifference = new Vector2D(imageWidth - sizeTarget.getXi(),
					imageHeight - sizeTarget.getYi());
			Vector2D alpha = currentDifference.clone().divide(expectedDifference);
			alpha = new Vector2D(1.0f, 1.0f).subtract(alpha);
			if (Float.isNaN(alpha.getX())) {
				alpha.setX(0.0f);
			}
			if (Float.isNaN(alpha.getY())) {
				alpha.setY(0.0f);
			}

			onScreenSizeChanged(alpha);
		} else if (isScreenSizeChanging) {
			onScreenSizeChangeCompleted();
			isScreenSizeChanging = false;
		}
	}

	protected void onScreenSizeChanged(Vector2D alpha) {

	}

	protected void onScreenSizeChangeCompleted() {

	}

	public GuiTabManager getTabManager() {
		return tabManager;
	}

	@Override
	public void onClose() {
		super.onClose();
	}
}
