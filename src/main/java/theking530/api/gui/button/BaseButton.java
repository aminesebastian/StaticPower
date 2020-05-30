package theking530.api.gui.button;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import theking530.api.gui.widgets.IGuiWidget;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public abstract class BaseButton implements IGuiWidget {

	public enum ClickedButton {
		NONE, LEFT, RIGHT, MIDDLE;
	}

	protected int width;
	protected int height;
	protected int xPosition;
	protected int yPosition;
	protected int mouseX;
	protected int mouseY;

	private boolean hovered = false;
	private boolean isVisible = true;
	private ClickedButton clicked = ClickedButton.NONE;

	private boolean toggleable = false;
	private boolean toggled = false;

	private float clickSoundPitch;
	protected StaticPowerContainerGui<?> owningGui;
	protected Consumer<BaseButton> onClicked;

	private List<ITextComponent> tooltip;

	public BaseButton(int width, int height, int xPos, int yPos, Consumer<BaseButton> onClickedEvent) {
		this.width = width;
		this.height = height;
		xPosition = xPos;
		yPosition = yPos;
		clickSoundPitch = 1.0f;
		onClicked = onClickedEvent;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (owningGui == null) {
			return;
		}
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
		if (owningGui == null) {
			return EInputResult.UNHANDLED;
		}
		if (!isVisible()) {
			return EInputResult.UNHANDLED;
		}
		if (mouseX > owningGui.getGuiLeft() + xPosition && mouseX < owningGui.getGuiLeft() + xPosition + width && isVisible) {
			if (mouseY > owningGui.getGuiTop() + yPosition && mouseY < owningGui.getGuiTop() + yPosition + height) {
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
		}
		return EInputResult.UNHANDLED;
	}

	@Override
	public void mouseHover(int mouseX, int mouseY) {
		// Always just update the clicked state to NONE here.
		clicked = ClickedButton.NONE;

		if (owningGui == null) {
			return;
		}
		if (!isVisible()) {
			return;
		}

		this.mouseX = mouseX;
		this.mouseY = mouseY;
		if (mouseX > owningGui.getGuiLeft() + xPosition && mouseX < owningGui.getGuiLeft() + xPosition + width && isVisible) {
			if (mouseY > owningGui.getGuiTop() + yPosition && mouseY < owningGui.getGuiTop() + yPosition + height) {
				hovered = true;
				return;
			}
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
	public IGuiWidget setPosition(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		return this;
	}

	@Override
	public IGuiWidget setSize(int xSize, int ySize) {
		width = xSize;
		height = ySize;
		return this;
	}

	@Override
	public void getTooltips(List<ITextComponent> tooltips, boolean showAdvanced) {
		tooltips.addAll(tooltip);
	}

	@Override
	public boolean shouldDrawTooltip(int mouseX, int mouseY) {
		return isHovered();
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public IGuiWidget setVisible(boolean visible) {
		this.isVisible = visible;
		return this;
	}

	@Override
	public void setOwningGui(StaticPowerContainerGui<?> owningGui) {
		this.owningGui = owningGui;
	}
}
