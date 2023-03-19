package theking530.staticcore.gui.widgets.button;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreGuiTextures;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.math.Vector2D;

@OnlyIn(Dist.CLIENT)
public class StandardButton extends AbstractGuiWidget<StandardButton> {

	public enum MouseButton {
		NONE, LEFT, RIGHT, MIDDLE;
	}

	protected BiConsumer<StandardButton, MouseButton> onClicked;
	protected int mouseX;
	protected int mouseY;
	protected Object data;

	private MouseButton currentlyPressedMouseButton;
	private boolean toggleable;
	private boolean toggled;
	private boolean drawBackground;
	private float clickSoundPitch;
	private boolean clickSoundEnabled;
	private List<Component> tooltip;

	public StandardButton(int xPos, int yPos, int width, int height, BiConsumer<StandardButton, MouseButton> onClickedEvent) {
		super(xPos, yPos, width, height);
		clickSoundPitch = 1.0f;
		onClicked = onClickedEvent;
		currentlyPressedMouseButton = MouseButton.NONE;
		toggleable = false;
		toggled = false;
		clickSoundEnabled = true;
		drawBackground = true;
	}

	public boolean shouldDrawButtonBackground() {
		return drawBackground;
	}

	public StandardButton setShouldDrawButtonBackground(boolean shouldDraw) {
		drawBackground = shouldDraw;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData() {
		if (data == null) {
			return null;
		}
		return (T) data;
	}

	public StandardButton setContainedData(Object dataIn) {
		data = dataIn;
		return this;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (!isVisible()) {
			return;
		}

		// Draw the button and then the overlay.
		if (shouldDrawButtonBackground()) {
			drawButton(pose);
		}
		drawButtonOverlay(pose, 0, 0);
	}

	@Override
	public EInputResult mouseClick(double mouseX, double mouseY, int button) {
		if (!isVisible() || !isEnabled()) {
			return EInputResult.UNHANDLED;
		}
		if (this.isPointInsideBounds(new Vector2D(mouseX, mouseY))) {
			// Set the clicked state.
			currentlyPressedMouseButton = button == 0 ? MouseButton.LEFT : MouseButton.RIGHT;

			// Play the clicked sound.
			playSound(currentlyPressedMouseButton);

			// If toggleable, update the toggled state.
			if (toggleable) {
				toggled = !toggled;
			}

			// Raise the on clicked event.
			onClicked.accept(this, currentlyPressedMouseButton);

			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	@Override
	public EInputResult mouseMove(double mouseX, double mouseY) {
		// Always just update the clicked state to NONE here.
		currentlyPressedMouseButton = MouseButton.NONE;

		if (!isVisible() || !isEnabled()) {
			return EInputResult.UNHANDLED;
		}

		this.mouseX = (int) mouseX;
		this.mouseY = (int) mouseY;

		if (isHovered()) {
			return EInputResult.HANDLED;
		} else {
			return EInputResult.UNHANDLED;
		}
	}

	protected void drawButton(PoseStack pose) {
		boolean shouldDrawHighlighted = isClicked() || isHovered() || isToggled();
		ResourceLocation texture = shouldDrawHighlighted ? StaticCoreGuiTextures.BUTTON_HOVER : StaticCoreGuiTextures.BUTTON;

		float uPixel = 1.0f / 200.0f;
		float vPixel = 1.0f / 20.0f;
		float width = getSize().getX();
		float height = getSize().getY();
		float x = 0;
		float y = 0;
		float zLevel = 0;

		// Body
		GuiDrawUtilities.drawTexture(pose, texture, width - 4, height - 5, x + 2, y + 2, zLevel, uPixel * 2, vPixel * 2, uPixel * 198, vPixel * 17);

		// Corners
		GuiDrawUtilities.drawTexture(pose, texture, 2, 2, x, y, zLevel, 0.0f, 0.0f, 2 * uPixel, 2 * vPixel);
		GuiDrawUtilities.drawTexture(pose, texture, 2, 2, x + width - 2, y, zLevel, 198 * uPixel, 0, 1, 2 * vPixel);
		GuiDrawUtilities.drawTexture(pose, texture, 2, 3, x, y + height - 3, zLevel, 0.0f, 17 * vPixel, 2 * uPixel, 20 * vPixel);
		GuiDrawUtilities.drawTexture(pose, texture, 2, 3, x + width - 2, y + height - 3, zLevel, 198 * uPixel, 17 * vPixel, 1, 20 * vPixel);

		// Sides
		GuiDrawUtilities.drawTexture(pose, texture, width - 4, 2, x + 2, y, zLevel, 2 * uPixel, 0, 198 * uPixel, 2 * vPixel);
		GuiDrawUtilities.drawTexture(pose, texture, 2, height - 5, x, y + 2, zLevel, 0.0f, 2 * vPixel, 2 * uPixel, 17 * vPixel);
		GuiDrawUtilities.drawTexture(pose, texture, 2, height - 5, x + width - 2, y + 2, zLevel, 198 * uPixel, 2 * vPixel, 1, 17 * vPixel);
		GuiDrawUtilities.drawTexture(pose, texture, width - 4, 3, x + 2, y + height - 3, zLevel, 2 * uPixel, 17 * vPixel, 198 * uPixel, 20 * vPixel);
	}

	protected void drawButtonOverlay(PoseStack stack, int buttonLeft, int buttonTop) {
	}

	protected void playSound(MouseButton state) {
		if (clickSoundEnabled) {
			float pitch = state == MouseButton.LEFT ? clickSoundPitch : clickSoundPitch * 1.1f;
			playSoundLocally(SoundEvents.UI_BUTTON_CLICK, 1.0f, pitch);
		}
	}

	public StandardButton setToggleable(boolean toggleable) {
		this.toggleable = toggleable;
		return this;
	}

	public StandardButton setToggled(boolean toggled) {
		this.toggled = toggled;
		return this;
	}

	public boolean isToggleable() {
		return toggled;
	}

	public boolean isToggled() {
		return toggled;
	}

	public boolean isClicked() {
		return currentlyPressedMouseButton != MouseButton.NONE;
	}

	public MouseButton getClickedState() {
		return currentlyPressedMouseButton;
	}

	public StandardButton setClicked(MouseButton newClickedState) {
		this.currentlyPressedMouseButton = newClickedState;
		return this;
	}

	public boolean isClickSoundEnabled() {
		return clickSoundEnabled;
	}

	public StandardButton setClickSoundEnabled(boolean enabled) {
		clickSoundEnabled = enabled;
		return this;
	}

	public StandardButton setTooltip(Component... tooltip) {
		this.tooltip = Arrays.asList(tooltip);
		return this;
	}

	public StandardButton setClickSoundPitch(float newSoundPitch) {
		this.clickSoundPitch = newSoundPitch;
		return this;
	}

	public float getClickSoundPitch() {
		return clickSoundPitch;
	}

	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		if (tooltip != null) {
			tooltips.addAll(tooltip);
		}
	}
}
