package theking530.staticcore.initialization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.objectweb.asm.Type;

import net.minecraft.world.inventory.MenuType;
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
import theking530.staticcore.StaticCore;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

// TODO: Clean up exceptions.
// Examine if there are any benefits/pitfalls to caching these.
public class StaticCoreRegistrationHelper {
	private final String modId;

	public StaticCoreRegistrationHelper(String modId) {
		this.modId = modId;
	}

	public void registerBlockEntityTypes(IEventBus eventBus, DeferredRegister<BlockEntityType<?>> blockEntityRegistry) {
		StaticCore.LOGGER.info(String.format("Registering BlockEntityTypes for mod: %1$s.", modId));

		AtomicInteger registeredCount = new AtomicInteger(0);
		processBlockEntityTypeAllocators((beAllocator) -> {
			try {
				blockEntityRegistry.register(beAllocator.getName(), () -> beAllocator.getType());
				registeredCount.incrementAndGet();
			} catch (Exception e) {
				StaticCore.LOGGER.error(String.format("An error occured when attempting to process block entity type for mod: %1$s.", modId), e);
			}
		});
		blockEntityRegistry.register(eventBus);

		StaticCore.LOGGER.info(String.format("Registered %1$d BlockEntityTypes for mod: %2$s.", registeredCount.get(), modId));
	}

	public void registerContainerTypes(IEventBus eventBus, DeferredRegister<MenuType<?>> menuTypeRegistry) {
		StaticCore.LOGGER.info(String.format("Registering MenuTypes for mod: %1$s.", modId));

		AtomicInteger registeredCount = new AtomicInteger(0);
		processContainerTypeAllocators((containerAllocator) -> {
			try {
				menuTypeRegistry.register(containerAllocator.getName(), () -> containerAllocator.getType());
				registeredCount.incrementAndGet();
			} catch (Exception e) {
				StaticCore.LOGGER.error(String.format("An error occured when attempting to process MenuType for mod: %1$s.", modId), e);
			}
		});
		menuTypeRegistry.register(eventBus);

		StaticCore.LOGGER.info(String.format("Registered %1$d MenuTypes for mod: %2$s.", registeredCount.get(), modId));

	}

	@OnlyIn(Dist.CLIENT)
	public void registerBlockEntityRenderers(RegisterRenderers event) {
		StaticCore.LOGGER.info(String.format("Registering BlockEntityRenderers for mod: %1$s.", modId));

		AtomicInteger registeredCount = new AtomicInteger(0);
		processBlockEntityTypeAllocators((allocator) -> {
			if (allocator.requiresBlockEntitySpecialRenderer()) {
				event.registerBlockEntityRenderer(allocator.getType(), allocator.getBlockEntitySpecialRenderer());
				registeredCount.incrementAndGet();
			}
		});

		StaticCore.LOGGER.info(String.format("Registered %1$d BlockEntityRenderers for mod: %2$s.", registeredCount.get(), modId));
	}

	@OnlyIn(Dist.CLIENT)
	public void registerScreenFactories(FMLClientSetupEvent event) {
		StaticCore.LOGGER.info(String.format("Registering screen factories for mod: %1$s.", modId));

		AtomicInteger registeredCount = new AtomicInteger(0);
		processContainerTypeAllocators((containerAllocator) -> {
			try {
				containerAllocator.registerScreen();
				registeredCount.incrementAndGet();
			} catch (Exception e) {
				StaticCore.LOGGER.error(String.format("An error occured when attempting to process screen factory for mod: %1$s.", modId), e);
			}
		});

		StaticCore.LOGGER.info(String.format("Registered %1$d screen factories for mod: %2$s.", registeredCount.get(), modId));
	}

	@SuppressWarnings("unchecked")
	private void processBlockEntityTypeAllocators(Consumer<BlockEntityTypeAllocator<BlockEntityBase>> allocatorConsumer) {
		for (AnnotationData annotation : getAnnotationsOfType(BlockEntityTypePopulator.class)) {
			try {
				String name = annotation.clazz().toString().replace('/', '.').substring(1);
				name = name.substring(0, name.length() - 1);
				Field field = Class.forName(name).getField(annotation.memberName());
				allocatorConsumer.accept((BlockEntityTypeAllocator<BlockEntityBase>) field.get(null));
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to process block entity allocator: %1$s.", annotation.memberName()), e);
			}
		}
	}

	private void processContainerTypeAllocators(Consumer<ContainerTypeAllocator<?, ?>> allocatorConsumer) {
		for (AnnotationData annotation : getAnnotationsOfType(ContainerTypePopulator.class)) {
			try {
				String name = annotation.clazz().toString().replace('/', '.').substring(1);
				name = name.substring(0, name.length() - 1);
				Field field = Class.forName(name).getField(annotation.memberName());
				allocatorConsumer.accept((ContainerTypeAllocator<?, ?>) field.get(null));
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to process menu allocator: %1$s.", annotation.memberName()), e);
			}
		}
	}

	private ArrayList<AnnotationData> getAnnotationsOfType(Class<? extends Annotation> annotationType) {
		// Allocate the output.
		ArrayList<AnnotationData> output = new ArrayList<AnnotationData>();

		Optional<ModFileInfo> modFile = FMLLoader.getLoadingModList().getModFiles().stream()
				.filter((file) -> file.getMods().stream().anyMatch((mod) -> mod.getModId().equals(modId))).findFirst();
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
				throw new RuntimeException(String.format("An error occured when attempting to process annotation: %1$s.", anno.annotationType().getClassName()), e);
			}
		}

		// Return the output.
		return output;
	}
}
