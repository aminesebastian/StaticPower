package theking530.staticpower.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.events.GuiEventListener;
import theking530.staticcore.gui.WidgetContainer;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.gui.widgets.tabs.GuiTabManager;
import theking530.staticcore.utilities.Vector2D;

public abstract class StaticPowerDetatchedGui implements GuiEventListener {

	/** The container responsible for managing all the widget. */
	protected final WidgetContainer widgetContainer;
	/** The tab manager widget. */
	protected final GuiTabManager tabManager;
	protected int width;
	protected int height;
	protected int leftOffset;
	protected int topOffset;

	protected int mouseX;
	protected int mouseY;
	protected float partialTicks;

	public StaticPowerDetatchedGui(int width, int height) {
		widgetContainer = new WidgetContainer();
		registerWidget(tabManager = new GuiTabManager());
	}

	public void updateData(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		widgetContainer.tick();
	}

	/**
	 * Renders the UI.
	 */
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// Cache these values because we dont get them in the background render call.
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.partialTicks = partialTicks;

		// Raise the mouse hovered event for all the widgets,
		widgetContainer.handleMouseMove(mouseX, mouseY);

		// Render the foreground of all the widgets.
		widgetContainer.renderForegound(pose, mouseX, mouseY, partialTicks);

		// Draw any additional foreground elements.
		pose.pushPose();
		pose.translate(leftOffset, topOffset, 0);
		drawForegroundExtras(pose, partialTicks, mouseX, mouseY);
		pose.popPose();

		// Render the widget tooltips as needed.
		widgetContainer.renderTooltips(pose, mouseX, mouseY);
	}

	public void renderBackground(PoseStack pose) {
		// Update the widgets and then draw the background.
		widgetContainer.update(pose, new Vector2D(leftOffset, topOffset), new Vector2D(width, height), partialTicks, mouseX, mouseY);
		widgetContainer.renderBackground(pose, mouseX, mouseY, partialTicks);

		// Draw any extras.
		pose.pushPose();
		pose.translate(leftOffset, topOffset, 0);
		drawBackgroundExtras(pose, partialTicks, mouseX, mouseY);
		pose.popPose();

		// Draw any widgets that need to appear above slots/items.
		widgetContainer.renderBehindItems(pose, mouseX, mouseY, partialTicks);

		// Draw anything infront of the background but behind the items.
		pose.pushPose();
		pose.translate(leftOffset, topOffset, 0);
		drawBehindItems(pose, partialTicks, mouseX, mouseY);
		pose.popPose();
	}

	/**
	 * Override this method to draw any additional background features (features
	 * that should appear behind items).
	 * 
	 * @param pose
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	/**
	 * Override this method to draw any additional background features (features
	 * that should appear infront of the background, but behind items.).
	 * 
	 * @param pose
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawBehindItems(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	/**
	 * Override this method to draw any additional foreground features (features
	 * that should appear in front of items).
	 * 
	 * @param pose
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		EInputResult result = widgetContainer.handleMouseClick(mouseX, mouseY, button);
		return result == EInputResult.HANDLED;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		EInputResult result = widgetContainer.handleMouseScrolled(mouseX, mouseY, scrollDelta);
		return result == EInputResult.HANDLED;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		EInputResult result = widgetContainer.handleMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		return result == EInputResult.HANDLED;
	}

	@Override
	public boolean charTyped(char character, int p_charTyped_2_) {
		EInputResult result = widgetContainer.characterTyped(character, p_charTyped_2_);
		return result == EInputResult.HANDLED;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		EInputResult result = widgetContainer.handleKeyPressed(key, scanCode, modifiers);
		return result == EInputResult.HANDLED;
	}

	public void registerWidget(AbstractGuiWidget widget) {
		widgetContainer.registerWidget(widget);
	}

}
