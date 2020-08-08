package theking530.common.gui.widgets.progressbars;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.utilities.Color;
import theking530.common.utilities.Vector2D;

public class SquareProgressBar extends AbstractProgressBar {

	public SquareProgressBar(int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);
		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = visualCurrentProgress / maxProgress;

		GuiDrawUtilities.drawSlot(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY());
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
