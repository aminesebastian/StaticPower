package theking530.staticpower.entities.cauldroncontainedentity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.init.ModEntities;

public class CauldronContainedEntityType extends AbstractEntityType<CauldronContainedEntity> {

	public CauldronContainedEntityType(String name) {
		super(name, EntityType.Builder.<CauldronContainedEntity>of(CauldronContainedEntity::new, MobCategory.MISC)
				.sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
	}

	@Override
	public void registerAttributes(Register<EntityType<?>> event) {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.CauldronContainedEntity.getType(), (Context ctx) -> {
			return new ItemEntityRenderer(ctx);
		});
	}
}