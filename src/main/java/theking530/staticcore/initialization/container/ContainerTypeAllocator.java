package theking530.staticcore.initialization.container;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;

/**
 * Pre-registers a {@link Container} for registration through the registration
 * event. Returns an instance of the {@link ContainerType} for this
 * {@link Container}.
 */
public class ContainerTypeAllocator<T extends AbstractContainerMenu, K extends AbstractContainerScreen<T>> {
	protected final String name;
	protected final IContainerFactory<T> containerFactory;
	protected ScreenConstructor<T, K> screenFactory;
	protected MenuType<T> type;
	private boolean screenRegistered;

	public ContainerTypeAllocator(String name, IContainerFactory<T> factory) {
		this.name = name;
		this.containerFactory = factory;
		this.screenRegistered = false;
	}

	public String getName() {
		return name;
	}

	public IContainerFactory<T> getContainerFactory() {
		return containerFactory;
	}

	@OnlyIn(Dist.CLIENT)
	public void setScreenFactory(ScreenConstructor<T, K> screenFactory) {
		this.screenFactory = screenFactory;
	}

	@OnlyIn(Dist.CLIENT)
	public void registerScreen() {
		if (screenRegistered) {
			throw new RuntimeException("Attempted to register an already registered TileEntityTypeAllocator!");
		} else {
			MenuScreens.register(type, screenFactory);
			screenRegistered = true;
		}
	}

	public MenuType<T> getType() {
		if (type == null) {
			type = IForgeMenuType.create(containerFactory);
		}
		return type;
	}
}
