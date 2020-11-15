package theking530.api.attributes.defenitions;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.BooleanAttributeModifier;
import theking530.api.attributes.registration.AttributeRegistration;

@AttributeRegistration("staticpower:hardened_emerald")
public class EmeraldHardenedDefenition extends AbstractAttributeDefenition<Boolean, BooleanAttributeModifier> {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "hardened_emerald");

	public EmeraldHardenedDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.hardened_emerald", TextFormatting.GREEN, BooleanAttributeModifier.class);
		baseValue = false;
	}

	@Override
	public Boolean getValue() {
		return modifiers.size() > 0;
	}

	@Override
	protected void serializeBaseValue(CompoundNBT nbt) {
		nbt.putBoolean("base_value", baseValue);
	}

	@Override
	protected void deserializeBaseValue(CompoundNBT nbt) {
		baseValue = nbt.getBoolean("base_value");
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, BooleanAttributeModifier modifier) {
		// If we're already enabled, do nothing.
		if (getValue() == true) {
			return false;
		}

		// Check to make sure we don't have any of the other hardening types.
		if (AttributeUtilities.safeCheckAttributeValue(attributable, RubyHardenedDefenition.ID, true)) {
			return false;
		} else if (AttributeUtilities.safeCheckAttributeValue(attributable, SapphireHardenedDefenition.ID, true)) {
			return false;
		} else if (AttributeUtilities.safeCheckAttributeValue(attributable, DiamondHardenedDefenition.ID, true)) {
			return false;
		}

		// If the above checks passed, return true.
		return true;
	}

	@Override
	public boolean isActive() {
		return getValue();
	}

	@Override
	public IFormattableTextComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other) {
		if (other.getValue() == this.getValue()) {
			return null;
		}
		return super.getAttributeTitle(false);
	}
}
