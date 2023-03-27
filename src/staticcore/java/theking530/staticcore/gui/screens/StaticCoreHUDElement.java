package theking530.staticcore.gui.screens;

import com.mojang.blaze3d.platform.Window;

public abstract class StaticCoreHUDElement extends StaticPowerDetatchedGui {
	private Window currentWindow;

	public StaticCoreHUDElement(int width, int height) {
		super(width, height);
	}

	public void setCurrentWindow(Window window) {
		currentWindow = window;
	}

	public Window getWindow() {
		return currentWindow;
	}
}
