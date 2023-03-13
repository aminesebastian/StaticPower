package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class GrinderProgressBar extends AbstractProgressBar<GrinderProgressBar> {

	public GrinderProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 18, 17);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(pose, mouseX, mouseY, partialTicks);

		GuiDrawUtilities.drawTexture(pose, GuiTextures.GRINDER_PROGRESS_BAR, getSize().getX(), getSize().getY(), 0, 0, 0.0f, 0.25f, 0.0f, 0.75f, 0.5f);

		if (visualCurrentProgresPercentage > 0) {
			GuiDrawUtilities.drawTexture(pose, GuiTextures.GRINDER_PROGRESS_BAR, getSize().getX(), getSize().getY() * (visualCurrentProgresPercentage), 0, 0, 0.0f, 0.25f, 0.5f, 0.75f,
					0.5f + (0.5f * visualCurrentProgresPercentage));
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(pose, 1.0f, 0);
		}
	}
}
