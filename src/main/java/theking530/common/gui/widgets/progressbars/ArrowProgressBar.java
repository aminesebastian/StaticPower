package theking530.common.gui.widgets.progressbars;

import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.GuiTextures;
import theking530.common.utilities.Vector2D;

public class ArrowProgressBar extends AbstractProgressBar {
	private boolean flipped;

	public ArrowProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 22, 16);
	}

	public ArrowProgressBar setFlipped(boolean flipped) {
		this.flipped = flipped;
		this.setPosition(this.getPosition().getX() - this.getSize().getX(), this.getPosition().getY());
		return this;
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(mouseX, mouseY, partialTicks);

		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = visualCurrentProgress / maxProgress;

		if (flipped) {
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX() + getSize().getX(), screenSpacePosition.getY(), -getSize().getX(), getSize().getY(), 0,
					0.5f, 0.6875f, 1.0f);
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX() + getSize().getX(), screenSpacePosition.getY(), -getSize().getX() * adjustedProgress,
					getSize().getY(), 0.0f, 0.0f, adjustedProgress * 0.6875f, 0.5f);
		} else {
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0, 0.5f, 0.6875f, 1.0f);
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX() * adjustedProgress, getSize().getY(), 0.0f,
					0.0f, adjustedProgress * 0.6875f, 0.5f);
		}
	}
}
