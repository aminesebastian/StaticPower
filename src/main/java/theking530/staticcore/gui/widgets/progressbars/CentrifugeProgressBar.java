package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class CentrifugeProgressBar extends AbstractProgressBar<CentrifugeProgressBar> {
	public CentrifugeProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 16, 16);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);

		float smoothedProgress = visualCurrentProgresPercentage * visualCurrentProgresPercentage;

		matrix.pushPose();
		matrix.translate(getPosition().getX() + 1.5f + getSize().getX() / 2, getSize().getY() / 2, getSize().getY() / 2);
		matrix.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0.0f, 0.0f, smoothedProgress * 3600.0f)));
		matrix.translate(-(getPosition().getX() + getSize().getX() / 2), -(getSize().getY() / 2), -(getSize().getY() / 2));
		GuiDrawUtilities.drawTexture(matrix, GuiTextures.CENTRIFUGE_PROGRESS_BAR, getSize().getX(), getSize().getY(), 0, 0, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
		GuiDrawUtilities.drawTexture(matrix, GuiTextures.CENTRIFUGE_PROGRESS_BAR, getSize().getX(), getSize().getY() * visualCurrentProgresPercentage, 0, 0, 0.0f, 0.0f, 0.0f, 1.0f,
				(0.5f * visualCurrentProgresPercentage));
		matrix.popPose();
		if (isProcessingErrored) {
			getErrorDrawable().draw(matrix, 1.5f, 0.5f);
		}
	}
}
