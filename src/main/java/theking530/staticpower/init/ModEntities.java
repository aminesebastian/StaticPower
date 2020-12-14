package theking530.staticpower.init;

import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.entities.AbstractSpawnableEntityType;
import theking530.staticpower.entities.enox.TypeEnox;
import theking530.staticpower.entities.smeep.TypeSmeep;

public class ModEntities {
	public static TypeSmeep Smeep;
	public static TypeEnox Enox;


	public static void init() {
		StaticPowerRegistry.preRegisterEntity(Smeep = new TypeSmeep("smeep"));
		StaticPowerRegistry.preRegisterEntity(Enox = new TypeEnox("enox"));
	}

	public static void addSpawns(BiomeLoadingEvent event) {
		for (AbstractSpawnableEntityType<?> type : StaticPowerRegistry.ENTITES) {
			type.spawn(event);
		}
	}
}
