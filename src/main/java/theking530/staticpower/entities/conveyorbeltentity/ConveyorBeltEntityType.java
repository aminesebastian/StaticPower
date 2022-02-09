package theking530.staticpower.entities.conveyorbeltentity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.init.ModEntities;

public class ConveyorBeltEntityType extends AbstractEntityType<ConveyorBeltEntity> {

	public ConveyorBeltEntityType(String name) {
		super(name, EntityType.Builder.<ConveyorBeltEntity>of(ConveyorBeltEntity::new, MobCategory.MISC)
				.sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
	}

	@Override
	public void registerAttributes(Register<EntityType<?>> event) {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.ConveyorBeltEntity.getType(), (Context ctx) -> {
			return new ConveyorBeltEntityRenderer(ctx, ctx.getItemRenderer());
		});
	}
}
