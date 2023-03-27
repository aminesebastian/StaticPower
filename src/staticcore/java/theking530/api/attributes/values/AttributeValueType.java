package theking530.api.attributes.values;

import com.google.gson.JsonElement;

import net.minecraft.nbt.CompoundTag;

public abstract class AttributeValueType<T> {
	public abstract void serializeValue(T value, CompoundTag nbt);

	public abstract T deserializeValue(CompoundTag nbt);

	public abstract JsonElement serializeValueToJson(T value);

	public abstract T deserializeValueFromJson(JsonElement json);
}
