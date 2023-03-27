package theking530.staticcore.gui.widgets.button;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;

@OnlyIn(Dist.CLIENT)
public class SolidColorButton extends StandardButton {
	private int padding;
	private SDColor color;

	public SolidColorButton(int xPos, int yPos, int width, int height, SDColor color, BiConsumer<StandardButton, MouseButton> onClicked) {
		super(xPos, yPos, width, height, onClicked);
		this.color = color;
	}

	public SolidColorButton setColor(SDColor color) {
		this.color = color;
		return this;
	}

	public SolidColorButton setPadding(int padding) {
		this.padding = padding;
		return this;
	}

	public SDColor getColor() {
		return color;
	}

	public int getPadding() {
		return padding;
	}

	@Override
	protected void drawButtonOverlay(PoseStack pose, int buttonLeft, int buttonTop) {
		Vector2D size = getSize();
		GuiDrawUtilities.drawRectangle(pose, buttonLeft + padding, buttonTop + padding, size.getX() - (padding * 2), size.getY() - (padding * 2), 1.0f, color);
	}
}
