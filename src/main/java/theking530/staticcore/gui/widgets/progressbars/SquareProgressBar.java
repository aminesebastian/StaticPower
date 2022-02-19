package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.StaticPowerSprites;

@OnlyIn(Dist.CLIENT)
public class SquareProgressBar extends AbstractProgressBar<SquareProgressBar> {

	public SquareProgressBar(int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		errorDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 8, 8);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(pose, mouseX, mouseY, partialTicks);

		// Draw a red border if erorred and too small. If large enough, draw the red X.
		if (isProcessingErrored) {
			if (getSize().getX() > 18 && getSize().getY() > 18) {
				errorDrawable.draw(pose, 0, 0);
			} else {
				GuiDrawUtilities.drawSlot(pose, getSize().getX(), getSize().getY(), 0, 0, 0, new Color(1.0f, 0.0f, 0.0f));
			}
		} else {
			GuiDrawUtilities.drawSlot(pose, getSize().getX(), getSize().getY(), 0, 0, 0);
		}

		drawRect(pose, 0, 0, ((getSize().getX() * visualCurrentProgresPercentage)), getSize().getY(), new Color(1.0f, 1.0f, 1.0f));
	}

	public static void drawRect(PoseStack pose, float left, float top, float right, float bottom, Color color) {
		if (left < right) {
			float i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			float j = top;
			top = bottom;
			bottom = j;
		}

		GuiDrawUtilities.drawRectangle(pose, right - left, bottom - top, left, top, 0.0f, color);

	}
}
