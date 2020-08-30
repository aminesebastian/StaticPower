package theking530.staticcore.gui.widgets.progressbars;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
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
	/**
	 * The amount of time units that coorelates to a tick. Usually this value should
	 * remain at 1.
	 */
	protected int tickDownRate;
	/**
	 * Indicates whether or not the processing is stopped due to an error.
	 */
	protected boolean isProcessingErrored;
	/**
	 * If the processing is stopped due to an error, this message will indicate what
	 * that error is to the user.
	 */
	protected String processingErrorMessage;
	/**
	 * Instance of the error drawable.
	 */
	protected SpriteDrawable errorDrawable;
	/**
	 * If true, a tooltip with the number of seconds remaining will appear when the
	 * progress bar is not errored and is hovered.
	 */
	protected boolean enableProgressTooltip;

	public AbstractProgressBar(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		currentProgress = 0;
		tickDownRate = 1;
		visualCurrentProgress = 0.0f;
		enableProgressTooltip = true;
		errorDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 16, 16);
	}

	@Override
	public void renderBehindItems(int mouseX, int mouseY, float partialTicks) {
		// Capture the max progress.
		if (machineProcessingComponent != null) {
			maxProgress = machineProcessingComponent.getMaxProcessingTime();
			currentProgress = machineProcessingComponent.getCurrentProcessingTime();
			tickDownRate = machineProcessingComponent.getTimeUnitsPerTick();
			isProcessingErrored = machineProcessingComponent.isProcessingStoppedDueToError();
			processingErrorMessage = machineProcessingComponent.getProcessingErrorMessage();
		}

		// Calculate the visual current progress.
		visualCurrentProgress = SDMath.clamp(visualCurrentProgress + partialTicks, currentProgress - 1, currentProgress);
		if (visualCurrentProgress > maxProgress) {
			visualCurrentProgress = maxProgress;
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");

		if (isProcessingErrored) {
			String[] splitTooltips = processingErrorMessage.split("\\$");
			for (String tip : splitTooltips) {
				tooltips.add(new StringTextComponent(tip));
			}
		} else if (enableProgressTooltip) {
			if (currentProgress > 0) {
				String remainingTime = decimalFormat.format((maxProgress - currentProgress) / (tickDownRate * 20.0f));
				tooltips.add(new TranslationTextComponent("gui.staticpower.remaining").appendText(": ").appendText(remainingTime)
						.appendSibling(new TranslationTextComponent("gui.staticpower.seconds.short")));
			} else {
				String maxTime = decimalFormat.format(maxProgress / (tickDownRate * 20.0f));
				tooltips.add(new TranslationTextComponent("gui.staticpower.max").appendText(": ").appendText(maxTime).appendSibling(new TranslationTextComponent("gui.staticpower.seconds.short")));
			}
		}
	}

	public AbstractProgressBar disableProgressTooltip() {
		enableProgressTooltip = false;
		return this;
	}

	public AbstractProgressBar enableProgressTooltip() {
		enableProgressTooltip = true;
		return this;
	}

	public AbstractProgressBar setErrorState(boolean errored) {
		isProcessingErrored = errored;
		return this;
	}

	public AbstractProgressBar setErrorMessage(String message) {
		processingErrorMessage = message;
		return this;
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
		maxProgress = machineProcessingComponent.getMaxProcessingTime();
		currentProgress = machineProcessingComponent.getCurrentProcessingTime();
		tickDownRate = machineProcessingComponent.getTimeUnitsPerTick();
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

	protected IDrawable getErrorDrawable() {
		return errorDrawable;
	}
}
