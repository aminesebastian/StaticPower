package theking530.staticpower.attributes;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.attributes.AttributeValues;
import theking530.staticcore.utilities.math.SDMath;

public class FortuneAttributeType extends AttributeType<Number> {
	public static final int MAX_VALUE = 300;

	public FortuneAttributeType() {
		super("attribute.staticpower.fortune", ChatFormatting.BLUE, AttributeValues.Number);
	}

	public int getFortuneLevel(AttributeInstance<Number> instance) {
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

	public int getFortuneLevelWithChance(AttributeInstance<Number> instance) {
		int minLevel = instance.getValue().intValue() / MAX_VALUE;
		int potentialRandomValue = (int) Math.ceil(instance.getValue().doubleValue() / MAX_VALUE);
		return SDMath.getRandomIntInRange(minLevel, potentialRandomValue);
	}

	@Override
	public MutableComponent getAttributeTitle(AttributeInstance<Number> instance, boolean showAdvanced) {
		MutableComponent baseTitle = super.getAttributeTitle(instance, showAdvanced);

		int fortuneLevel = getFortuneLevel(instance);
		String fortuneTier = "";

		if (fortuneLevel == 1) {
			fortuneTier = "I";
		} else if (fortuneLevel == 2) {
			fortuneTier = "II";
		} else if (fortuneLevel == 3) {
			fortuneTier = "III";
		} else if (fortuneLevel == 4) {
			fortuneTier = "IV";
		}

		if (showAdvanced) {
			return baseTitle.append(" " + fortuneTier).append((ChatFormatting.GRAY.toString() + " (" + instance.getValue() + "/" + MAX_VALUE + ")"));
		} else {
			return baseTitle.append(" " + fortuneTier);
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
		if (!(other.getAttribute() instanceof FortuneAttributeType)) {
			return null;
		}

		int difference = other.getValue().intValue() - instance.getValue().intValue();
		if (difference == 0) {
			return null;
		}

		String sign = difference > 0 ? "+" : difference < 0 ? "-" : "";
		String color = difference > 0 ? ChatFormatting.GREEN.toString() : ChatFormatting.RED.toString();
		return super.getAttributeTitle(instance, false).append(" ").append(Component.literal(color + sign + difference));
	}

}
