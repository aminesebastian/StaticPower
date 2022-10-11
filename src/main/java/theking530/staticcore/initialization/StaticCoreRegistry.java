package theking530.staticcore.initialization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;
import theking530.api.attributes.modifiers.AbstractAttributeModifier;
import theking530.api.attributes.registration.AttributeModifierRegistration;
import theking530.api.attributes.registration.AttributeModifierRegistry;
import theking530.api.attributes.registration.AttributeRegistration;
import theking530.api.attributes.registration.AttributeRegistry;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.BlockEntityBase;

// TODO: Clean up exceptions.
public class StaticCoreRegistry {
	protected static final Logger LOGGER = LogManager.getLogger("StaticCore");
	protected static final List<BlockEntityTypeAllocator<? extends BlockEntity>> BLOCK_ENTITY_ALLOCATORS = new LinkedList<>();
	protected static final List<ContainerTypeAllocator<? extends AbstractContainerMenu, ? extends Screen>> CONTAINER_ALLOCATORS = new LinkedList<>();

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StaticPower.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StaticPower.MOD_ID);

	private static boolean preInitialized;
	private static boolean initialized;

	public static void preInitialize() {
		// Don't preinitialize more than once.
		if (preInitialized) {
			throw new RuntimeException("Attempted to pre-initialize StaticCore more than once!");
		}

		LOGGER.info("Pre-Initializing StaticCore.");

		// Register first the modifiers, then the defenitions.
		LOGGER.info("Pre-Initializing Registry Attributes and Modifiers.");
		try {
			registerAttributeModifiers();
			registerAttributeDefenitions();
		} catch (Exception e) {
			throw new RuntimeException("An error occured when attempting to register attribute modifiers or defenitions!", e);
		}
		LOGGER.info(String.format("Pre-Initialized: %1$d Attribute Defenitions and %2$d Attribute Modifiers.", AttributeRegistry.getRegisteredAttributeCount(),
				AttributeModifierRegistry.getRegisteredAttributeModifierCount()));

		preInitialized = true;
		LOGGER.info("StaticCore Pre-Initialized.");
	}

	public static void postInitialize() {
		// Don't initialize more than once.
		if (initialized) {
			throw new RuntimeException("Attempted to initialize StaticCore more than once!");
		}

		LOGGER.info("Initializing StaticCore.");

		try {
			processBlockEntityTypeAllocators((beAllocator) -> {
				BLOCK_ENTITY_ALLOCATORS.add(beAllocator);
			});
			processContainerTypeAllocators((containerAllocator) -> {
				CONTAINER_ALLOCATORS.add(containerAllocator);
			});
		} catch (Exception e) {
			throw new RuntimeException("An error occured when attempting to process block entity type or menu type allocators!", e);
		}

		LOGGER.info(String.format("Initialized: %1$d Block Entity Allocators and %2$d Container Type Allocators.", BLOCK_ENTITY_ALLOCATORS.size(), CONTAINER_ALLOCATORS.size()));

		initialized = true;
		LOGGER.info("StaticCore Initialized.");
	}

	public static void registerBlockEntityTypes(IEventBus eventBus) {
		for (BlockEntityTypeAllocator<?> allocator : StaticCoreRegistry.BLOCK_ENTITY_ALLOCATORS) {
			BLOCK_ENTITIES.register(allocator.getName(), () -> allocator.getType());
		}
		BLOCK_ENTITIES.register(eventBus);
	}

	public static void registerAttributeDefenitions() throws Exception {
		// Process the attributes.
		for (AnnotationData annotation : getAnnotationsOfType(AttributeRegistration.class)) {
			try {
				// Get the constructor on the class that takes a resource location.
				Constructor<?> cons = Class.forName(annotation.memberName()).getConstructor(ResourceLocation.class);

				// Get the ID for the annotation.
				ResourceLocation id = new ResourceLocation(annotation.annotationData().get("value").toString());

				// Register the attribute defenition.
				AttributeRegistry.registerAttribute(id, (idIn) -> {
					try {
						return (AbstractAttributeDefenition<?, ?>) cons.newInstance(idIn);
					} catch (Exception e) {
						throw new RuntimeException(String.format("An error occured when attempting to register attribute defenition: %1$s.", id.toString()), e);
					}
				});
			} catch (Exception e) {
				LOGGER.error(String.format("An error occured when attempting to process attribute defeinition: %1$s.", annotation.memberName()), e);
			}
		}
	}

	public static void registerAttributeModifiers() throws Exception {
		// Process the attributes.
		for (AnnotationData annotation : getAnnotationsOfType(AttributeModifierRegistration.class)) {
			try {
				// Get the constructor on the class.
				Constructor<?> cons = Class.forName(annotation.memberName()).getConstructor();

				// Get the ID for the annotation.
				String id = annotation.annotationData().get("value").toString();

				// Register the attribute defenition.
				AttributeModifierRegistry.registerAttributeType(id, () -> {
					try {
						return (AbstractAttributeModifier<?>) cons.newInstance();
					} catch (Exception e) {
						throw new RuntimeException(String.format("An error occured when attempting to register attribute modifier: %1$s.", id.toString()), e);
					}
				});
			} catch (Exception e) {
				throw new Exception(String.format("An error occured when attempting to process attribute modifier: %1$s.", annotation.memberName()), e);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerBlockEntityRenderers(RegisterRenderers event) throws Exception {
		StaticCoreRegistry.processBlockEntityTypeAllocators((allocator) -> {
			if (allocator.requiresBlockEntitySpecialRenderer()) {
				event.registerBlockEntityRenderer(allocator.getType(), allocator.getBlockEntitySpecialRenderer());
			}
		});
	}

	public static void registerContainerTypes(IEventBus eventBus) {
		for (ContainerTypeAllocator<? extends AbstractContainerMenu, ? extends Screen> allocator : StaticCoreRegistry.CONTAINER_ALLOCATORS) {
			MENU_TYPES.register(allocator.getName(), () -> allocator.getType());
		}
		MENU_TYPES.register(eventBus);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerScreenFactories(FMLClientSetupEvent event) {
		for (ContainerTypeAllocator<? extends AbstractContainerMenu, ? extends Screen> container : CONTAINER_ALLOCATORS) {
			container.registerScreen();
		}
		LOGGER.info("Registered all Static Power container types.");
	}

	@SuppressWarnings("unchecked")
	public static void processBlockEntityTypeAllocators(Consumer<BlockEntityTypeAllocator<BlockEntityBase>> allocatorConsumer) throws Exception {
		// Process the allocators.
		for (AnnotationData annotation : getAnnotationsOfType(BlockEntityTypePopulator.class)) {
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

	public static void processContainerTypeAllocators(Consumer<ContainerTypeAllocator<?, ?>> allocatorConsumer) throws Exception {
		// Process the allocators.
		for (AnnotationData annotation : getAnnotationsOfType(ContainerTypePopulator.class)) {
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

	public static ArrayList<AnnotationData> getAnnotationsOfType(Class<? extends Annotation> annotationType) throws Exception {
		// Allocate the output.
		ArrayList<AnnotationData> output = new ArrayList<AnnotationData>();

		// Iterate through all the file infos.
		for (ModFileInfo mod : FMLLoader.getLoadingModList().getModFiles()) {
			// Iterate through all the annotations.
			for (AnnotationData anno : mod.getFile().getScanResult().getAnnotations()) {
				// Check to see if the annotation is of the requested type.
				try {
					if (anno.annotationType().equals(Type.getType(annotationType))) {
						output.add(anno);
					}
				} catch (Exception e) {
					throw new Exception(String.format("An error occured when attempting to process annotation: %1$s.", anno.annotationType().getClassName()), e);
				}
			}
		}

		// Return the output.
		return output;
	}
}
