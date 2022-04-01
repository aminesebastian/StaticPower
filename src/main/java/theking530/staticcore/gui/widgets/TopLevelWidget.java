package theking530.staticcore.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import theking530.staticcore.utilities.Vector2D;

public class TopLevelWidget extends AbstractGuiWidget<TopLevelWidget> {

	public TopLevelWidget() {
		super(0, 0, 0, 0);
	}

	@Override
	public void updateWidgetBeforeRender(PoseStack matrixStack, Vector2D parentSize, float partialTicks, int mouseX, int mouseY) {
		setWidth(getMinecraft().getWindow().getGuiScaledWidth());
		setHeight(getMinecraft().getWindow().getGuiScaledHeight());
	}
}
