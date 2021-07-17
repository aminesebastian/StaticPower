package theking530.staticpower.entities.conveyorbeltentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.init.ModEntities;

public class ConveyorBeltEntityType extends AbstractEntityType<ConveyorBeltEntity> {

	public ConveyorBeltEntityType(String name) {
		super(name, EntityType.Builder.<ConveyorBeltEntity>create(ConveyorBeltEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(6).func_233608_b_(20));
	}

	@Override
	public void registerAttributes(Register<EntityType<?>> event) {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ConveyorBeltEntity.getType(), (EntityRendererManager manager) -> {
			return new ConveyorBeltEntityRenderer(manager, Minecraft.getInstance().getItemRenderer());
		});
	}
}
