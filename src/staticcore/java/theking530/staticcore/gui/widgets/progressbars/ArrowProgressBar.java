package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class ArrowProgressBar extends AbstractProgressBar<ArrowProgressBar> {
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
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);

		if (flipped) {
			GuiDrawUtilities.drawTexture(matrix, GuiTextures.ARROW_PROGRESS_BAR, -getSize().getX(), getSize().getY(), getSize().getX(), 0, 0.0f, 0, 0.5f, 0.6875f, 1.0f);
			GuiDrawUtilities.drawTexture(matrix, GuiTextures.ARROW_PROGRESS_BAR, -getSize().getX() * visualCurrentProgresPercentage, getSize().getY(), getSize().getX(), 0.5f, 0.0f, 0.0f,
					0.0f, visualCurrentProgresPercentage * 0.6875f, 0.5f);
		} else {
			GuiDrawUtilities.drawTexture(matrix, GuiTextures.ARROW_PROGRESS_BAR, getSize().getX(), getSize().getY(), 0, 0, 0.0f, 0, 0.5f, 0.6875f, 1.0f);
			GuiDrawUtilities.drawTexture(matrix, GuiTextures.ARROW_PROGRESS_BAR, getSize().getX() * visualCurrentProgresPercentage, getSize().getY(), 0, 0 + 0.5f, 0.0f, 0.0f, 0.0f,
					visualCurrentProgresPercentage * 0.6875f, 0.5f);
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(matrix, 2.5f, 0.5f);
		}
	}
}
