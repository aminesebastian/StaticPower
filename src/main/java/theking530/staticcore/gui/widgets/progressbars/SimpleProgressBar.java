package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class SimpleProgressBar extends AbstractProgressBar<SimpleProgressBar> {
	private boolean flipped;
	private Color barColor;
	private Color emptyBarColor;

	public SimpleProgressBar(int xPosition, int yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		barColor = new Color(3.5f, 0.5f, 0.5f, 1.0f);
		emptyBarColor = new Color(0.4f, 0.4f, 0.4f, 1.0f);
	}

	public SimpleProgressBar setFlipped(boolean flipped) {
		this.flipped = flipped;
		this.setPosition(this.getPosition().getX() - this.getSize().getX(), this.getPosition().getY());
		return this;
	}

	@Override
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		int width = (int) (getSize().getXi() * visualCurrentProgresPercentage);
		if (width < 7 && currentProgress > 0) {
			width = 7;
		} else if (width == getSize().getXi() && visualCurrentProgresPercentage < 1.0f) {
			width = getSize().getXi() - 1;
		}

		int percent = (int) (visualCurrentProgresPercentage * 100);
		if (visualCurrentProgresPercentage > 0 && percent == 0) {
			percent = 1;
		}

		if (flipped) {
			GuiDrawUtilities.drawGenericBackground(getSize().getXi(), getSize().getYi(), (int) screenSpacePosition.getX(), (int) screenSpacePosition.getY(), 0, emptyBarColor);
			if (width >= 7) {
				GuiDrawUtilities.drawGenericBackground(width, getSize().getYi(), (int) screenSpacePosition.getX(), (int) screenSpacePosition.getY(), 0, barColor);
			}
		} else {
			GuiDrawUtilities.drawGenericBackground(getSize().getXi(), getSize().getYi(), (int) screenSpacePosition.getX(), (int) screenSpacePosition.getY(), 0, emptyBarColor);
			if (width >= 7) {
				GuiDrawUtilities.drawGenericBackground(width, getSize().getYi(), (int) screenSpacePosition.getX(), (int) screenSpacePosition.getY(), 0, barColor);
			}
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX() + 2.5f, screenSpacePosition.getY() + 0.5f);
		}

		if (visualCurrentProgresPercentage >= 1.0f) {
			GuiDrawUtilities.drawStringWithSizeCentered(matrix, "Completed!", screenSpacePosition.getX() + (getSize().getXi() / 2), screenSpacePosition.getY() + (getSize().getYi() / 2) + 2f, 0.5f,
					Color.EIGHT_BIT_WHITE, true);
		} else {
			GuiDrawUtilities.drawStringWithSizeCentered(matrix, GuiTextUtilities.formatNumberAsStringNoDecimal(percent).getString() + "%",
					screenSpacePosition.getX() + Math.min(Math.max(width, 12 + (visualCurrentProgresPercentage * getSize().getX() - 5)), getSize().getXi() - 10),
					screenSpacePosition.getY() + (getSize().getYi() / 2) + 2f, 0.5f, Color.EIGHT_BIT_WHITE, true);
		}
	}
}
