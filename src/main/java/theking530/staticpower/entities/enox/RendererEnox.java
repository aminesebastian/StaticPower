package theking530.staticpower.entities.enox;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;

@OnlyIn(Dist.CLIENT)
public class RendererEnox<T extends Entity> extends MobRenderer<EntityEnox, ModelEnox> {
	private static final ResourceLocation ENOX_TEXTURE = new ResourceLocation(StaticPower.MOD_ID, "textures/entity/enox/enox.png");

	public RendererEnox(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new ModelEnox(), 0.7F);
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getTextureLocation(EntityEnox entity) {
		return ENOX_TEXTURE;
	}
}
