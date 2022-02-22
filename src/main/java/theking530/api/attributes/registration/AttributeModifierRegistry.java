package theking530.api.attributes.registration;

import java.util.HashMap;
import java.util.function.Supplier;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import theking530.api.attributes.modifiers.AbstractAttributeModifier;

public class AttributeModifierRegistry {
	private static final HashMap<String, Supplier<AbstractAttributeModifier<?>>> MODIFIER_MAP = new HashMap<>();

	public static void registerAttributeType(String type, Supplier<AbstractAttributeModifier<?>> factory) {
		MODIFIER_MAP.put(type, factory);
	}

	@SuppressWarnings("unchecked")
	public static <T> AbstractAttributeModifier<T> createEmptyInstance(String type) {
		return (AbstractAttributeModifier<T>) MODIFIER_MAP.get(type).get();
	}

	@SuppressWarnings("unchecked")
	public static <T> AbstractAttributeModifier<T> createInstance(JsonObject instanceJson) {
		AbstractAttributeModifier<T> output = (AbstractAttributeModifier<T>) MODIFIER_MAP.get(instanceJson.get("type").getAsString()).get();
		output.deserialize(instanceJson);
		return output;
	}

	@SuppressWarnings("unchecked")
	public static <T> AbstractAttributeModifier<T> createInstance(CompoundTag nbt) {
		AbstractAttributeModifier<T> output = (AbstractAttributeModifier<T>) MODIFIER_MAP.get(nbt.getString("type")).get();
		output.deserialize(nbt);
		return output;
	}

	public static int getRegisteredAttributeModifierCount() {
		return MODIFIER_MAP.size();
	}
}