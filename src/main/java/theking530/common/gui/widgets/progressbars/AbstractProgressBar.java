package theking530.common.gui.widgets.progressbars;

import theking530.common.gui.widgets.AbstractGuiWidget;
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
	}

	public AbstractProgressBar bindToMachineProcessingComponent(MachineProcessingComponent component) {
		machineProcessingComponent = component;
		currentProgress = 0;
		maxProgress = machineProcessingComponent.getProcessingTime();
		return this;
	}
}
