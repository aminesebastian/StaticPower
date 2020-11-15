package theking530.api.attributes.rendering;

import net.minecraft.util.ResourceLocation;
import theking530.api.attributes.capability.IAttributable;

public class BasicAttributeRenderLayer extends AbstractAttributeRenderLayer {
	private ResourceLocation sprite;

	/**
	 * Creates a basic single sprite render layer.
	 * 
	 * @param attributeId
	 * @param layer
	 * @param sprite
	 */
	public BasicAttributeRenderLayer(ResourceLocation sprite, int layer) {
		super(layer);
		this.sprite = sprite;
	}

	@Override
	public ResourceLocation getSprite(IAttributable attributable) {
		return sprite;
	}
}