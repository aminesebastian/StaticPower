package theking530.api.attributes.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.type.AttributeType;

public class ItemAttributeRegistration {
	private final Map<AttributeType<?>, AttributeRegistration<?>> attributes;

	public record AttributeRegistration<T> (AttributeType<T> attribute, T baseValue, @Nullable AbstractAttributeRenderLayer renderLayer) {
		public boolean hasRenderLayer() {
			return renderLayer != null;
		}
	}

	/**
	 * Creates an empty attributable render layer container.
	 */
	public ItemAttributeRegistration() {
		attributes = new HashMap<>();
	}

	/**
	 * Adds a layer to this render layer set. Only one layer can be added per
	 * attribute id.
	 * 
	 * @param layer
	 */
	public <T> void addAttribute(AttributeType<T> attributeId, T baseValue, @Nullable AbstractAttributeRenderLayer layer) {
		attributes.put(attributeId, new AttributeRegistration<T>(attributeId, baseValue, layer));
	}

	public Set<AttributeType<?>> getAttributes() {
		return attributes.keySet();
	}

	public Collection<AttributeRegistration<?>> getRegistrations() {
		return attributes.values();
	}

	@SuppressWarnings("unchecked")
	public <T> AttributeRegistration<T> getAttribute(AttributeType<T> type) {
		return (AttributeRegistration<T>) attributes.get(type);
	}

	/**
	 * Generates a list of sprites to render in the appropriate order for the
	 * provided attributable.
	 * 
	 * @param attributable
	 * @return
	 */
	public List<BakedQuad> getOrderedRenderQuads(ItemStack stack, IAttributable attributable, BlockState state, Direction side, RandomSource rand, ModelData data) {
		// Allocate a list for the appropriate layers.
		ArrayList<AbstractAttributeRenderLayer> applicableLayers = new ArrayList<>();

		// Then get all the layers in an unordered fashion.
		for (AttributeType<?> attribute : attributable.getAllAttributes()) {
			if (attributes.containsKey(attribute) && attributable.getAttribute(attribute).isActive()) {
				if (attributes.get(attribute).hasRenderLayer()) {
					applicableLayers.add(attributes.get(attribute).renderLayer());
				}
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
