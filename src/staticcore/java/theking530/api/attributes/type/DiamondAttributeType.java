package theking530.api.attributes.type;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;

public class DiamondAttributeType extends AbstractHardenedAttributeType {

	public DiamondAttributeType() {
		super("attribute.staticpower.hardened_diamond", ChatFormatting.AQUA);
	}

	@Override
	public int applyHardening(AttributeInstance<Boolean> instance, int value) {
		if (!instance.getValue()) {
			return value;
		}

		// Get the modifier amount.
		double modifier = StaticPowerConfig.getTier(StaticPowerTiers.DIAMOND).toolConfiguration.hardenedDurabilityBoost.get();

		// Apply the modification depending on whether or not this is an additive
		// durability boost.
		if (StaticPowerConfig.getTier(StaticPowerTiers.DIAMOND).toolConfiguration.hardenedDurabilityBoostAdditive.get()) {
			value += modifier;
		} else {
			value *= modifier;
		}

		// Return the modified value.
		return value;
	}

}
