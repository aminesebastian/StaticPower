package theking530.staticpower.attributes;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;
import theking530.staticcore.StaticCoreConfig;
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
		double modifier = StaticCoreConfig.getTier(StaticPowerTiers.DIAMOND).toolConfiguration.hardenedDurabilityBoost
				.get();
		value *= modifier;

		// Return the modified value.
		return value;
	}

}
