package theking530.common.gui.widgets.progressbars;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.common.gui.widgets.AbstractGuiWidget;
import theking530.common.utilities.SDMath;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;

/**
 * Abstract progress bar that can be used to render the current progress of a
 * machine or process.
 * 
 * @author TheKing530
 *
 */
public abstract class AbstractProgressBar extends AbstractGuiWidget {
	/**
	 * The machine processing component this progress bar is bound to (if one
	 * exists).
	 */
	@Nullable
	protected MachineProcessingComponent machineProcessingComponent;

	/**
	 * The visual current progress. This is interpolated to match the current
	 * progress value.
	 */
	protected float visualCurrentProgress;
	/**
	 * The actual current progress as defined by the processing component or set
	 * manually.
	 */
	protected int currentProgress;
	/**
	 * The maximum amount of progress this progress bar should be able to display.
	 */
	protected int maxProgress;

	public AbstractProgressBar(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		currentProgress = 0;
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		// Capture the max progress.
		if (machineProcessingComponent != null) {
			maxProgress = machineProcessingComponent.getProcessingTime();
			currentProgress = machineProcessingComponent.getCurrentProcessingTime();
		}

		// Calculate the visual current progress.
		visualCurrentProgress = SDMath.clamp(visualCurrentProgress + partialTicks, currentProgress - 1, currentProgress);
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");

		if (currentProgress > 0) {
			String remainingTime = decimalFormat.format((maxProgress - currentProgress) / 20.0f);
			tooltips.add(
					new TranslationTextComponent("gui.staticpower.remaining").appendText(": ").appendText(remainingTime).appendSibling(new TranslationTextComponent("gui.staticpower.seconds.short")));
		} else {
			String maxTime = decimalFormat.format(maxProgress / 20.0f);
			tooltips.add(new TranslationTextComponent("gui.staticpower.max").appendText(": ").appendText(maxTime).appendSibling(new TranslationTextComponent("gui.staticpower.seconds.short")));
		}
	}

	/**
	 * Binds this progress bar to the provided {@link MachineProcessingComponent}.
	 * 
	 * @param component The component to bind to.
	 * @return This progress bar for chaining of commands.
	 */
	public AbstractProgressBar bindToMachineProcessingComponent(MachineProcessingComponent component) {
		machineProcessingComponent = component;

		// Set the initial values.
		maxProgress = machineProcessingComponent.getProcessingTime();
		currentProgress = machineProcessingComponent.getCurrentProcessingTime();
		visualCurrentProgress = currentProgress;
		return this;
	}

	/**
	 * Sets the current progress of this progress bar manually. Useless if bound to
	 * a machine processing component.
	 * 
	 * @param currentProgress The current progress in ticks.
	 */
	public void setCurrentProgress(int currentProgress) {
		this.currentProgress = currentProgress;
	}

	/**
	 * Resets the visual and actual current progress back to zero. Useless if bound
	 * to a machine processing component.
	 */
	public void reset() {
		currentProgress = 0;
		visualCurrentProgress = 0;
	}

	/**
	 * Gets the maximum progress this progress bar can support.
	 * 
	 * @return
	 */
	public int getMaxProgress() {
		return maxProgress;
	}

	/**
	 * Sets the maximum progress this progress bar can support.Useless if bound to a
	 * machine processing component.
	 * 
	 * @param maxProgress
	 */
	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

}
