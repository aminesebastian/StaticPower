package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
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
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);

		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());

		if (flipped) {
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX() + getSize().getX(), screenSpacePosition.getY(), -getSize().getX(), getSize().getY(), 0, 0.5f, 0.6875f, 1.0f);
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX() + getSize().getX(), screenSpacePosition.getY(), -getSize().getX() * visualCurrentProgresPercentage, getSize().getY(), 0.0f,
					0.0f, visualCurrentProgresPercentage * 0.6875f, 0.5f);
		} else {
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0, 0.5f, 0.6875f, 1.0f);
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.ARROW_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX() * visualCurrentProgresPercentage, getSize().getY(), 0.0f, 0.0f,
					visualCurrentProgresPercentage * 0.6875f, 0.5f);
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX() + 2.5f, screenSpacePosition.getY() + 0.5f);
		}
	}
}
