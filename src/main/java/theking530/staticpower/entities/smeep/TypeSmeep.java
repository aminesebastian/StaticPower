package theking530.staticpower.entities.smeep;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.entities.AbstractSpawnableMobType;

public class TypeSmeep extends AbstractSpawnableMobType<EntitySmeep> {
	public TypeSmeep(String name) {
		super(name, Color.EIGHT_BIT_WHITE, new Color(54, 239, 88), EntityType.Builder.of(EntitySmeep::new, MobCategory.CREATURE).sized(0.9f, 1.3f));
	}

	@Override
	public void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(getType(), EntitySmeep.createAttributes().build());

	}

	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(getType(), RendererSmeep::new);
	}

	public void spawn(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.BiomeCategory.EXTREME_HILLS || event.getCategory() == Biome.BiomeCategory.FOREST || event.getCategory() == Biome.BiomeCategory.JUNGLE
				|| event.getCategory() == Biome.BiomeCategory.MESA || event.getCategory() == Biome.BiomeCategory.PLAINS || event.getCategory() == Biome.BiomeCategory.RIVER
				|| event.getCategory() == Biome.BiomeCategory.SAVANNA || event.getCategory() == Biome.BiomeCategory.TAIGA) {
			event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(getType(), StaticPowerConfig.SERVER.smeepSpawnWeight.get(),
					StaticPowerConfig.SERVER.smeepMinCount.get(), StaticPowerConfig.SERVER.smeepMaxCount.get()));
		}
	}
}
