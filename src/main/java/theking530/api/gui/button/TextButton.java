package theking530.api.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import theking530.api.utilities.Color;

public class TextButton extends StandardButton {

	private String text;
	private FontRenderer fontRenderer;

	public TextButton(int width, int height, int xPos, int yPos, String text) {
		super(width, height, xPos, yPos);
		this.text = text;
		fontRenderer = Minecraft.getInstance().fontRenderer;
	}

	@Override
	protected void drawExtra() {
		fontRenderer.drawStringWithShadow(text, owningGui.getGuiLeft() + xPosition + width / 2 - fontRenderer.getStringWidth(text) / 2,
				owningGui.getGuiTop() + yPosition - fontRenderer.FONT_HEIGHT / 2 + height / 2, new Color(1.0f, 1.0f, 1.0f).encodeInInteger());
	}

	public void setText(String text) {
		this.text = text;
	}
}
