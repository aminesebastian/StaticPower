package theking530.staticpower.client.gui;

import net.minecraftforge.client.event.ScreenEvent.InitScreenEvent;

public abstract class StaticPowerExtensionGui extends StaticPowerDetatchedGui {

	public StaticPowerExtensionGui(int width, int height) {
		super(width, height);
	}

	public abstract boolean shouldAttach(InitScreenEvent event);

	public abstract void onAttached(InitScreenEvent event);
}
