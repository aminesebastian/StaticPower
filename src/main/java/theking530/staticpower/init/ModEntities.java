package theking530.staticpower.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;
import theking530.staticpower.entities.AbstractEntityBuilder;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.entities.anvilentity.AnvilForgeEntity;
import theking530.staticpower.entities.anvilentity.AnvilForgeEntityType;
import theking530.staticpower.entities.cauldroncontainedentity.CauldronContainedEntity;
import theking530.staticpower.entities.cauldroncontainedentity.CauldronContainedEntityType;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntityType;
import theking530.staticpower.entities.enox.EntityEnox;
import theking530.staticpower.entities.enox.TypeEnox;
import theking530.staticpower.entities.smeep.EntitySmeep;
import theking530.staticpower.entities.smeep.TypeSmeep;

public class ModEntities {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StaticPower.MOD_ID);
	private static final List<AbstractEntityBuilder<?>> ENTITIES = new ArrayList<>();

	public static final AbstractEntityBuilder<EntitySmeep> Smeep = registerEntity("smeep", new TypeSmeep());
	public static final AbstractEntityBuilder<EntityEnox> Enox = registerEntity("enox", new TypeEnox());
	public static final AbstractEntityBuilder<CauldronContainedEntity> CauldronContainedEntity = registerEntity("cauldron_contained_entity", new CauldronContainedEntityType());
	public static final AbstractEntityBuilder<ConveyorBeltEntity> ConveyorBeltEntity = registerEntity("conveyor_belt_entity", new ConveyorBeltEntityType());
	public static final AbstractEntityBuilder<AnvilForgeEntity> AnvilForgeEntity = registerEntity("anvil_forge_entity", new AnvilForgeEntityType());

	public static void init(IEventBus eventBus) {
		ENTITY_TYPES.register(eventBus);
	}

	private static <T extends Entity> AbstractEntityBuilder<T> registerEntity(String name, AbstractEntityBuilder<T> entity) {
		ENTITY_TYPES.register(name, () -> entity.build(name));
		if (entity instanceof AbstractSpawnableMobType) {
			((AbstractSpawnableMobType<?>) entity).registerSpawnEgg(name);
		}
		ENTITIES.add(entity);
		return entity;
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		for (AbstractEntityBuilder<?> entity : ENTITIES) {
			entity.registerRenderers(event);
		}
	}

	public static void registerPlacements(FMLCommonSetupEvent event) {
		for (AbstractEntityBuilder<?> entity : ENTITIES) {
			entity.registerPlacements(event);
		}
	}

	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		// Register mobs.
		for (AbstractEntityBuilder<?> type : ENTITIES) {
			if (type instanceof AbstractSpawnableMobType) {
				((AbstractSpawnableMobType<?>) type).registerAttributes(event);
			}
		}
	}
}
