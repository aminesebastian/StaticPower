package theking530.staticcore.attribiutes.types;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.attribiutes.AttributeValues;

public abstract class AbstractHardenedAttributeType extends AttributeType<Boolean> {

	public AbstractHardenedAttributeType(String unlocalizedName, ChatFormatting color) {
		super(unlocalizedName, color, AttributeValues.Boolean);
	}

	public abstract int applyHardening(AttributeInstance<Boolean> instance, int baseValue);

	@Override
	public boolean canApplyToAttributable(IAttributable attributable) {
		// Check to make sure we don't have any of the other hardening types.
		for (AttributeType<?> attribId : attributable.getAllAttributes()) {
			if (attributable.getAttribute(attribId).getAttribute() instanceof AbstractHardenedAttributeType) {
				if (AttributeUtilities.safeCheckAttributeValue(attributable, attribId, true)) {
					return false;
				}
			}
		}

		// If the above checks passed, return true.
		return true;
	}

	@Override
	public boolean isActive(AttributeInstance<Boolean> instance) {
		return instance.getValue();
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, AttributeInstance<Boolean> instance, AttributeModifierInstance<Boolean> modifier) {
		return !instance.getValue();
	}

	@Override
	public MutableComponent getDifferenceLabel(AttributeInstance<Boolean> first, AttributeInstance<Boolean> second) {
		if (first.getValue() == second.getValue()) {
			return null;
		}
		return super.getAttributeTitle(first, false);
	}
}
