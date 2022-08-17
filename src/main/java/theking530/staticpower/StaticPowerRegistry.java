package theking530.staticpower;

import java.util.HashSet;
import java.util.LinkedHashSet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.world.trees.AbstractStaticPowerTree;

/**
 * Main registry class responsible for preparing entities for registration and
 * then registering them.
 * 
 * @author Amine Sebastian
 *
 */
@SuppressWarnings({ "rawtypes" })
public class StaticPowerRegistry {
	public static final HashSet<FlowingFluid> FLUIDS = new LinkedHashSet<>();
	public static final HashSet<RecipeSerializer> RECIPE_SERIALIZERS = new LinkedHashSet<>();
	public static final HashSet<AbstractEntityType<?>> ENTITIES = new LinkedHashSet<>();
	public static final HashSet<AbstractStaticPowerTree> TREES = new LinkedHashSet<>();

	/**
	 * Pre-registers an entity for registration through the registry event.
	 * 
	 * @param entity The entity to pre-register.
	 * @return The entity that was passed.
	 */
	public static <T extends Entity> AbstractEntityType<T> preRegisterEntity(AbstractEntityType<T> entity) {
		ENTITIES.add(entity);
		return entity;
	}

	/**
	 * Pre-registers a tree for registration through the init method.
	 * 
	 * @param tree The tree to pre-register.
	 * @return The tree that was passed.
	 */
	public static AbstractStaticPowerTree preRegisterTree(AbstractStaticPowerTree tree) {
		TREES.add(tree);
		return tree;
	}

	public static FlowingFluid preRegisterFluid(FlowingFluid fluid) {
		FLUIDS.add(fluid);
		return fluid;
	}

	public static RecipeSerializer preRegisterRecipeSerializer(RecipeSerializer recipeSerializer) {
		RECIPE_SERIALIZERS.add(recipeSerializer);
		return recipeSerializer;
	}

	public static void onRegisterFluids(RegistryEvent.Register<Fluid> event) {
		for (Fluid fluid : FLUIDS) {
			event.getRegistry().register(fluid);
		}
	}

	public static void onRegisterTileEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
		StaticCoreRegistry.registerTileEntityTypes(event);
	}

	public static void onRegisterContainerTypes(RegistryEvent.Register<MenuType<?>> event) {
		StaticCoreRegistry.registerContainerTypes(event);
	}

	public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
		// Register mobs.
		for (AbstractEntityType<?> type : ENTITIES) {
			event.getRegistry().register(type.getType());
		}
	}

	public static void onRegisterEntityAttributes(EntityAttributeCreationEvent event) {
		// Register mobs.
		for (AbstractEntityType<?> type : ENTITIES) {
			if (type instanceof AbstractSpawnableMobType) {
				((AbstractSpawnableMobType<?>) type).registerAttributes(event);
			}
		}
	}

	public static void onRegisterRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		for (RecipeSerializer serializer : RECIPE_SERIALIZERS) {
			event.getRegistry().register(serializer);
		}
	}
}
