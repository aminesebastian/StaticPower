package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;

public class SquareProgressBar extends AbstractProgressBar {

	public SquareProgressBar(int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		errorDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 8, 8);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);
		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = visualCurrentProgress / maxProgress;

		// Draw a red border if erorred and too small. If large enough, draw the red X.
		if (isProcessingErrored) {
			if (getSize().getX() > 18 && getSize().getY() > 18) {
				errorDrawable.draw(screenSpacePosition.getX(), screenSpacePosition.getY());
			} else {
				GuiDrawUtilities.drawSlot(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), new Color(1.0f, 0.0f, 0.0f));
			}
		} else {
			GuiDrawUtilities.drawSlot(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY());
		}

		drawRect(screenSpacePosition.getX(), screenSpacePosition.getY(), (screenSpacePosition.getX() + (getSize().getX() * adjustedProgress)), screenSpacePosition.getY() + getSize().getY(),
				new Color(1.0f, 1.0f, 1.0f).encodeInInteger());
	}

	public static void drawRect(float left, float top, float right, float bottom, int color) {
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

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos((double) left, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) top, 0.0D).endVertex();
		bufferbuilder.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
