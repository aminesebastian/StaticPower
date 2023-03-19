package theking530.staticcore.attribiutes.types;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;

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
		double modifier = 1.5f; // StaticPowerConfig.getTier(StaticPowerTiers.DIAMOND).toolConfiguration.hardenedDurabilityBoost.get();
		value *= modifier;

		// Return the modified value.
		return value;
	}

}
