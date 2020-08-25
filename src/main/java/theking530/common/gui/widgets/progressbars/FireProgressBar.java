package theking530.common.gui.widgets.progressbars;

import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.GuiTextures;
import theking530.common.utilities.Vector2D;

public class FireProgressBar extends AbstractProgressBar {
	public FireProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 15, 14);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);

		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = visualCurrentProgress / maxProgress;

		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.FIRE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.0f, 0.46875f, 0.4375f,
				0.875f);
		float topOffset = getSize().getY() * (adjustedProgress);

		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.FIRE_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY() + topOffset, getSize().getX(),
				getSize().getY() * (1.0f - adjustedProgress), 0.0f, 0.40625f * adjustedProgress, 0.4375f, 0.40625f);

		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX(), screenSpacePosition.getY());
		}
	}
}
