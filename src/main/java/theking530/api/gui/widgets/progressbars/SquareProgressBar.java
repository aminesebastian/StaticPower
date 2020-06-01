package theking530.api.gui.widgets.progressbars;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.utilities.Color;
import theking530.api.utilities.Vector2D;
import theking530.staticpower.tileentities.utilities.interfaces.IProcessing;

public class SquareProgressBar extends AbstractProgressBar {

	private IProcessing processingEntity;

	public SquareProgressBar(IProcessing processingEntity, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.processingEntity = processingEntity;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (lastValue != processingEntity.getCurrentProgress()) {
			lastValue = processingEntity.getCurrentProgress();
			interp = lastValue;
		}
		if (processingEntity.isProcessing()) {
			float seconds = processingEntity.getProcessingTime() / 20.0f;
			float perSecond = partialTicks / seconds;
			interp += perSecond;
		}
		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		GuiDrawUtilities.drawSlot(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY());
		drawRect(screenSpacePosition.getX(), screenSpacePosition.getY(), (screenSpacePosition.getX() + (getSize().getX() * (interp / ((float) processingEntity.getProcessingTime())))),
				screenSpacePosition.getY() + getSize().getY(), new Color(1.0f, 1.0f, 1.0f).encodeInInteger());
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

//		float f3 = (float) (color >> 24 & 255) / 255.0F;
//		float f = (float) (color >> 16 & 255) / 255.0F;
//		float f1 = (float) (color >> 8 & 255) / 255.0F;
//		float f2 = (float) (color & 255) / 255.0F;
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
