package theking530.staticcore.attribiutes.types;

import net.minecraft.ChatFormatting;
import theking530.api.attributes.AttributeInstance;

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
		double modifier = 1000;
		value += modifier;

		// Return the modified value.
		return value;
	}
}
