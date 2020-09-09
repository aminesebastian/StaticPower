package theking530.staticcore.initialization.container;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;

/**
 * Pre-registers a {@link Container} for registration through the registration
 * event. Returns an instance of the {@link ContainerType} for this
 * {@link Container}.
 */
public class ContainerTypeAllocator<T extends Container, K extends ContainerScreen<T>> {
	protected final String name;
	protected final IContainerFactory<T> containerFactory;
	protected final IScreenFactory<T, K> screenFactory;
	protected ContainerType<T> type;
	private boolean containerRegistered;
	private boolean screenRegistered;

	public ContainerTypeAllocator(String name, IContainerFactory<T> factory, IScreenFactory<T, K> screen) {
		super();
		this.name = name;
		this.containerFactory = factory;
		this.screenFactory = screen;
		this.containerRegistered = false;
		this.screenRegistered = false;
	}

	public void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
		if (containerRegistered) {
			throw new RuntimeException("Attempted to register an already registered TileEntityTypeAllocator!");
		} else {
			type = IForgeContainerType.create(containerFactory);
			type.setRegistryName(name);
			event.getRegistry().register(type);
			containerRegistered = true;
		}
	}

	public IContainerFactory<T> getContainerFactory() {
		return containerFactory;
	}

	public void registerScreen() {
		if (screenRegistered) {
			throw new RuntimeException("Attempted to register an already registered TileEntityTypeAllocator!");
		} else {
			ScreenManager.registerFactory(type, screenFactory);
			screenRegistered = true;
		}
	}

	public ContainerType<T> getType() {
		return type;
	}
}
