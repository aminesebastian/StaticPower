package theking530.api.attributes.rendering;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
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
	 * This method should return the quads to render on top of the item.
	 * @param stack TODO
	 * @param attributable
	 * 
	 * @return
	 */
	public abstract List<BakedQuad> getQuads(ItemStack stack, IAttributable attributable, BlockState state, Direction side, Random rand, IModelData data);

}