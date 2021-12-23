package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;

@OnlyIn(Dist.CLIENT)
public class SquareProgressBar extends AbstractProgressBar {

	public SquareProgressBar(int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		errorDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 8, 8);
	}

	@Override
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());

		// Draw a red border if erorred and too small. If large enough, draw the red X.
		if (isProcessingErrored) {
			if (getSize().getX() > 18 && getSize().getY() > 18) {
				errorDrawable.draw(screenSpacePosition.getX(), screenSpacePosition.getY());
			} else {
				GuiDrawUtilities.drawSlot(null, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0, new Color(1.0f, 0.0f, 0.0f));
			}
		} else {
			GuiDrawUtilities.drawSlot(null, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0);
		}

		drawRect(screenSpacePosition.getX(), screenSpacePosition.getY(), (screenSpacePosition.getX() + (getSize().getX() * visualCurrentProgresPercentage)), screenSpacePosition.getY() + getSize().getY(),
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

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		GlStateManager._enableBlend();
		GlStateManager._disableTexture();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		bufferbuilder.vertex((double) left, (double) bottom, 0.0D).endVertex();
		bufferbuilder.vertex((double) right, (double) bottom, 0.0D).endVertex();
		bufferbuilder.vertex((double) right, (double) top, 0.0D).endVertex();
		bufferbuilder.vertex((double) left, (double) top, 0.0D).endVertex();
		tessellator.end();
		GlStateManager._enableTexture();
		GlStateManager._disableBlend();
	}
}
