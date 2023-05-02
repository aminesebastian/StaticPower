package theking530.staticpower.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticpower.StaticPower;

public abstract class AbstractEntityBuilder<T extends Entity> {
	private final EntityType.Builder<T> builder;
	private EntityType<T> type;

	public AbstractEntityBuilder(EntityType.Builder<T> builder) {
		this.builder = builder;
	}

	public void registerAttributes(EntityAttributeCreationEvent event) {
	}

	public abstract void registerRenderers(EntityRenderersEvent.RegisterRenderers event);

	public void registerPlacements(FMLCommonSetupEvent event) {
	}

	protected boolean canSpawn(EntityType<T> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos,
			RandomSource random) {
		return false;
	}

	public EntityType<T> build(String name) {
		if (type == null) {
			ResourceLocation fullRegistryName = new ResourceLocation(StaticPower.MOD_ID, name);
			type = builder.build(fullRegistryName.toString());
			return type;
		} else {
			throw new RuntimeException("Attempting to build an AbstractEntityBuilder that has already been built!");
		}
	}

	public EntityType<T> getType() {
		return type;
	}
}
