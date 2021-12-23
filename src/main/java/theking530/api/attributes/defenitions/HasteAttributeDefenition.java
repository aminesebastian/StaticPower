package theking530.api.attributes.defenitions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.FloatAttributeModifier;
import theking530.api.attributes.registration.AttributeRegistration;

@AttributeRegistration("staticpower:haste")
public class HasteAttributeDefenition extends AbstractAttributeDefenition<Integer, FloatAttributeModifier> {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "haste");
	public static final int MAX_VALUE = 300;

	public HasteAttributeDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.haste", ChatFormatting.YELLOW, FloatAttributeModifier.class);
		baseValue = 0;
	}

	public int getHasteLevel() {
		int value = getValue();
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
	public MutableComponent getAttributeTitle(boolean showAdvanced) {
		MutableComponent baseTitle = super.getAttributeTitle(showAdvanced);

		int hasteLevel = getHasteLevel();
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
			return baseTitle.append(" " + hasteTier).append((ChatFormatting.GRAY.toString() + " (" + getValue() + "/" + MAX_VALUE + ")"));
		} else {
			return baseTitle.append(" " + hasteTier);
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
	protected void serializeBaseValue(CompoundTag nbt) {
		nbt.putInt("base_value", baseValue);
	}

	@Override
	protected void deserializeBaseValue(CompoundTag nbt) {
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
	public MutableComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other) {
		if (!(other instanceof HasteAttributeDefenition)) {
			return null;
		}

		HasteAttributeDefenition otherFortune = (HasteAttributeDefenition) other;
		int difference = this.getValue() - otherFortune.getValue();
		if (difference == 0) {
			return null;
		}

		String sign = difference > 0 ? "+" : difference < 0 ? "-" : "";
		String color = difference > 0 ? ChatFormatting.GREEN.toString() : ChatFormatting.RED.toString();
		return super.getAttributeTitle(false).append(" ").append(new TextComponent(color + sign + difference));
	}
}
