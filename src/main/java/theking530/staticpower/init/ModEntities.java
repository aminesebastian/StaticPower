package theking530.staticpower.init;

import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.entities.RubberWoodBarkEntity;
import theking530.staticpower.entities.RubberWoodBarkEntity.RubberWoodBarkEntityType;
import theking530.staticpower.entities.enox.TypeEnox;
import theking530.staticpower.entities.smeep.TypeSmeep;

public class ModEntities {
	public static TypeSmeep Smeep;
	public static TypeEnox Enox;
	public static AbstractEntityType<RubberWoodBarkEntity> RubberWoodBark;

	public static void init() {
		StaticPowerRegistry.preRegisterEntity(Smeep = new TypeSmeep("smeep"));
		StaticPowerRegistry.preRegisterEntity(Enox = new TypeEnox("enox"));
		StaticPowerRegistry.preRegisterEntity(RubberWoodBark = new RubberWoodBarkEntityType("rubber_wood_bark"));
	}

	public static void addSpawns(BiomeLoadingEvent event) {
		for (AbstractEntityType<?> type : StaticPowerRegistry.ENTITIES) {
			if (type instanceof AbstractSpawnableMobType) {
				((AbstractSpawnableMobType<?>) type).spawn(event);
			}
		}
	}
}
