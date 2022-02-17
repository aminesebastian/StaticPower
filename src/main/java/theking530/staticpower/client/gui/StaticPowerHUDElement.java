package theking530.staticpower.client.gui;

import com.mojang.blaze3d.platform.Window;

public abstract class StaticPowerHUDElement extends StaticPowerDetatchedGui {
	private Window currentWindow;

	public StaticPowerHUDElement(int width, int height) {
		super(width, height);
	}

	public void setCurrentWindow(Window window) {
		currentWindow = window;
	}

	public Window getWindow() {
		return currentWindow;
	}
}
