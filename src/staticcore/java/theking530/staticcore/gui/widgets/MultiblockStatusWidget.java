package theking530.staticcore.gui.widgets;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiBlockFormationStatus;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.math.Vector2D;

public class MultiblockStatusWidget extends AbstractGuiWidget<MultiblockStatusWidget> {
	private final SpriteDrawable icon;
	private MultiBlockFormationStatus status;
	private boolean renderWhenWellFormed;

	public MultiblockStatusWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		icon = new SpriteDrawable(StaticCoreSprites.NOTIFICATION, width, height);
		status = MultiBlockFormationStatus.OK;
		renderWhenWellFormed = false;
	}

	public MultiblockStatusWidget setShouldRenderWhenWellFormed(boolean shouldRender) {
		renderWhenWellFormed = shouldRender;
		return this;
	}

	public MultiblockStatusWidget setStatus(MultiBlockFormationStatus status) {
		this.status = status;
		if (status.isSuccessful()) {
			icon.setSprite(StaticCoreSprites.NOTIFICATION_GOOD);
		} else {
			icon.setSprite(StaticCoreSprites.NOTIFICATION);
		}

		setVisible(shouldDraw());
		return this;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		icon.draw(matrix);
	}

	@Override
	protected void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		String translatedMessage = Component.translatable(status.getUnlocalizedStatus()).getString();

		List<String> wrappedMessage = GuiDrawUtilities.wrapString(translatedMessage, 200);
		for (String wrappedString : wrappedMessage) {
			tooltips.add(Component.literal(wrappedString));
		}
	}

	protected boolean shouldDraw() {
		return !status.isSuccessful() || (renderWhenWellFormed && status.isSuccessful());
	}
}
