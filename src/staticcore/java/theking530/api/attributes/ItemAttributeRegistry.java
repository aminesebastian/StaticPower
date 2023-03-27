package theking530.api.attributes;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import theking530.api.attributes.rendering.AbstractAttributeRenderLayer;
import theking530.api.attributes.rendering.ItemAttributeRegistration;
import theking530.api.attributes.type.AttributeType;

public class ItemAttributeRegistry {
	private static final ItemAttributeRegistry INSTANCE = new ItemAttributeRegistry();
	private final Map<Item, ItemAttributeRegistration> layers;

	private ItemAttributeRegistry() {
		layers = new HashMap<>();
	}

	protected <T> void registerLayer(Item item, AttributeType<T> attribute, T baseValue, AbstractAttributeRenderLayer layer) {
		if (!layers.containsKey(item)) {
			layers.put(item, new ItemAttributeRegistration());
		}
		layers.get(item).addAttribute(attribute, baseValue, layer);
	}

	public static @Nullable ItemAttributeRegistration get(Item item) {
		if (!INSTANCE.layers.containsKey(item)) {
			return null;
		}
		return INSTANCE.layers.get(item);
	}

	public static class ItemAttributeRegisterEvent extends Event implements IModBusEvent {

		public <T> void attach(Item item, AttributeType<T> attribute, T baseValue, AbstractAttributeRenderLayer layer) {
			INSTANCE.registerLayer(item, attribute, baseValue, layer);
		}
	}
}
