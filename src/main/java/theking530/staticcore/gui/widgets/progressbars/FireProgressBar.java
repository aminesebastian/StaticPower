package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.matrix.MatrixStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;

public class FireProgressBar extends AbstractProgressBar {
	public FireProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 15, 14);
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);

		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = visualCurrentProgress / maxProgress;

		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.FIRE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.0f, 0.46875f, 0.4375f,
				0.875f);
		float topOffset = getSize().getY() * (adjustedProgress);

		if (visualCurrentProgress > 0) {
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.FIRE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY() + topOffset, getSize().getX(),
					getSize().getY() * (1.0f - adjustedProgress), 0.0f, 0.40625f * adjustedProgress, 0.4375f, 0.40625f);
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX(), screenSpacePosition.getY());
		}
	}
}
