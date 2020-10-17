package theking530.api.smithingattributes.attributes;

import java.util.HashMap;
import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

public class AttributeRegistry {
	private static final HashMap<ResourceLocation, Function<ResourceLocation, AbstractAttributeDefenition<?, ?>>> ATTRIBUTE_MAP = new HashMap<>();

	public static void registerAttributeType(ResourceLocation id, Function<ResourceLocation, AbstractAttributeDefenition<?, ?>> factory) {
		ATTRIBUTE_MAP.put(id, factory);
	}

	public static AbstractAttributeDefenition<?, ?> createInstance(ResourceLocation id) {
		return ATTRIBUTE_MAP.get(id).apply(id);
	}
}
