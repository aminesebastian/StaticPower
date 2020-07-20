package theking530.common.gui.widgets.progressbars;

import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.GuiTextures;
import theking530.common.utilities.Vector2D;

public class GrinderProgressBar extends AbstractProgressBar {

	public GrinderProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 18, 17);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);
		if (machineProcessingComponent != null) {
			if (lastValue != machineProcessingComponent.getCurrentProcessingTime()) {
				lastValue = machineProcessingComponent.getCurrentProcessingTime();
				interp = lastValue;
			}
			if (machineProcessingComponent.isProcessing() && interp < lastValue) {
				double seconds = machineProcessingComponent.getProcessingTime() / 20.0;
				double perSecond = (partialTicks) / seconds;
				interp += perSecond;
			}
		} else {
			if (lastValue != currentProgress) {
				lastValue = currentProgress;
				interp = lastValue;
			}
			if (interp < lastValue) {
				double seconds = maxProgress / 20.0;
				double perSecond = (partialTicks) / seconds;
				interp += perSecond;
			}
		}

		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = interp / (float) maxProgress;

		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.GRINDER_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.25f, 0.0f, 0.75f, 0.5f);
		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.GRINDER_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY() * adjustedProgress, 0.25f, 0.5f, 0.75f, 0.5f + (0.5f * adjustedProgress));
	}
}
