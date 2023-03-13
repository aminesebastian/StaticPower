package theking530.api.attributes.modifiers;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPowerRegistries;

public class AttributeModifierInstance<T> {
	protected final AttributeModifierType<T> type;
	protected T modifierValue;

	public AttributeModifierInstance(AttributeModifierType<T> type, T modifierValue) {
		this.type = type;
		this.modifierValue = modifierValue;
	}

	public AttributeModifierType<T> getType() {
		return type;
	}

	public T getModifierValue() {
		return modifierValue;
	}

	public void setModifierValue(T modifierValue) {
		this.modifierValue = modifierValue;
	}

	public T modifyValue(T value) {
		return type.modifyValue(this, value);
	}

	public void serialize(CompoundTag nbt) {
		nbt.putString("type", StaticPowerRegistries.AttributeModifier().getKey(type).toString());
		getType().getValueType().serializeValue(modifierValue, nbt);
	}

	@SuppressWarnings("unchecked")
	public static <T> AttributeModifierInstance<T> deserialize(CompoundTag nbt) {
		ResourceLocation modifierKey = new ResourceLocation(nbt.getString("type"));
		AttributeModifierType<T> type = (AttributeModifierType<T>) StaticPowerRegistries.AttributeModifier().getValue(modifierKey);
		T value = type.getValueType().deserializeValue(nbt);
		return new AttributeModifierInstance<T>(type, value);
	}

	public JsonObject serializeToJson() {
		JsonObject output = new JsonObject();
		output.addProperty("type", StaticPowerRegistries.AttributeModifier().getKey(type).toString());
		output.add("value", type.getValueType().serializeValueToJson(modifierValue));
		return output;
	}

	@SuppressWarnings("unchecked")
	public static <T> AttributeModifierInstance<T> deserializeFromJson(JsonObject json) {
		ResourceLocation modifierKey = new ResourceLocation(json.get("type").getAsString());
		AttributeModifierType<T> type = (AttributeModifierType<T>) StaticPowerRegistries.AttributeModifier().getValue(modifierKey);

		T value = type.getValueType().deserializeValueFromJson(json.get("value"));
		return new AttributeModifierInstance<T>(type, value);
	}
}
