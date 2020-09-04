package theking530.staticcore.initialization;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPower;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.tileentities.TileEntityBase;

@SuppressWarnings("deprecation")
public class StaticCoreRegistry {
	protected static final Logger LOGGER = LogManager.getLogger("StaticCore");
	protected static final List<TileEntityTypeAllocator<? extends TileEntity>> TILE_ENTITY_ALLOCATORS = new LinkedList<>();
	protected static final List<ContainerTypeAllocator<? extends Container, ? extends Screen>> CONTAINER_ALLOCATORS = new LinkedList<>();
	private static boolean initialized;

	public static void initialize() {
		// Don't initialize more than once.
		if (initialized) {
			throw new RuntimeException("Attempted to initialize StaticCore more than once!");
		}

		processTileEntityTypeAllocators((teAllocator) -> {
			TILE_ENTITY_ALLOCATORS.add(teAllocator);
		});
		processContainerTypeAllocators((containerAllocator) -> {
			CONTAINER_ALLOCATORS.add(containerAllocator);
		});

		LOGGER.info(String.format("Initialized: %1$d Tile Entity Allocators and %2$d Container Type Allocators.", TILE_ENTITY_ALLOCATORS.size(), CONTAINER_ALLOCATORS.size()));
	}

	public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
		for (TileEntityTypeAllocator<?> allocator : StaticCoreRegistry.TILE_ENTITY_ALLOCATORS) {
			allocator.register(event);
		}
	}

	public static void registerTileEntitySpecialRenderers() {
		StaticCoreRegistry.processTileEntityTypeAllocators((allocator) -> {
			if (allocator.requiresTileEntitySpecialRenderer()) {
				ClientRegistry.bindTileEntityRenderer(allocator.getType(), allocator.getTileEntitySpecialRenderer());
			}
		});
	}

	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		for (ContainerTypeAllocator<? extends Container, ? extends Screen> container : CONTAINER_ALLOCATORS) {
			container.registerContainer(event);
		}
	}

	public static void registerScreenFactories() {
		DeferredWorkQueue.runLater(() -> {
			for (ContainerTypeAllocator<? extends Container, ? extends Screen> container : CONTAINER_ALLOCATORS) {
				container.registerScreen();
			}
			StaticPower.LOGGER.info("Registered all Static Power container types.");
		});
	}

	@SuppressWarnings("unchecked")
	public static void processTileEntityTypeAllocators(Consumer<TileEntityTypeAllocator<TileEntity>> allocatorConsumer) {
		// Process the allocators.
		Set<Class<? extends TileEntityBase>> classes = getAllModTileEntityClasses();
		for (Class<? extends TileEntityBase> baseClass : classes) {
			for (Field field : baseClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(TileEntityTypePopulator.class)) {
					try {
						allocatorConsumer.accept((TileEntityTypeAllocator<TileEntity>) field.get(null));
					} catch (Exception e) {
						LOGGER.error(String.format("An error occured when attempting to process tile entity allocator: %1$s.", field.getName()));
					}
				}
			}
		}
	}

	public static void processContainerTypeAllocators(Consumer<ContainerTypeAllocator<?, ?>> allocatorConsumer) {
		// Process the allocators.
		Set<Class<? extends StaticPowerContainer>> classes = getAllModContainerClasses();
		for (Class<? extends StaticPowerContainer> baseClass : classes) {
			for (Field field : baseClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(ContainerTypePopulator.class)) {
					try {
						allocatorConsumer.accept((ContainerTypeAllocator<?, ?>) field.get(null));
					} catch (Exception e) {
						LOGGER.error(String.format("An error occured when attempting to process container allocator: %1$s.", field.getName()));
					}
				}
			}
		}
	}

	public static Set<Class<? extends StaticPowerContainer>> getAllModContainerClasses() {
		Reflections reflections = new Reflections("");
		return reflections.getSubTypesOf(StaticPowerContainer.class);
	}

	public static Set<Class<? extends TileEntityBase>> getAllModTileEntityClasses() {
		Reflections reflections = new Reflections("");
		return reflections.getSubTypesOf(TileEntityBase.class);
	}
}
