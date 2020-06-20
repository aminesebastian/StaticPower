package theking530.api.gui.widgets.button;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import theking530.api.utilities.Color;

public class TextButton extends StandardButton {

	private String text;
	private FontRenderer fontRenderer;

	public TextButton(int xPos, int yPos, int width, int height, String text, Consumer<BaseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.text = text;
		fontRenderer = Minecraft.getInstance().fontRenderer;
	}

	@Override
	protected void drawButtonOverlay() {
		fontRenderer.drawStringWithShadow(text, getScreenSpacePosition().getX() + getSize().getX() / 2 - fontRenderer.getStringWidth(text) / 2,
				getScreenSpacePosition().getY() - fontRenderer.FONT_HEIGHT / 2 + getSize().getY() / 2, new Color(255, 255, 255).encodeInInteger());
	}

	public TextButton setText(String text) {
		this.text = text;
		return this;
	}
}
