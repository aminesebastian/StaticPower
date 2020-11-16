package theking530.api.attributes.defenitions;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import theking530.api.attributes.registration.AttributeRegistration;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;

@AttributeRegistration("staticpower:hardened_sapphire")
public class SapphireHardenedDefenition extends AbstractHardenedDefenition {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "hardened_sapphire");

	public SapphireHardenedDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.hardened_sapphire", TextFormatting.DARK_BLUE);
	}

	@Override
	public int applyHardening(int value) {
		if (!getValue()) {
			return value;
		}

		// Get the modifier amount.
		double modifier = StaticPowerConfig.getTier(StaticPowerTiers.SAPPHIRE).hardenedDurabilityBoost.get();

		// Apply the modification depending on whether or not this is an additive
		// durability boost.
		if (StaticPowerConfig.getTier(StaticPowerTiers.SAPPHIRE).hardenedDurabilityBoostAdditive.get()) {
			value += modifier;
		} else {
			value *= modifier;
		}

		// Return the modified value.
		return value;
	}
}
