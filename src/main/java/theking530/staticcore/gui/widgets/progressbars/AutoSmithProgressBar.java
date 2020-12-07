package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class AutoSmithProgressBar extends AbstractProgressBar {

	public AutoSmithProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 18, 17);
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());

		GuiDrawUtilities.drawTexturedModalRect(GuiTextures.AUTO_SMITH_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY() + 0.5f, getSize().getX(), getSize().getY(), 0.25f,
				0.0f, 0.75f, 0.5f);

		if (visualCurrentProgresPercentage > 0) {
			GuiDrawUtilities.drawTexturedModalRect(GuiTextures.AUTO_SMITH_PROGRESS_BAR, screenSpacePosition.getX(), screenSpacePosition.getY() + 0.5f, getSize().getX(),
					getSize().getY() * (visualCurrentProgresPercentage), 0.25f, 0.5f, 0.75f, 0.5f + (0.5f * visualCurrentProgresPercentage));
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(screenSpacePosition.getX() + 1.0f, screenSpacePosition.getY() + 1.0f);
		}
	}
}
