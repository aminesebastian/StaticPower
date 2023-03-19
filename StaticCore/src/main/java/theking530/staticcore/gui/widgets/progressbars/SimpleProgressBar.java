package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;

public class SimpleProgressBar extends AbstractProgressBar<SimpleProgressBar> {
	private boolean flipped;
	private SDColor barColor;
	private SDColor emptyBarColor;

	public SimpleProgressBar(int xPosition, int yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		barColor = new SDColor(3.5f, 0.5f, 0.5f, 1.0f);
		emptyBarColor = new SDColor(0.4f, 0.4f, 0.4f, 1.0f);
	}

	public SimpleProgressBar setFlipped(boolean flipped) {
		this.flipped = flipped;
		this.setPosition(this.getPosition().getX() - this.getSize().getX(), this.getPosition().getY());
		return this;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(pose, mouseX, mouseY, partialTicks);

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

		// Handle the glow effect for when progress is completed.
		SDColor actualBarColor = barColor.copy();
		if (visualCurrentProgresPercentage >= 1.0f) {
			float glowFactor = GuiDrawUtilities.getSinFunction(5.0f, 3.0f);
			actualBarColor.setAlpha(glowFactor);
		}

		if (flipped) {
			GuiDrawUtilities.drawGenericBackground(pose, getSize().getXi(), getSize().getYi(), 0, 0, 0, emptyBarColor);
			if (width >= 7) {
				GuiDrawUtilities.drawGenericBackground(pose, width, getSize().getYi(), 0, 0, 0, actualBarColor);
			}
		} else {
			GuiDrawUtilities.drawGenericBackground(pose, getSize().getXi(), getSize().getYi(), 0, 0, 0, emptyBarColor);
			if (width >= 7) {
				GuiDrawUtilities.drawGenericBackground(pose, width, getSize().getYi(), 0, 0, 0, actualBarColor);
			}
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(pose, 2.5f, 0.5f);
		}

		if (visualCurrentProgresPercentage >= 1.0f) {
			GuiDrawUtilities.drawStringCentered(pose, "Completed!", (getSize().getXi() / 2), (getSize().getYi() / 2) + 2f, 0.0f, 0.5f, SDColor.EIGHT_BIT_WHITE, true);
		} else {
			GuiDrawUtilities.drawStringCentered(pose, GuiTextUtilities.formatNumberAsStringNoDecimal(percent).getString() + "%",
					Math.min(Math.max(width, 12 + (visualCurrentProgresPercentage * getSize().getX() - 5)), getSize().getXi() - 10), (getSize().getYi() / 2) + 2f, 0.0f, 0.5f, SDColor.EIGHT_BIT_WHITE,
					true);
		}
	}
}
