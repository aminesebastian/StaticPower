package theking530.api.attributes.rendering;

import net.minecraft.util.ResourceLocation;
import theking530.api.attributes.capability.IAttributable;

public abstract class AbstractAttributeRenderLayer {
	protected int layer;

	/**
	 * Creates an abstract render layer for the provided attribute and defaulting to
	 * the provided layer.
	 * 
	 * @param attributeId
	 * @param layer
	 */
	public AbstractAttributeRenderLayer(int layer) {
		this.layer = layer;
	}

	/**
	 * The layer on which to render this attribute. The render order is lowest to
	 * highest number, with values ranging from negative to positive integers.
	 * 
	 * @return
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * This method should return the resource location to use when rendering this
	 * attrribute for the provided attributable.
	 * 
	 * @param attributable
	 * @return
	 */
	public abstract ResourceLocation getSprite(IAttributable attributable);

}