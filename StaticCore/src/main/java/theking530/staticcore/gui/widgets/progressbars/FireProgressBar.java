package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreGuiTextures;

@OnlyIn(Dist.CLIENT)
public class FireProgressBar extends AbstractProgressBar<FireProgressBar> {
	public FireProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 15, 14);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);

		GuiDrawUtilities.drawTexture(matrix, StaticCoreGuiTextures.FIRE_PROGRESS_BAR, getSize().getX(), getSize().getY(), 0, 0, 0.0f, 0.0f, 0.46875f, 0.4375f, 0.875f);
		float topOffset = getSize().getY() * (visualCurrentProgresPercentage);

		if (visualCurrentProgresPercentage > 0) {
			GuiDrawUtilities.drawTexture(matrix, StaticCoreGuiTextures.FIRE_PROGRESS_BAR, getSize().getX(), getSize().getY() * (1.0f - visualCurrentProgresPercentage), 0, 0 + topOffset,
					0.0f, 0.0f, 0.40625f * visualCurrentProgresPercentage, 0.4375f, 0.40625f);
		}

		if (isProcessingErrored) {
			getErrorDrawable().draw(matrix, 0, 0);
		}
	}
}
