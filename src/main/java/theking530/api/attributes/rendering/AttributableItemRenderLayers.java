package theking530.api.attributes.rendering;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import theking530.api.attributes.capability.IAttributable;

public class AttributableItemRenderLayers {
	private final Map<ResourceLocation, AbstractAttributeRenderLayer> layers;

	/**
	 * Creates an empty attributable render layer container.
	 */
	public AttributableItemRenderLayers() {
		layers = new HashMap<>();
	}

	/**
	 * Adds a layer to this render layer set. Only one layer can be added per
	 * attribute id.
	 * 
	 * @param layer
	 */
	public void addLayer(ResourceLocation attributeId, AbstractAttributeRenderLayer layer) {
		layers.put(attributeId, layer);
	}

	/**
	 * Generates a list of sprites to render in the appropriate order for the
	 * provided attributable.
	 * 
	 * @param attributable
	 * @return
	 */
	public List<BakedQuad> getOrderedRenderQuads(ItemStack stack, IAttributable attributable, BlockState state, Direction side, Random rand, IModelData data) {
		// Allocate a list for the appropriate layers.
		ArrayList<AbstractAttributeRenderLayer> applicableLayers = new ArrayList<>();

		// Then get all the layers in an unordered fashion.
		for (ResourceLocation attribute : attributable.getAllAttributes()) {
			if (layers.containsKey(attribute) && attributable.getAttribute(attribute).isActive()) {
				applicableLayers.add(layers.get(attribute));
			}
		}

		// Sort the layers into the appropriate order.
		applicableLayers.sort(new Comparator<AbstractAttributeRenderLayer>() {
			@Override
			public int compare(AbstractAttributeRenderLayer o1, AbstractAttributeRenderLayer o2) {
				return o1.getLayer() - o2.getLayer();
			}
		});

		// Allocate the output.
		ArrayList<BakedQuad> output = new ArrayList<>();

		// Then, populate the quads.
		for (AbstractAttributeRenderLayer orderedLayer : applicableLayers) {
			output.addAll(orderedLayer.getQuads(stack, attributable, state, side, rand, data));
		}

		// Finally, return the ordered list of sprites.
		return output;
	}
}
