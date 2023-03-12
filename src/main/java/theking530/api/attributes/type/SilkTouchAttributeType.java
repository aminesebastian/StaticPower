package theking530.api.attributes.type;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.AttributeValues;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;

public class SilkTouchAttributeType extends AttributeType<Boolean> {

	public SilkTouchAttributeType() {
		super("attribute.staticpower.silk_touch", ChatFormatting.WHITE, AttributeValues.Boolean);
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, AttributeInstance<Boolean> instance, AttributeModifierInstance<Boolean> modifier) {
		return !instance.getValue();
	}

	@Override
	public boolean isActive(AttributeInstance<Boolean> instance) {
		return instance.getValue();
	}

	@Override
	public MutableComponent getDifferenceLabel(AttributeInstance<Boolean> first, AttributeInstance<Boolean> second) {
		if (first.getValue() == second.getValue()) {
			return null;
		}
		return super.getAttributeTitle(first, false);
	}
}
