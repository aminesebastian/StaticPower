package theking530.staticcore.initialization.container;

import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;

public class ScreenAllocatorProxy<T extends Container, K extends ContainerScreen<T>> {
	protected final IScreenFactory<T, K> screenFactory;

	public ScreenAllocatorProxy(IScreenFactory<T, K> screenFactory) {
		this.screenFactory = screenFactory;
	}

	public IScreenFactory<T, K> getScreenFactory() {
		return screenFactory;
	}
}
