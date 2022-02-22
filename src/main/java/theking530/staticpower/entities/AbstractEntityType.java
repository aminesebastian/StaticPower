package theking530.staticpower.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import theking530.staticpower.StaticPower;

public abstract class AbstractEntityType<T extends Entity> {
	private final EntityType<T> type;

	public AbstractEntityType(String name, EntityType.Builder<T> builder) {
		ResourceLocation fullRegistryName = new ResourceLocation(StaticPower.MOD_ID, name);
		type = builder.build(fullRegistryName.toString());
		type.setRegistryName(fullRegistryName);
	}

	public void registerAttributes(EntityAttributeCreationEvent event) {
	}

	public abstract void registerRenderers(EntityRenderersEvent.RegisterRenderers event);

	public EntityType<T> getType() {
		return type;
	}
}
