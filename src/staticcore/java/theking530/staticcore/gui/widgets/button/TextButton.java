package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;

@OnlyIn(Dist.CLIENT)
public class TextButton extends StandardButton {
	private String text;

	public TextButton(int xPos, int yPos, int height, String text, BiConsumer<StandardButton, MouseButton> onClicked) {
		this(xPos, yPos, 0, height, text, onClicked);
		this.setSize(this.getFontRenderer().width(text) + 8, height);
	}

	public TextButton(int xPos, int yPos, int width, int height, String text, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.text = text;
	}

	@Override
	protected void drawButtonOverlay(PoseStack pose, int buttonLeft, int buttonTop) {
		int width = getFontRenderer().width(text);

		GuiDrawUtilities.drawString(pose, text, getSize().getX() / 2 + width / 2, -1 + getFontRenderer().lineHeight / 2 + getSize().getY() / 2, 0.0f, 1.0f,
				isEnabled() ? SDColor.EIGHT_BIT_WHITE : SDColor.EIGHT_BIT_WHITE, true);

		if (!isEnabled()) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), new SDColor(0.5f, 0.5f, 0.5f, 0.75f));
		}
	}

	public TextButton setText(String text) {
		this.text = text;
		return this;
	}
}
