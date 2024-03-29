package theking530.staticpower.attributes;

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
		double modifier = 500; // StaticCoreConfig.getTier(StaticCoreTiers.SAPPHIRE).toolConfiguration.hardenedDurabilityBoost.get();
		value += modifier;

		// Return the modified value.
		return value;
	}
}
