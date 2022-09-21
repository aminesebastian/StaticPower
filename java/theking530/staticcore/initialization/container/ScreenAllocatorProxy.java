package theking530.staticcore.initialization.container;

import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ScreenAllocatorProxy<T extends AbstractContainerMenu, K extends AbstractContainerScreen<T>> {
	protected final ScreenConstructor<T, K> screenFactory;

	public ScreenAllocatorProxy(ScreenConstructor<T, K> screenFactory) {
		this.screenFactory = screenFactory;
	}

	public ScreenConstructor<T, K> getScreenFactory() {
		return screenFactory;
	}
}
