package theking530.staticpower.entities.anvilentity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.init.ModEntities;

public class AnvilForgeEntityType extends AbstractEntityType<AnvilForgeEntity> {

	public AnvilForgeEntityType(String name) {
		super(name, EntityType.Builder.<AnvilForgeEntity>of(AnvilForgeEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.AnvilForgeEntity.getType(), (Context ctx) -> {
			return new AnvilForgeEntityRenderer(ctx, ctx.getItemRenderer());
		});
	}
}
