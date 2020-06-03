package theking530.api.gui.widgets.progressbars;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.gui.GuiTextures;
import theking530.api.utilities.Vector2D;

public class ArrowProgressBar extends AbstractProgressBar {
	public ArrowProgressBar(int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (machineProcessingComponent != null) {
			if (lastValue != machineProcessingComponent.getProcessingTimeRemaining()) {
				lastValue = machineProcessingComponent.getProcessingTimeRemaining();
				interp = lastValue;
			}
			if (machineProcessingComponent.isProcessing() && interp < lastValue) {
				double seconds = machineProcessingComponent.getProcessingTime() / 20.0;
				double perSecond = (partialTicks) / seconds;
				interp += perSecond;
			}
			Vector2D screenSpacePosition = this.getScreenSpacePosition();
			float adjustedProgress = 0.6875f * interp / (float) machineProcessingComponent.getProcessingTime();

			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.ARROW_PROGRESS_BAR);
			GlStateManager.enableBlend();
			GuiDrawUtilities.drawTexturedModalRect(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0, 0.5f, 1.0f, 1.0f);
			GuiDrawUtilities.drawTexturedModalRect(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX() * adjustedProgress, getSize().getY(), 0, 0, adjustedProgress, 0.5f);
			GlStateManager.disableBlend();
		}
	}
}
