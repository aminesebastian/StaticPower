package theking530.common.gui.widgets.progressbars;

import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.GuiTextures;
import theking530.common.utilities.Vector2D;

public class FusionProgressBar extends AbstractProgressBar {

	public FusionProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 18, 17);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);
		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = visualCurrentProgress / maxProgress;

		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.FUSION_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY() + 0.5f, getSize().getX(), getSize().getY(), 0.25f, 0.0f, 0.75f,
				0.5f);

		if (visualCurrentProgress > 0) {
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.FUSION_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY() + 0.5f, getSize().getX(),
					getSize().getY() * (adjustedProgress), 0.25f, 0.5f, 0.75f, 0.5f + (0.5f * adjustedProgress));
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX() + 1.0f, screenSpacePosition.getY());
		}
	}
}
