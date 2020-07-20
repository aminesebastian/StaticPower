package theking530.common.gui.widgets.progressbars;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;

public abstract class AbstractProgressBar extends AbstractGuiWidget {
	protected int lastValue;
	protected float interp;
	protected MachineProcessingComponent machineProcessingComponent;

	protected int currentProgress;
	protected int maxProgress;

	public AbstractProgressBar(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		lastValue = 0;
		interp = 0.0f;
		currentProgress = 0;
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		if (machineProcessingComponent != null) {
			maxProgress = machineProcessingComponent.getProcessingTime();
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");

		if (lastValue > 0) {
			String remainingTime = decimalFormat.format((maxProgress - lastValue) / 20.0f);
			tooltips.add(new TranslationTextComponent("gui.staticpower.remaining").appendText(": ").appendText(remainingTime).appendSibling(new TranslationTextComponent("gui.staticpower.seconds.short")));
		} else {
			String maxTime = decimalFormat.format(maxProgress / 20.0f);
			tooltips.add(new TranslationTextComponent("gui.staticpower.max").appendText(": ").appendText(maxTime).appendSibling(new TranslationTextComponent("gui.staticpower.seconds.short")));
		}
	}

	public AbstractProgressBar bindToMachineProcessingComponent(MachineProcessingComponent component) {
		machineProcessingComponent = component;
		return this;
	}

	public int getCurrentProgress() {
		return currentProgress;
	}

	public void setCurrentProgress(int currentProgress) {
		this.currentProgress = currentProgress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

}
