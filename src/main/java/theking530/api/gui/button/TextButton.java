package theking530.api.gui.button;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import theking530.api.utilities.Color;

public class TextButton extends StandardButton {

	private String text;
	private FontRenderer fontRenderer;

	public TextButton(int width, int height, int xPos, int yPos, String text, Consumer<BaseButton> onClicked) {
		super(width, height, xPos, yPos, onClicked);
		this.text = text;
		fontRenderer = Minecraft.getInstance().fontRenderer;
	}

	@Override
	protected void drawButtonOverlay() {
		fontRenderer.drawStringWithShadow(text, owningGui.getGuiLeft() + xPosition + width / 2 - fontRenderer.getStringWidth(text) / 2,
				owningGui.getGuiTop() + yPosition - fontRenderer.FONT_HEIGHT / 2 + height / 2, new Color(255, 255, 255).encodeInInteger());
	}

	public TextButton setText(String text) {
		this.text = text;
		return this;
	}
}
