package theking530.api.itemattributes.attributes;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.itemattributes.attributes.modifiers.BooleanAttributeModifier;

public class GrindingAttributeDefenition extends AbstractAttributeDefenition<Boolean, BooleanAttributeModifier> {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "grinding");

	public GrindingAttributeDefenition(ResourceLocation id) {
		super(ID, "gui.staticpower.grinding", TextFormatting.GRAY, BooleanAttributeModifier.class);
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
	public boolean canAcceptModifier(BooleanAttributeModifier modifier) {
		// If we already have the grinding modifier, dont do anything.
		return !getValue();
	}

	@Override
	public boolean shouldDisplayOnTooltip() {
		return getValue();
	}

	@Override
	public IFormattableTextComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other) {
		return super.getAttributeTitle(false);
	}
}
