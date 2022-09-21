package theking530.api.attributes.defenitions;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import theking530.api.attributes.registration.AttributeRegistration;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;

@AttributeRegistration("staticpower:hardened_emerald")
public class EmeraldHardenedDefenition extends AbstractHardenedDefenition {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "hardened_emerald");

	public EmeraldHardenedDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.hardened_emerald", ChatFormatting.GREEN);
	}

	@Override
	public int applyHardening(int value) {
		if (!getValue()) {
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
