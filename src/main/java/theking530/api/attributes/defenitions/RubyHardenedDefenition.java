package theking530.api.attributes.defenitions;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import theking530.api.attributes.registration.AttributeRegistration;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;

@AttributeRegistration("staticpower:hardened_ruby")
public class RubyHardenedDefenition extends AbstractHardenedDefenition {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "hardened_ruby");

	public RubyHardenedDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.hardened_ruby", TextFormatting.RED);
	}

	@Override
	public int applyHardening(int value) {
		if (!getValue()) {
			return value;
		}

		// Get the modifier amount.
		double modifier = StaticPowerConfig.getTier(StaticPowerTiers.RUBY).hardenedDurabilityBoost.get();

		// Apply the modification depending on whether or not this is an additive
		// durability boost.
		if (StaticPowerConfig.getTier(StaticPowerTiers.RUBY).hardenedDurabilityBoostAdditive.get()) {
			value += modifier;
		} else {
			value *= modifier;
		}

		// Return the modified value.
		return value;
	}
}
