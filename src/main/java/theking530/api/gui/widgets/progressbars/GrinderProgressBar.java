package theking530.api.gui.widgets.progressbars;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.gui.GuiTextures;
import theking530.api.utilities.Vector2D;
import theking530.staticpower.tileentities.utilities.interfaces.IProcessing;

public class GrinderProgressBar extends AbstractProgressBar {

	private IProcessing processingEntity;

	public GrinderProgressBar(IProcessing processingEntity, int xPosition, int yPosition, int xSize, int ySize) {
		super(xPosition, yPosition, xSize, ySize);
		this.processingEntity = processingEntity;
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		if (lastValue != processingEntity.getCurrentProgress()) {
			lastValue = processingEntity.getCurrentProgress();
			interp = lastValue;
		}
		if (processingEntity.isProcessing()) {
			double seconds = processingEntity.getProcessingTime() / 20.0;
			double perSecond = (partialTicks) / seconds;
			interp += perSecond;
		}
		interp = Math.min(interp, processingEntity.getProcessingTime());
		Vector2D screenSpacePosition = this.getScreenSpacePosition();
		float adjustedProgress = 0.9f * interp / (float) processingEntity.getProcessingTime();

		GlStateManager.enableBlend();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.GRINDER_PROGRESS_BAR);
		GuiDrawUtilities.drawTexturedModalRect(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY(), 0.25f, 0.0f, 0.75f, 0.5f);
		GuiDrawUtilities.drawTexturedModalRect(screenSpacePosition.getX(), screenSpacePosition.getY(), getSize().getX(), getSize().getY() * adjustedProgress, 0.25f, 0.5f, 0.75f,
				0.5f + (0.5f * adjustedProgress));

		GlStateManager.disableBlend();
	}
}
