package theking530.staticcore.attribiutes.types;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;

public class RubyAttributeType extends AbstractHardenedAttributeType {

	public RubyAttributeType() {
		super("attribute.staticpower.hardened_ruby", ChatFormatting.RED);
	}

	@Override
	public int applyHardening(AttributeInstance<Boolean> instance, int value) {
		if (!instance.getValue()) {
			return value;
		}

		// Get the modifier amount.
		double modifier = 1.25f; // StaticPowerConfig.getTier(StaticPowerTiers.RUBY).toolConfiguration.hardenedDurabilityBoost.get();
		value *= modifier;

		// Return the modified value.
		return value;
	}
}
