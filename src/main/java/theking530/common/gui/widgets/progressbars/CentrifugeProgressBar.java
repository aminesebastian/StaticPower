package theking530.common.gui.widgets.progressbars;

import org.lwjgl.opengl.GL11;

import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.GuiTextures;
import theking530.common.utilities.Vector2D;

public class CentrifugeProgressBar extends AbstractProgressBar {
	public CentrifugeProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 16, 16);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);

		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = visualCurrentProgress / maxProgress;

		GL11.glPushMatrix();
		GL11.glTranslatef(screenSpacePosition.getX() + 1.5f + getSize().getX() / 2, screenSpacePosition.getY() + getSize().getY() / 2, screenSpacePosition.getY() + getSize().getY() / 2);
		GL11.glRotatef(-adjustedProgress * 720.0f, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-(screenSpacePosition.getX() + getSize().getX() / 2), -(screenSpacePosition.getY() + getSize().getY() / 2), -(screenSpacePosition.getY() + getSize().getY() / 2));
		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.CENTRIFUGE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.0f, 0.5f, 1.0f, 1.0f);
		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.CENTRIFUGE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY() * adjustedProgress, 0.0f,
				0.0f, 1.0f, (0.5f * adjustedProgress));
		GL11.glPopMatrix();
	}
}
