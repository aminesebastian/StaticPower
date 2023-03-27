package theking530.staticcore.attribiutes.types;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.data.StaticCoreTiers;

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
		double modifier = StaticCoreConfig.getTier(StaticCoreTiers.DIAMOND).toolConfiguration.hardenedDurabilityBoost.get();
		value *= modifier;

		// Return the modified value.
		return value;
	}

}
