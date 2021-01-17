package theking530.staticpower.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticpower.StaticPower;

public abstract class AbstractEntityType<T extends Entity> {
	private final EntityType<T> type;

	public AbstractEntityType(String name, EntityType.Builder<T> builder) {
		ResourceLocation fullRegistryName = new ResourceLocation(StaticPower.MOD_ID, name);
		type = builder.build(fullRegistryName.toString());
		type.setRegistryName(fullRegistryName);
	}

	public abstract void registerAttributes(RegistryEvent.Register<EntityType<?>> event);

	public abstract void registerRenderers(FMLClientSetupEvent event);

	public EntityType<T> getType() {
		return type;
	}
}
