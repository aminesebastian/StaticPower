package theking530.api.attributes.modifiers;

import theking530.api.attributes.AttributeValues;

public class MultiplicativeNumberAttributeModifierType extends AttributeModifierType<Number> {

	public MultiplicativeNumberAttributeModifierType() {
		super(AttributeValues.Number);
	}

	@Override
	public Number modifyValue(AttributeModifierInstance<Number> modifier, Number value) {
		return value.doubleValue() * modifier.getModifierValue().doubleValue();
	}
}
