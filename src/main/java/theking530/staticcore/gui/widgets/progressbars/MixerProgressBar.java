package theking530.staticcore.gui.widgets.progressbars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class MixerProgressBar extends AbstractProgressBar<MixerProgressBar> {
	public MixerProgressBar(int xPosition, int yPosition) {
		super(xPosition, yPosition, 16, 16);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(pose, mouseX, mouseY, partialTicks);

		float smoothedProgress = visualCurrentProgresPercentage * visualCurrentProgresPercentage;

		pose.pushPose();
		pose.translate(1.5f + getSize().getX() / 2, getSize().getY() / 2, getSize().getY() / 2);
		pose.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0.0f, 0.0f, smoothedProgress * 3600.0f)));
		pose.translate(-(getSize().getX() / 2) + 0.5f, -(getSize().getY() / 2), -(getSize().getY() / 2) + 0.5f);
		GuiDrawUtilities.drawTexture(pose, GuiTextures.MIXER_PROGRESS_BAR, getSize().getX(), getSize().getY(), 0, 0, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
		GuiDrawUtilities.drawTexture(pose, GuiTextures.MIXER_PROGRESS_BAR, getSize().getX(), getSize().getY() * visualCurrentProgresPercentage, 0, 0, 0.0f, 0.0f, 0.0f, 1.0f,
				(0.5f * visualCurrentProgresPercentage));
		pose.popPose();

		if (isProcessingErrored) {
			getErrorDrawable().draw(pose, 2f, 0);
		}

	}
}
