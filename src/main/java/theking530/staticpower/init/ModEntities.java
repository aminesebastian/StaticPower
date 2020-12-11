package theking530.staticpower.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.entities.EntitySmeep;
import theking530.staticpower.items.StaticPowerMobSpawnEgg;

public class ModEntities {

	public static EntityType<EntitySmeep> SMEEP;
	public static StaticPowerMobSpawnEgg SmeepEgg;

	public static void init() {
		StaticPowerRegistry.preRegisterEntity(SMEEP = createEntity("smeep", EntityType.Builder.create(EntitySmeep::new, EntityClassification.CREATURE).size(0.9f, 1.3f)));
		StaticPowerRegistry.preRegisterItem(SmeepEgg = new StaticPowerMobSpawnEgg("egg_smeep", ModEntities.SMEEP, new Color(54, 239, 88), Color.EIGHT_BIT_WHITE));
	}

	public static void addSpawns(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.Category.EXTREME_HILLS || event.getCategory() == Biome.Category.FOREST || event.getCategory() == Biome.Category.JUNGLE
				|| event.getCategory() == Biome.Category.MESA || event.getCategory() == Biome.Category.PLAINS || event.getCategory() == Biome.Category.RIVER
				|| event.getCategory() == Biome.Category.SAVANNA || event.getCategory() == Biome.Category.TAIGA) {
			event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(ModEntities.SMEEP, 5, 4, 10));
		}
	}

	private static <T extends Entity> EntityType<T> createEntity(String name, EntityType.Builder<T> builder) {
		ResourceLocation registryName = new ResourceLocation(StaticPower.MOD_ID, name);
		EntityType<T> entityType = builder.build(registryName.toString());
		entityType.setRegistryName(registryName);
		return entityType;
	}
}
