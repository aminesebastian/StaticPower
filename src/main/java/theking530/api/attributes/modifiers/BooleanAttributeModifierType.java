package theking530.api.attributes.modifiers;

import theking530.api.attributes.AttributeValues;

public class BooleanAttributeModifierType extends AttributeModifierType<Boolean> {

	public BooleanAttributeModifierType() {
		super(AttributeValues.Boolean);
	}

	@Override
	public Boolean modifyValue(AttributeModifierInstance<Boolean> modifier, Boolean value) {
		return value || modifier.getModifierValue();
	}
}
