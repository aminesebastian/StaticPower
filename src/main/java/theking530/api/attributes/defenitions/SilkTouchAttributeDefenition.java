package theking530.api.attributes.defenitions;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.BooleanAttributeModifier;
import theking530.api.attributes.registration.AttributeRegistration;

@AttributeRegistration("staticpower:silk_touch")
public class SilkTouchAttributeDefenition extends AbstractAttributeDefenition<Boolean, BooleanAttributeModifier> {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "silk_touch");

	public SilkTouchAttributeDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.silk_touch", ChatFormatting.WHITE, BooleanAttributeModifier.class);
		baseValue = false;
	}

	@Override
	public Boolean getValue() {
		return modifiers.size() > 0;
	}

	@Override
	protected void serializeBaseValue(CompoundTag nbt) {
		nbt.putBoolean("base_value", baseValue);
	}

	@Override
	protected void deserializeBaseValue(CompoundTag nbt) {
		baseValue = nbt.getBoolean("base_value");
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, BooleanAttributeModifier modifier) {
		// If we already have the grinding modifier, dont do anything.
		return !getValue();
	}

	@Override
	public boolean isActive() {
		return getValue();
	}

	@Override
	public MutableComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other) {
		if (other.getValue() == this.getValue()) {
			return null;
		}
		return super.getAttributeTitle(false);
	}
}
