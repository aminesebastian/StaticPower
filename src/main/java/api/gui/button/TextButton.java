package api.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import theking530.staticpower.assists.utilities.GuiUtilities;

public class TextButton extends StandardButton{

	private String text;
	private FontRenderer fontRenderer;
	public TextButton(int width, int height, int xPos, int yPos, String text) {
		super(width, height, xPos, yPos);
		this.text = text;
		fontRenderer = Minecraft.getMinecraft().fontRenderer;
	}

	@Override
	protected void drawExtra() {
		fontRenderer.drawStringWithShadow(text, xPosition+fontRenderer.getStringWidth(text)/2+width/5, yPosition+height/3, GuiUtilities.getColor(255, 255, 255));	
	}
	public void setText(String text){
		this.text = text;
	}
}
