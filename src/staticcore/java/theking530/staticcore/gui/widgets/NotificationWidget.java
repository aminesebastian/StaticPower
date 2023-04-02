package theking530.staticcore.gui.widgets;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.math.Vector2D;

public class NotificationWidget extends AbstractGuiWidget<NotificationWidget> {
	private final SpriteDrawable icon;
	private String tooltipMessage;

	public NotificationWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		icon = new SpriteDrawable(StaticCoreSprites.NOTIFICATION, width, height);
		tooltipMessage = "";
	}

	public void setMessage(String message) {
		tooltipMessage = message;
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		icon.draw(matrix);
	}

	@Override
	protected void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		String translatedMessage = Component.translatable(tooltipMessage).getString();

		List<String> wrappedMessage = GuiDrawUtilities.wrapString(translatedMessage, 200);
		for (String wrappedString : wrappedMessage) {
			tooltips.add(Component.literal(wrappedString));
		}
	}
}
