package theking530.api.gui.widgets.progressbars;

import theking530.api.gui.widgets.AbstractGuiWidget;

public abstract class AbstractProgressBar extends AbstractGuiWidget {
	protected int lastValue;
	protected float interp;

	public AbstractProgressBar(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		lastValue = 0;
		interp = 0.0f;
	}
}
