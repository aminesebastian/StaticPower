package theking530.staticcore.gui.widgets;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.network.chat.Component;
import theking530.staticcore.blockentity.components.control.processing.machine.AbstractMachineProcessingComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.math.Vector2D;

public class PowerSatisfactionWidget extends AbstractGuiWidget<PowerSatisfactionWidget> {
	@Nullable
	protected AbstractMachineProcessingComponent<?, ?> machineProcessingComponent;

	private final SpriteDrawable base;
	private final SpriteDrawable indicator;
	private final SpriteDrawable glass;
	private final SpriteDrawable frame;

	public PowerSatisfactionWidget(float xPosition, float yPosition, float scale,
			AbstractMachineProcessingComponent<?, ?> machineProcessingComponent) {
		super(xPosition, yPosition, scale, scale);
		this.machineProcessingComponent = machineProcessingComponent;
		base = new SpriteDrawable(StaticCoreSprites.POWER_SATISFACTION_BASE, scale, scale);
		indicator = new SpriteDrawable(StaticCoreSprites.POWER_SATISFACTION_INDICATOR, scale, scale);
		glass = new SpriteDrawable(StaticCoreSprites.POWER_SATISFACTION_GLASS, scale, scale);
		frame = new SpriteDrawable(StaticCoreSprites.POWER_SATISFACTION_FRAME, scale, scale);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		base.draw(matrix);

		float satisfaction = (float) machineProcessingComponent.getPowerSatisfaction();
		satisfaction *= 2.0f;
		satisfaction -= 1f;
		satisfaction *= 90.0f;

		matrix.pushPose();
		matrix.translate(getSize().getX() / 2, 3 + getSize().getY() / 2, 0);
		matrix.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0.0f, 0.0f, satisfaction)));
		matrix.translate(-getSize().getX() / 2, -getSize().getY() / 2 - 3, 0);
		indicator.draw(matrix);
		matrix.popPose();

		glass.draw(matrix);
		frame.draw(matrix);
	}

	@Override
	protected void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		String translatedMessage = Component.translatable("gui.staticcore.power_satisfaction").append(": ")
				.append(GuiTextUtilities
						.formatNumberAsPercentStringNoDecimal(machineProcessingComponent.getPowerSatisfaction()))
				.getString();

		List<String> wrappedMessage = GuiDrawUtilities.wrapString(translatedMessage, 200);
		for (String wrappedString : wrappedMessage) {
			tooltips.add(Component.literal(wrappedString));
		}
	}
}
