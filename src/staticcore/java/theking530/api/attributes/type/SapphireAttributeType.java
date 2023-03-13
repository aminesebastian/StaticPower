package theking530.api.attributes.type;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;

public class SapphireAttributeType extends AbstractHardenedAttributeType {

	public SapphireAttributeType() {
		super("attribute.staticpower.hardened_sapphire", ChatFormatting.DARK_BLUE);
	}

	@Override
	public int applyHardening(AttributeInstance<Boolean> instance, int value) {
		if (!instance.getValue()) {
			return value;
		}

		// Get the modifier amount.
		double modifier = StaticPowerConfig.getTier(StaticPowerTiers.SAPPHIRE).toolConfiguration.hardenedDurabilityBoost.get();

		// Apply the modification depending on whether or not this is an additive
		// durability boost.
		if (StaticPowerConfig.getTier(StaticPowerTiers.SAPPHIRE).toolConfiguration.hardenedDurabilityBoostAdditive.get()) {
			value += modifier;
		} else {
			value *= modifier;
		}

		// Return the modified value.
		return value;
	}
}
