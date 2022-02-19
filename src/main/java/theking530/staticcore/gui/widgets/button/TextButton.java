package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public class TextButton extends StandardButton {

	private String text;
	private Font fontRenderer;

	public TextButton(int xPos, int yPos, int width, int height, String text, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.text = text;
		fontRenderer = Minecraft.getInstance().font;
	}

	@Override
	protected void drawButtonOverlay(PoseStack pose, int buttonLeft, int buttonTop) {
		int width = fontRenderer.width(text);

		GuiDrawUtilities.drawString(pose, text, getSize().getX() / 2 + width / 2, -1 + fontRenderer.lineHeight / 2 + getSize().getY() / 2, 0.0f, 1.0f,
				isEnabled() ? Color.EIGHT_BIT_WHITE : Color.EIGHT_BIT_WHITE, true);

		if (!isEnabled()) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), new Color(0.5f, 0.5f, 0.5f, 0.75f));
		}
	}

	public TextButton setText(String text) {
		this.text = text;
		return this;
	}
}
