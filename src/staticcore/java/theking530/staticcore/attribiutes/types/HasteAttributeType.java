package theking530.staticcore.attribiutes.types;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.attribiutes.AttributeValues;

public class HasteAttributeType extends AttributeType<Number> {
	public static final int MAX_VALUE = 300;

	public HasteAttributeType() {
		super("attribute.staticpower.haste", ChatFormatting.YELLOW, AttributeValues.Number);
	}

	public int getHasteLevel(AttributeInstance<Number> instance) {
		int value = instance.getValue().intValue();
		if (value < 100) {
			return 1;
		} else if (value > 100 && value < 200) {
			return 2;
		} else if (value >= 200) {
			return 3;
		} else if (value >= 300) {
			return 4;
		} else {
			return 0;
		}
	}

	@Override
	public MutableComponent getAttributeTitle(AttributeInstance<Number> instance, boolean showAdvanced) {
		MutableComponent baseTitle = super.getAttributeTitle(instance, showAdvanced);

		int hasteLevel = getHasteLevel(instance);
		String hasteTier = "";

		if (hasteLevel == 1) {
			hasteTier = "I";
		} else if (hasteLevel == 2) {
			hasteTier = "II";
		} else if (hasteLevel == 3) {
			hasteTier = "III";
		} else if (hasteLevel == 4) {
			hasteTier = "IV";
		}

		if (showAdvanced) {
			return baseTitle.append(" " + hasteTier).append((ChatFormatting.GRAY.toString() + " (" + instance.getValue().intValue() + "/" + MAX_VALUE + ")"));
		} else {
			return baseTitle.append(" " + hasteTier);
		}
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, AttributeInstance<Number> instance, AttributeModifierInstance<Number> modifier) {
		// If we're already at the max, we can't take another modifier.
		if (instance.getValue().intValue() >= MAX_VALUE) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isActive(AttributeInstance<Number> instance) {
		return instance.getValue().intValue() > 0;
	}

	@Override
	public MutableComponent getDifferenceLabel(AttributeInstance<Number> instance, AttributeInstance<Number> other) {
		int difference = instance.getValue().intValue() - other.getValue().intValue();
		if (difference == 0) {
			return null;
		}

		String sign = difference > 0 ? "+" : difference < 0 ? "-" : "";
		String color = difference > 0 ? ChatFormatting.GREEN.toString() : ChatFormatting.RED.toString();
		return super.getAttributeTitle(instance, false).append(" ").append(Component.literal(color + sign + difference));
	}
}
