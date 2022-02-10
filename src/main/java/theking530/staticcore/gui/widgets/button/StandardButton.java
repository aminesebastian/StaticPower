package theking530.staticcore.gui.widgets.button;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;

@OnlyIn(Dist.CLIENT)
public class StandardButton extends AbstractGuiWidget {

	public enum MouseButton {
		NONE, LEFT, RIGHT, MIDDLE;
	}

	protected BiConsumer<StandardButton, MouseButton> onClicked;
	protected int mouseX;
	protected int mouseY;
	protected Object data;

	private boolean hovered;
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
		hovered = false;
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
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (!isVisible()) {
			return;
		}

		// Calculate the button's left and top.
		Vector2D position = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		int buttonLeft = (int) position.getX();
		int buttonTop = (int) position.getY();

		// Draw the button and then the overlay.
		if (shouldDrawButtonBackground()) {
			drawButton(matrix, buttonLeft, buttonTop);
		}
		drawButtonOverlay(matrix, buttonLeft, buttonTop);
	}

	@Override
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
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
	public void mouseMove(int mouseX, int mouseY) {
		// Always just update the clicked state to NONE here.
		currentlyPressedMouseButton = MouseButton.NONE;

		if (!isVisible() || !isEnabled()) {
			return;
		}

		this.mouseX = mouseX;
		this.mouseY = mouseY;
		if (isPointInsideBounds(new Vector2D(mouseX, mouseY))) {
			hovered = true;
			return;
		}
		hovered = false;
	}

	protected void drawButton(PoseStack stack, int transformedButtonLeft, int transformedButtonTop) {
		GuiDrawUtilities.drawDefaultButton(isClicked() || isHovered() || isToggled(), transformedButtonLeft, transformedButtonTop, getSize().getX(), getSize().getY(), 0.0f);
	}

	protected void drawButtonOverlay(PoseStack stack, int buttonLeft, int buttonTop) {
	}

	@Override
	public StandardButton setEnabled(boolean isEnabled) {
		super.setEnabled(isEnabled);
		if (!isEnabled) {
			this.hovered = false;
		}
		return this;
	}

	protected void playSound(MouseButton state) {
		if (clickSoundEnabled) {
			float pitch = state == MouseButton.LEFT ? clickSoundPitch : clickSoundPitch * 1.1f;
			LocalPlayer player = Minecraft.getInstance().player;
			player.level.playSound(player, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0f, pitch);
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

	public boolean isHovered() {
		return hovered;
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
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		if (tooltip != null) {
			tooltips.addAll(tooltip);
		}
	}
}
