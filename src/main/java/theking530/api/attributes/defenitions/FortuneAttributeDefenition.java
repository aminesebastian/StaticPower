package theking530.api.attributes.defenitions;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.FloatAttributeModifier;
import theking530.api.attributes.registration.AttributeRegistration;
import theking530.staticcore.utilities.SDMath;

@AttributeRegistration("staticpower:fortune")
public class FortuneAttributeDefenition extends AbstractAttributeDefenition<Integer, FloatAttributeModifier> {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "fortune");
	public static final int MAX_VALUE = 300;

	public FortuneAttributeDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.fortune", TextFormatting.BLUE, FloatAttributeModifier.class);
		baseValue = 0;
	}

	public int getFortuneLevel() {
		if (getValue() < 100) {
			return 1;
		} else if (getValue() < 200) {
			return 2;
		} else if (getValue() >= 300) {
			return 3;
		} else {
			return 0;
		}
	}

	public int getFortuneLevelWithChance() {
		int minLevel = getValue() / MAX_VALUE;
		int potentialRandomValue = (int) Math.ceil(getValue() / MAX_VALUE);
		return SDMath.getRandomIntInRange(minLevel, potentialRandomValue);
	}

	@Override
	public IFormattableTextComponent getAttributeTitle(boolean showAdvanced) {
		IFormattableTextComponent baseTitle = super.getAttributeTitle(showAdvanced);

		int fortuneLevel = getFortuneLevel();
		String fortuneTier = "";

		if (fortuneLevel == 1) {
			fortuneTier = "I";
		} else if (fortuneLevel == 2) {
			fortuneTier = "II";
		} else if (fortuneLevel == 3) {
			fortuneTier = "III";
		}

		if (showAdvanced) {
			return baseTitle.appendString(" " + fortuneTier).appendString((TextFormatting.GRAY.toString() + " (" + getValue() + "/" + MAX_VALUE + ")"));
		} else {
			return baseTitle.appendString(" " + fortuneTier);
		}
	}

	@Override
	public Integer getValue() {
		// Allocate the base value.
		int output = baseValue;

		// Apply the modifiers.
		for (FloatAttributeModifier modifier : modifiers) {
			if (modifier.isAdditive) {
				output += modifier.getValue();
			} else {
				output *= modifier.getValue();
			}
		}

		// Return the output.
		return Math.min(output, MAX_VALUE);
	}

	@Override
	protected void serializeBaseValue(CompoundNBT nbt) {
		nbt.putInt("base_value", baseValue);
	}

	@Override
	protected void deserializeBaseValue(CompoundNBT nbt) {
		baseValue = nbt.getInt("base_value");
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, FloatAttributeModifier modifier) {
		// If we're already at the max, we can't take another modifier.
		if (getValue() >= MAX_VALUE) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isActive() {
		return getValue() > 0;
	}

	@Override
	public IFormattableTextComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other) {
		if (!(other instanceof FortuneAttributeDefenition)) {
			return null;
		}

		FortuneAttributeDefenition otherFortune = (FortuneAttributeDefenition) other;
		int difference = this.getValue() - otherFortune.getValue();
		if (difference == 0) {
			return null;
		}

		String sign = difference > 0 ? "+" : difference < 0 ? "-" : "";
		String color = difference > 0 ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
		return super.getAttributeTitle(false).appendString(" ").append(new StringTextComponent(color + sign + difference));
	}
}
