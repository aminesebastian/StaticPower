package theking530.staticpower.entities.cauldroncontainedentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.init.ModEntities;

public class CauldronContainedEntityType extends AbstractEntityType<CauldronContainedEntity> {

	public CauldronContainedEntityType(String name) {
		super(name, EntityType.Builder.<CauldronContainedEntity>of(CauldronContainedEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
	}

	@Override
	public void registerAttributes(Register<EntityType<?>> event) {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CauldronContainedEntity.getType(), (EntityRenderDispatcher manager) -> {
			return new ItemEntityRenderer(manager, Minecraft.getInstance().getItemRenderer());
		});
	}
}