package theking530.staticpower.entities;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;

@OnlyIn(Dist.CLIENT)
public class SmeepRenderer<T extends Entity> extends MobRenderer<EntitySmeep, SmeepModel> {
	private static final ResourceLocation SHEARED_SHEEP_TEXTURES = new ResourceLocation(StaticPower.MOD_ID, "textures/entity/smeep/smeep.png");

	public SmeepRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SmeepModel(), 0.7F);
		this.addLayer(new SmeepWoolLayer(this));
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getEntityTexture(EntitySmeep entity) {
		return SHEARED_SHEEP_TEXTURES;
	}
}
