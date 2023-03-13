package theking530.api.attributes.type;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;

public class EmeraldAttributeType extends AbstractHardenedAttributeType {

	public EmeraldAttributeType() {
		super("attribute.staticpower.hardened_emerald", ChatFormatting.GREEN);
	}

	@Override
	public int applyHardening(AttributeInstance<Boolean> instance, int value) {
		if (!instance.getValue()) {
			return value;
		}

		// Get the modifier amount.
		double modifier = StaticPowerConfig.getTier(StaticPowerTiers.EMERALD).toolConfiguration.hardenedDurabilityBoost.get();

		// Apply the modification depending on whether or not this is an additive
		// durability boost.
		if (StaticPowerConfig.getTier(StaticPowerTiers.EMERALD).toolConfiguration.hardenedDurabilityBoostAdditive.get()) {
			value += modifier;
		} else {
			value *= modifier;
		}

		// Return the modified value.
		return value;
	}
}
