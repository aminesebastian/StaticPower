package theking530.api.attributes.modifiers;

import theking530.api.attributes.AttributeValues;

public class AdditiveNumberAttributeModifierType extends AttributeModifierType<Number> {

	public AdditiveNumberAttributeModifierType() {
		super(AttributeValues.Number);
	}

	@Override
	public Number modifyValue(AttributeModifierInstance<Number> modifier, Number value) {
		return value.doubleValue() + modifier.getModifierValue().doubleValue();
	}
}
