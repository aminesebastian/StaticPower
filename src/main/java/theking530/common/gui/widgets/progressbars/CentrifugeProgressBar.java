package theking530.common.gui.widgets.progressbars;

import org.lwjgl.opengl.GL11;

import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.GuiTextures;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.tileentities.utilities.interfaces.IProcessing;

public class CentrifugeProgressBar  extends AbstractProgressBar {

	private IProcessing processingEntity;
	public CentrifugeProgressBar(IProcessing processingEntity, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.processingEntity = processingEntity;
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);
		if (lastValue != processingEntity.getCurrentProgress()) {
			lastValue = processingEntity.getCurrentProgress();
			interp = lastValue;
		}
		if (processingEntity.isProcessing()) {
			double seconds = processingEntity.getProcessingTime() / 20.0;
			double perSecond = (partialTicks) / seconds;
			interp += perSecond * 5;
		}
		interp = Math.min(interp, processingEntity.getProcessingTime());
		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = interp / processingEntity.getProcessingTime();

		GL11.glPushMatrix();
		float offset = 0.5f;
		GL11.glTranslatef(screenSpacePosition.getX() + offset + getSize().getX() / 2, screenSpacePosition.getY() + offset + getSize().getY() / 2, screenSpacePosition.getY() + offset + getSize().getY() / 2);
		GL11.glRotatef(adjustedProgress * 720, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-(screenSpacePosition.getX() + offset + getSize().getX() / 2), -(screenSpacePosition.getY() + offset + getSize().getY() / 2), -(screenSpacePosition.getY() + offset + getSize().getY() / 2));
		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.CENTRIFUGE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.0f, 0.5f, 1.0f, 1.0f);
		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.CENTRIFUGE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY() * adjustedProgress, 0.0f, 0.0f, 1.0f, (0.5f * adjustedProgress));
		GL11.glPopMatrix();
	}
}
