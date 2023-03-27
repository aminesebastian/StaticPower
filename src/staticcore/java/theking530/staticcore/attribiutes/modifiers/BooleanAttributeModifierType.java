package theking530.staticcore.attribiutes.modifiers;

import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.staticcore.attribiutes.AttributeValues;

public class BooleanAttributeModifierType extends AttributeModifierType<Boolean> {

	public BooleanAttributeModifierType() {
		super(AttributeValues.Boolean);
	}

	@Override
	public Boolean modifyValue(AttributeModifierInstance<Boolean> modifier, Boolean value) {
		return value || modifier.getModifierValue();
	}
}
