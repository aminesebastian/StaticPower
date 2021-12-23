package theking530.staticcore.gui.widgets.progressbars;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class CentrifugeProgressBar extends AbstractProgressBar {
	public CentrifugeProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 16, 16);
	}

	@Override
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		float smoothedProgress = visualCurrentProgresPercentage * visualCurrentProgresPercentage;

		GL11.glPushMatrix();
		GL11.glTranslatef(screenSpacePosition.getX() + 1.5f + getSize().getX() / 2, screenSpacePosition.getY() + getSize().getY() / 2, screenSpacePosition.getY() + getSize().getY() / 2);
		GL11.glRotatef(-smoothedProgress * 3600.0f, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-(screenSpacePosition.getX() + getSize().getX() / 2), -(screenSpacePosition.getY() + getSize().getY() / 2), -(screenSpacePosition.getY() + getSize().getY() / 2));
		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.CENTRIFUGE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.0f, 0.5f, 1.0f, 1.0f);
		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.CENTRIFUGE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY() * visualCurrentProgresPercentage, 0.0f, 0.0f, 1.0f,
				(0.5f * visualCurrentProgresPercentage));
		GL11.glPopMatrix();

		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX() + 1.5f, screenSpacePosition.getY() + 0.5f);
		}
	}
}
