package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import theking530.staticcore.utilities.Color;

public class TextButton extends StandardButton {

	private String text;
	private FontRenderer fontRenderer;

	public TextButton(int xPos, int yPos, int width, int height, String text, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.text = text;
		fontRenderer = Minecraft.getInstance().fontRenderer;
	}

	@Override
	protected void drawButtonOverlay(int buttonLeft, int buttonTop) {
		fontRenderer.drawStringWithShadow(text, buttonLeft + getSize().getX() / 2 - fontRenderer.getStringWidth(text) / 2, buttonTop - fontRenderer.FONT_HEIGHT / 2 + getSize().getY() / 2, new Color(255, 255, 255).encodeInInteger());
	}

	public TextButton setText(String text) {
		this.text = text;
		return this;
	}
}