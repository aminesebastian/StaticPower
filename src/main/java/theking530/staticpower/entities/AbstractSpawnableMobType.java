package theking530.staticpower.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.items.StaticPowerMobSpawnEgg;

public abstract class AbstractSpawnableMobType<T extends Mob> extends AbstractEntityBuilder<T> {
	private RegistryObject<StaticPowerMobSpawnEgg> spawnEgg;
	private Color primaryEggColor;
	private Color secondaryEggColor;

	public AbstractSpawnableMobType(Color primaryEggColor, Color secondaryEggColor, EntityType.Builder<T> builder) {
		super(builder);
		this.primaryEggColor = primaryEggColor;
		this.secondaryEggColor = secondaryEggColor;
	}

	public abstract void spawn(BiomeLoadingEvent event);

	public void registerSpawnEgg(String name) {
		spawnEgg = ModItems.ITEMS.register("egg_" + name, () -> new StaticPowerMobSpawnEgg(getType(), primaryEggColor, secondaryEggColor));
	}

	public StaticPowerMobSpawnEgg getEgg() {
		return spawnEgg.get();
	}
}
