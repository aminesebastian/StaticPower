package theking530.staticcore.gui.widgets;

import java.text.DecimalFormat;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;

public class ProcessingComponentStateWidget extends AbstractGuiWidget<ProcessingComponentStateWidget> {
	private final SpriteDrawable validDrawable;
	private final SpriteDrawable errorDrawable;
	private final MachineProcessingComponent machineProcessingComponent;

	public ProcessingComponentStateWidget(MachineProcessingComponent machineProcessingComponent, float xPosition, float yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		errorDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, width, height);
		validDrawable = new SpriteDrawable(StaticPowerSprites.GREEN_CHECK, width, height);
		this.machineProcessingComponent = machineProcessingComponent;
	}

	public ProcessingComponentStateWidget(MachineProcessingComponent machineProcessingComponent, float xPosition, float yPosition) {
		this(machineProcessingComponent, xPosition, yPosition, 16, 16);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (machineProcessingComponent.isProcessingStoppedDueToError()) {
			errorDrawable.draw(pose);
		} else {
			validDrawable.draw(pose);
		}
	}

	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");

		if (machineProcessingComponent.isProcessingStoppedDueToError()) {
			String[] splitTooltips = machineProcessingComponent.getProcessingErrorMessage().split("\\$");
			for (String tip : splitTooltips) {
				tooltips.add(new TextComponent(tip));
			}
		} else if (machineProcessingComponent.getCurrentProcessingTime() > 0) {
			String remainingTime = decimalFormat
					.format((machineProcessingComponent.getMaxProcessingTime() - machineProcessingComponent.getCurrentProcessingTime()) / (machineProcessingComponent.getTimeUnitsPerTick() * 20.0f));
			tooltips.add(new TranslatableComponent("gui.staticpower.remaining").append(": ").append(remainingTime).append(new TranslatableComponent("gui.staticpower.seconds.short")));
		} else {
			String maxTime = decimalFormat.format(machineProcessingComponent.getMaxProcessingTime() / (machineProcessingComponent.getTimeUnitsPerTick() * 20.0f));
			tooltips.add(new TranslatableComponent("gui.staticpower.max").append(": ").append(maxTime).append(new TranslatableComponent("gui.staticpower.seconds.short")));
		}
	}
}
