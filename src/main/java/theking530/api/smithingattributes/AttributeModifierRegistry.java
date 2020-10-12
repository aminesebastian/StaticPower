package theking530.api.smithingattributes;

import java.util.HashMap;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

public class AttributeModifierRegistry {
	private static final HashMap<String, BiFunction<String, String, AbstractAttributeModifier>> MODIFIER_MAP = new HashMap<>();

	public static void registerAttributeType(String type, BiFunction<String, String, AbstractAttributeModifier> factory) {
		MODIFIER_MAP.put(type, factory);
	}

	public static AbstractAttributeModifier createEmptyInstance(String type) {
		return MODIFIER_MAP.get(type).apply(type, "EMPTY");
	}

	public static AbstractAttributeModifier createInstance(JsonObject instanceJson) {
		AbstractAttributeModifier output = MODIFIER_MAP.get(instanceJson.get("type").getAsString()).apply(instanceJson.get("type").getAsString(), instanceJson.get("name").getAsString());
		output.deserialize(instanceJson);
		return output;
	}

	public static AbstractAttributeModifier createInstance(CompoundNBT nbt) {
		AbstractAttributeModifier output = MODIFIER_MAP.get(nbt.getString("type")).apply(nbt.getString("type"), nbt.getString("name"));
		output.read(nbt);
		return output;
	}
}
