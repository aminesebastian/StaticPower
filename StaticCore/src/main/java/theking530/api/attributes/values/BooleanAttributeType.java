package theking530.api.attributes.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.CompoundTag;

public class BooleanAttributeType extends AttributeValueType<Boolean> {

	@Override
	public void serializeValue(Boolean value, CompoundTag nbt) {
		nbt.putBoolean("value", value);
	}

	@Override
	public Boolean deserializeValue(CompoundTag nbt) {
		return nbt.getBoolean("value");
	}

	@Override
	public JsonElement serializeValueToJson(Boolean value) {
		return new JsonPrimitive(value);
	}

	@Override
	public Boolean deserializeValueFromJson(JsonElement json) {
		return json.getAsBoolean();
	}
}
