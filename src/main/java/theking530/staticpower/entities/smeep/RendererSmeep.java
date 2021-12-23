package theking530.staticpower.entities.smeep;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;

@OnlyIn(Dist.CLIENT)
public class RendererSmeep<T extends Entity> extends MobRenderer<EntitySmeep, ModelSmeep> {
	private static final ResourceLocation SHEARED_SHEEP_TEXTURES = new ResourceLocation(StaticPower.MOD_ID, "textures/entity/smeep/smeep.png");

	public RendererSmeep(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new ModelSmeep(), 0.7F);
		this.addLayer(new WoolLayerSmeep(this));
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getTextureLocation(EntitySmeep entity) {
		return SHEARED_SHEEP_TEXTURES;
	}
}
