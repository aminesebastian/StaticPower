package theking530.api.gui.button;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.AbstractGuiWidget;
import theking530.api.utilities.Vector2D;

public abstract class BaseButton extends AbstractGuiWidget {

	public enum ClickedButton {
		NONE, LEFT, RIGHT, MIDDLE;
	}

	protected int mouseX;
	protected int mouseY;

	private boolean hovered = false;
	private ClickedButton clicked = ClickedButton.NONE;

	private boolean toggleable = false;
	private boolean toggled = false;

	private float clickSoundPitch;
	protected Consumer<BaseButton> onClicked;

	private List<ITextComponent> tooltip;

	public BaseButton(int xPos, int yPos, int width, int height, Consumer<BaseButton> onClickedEvent) {
		super(xPos, yPos, width, height);
		clickSoundPitch = 1.0f;
		onClicked = onClickedEvent;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (!isVisible()) {
			return;
		}
		drawButton();
		drawButtonOverlay();
	}

	public void renderForeground(int mouseX, int mouseY, float partialTicks) {
	}

	@Override
	public EInputResult mouseClick(int mouseX, int mouseY, int button) {
		if (!isVisible()) {
			return EInputResult.UNHANDLED;
		}
		if (this.isPointInsideBounds(new Vector2D(mouseX, mouseY))) {
			// Set the clicked state.
			clicked = button == 0 ? ClickedButton.LEFT : ClickedButton.RIGHT;

			// Play the clicked sound.
			playSound(clicked);

			// If toggleable, update the toggled state.
			if (toggleable) {
				toggled = !toggled;
			}

			// Raise the on clicked event.
			onClicked.accept(this);
			return EInputResult.HANDLED;
		}
		return EInputResult.UNHANDLED;
	}

	@Override
	public void mouseHover(int mouseX, int mouseY) {
		// Always just update the clicked state to NONE here.
		clicked = ClickedButton.NONE;

		if (!isVisible()) {
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

	protected abstract void drawButton();

	protected void drawButtonOverlay() {
	}

	protected void playSound(ClickedButton state) {
		float pitch = state == ClickedButton.LEFT ? clickSoundPitch : clickSoundPitch * 1.1f;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		player.world.playSound(player, player.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0f, pitch);
	}

	public BaseButton setToggleable(boolean toggleable) {
		this.toggleable = toggleable;
		return this;
	}

	public BaseButton setToggled(boolean toggled) {
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
		return clicked != ClickedButton.NONE;
	}

	public ClickedButton getClickedState() {
		return clicked;
	}

	public BaseButton setClicked(ClickedButton newClickedState) {
		this.clicked = newClickedState;
		return this;
	}

	public BaseButton setTooltip(ITextComponent... tooltip) {
		this.tooltip = Arrays.asList(tooltip);
		return this;
	}

	public BaseButton setClickSoundPitch(float newSoundPitch) {
		this.clickSoundPitch = newSoundPitch;
		return this;
	}

	public float getClickSoundPitch() {
		return clickSoundPitch;
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		if(tooltip != null) {
			tooltips.addAll(tooltip);
		}
	}
}
