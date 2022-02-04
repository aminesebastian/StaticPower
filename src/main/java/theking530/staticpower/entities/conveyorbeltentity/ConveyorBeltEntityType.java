package theking530.staticpower.entities.conveyorbeltentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.init.ModEntities;

public class ConveyorBeltEntityType extends AbstractEntityType<ConveyorBeltEntity> {

	public ConveyorBeltEntityType(String name) {
		super(name, EntityType.Builder.<ConveyorBeltEntity>of(ConveyorBeltEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
	}

	@Override
	public void registerAttributes(Register<EntityType<?>> event) {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ConveyorBeltEntity.getType(), (EntityRenderDispatcher manager) -> {
			return new ConveyorBeltEntityRenderer(manager, Minecraft.getInstance().getItemRenderer());
		});
	}
}
