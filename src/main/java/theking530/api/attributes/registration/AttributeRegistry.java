package theking530.api.attributes.registration;

import java.util.HashMap;
import java.util.function.Function;

import net.minecraft.util.ResourceLocation;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;

public class AttributeRegistry {
	private static final HashMap<ResourceLocation, Function<ResourceLocation, AbstractAttributeDefenition<?, ?>>> ATTRIBUTE_MAP = new HashMap<>();

	public static void registerAttribute(ResourceLocation id, Function<ResourceLocation, AbstractAttributeDefenition<?, ?>> factory) {
		ATTRIBUTE_MAP.put(id, factory);
	}

	public static AbstractAttributeDefenition<?, ?> createInstance(ResourceLocation id) {
		return ATTRIBUTE_MAP.get(id).apply(id);
	}

	public static int getRegisteredAttributeCount() {
		return ATTRIBUTE_MAP.size();
	}
}
