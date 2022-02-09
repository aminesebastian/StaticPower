package theking530.staticpower.entities.enox;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.init.ModEntities;

public class TypeEnox extends AbstractSpawnableMobType<EntityEnox> {
	public TypeEnox(String name) {
		super(name, Color.EIGHT_BIT_WHITE, new Color(16, 100, 180),
				EntityType.Builder.of(EntityEnox::new, MobCategory.CREATURE).sized(0.9f, 1.3f));
	}

	public void registerAttributes(RegistryEvent.Register<EntityType<?>> event) {
		// TO-DO: DefaultAttributes.put(ModEntities.Enox.getType(),
		// EntityEnox.getAttributes().build());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.Enox.getType(), (Context ctx) -> new RendererEnox(ctx));
	}

	public void spawn(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.BiomeCategory.EXTREME_HILLS
				|| event.getCategory() == Biome.BiomeCategory.FOREST
				|| event.getCategory() == Biome.BiomeCategory.JUNGLE || event.getCategory() == Biome.BiomeCategory.MESA
				|| event.getCategory() == Biome.BiomeCategory.PLAINS || event.getCategory() == Biome.BiomeCategory.RIVER
				|| event.getCategory() == Biome.BiomeCategory.SAVANNA
				|| event.getCategory() == Biome.BiomeCategory.TAIGA) {
			event.getSpawns().getSpawner(MobCategory.CREATURE)
					.add(new MobSpawnSettings.SpawnerData(getType(), StaticPowerConfig.SERVER.smeepSpawnWeight.get(),
							StaticPowerConfig.SERVER.smeepMinCount.get(),
							StaticPowerConfig.SERVER.smeepMaxCount.get()));
		}
	}
}
