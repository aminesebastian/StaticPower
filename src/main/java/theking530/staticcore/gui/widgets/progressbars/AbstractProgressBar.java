package theking530.staticcore.gui.widgets.progressbars;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
@OnlyIn(Dist.CLIENT)
public abstract class AbstractProgressBar<T extends AbstractProgressBar<?>> extends AbstractGuiWidget {
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
	 * The visual current progress percentage. This should be the only value used
	 * when rendering!
	 */
	protected float visualCurrentProgresPercentage;
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
	 * Indicates whether or not to render the error icon if processing is errored.
	 */
	protected boolean drawErrorIcons;
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
	/**
	 * Indicates when to offset the animation percentage. For example, if this value
	 * is set to 0.5, and {@link #percentageDelayBefore} is true, then this bar will
	 * only animate from progress percent 0.0 to 0.5. If
	 * {@link #percentageDelayBefore} is false, then this bar will animate 0.5 to
	 * 1.0.
	 */
	protected Optional<Float> percentageDelay;
	/**
	 * Indicates on which side of the {@link #percentageDelay} that the bar will
	 * populate.
	 */
	protected boolean percentageDelayBefore;

	public AbstractProgressBar(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		currentProgress = 0;
		tickDownRate = 1;
		visualCurrentProgress = 0.0f;
		enableProgressTooltip = true;
		percentageDelay = Optional.empty();
		percentageDelayBefore = false;
		errorDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 16, 16);
	}

	@Override
	public void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
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

		// Calculate the percentage.
		visualCurrentProgresPercentage = visualCurrentProgress / maxProgress;

		// Perform the animation offset.
		if (percentageDelay.isPresent()) {
			// Get the range of percentage covered by this progress bar and the starting
			// percentage.
			float totalPercentCovered;
			float startPercentage;
			if (percentageDelayBefore) {
				totalPercentCovered = percentageDelay.get();
				startPercentage = 0.0f;
			} else {
				totalPercentCovered = 1.0f - percentageDelay.get();
				startPercentage = percentageDelay.get();
			}

			// If we are below the starting percentage, do nothing. Otherwise, remap the
			// values into the range for this bar.
			if (visualCurrentProgresPercentage < startPercentage) {
				visualCurrentProgresPercentage = 0.0f;
			} else {
				visualCurrentProgresPercentage = (visualCurrentProgresPercentage - startPercentage) / totalPercentCovered;
			}
		}

	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");

		if (isProcessingErrored) {
			String[] splitTooltips = processingErrorMessage.split("\\$");
			for (String tip : splitTooltips) {
				tooltips.add(new TextComponent(tip));
			}
		} else if (enableProgressTooltip) {
			if (currentProgress > 0) {
				String remainingTime = decimalFormat.format((maxProgress - currentProgress) / (tickDownRate * 20.0f));
				tooltips.add(new TranslatableComponent("gui.staticpower.remaining").append(": ").append(remainingTime).append(new TranslatableComponent("gui.staticpower.seconds.short")));
			} else {
				String maxTime = decimalFormat.format(maxProgress / (tickDownRate * 20.0f));
				tooltips.add(new TranslatableComponent("gui.staticpower.max").append(": ").append(maxTime).append(new TranslatableComponent("gui.staticpower.seconds.short")));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T disableProgressTooltip() {
		enableProgressTooltip = false;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T enableProgressTooltip() {
		enableProgressTooltip = true;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setErrorState(boolean errored) {
		isProcessingErrored = errored;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setErrorMessage(String message) {
		processingErrorMessage = message;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setAnimationStartAfter(float percentage) {
		this.percentageDelay = Optional.of(percentage);
		this.percentageDelayBefore = false;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setAnimationLastUntil(float percentage) {
		this.percentageDelay = Optional.of(percentage);
		this.percentageDelayBefore = true;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setDisplayErrorIcon(boolean drawErrorIcons) {
		this.drawErrorIcons = drawErrorIcons;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Binds this progress bar to the provided {@link MachineProcessingComponent}.
	 * 
	 * @param component The component to bind to.
	 * @return This progress bar for chaining of commands.
	 */
	public T bindToMachineProcessingComponent(MachineProcessingComponent component) {
		machineProcessingComponent = component;

		// Set the initial values.
		maxProgress = machineProcessingComponent.getMaxProcessingTime();
		currentProgress = machineProcessingComponent.getCurrentProcessingTime();
		tickDownRate = machineProcessingComponent.getTimeUnitsPerTick();
		visualCurrentProgress = currentProgress;
		return (T) this;
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
