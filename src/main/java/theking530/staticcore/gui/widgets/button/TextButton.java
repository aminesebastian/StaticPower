package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public class TextButton extends StandardButton {

	private String text;
	private FontRenderer fontRenderer;

	public TextButton(int xPos, int yPos, int width, int height, String text, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.text = text;
		fontRenderer = Minecraft.getInstance().fontRenderer;
	}

	@Override
	protected void drawButtonOverlay(MatrixStack stack, int buttonLeft, int buttonTop) {
		int width = fontRenderer.getStringWidth(text);

		GuiDrawUtilities.drawStringWithSize(stack, text, getPosition().getXi() + getSize().getX() / 2 + width / 2, getPosition().getYi() - 1 + fontRenderer.FONT_HEIGHT / 2 + getSize().getY() / 2, 1.0f,
				isEnabled() ? Color.EIGHT_BIT_WHITE : Color.EIGHT_BIT_WHITE, true);

		if (!isEnabled()) {
			GuiDrawUtilities.drawColoredRectangle(buttonLeft, buttonTop, getSize().getX(), getSize().getY(), 1.0f, new Color(128.0f, 128.0f, 128.0f, 75.0f));
		}
	}

	public TextButton setText(String text) {
		this.text = text;
		return this;
	}
}
