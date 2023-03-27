package theking530.staticcore.attribiutes.modifiers;

import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.staticcore.attribiutes.AttributeValues;

public class MultiplicativeNumberAttributeModifierType extends AttributeModifierType<Number> {

	public MultiplicativeNumberAttributeModifierType() {
		super(AttributeValues.Number);
	}

	@Override
	public Number modifyValue(AttributeModifierInstance<Number> modifier, Number value) {
		return value.doubleValue() * modifier.getModifierValue().doubleValue();
	}
}
