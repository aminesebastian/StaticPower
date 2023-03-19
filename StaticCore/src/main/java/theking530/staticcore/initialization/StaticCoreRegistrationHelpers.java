package theking530.staticcore.initialization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import net.minecraftforge.registries.DeferredRegister;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

// TODO: Clean up exceptions.
public class StaticCoreRegistrationHelpers {
	protected static final Logger LOGGER = LogManager.getLogger("StaticCore");
	protected static final List<BlockEntityTypeAllocator<? extends BlockEntity>> BLOCK_ENTITY_ALLOCATORS = new LinkedList<>();
	protected static final List<ContainerTypeAllocator<? extends AbstractContainerMenu, ? extends Screen>> CONTAINER_ALLOCATORS = new LinkedList<>();

	public static void registerBlockEntityTypes(String modId) {
		LOGGER.info(String.format("Registering BlockEntityTypes for mod: %1$s.", modId));

		try {
			processBlockEntityTypeAllocators(modId, (beAllocator) -> {
				BLOCK_ENTITY_ALLOCATORS.add(beAllocator);
			});
		} catch (Exception e) {
			throw new RuntimeException("An error occured when attempting to process block entity type!", e);
		}

		LOGGER.info(String.format("Registered %1$d BlockEntityType Allocators.", BLOCK_ENTITY_ALLOCATORS.size()));
	}

	public static void registerMenuTypes(String modId) {
		LOGGER.info(String.format("Registering MenuTypes for mod: %1$s.", modId));

		try {
			processContainerTypeAllocators(modId, (containerAllocator) -> {
				CONTAINER_ALLOCATORS.add(containerAllocator);
			});
		} catch (Exception e) {
			throw new RuntimeException("An error occured when attempting to process menu type allocators!", e);
		}

		LOGGER.info(String.format("Registered %1$d MenuType Allocators.", CONTAINER_ALLOCATORS.size()));
	}

	public static void registerBlockEntityTypes(IEventBus eventBus, DeferredRegister<BlockEntityType<?>> blockEntityRegistry) {
		for (BlockEntityTypeAllocator<?> allocator : StaticCoreRegistrationHelpers.BLOCK_ENTITY_ALLOCATORS) {
			blockEntityRegistry.register(allocator.getName(), () -> allocator.getType());
		}
		blockEntityRegistry.register(eventBus);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerBlockEntityRenderers(String modId, RegisterRenderers event) throws Exception {
		StaticCoreRegistrationHelpers.processBlockEntityTypeAllocators(modId, (allocator) -> {
			if (allocator.requiresBlockEntitySpecialRenderer()) {
				event.registerBlockEntityRenderer(allocator.getType(), allocator.getBlockEntitySpecialRenderer());
			}
		});
	}

	public static void registerContainerTypes(IEventBus eventBus, DeferredRegister<MenuType<?>> menuTypeRegistry) {
		for (ContainerTypeAllocator<? extends AbstractContainerMenu, ? extends Screen> allocator : StaticCoreRegistrationHelpers.CONTAINER_ALLOCATORS) {
			menuTypeRegistry.register(allocator.getName(), () -> allocator.getType());
		}
		menuTypeRegistry.register(eventBus);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerScreenFactories(FMLClientSetupEvent event) {
		for (ContainerTypeAllocator<? extends AbstractContainerMenu, ? extends Screen> container : CONTAINER_ALLOCATORS) {
			container.registerScreen();
		}
		LOGGER.info("Registered all Static Power container types.");
	}

	@SuppressWarnings("unchecked")
	public static void processBlockEntityTypeAllocators(String modId, Consumer<BlockEntityTypeAllocator<BlockEntityBase>> allocatorConsumer) throws Exception {
		// Process the allocators.
		for (AnnotationData annotation : getAnnotationsOfType(modId, BlockEntityTypePopulator.class)) {
			try {
				String name = annotation.clazz().toString().replace('/', '.').substring(1);
				name = name.substring(0, name.length() - 1);
				Field field = Class.forName(name).getField(annotation.memberName());
				allocatorConsumer.accept((BlockEntityTypeAllocator<BlockEntityBase>) field.get(null));
			} catch (Exception e) {
				throw new Exception(String.format("An error occured when attempting to process block entity allocator: %1$s.", annotation.memberName()), e);
			}
		}
	}

	public static void processContainerTypeAllocators(String modId, Consumer<ContainerTypeAllocator<?, ?>> allocatorConsumer) throws Exception {
		// Process the allocators.
		for (AnnotationData annotation : getAnnotationsOfType(modId, ContainerTypePopulator.class)) {
			try {
				String name = annotation.clazz().toString().replace('/', '.').substring(1);
				name = name.substring(0, name.length() - 1);
				Field field = Class.forName(name).getField(annotation.memberName());
				allocatorConsumer.accept((ContainerTypeAllocator<?, ?>) field.get(null));
			} catch (Exception e) {
				throw new Exception(String.format("An error occured when attempting to process container allocator: %1$s.", annotation.memberName()), e);
			}
		}
	}

	public static ArrayList<AnnotationData> getAnnotationsOfType(String modId, Class<? extends Annotation> annotationType) throws Exception {
		// Allocate the output.
		ArrayList<AnnotationData> output = new ArrayList<AnnotationData>();

		Optional<ModFileInfo> modFile = FMLLoader.getLoadingModList().getModFiles().stream().filter((file) -> file.getMods().stream().anyMatch((mod) -> mod.getModId() == modId)).findFirst();
		if (modFile.isEmpty()) {
			return output;
		}

		// Iterate through all the annotations.
		for (AnnotationData anno : modFile.get().getFile().getScanResult().getAnnotations()) {
			// Check to see if the annotation is of the requested type.
			try {
				if (anno.annotationType().equals(Type.getType(annotationType))) {
					output.add(anno);
				}
			} catch (Exception e) {
				throw new Exception(String.format("An error occured when attempting to process annotation: %1$s.", anno.annotationType().getClassName()), e);
			}
		}

		// Return the output.
		return output;
	}
}
