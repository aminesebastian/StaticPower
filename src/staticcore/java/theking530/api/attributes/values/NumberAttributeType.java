package theking530.api.attributes.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.CompoundTag;

public class NumberAttributeType extends AttributeValueType<Number> {

	@Override
	public void serializeValue(Number value, CompoundTag nbt) {
		nbt.putDouble("value", value.doubleValue());
	}

	@Override
	public Number deserializeValue(CompoundTag nbt) {
		return nbt.getDouble("value");
	}

	@Override
	public JsonElement serializeValueToJson(Number value) {
		return new JsonPrimitive(value);
	}

	@Override
	public Number deserializeValueFromJson(JsonElement json) {
		return json.getAsNumber();
	}
}
