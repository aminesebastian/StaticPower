package theking530.staticpower.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.items.StaticPowerMobSpawnEgg;

public abstract class AbstractSpawnableEntityType<T extends Entity> {
	private final EntityType<T> type;
	private final StaticPowerMobSpawnEgg spawnEgg;

	public AbstractSpawnableEntityType(String name, Color primaryEggColor, Color secondaryEggColor, EntityType.Builder<T> builder) {
		ResourceLocation fullRegistryName = new ResourceLocation(StaticPower.MOD_ID, name);
		type = builder.build(fullRegistryName.toString());
		type.setRegistryName(fullRegistryName);
		StaticPowerRegistry.preRegisterItem(spawnEgg = new StaticPowerMobSpawnEgg("egg_" + name, type, primaryEggColor, secondaryEggColor));
	}

	public abstract void registerAttributes(RegistryEvent.Register<EntityType<?>> event);

	public abstract void registerRenderers(FMLClientSetupEvent event);

	public abstract void spawn(BiomeLoadingEvent event);

	public EntityType<T> getType() {
		return type;
	}

	public StaticPowerMobSpawnEgg getEgg() {
		return spawnEgg;
	}
}
