package theking530.staticpower.entities.smeep;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.entities.AbstractSpawnableMobType;
import theking530.staticpower.entities.enox.EntityEnox;

public class TypeSmeep extends AbstractSpawnableMobType<EntitySmeep> {
	public TypeSmeep() {
		super(SDColor.EIGHT_BIT_WHITE, new SDColor(54, 239, 88), EntityType.Builder.of(EntitySmeep::new, MobCategory.CREATURE).sized(0.9f, 1.3f));
	}

	@Override
	public void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(getType(), EntitySmeep.createAttributes().build());

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(getType(), RendererSmeep::new);
	}

	@Override
	public void registerPlacements(FMLCommonSetupEvent event) {
		SpawnPlacements.register(getType(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, null);
	}

	@Override
	protected boolean canSpawn(EntityType<EntityEnox> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
		return Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random) && level.getBlockState(pos).is(Blocks.GRASS_BLOCK);
	}
}
