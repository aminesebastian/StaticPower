package theking530.api.smithingattributes.attributes;

import java.util.HashMap;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;
import theking530.api.smithingattributes.attributes.modifiers.AbstractAttributeModifier;

public class AttributeModifierRegistry {
	private static final HashMap<String, BiFunction<String, String, AbstractAttributeModifier<?>>> MODIFIER_MAP = new HashMap<>();

	public static void registerAttributeType(String type, BiFunction<String, String, AbstractAttributeModifier<?>> factory) {
		MODIFIER_MAP.put(type, factory);
	}

	@SuppressWarnings("unchecked")
	public static <T> AbstractAttributeModifier<T> createEmptyInstance(String type) {
		return (AbstractAttributeModifier<T>) MODIFIER_MAP.get(type).apply(type, "EMPTY");
	}

	@SuppressWarnings("unchecked")
	public static <T> AbstractAttributeModifier<T> createInstance(JsonObject instanceJson) {
		AbstractAttributeModifier<T> output = (AbstractAttributeModifier<T>) MODIFIER_MAP.get(instanceJson.get("type").getAsString()).apply(instanceJson.get("type").getAsString(), instanceJson.get("name").getAsString());
		output.deserialize(instanceJson);
		return output;
	}

	@SuppressWarnings("unchecked")
	public static <T> AbstractAttributeModifier<T> createInstance(CompoundNBT nbt) {
		AbstractAttributeModifier<T> output = (AbstractAttributeModifier<T>) MODIFIER_MAP.get(nbt.getString("type")).apply(nbt.getString("type"), nbt.getString("name"));
		output.deserialize(nbt);
		return output;
	}
}
