package theking530.common.gui.widgets.progressbars;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.GuiTextures;
import theking530.common.utilities.Vector2D;

public class GrinderProgressBar extends AbstractProgressBar {

	public GrinderProgressBar(int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
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
			Vector2D screenSpacePosition = this.getScreenSpacePosition();
			float adjustedProgress = 0.9f * interp / (float) machineProcessingComponent.getProcessingTime();

			GlStateManager.enableBlend();
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.GRINDER_PROGRESS_BAR);
			GuiDrawUtilities.drawTexturedModalRect(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.25f, 0.0f, 0.75f, 0.5f);
			GuiDrawUtilities.drawTexturedModalRect(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY() * adjustedProgress, 0.25f, 0.5f, 0.75f,
					0.5f + (0.5f * adjustedProgress));

		}
	}
}
