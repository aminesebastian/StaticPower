package theking530.staticcore.attribiutes.types;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;

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
		double modifier = 500; // StaticPowerConfig.getTier(StaticPowerTiers.SAPPHIRE).toolConfiguration.hardenedDurabilityBoost.get();
		value += modifier;

		// Return the modified value.
		return value;
	}
}
