package theking530.api.smithingattributes;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

public class AttributeRegistry {
	private static final HashMap<ResourceLocation, AttributeDefenition> ATTRIBUTE_MAP = new HashMap<>();

	public static void registerAttributeType(ResourceLocation id, AttributeDefenition defenition) {
		ATTRIBUTE_MAP.put(id, defenition);
	}

	public static AttributeDefenition getAttributeDefenition(ResourceLocation id) {
		return ATTRIBUTE_MAP.get(id);
	}
}
