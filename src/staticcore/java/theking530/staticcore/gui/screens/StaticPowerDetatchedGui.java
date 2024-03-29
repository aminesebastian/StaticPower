package theking530.staticcore.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.AbstractGuiWidget.EInputResult;
import theking530.staticcore.gui.widgets.TopLevelWidget;
import theking530.staticcore.gui.widgets.tabs.GuiTabManager;
import theking530.staticcore.utilities.math.RectangleBounds;
import theking530.staticcore.utilities.math.Vector2D;

public abstract class StaticPowerDetatchedGui extends Screen {

	/** The container responsible for managing all the widget. */
	protected final TopLevelWidget widgetContainer;
	/** The tab manager widget. */
	protected final GuiTabManager tabManager;
	protected int width;
	protected int height;
	protected int leftOffset;
	protected int topOffset;

	protected int mouseX;
	protected int mouseY;
	protected float partialTicks;
	protected boolean isInitialized;
	protected boolean drawDefaultDarkBackground;
	protected boolean visible;

	public StaticPowerDetatchedGui(int width, int height) {
		super(Component.literal(""));
		drawDefaultDarkBackground = true;
		widgetContainer = new TopLevelWidget();
		visible = true;
		registerWidget(tabManager = new GuiTabManager());
		init(Minecraft.getInstance(), width, height);
	}

	@Override()
	public void init() {
		super.init();
		if (!isInitialized) {
			initializeGui();
			isInitialized = true;
		}
	}

	public void initializeGui() {

	}

	public void tick() {
		widgetContainer.tick();
		updateBeforeRender();
	}

	/**
	 * Renders the UI.
	 */
	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (!visible) {
			return;
		}

		super.render(pose, mouseX, mouseY, partialTicks);
		// Cache these values because we dont get them in the background render call.
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.partialTicks = partialTicks;

		// Update the widgets.
		widgetContainer.updateBeforeRender(pose, new Vector2D(width, height), partialTicks, mouseX, mouseY, getScreenBounds());

		// Raise the mouse hovered event for all the widgets,
		widgetContainer.mouseMove(mouseX, mouseY);

		// Render the foreground of all the widgets.
		widgetContainer.renderForeground(pose, mouseX, mouseY, partialTicks, getScreenBounds());

		// Draw any additional foreground elements.
		pose.pushPose();
		pose.translate(leftOffset, topOffset, 0);
		drawForegroundExtras(pose, partialTicks, mouseX, mouseY);
		pose.popPose();

		// Render the widget tooltips as needed.
		widgetContainer.renderTooltips(pose, mouseX, mouseY);
	}

	@Override
	public void renderBackground(PoseStack pose) {
		if (!visible) {
			return;
		}

		if (drawDefaultDarkBackground) {
			super.renderBackground(pose);
		}
		pose.pushPose();
		pose.translate(leftOffset, topOffset, 0);

		// Draw any extras.
		drawBackgroundExtras(pose, partialTicks, mouseX, mouseY);

		// Update the widgets and then draw the background.
		widgetContainer.renderBackground(pose, mouseX, mouseY, partialTicks, getScreenBounds());

		// Draw any widgets that need to appear above slots/items.
		widgetContainer.renderBehindItems(pose, mouseX, mouseY, partialTicks, getScreenBounds());

		// Draw anything infront of the background but behind the items.
		drawBehindItems(pose, partialTicks, mouseX, mouseY);
		pose.popPose();
	}

	/**
	 * Override this method to perform any logic before drawing.
	 * 
	 * @param pose
	 * @param partialTicks The delta time.
	 * @param mouseX       The mouse's x position.
	 * @param mouseY       The mouse's y position.
	 */
	protected void updateBeforeRender() {

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

	public RectangleBounds getScreenBounds() {
		return new RectangleBounds(0, 0, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
	}

	public void setDrawDefaultDarkBackground(boolean drawDefaultDarkBackground) {
		this.drawDefaultDarkBackground = drawDefaultDarkBackground;
	}

	public void registerWidget(AbstractGuiWidget<?> widget) {
		widgetContainer.registerWidget(widget);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean superCallResult = super.mouseClicked(mouseX, mouseY, button);
		if (visible) {
			widgetContainer.mouseClick(mouseX, mouseY, button);
		}
		return superCallResult;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean superCallResult = super.mouseReleased(mouseX, mouseY, button);
		if (visible) {
			widgetContainer.mouseReleased(mouseX, mouseY, button);
		}
		return superCallResult;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		EInputResult result = EInputResult.UNHANDLED;
		if (visible) {
			result = widgetContainer.mouseScrolled(mouseX, mouseY, scrollDelta);
		}
		if (result != EInputResult.HANDLED) {
			return super.mouseScrolled(mouseX, mouseY, scrollDelta);
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		EInputResult result = EInputResult.UNHANDLED;
		if (visible) {
			result = widgetContainer.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		if (result != EInputResult.HANDLED) {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		return true;
	}

	@Override
	public boolean charTyped(char character, int p_charTyped_2_) {
		EInputResult result = EInputResult.UNHANDLED;
		if (visible) {
			result = widgetContainer.characterTyped(character, p_charTyped_2_);
		}
		if (result == EInputResult.UNHANDLED) {
			return super.charTyped(character, p_charTyped_2_);
		}
		return true;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		EInputResult result = EInputResult.UNHANDLED;
		if (visible) {
			result = widgetContainer.keyPressed(key, scanCode, modifiers);
		}
		if (result == EInputResult.UNHANDLED) {
			return super.keyPressed(key, scanCode, modifiers);
		}
		return true;
	}
}
