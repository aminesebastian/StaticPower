package theking530.api.attributes.modifiers;

import net.minecraftforge.registries.RegistryObject;
import theking530.api.attributes.values.AttributeValueType;

public abstract class AttributeModifierType<T> {
	private final RegistryObject<AttributeValueType<T>> valueType;

	@SuppressWarnings("unchecked")
	public AttributeModifierType(RegistryObject<? extends AttributeValueType<T>> valueType) {
		this.valueType = (RegistryObject<AttributeValueType<T>>) valueType;
	}


	public AttributeValueType<T> getValueType() {
		return valueType.get();
	}

	public AttributeModifierInstance<T> create(T value) {
		return new AttributeModifierInstance<T>(this, value);
	}

	public abstract T modifyValue(AttributeModifierInstance<T> modifier, T value);
}
