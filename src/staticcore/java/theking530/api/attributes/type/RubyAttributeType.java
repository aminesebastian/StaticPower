package theking530.api.attributes.type;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;

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
		double modifier = StaticPowerConfig.getTier(StaticPowerTiers.RUBY).toolConfiguration.hardenedDurabilityBoost.get();

		// Apply the modification depending on whether or not this is an additive
		// durability boost.
		if (StaticPowerConfig.getTier(StaticPowerTiers.RUBY).toolConfiguration.hardenedDurabilityBoostAdditive.get()) {
			value += modifier;
		} else {
			value *= modifier;
		}

		// Return the modified value.
		return value;
	}
}
