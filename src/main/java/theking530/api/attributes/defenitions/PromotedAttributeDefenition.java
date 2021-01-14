package theking530.api.attributes.defenitions;

import java.util.List;

import net.minecraft.item.ItemTier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.BooleanAttributeModifier;
import theking530.api.attributes.registration.AttributeRegistration;

@AttributeRegistration("staticpower:promoted")
public class PromotedAttributeDefenition extends AbstractAttributeDefenition<Boolean, BooleanAttributeModifier> {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "promoted");

	public PromotedAttributeDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.promoted", TextFormatting.DARK_AQUA, BooleanAttributeModifier.class);
		baseValue = false;
	}

	@Override
	public Boolean getValue() {
		return modifiers.size() > 0;
	}

	@Override
	public boolean isActive() {
		return getValue();
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, BooleanAttributeModifier modifier) {
		// If we're already enabled, do nothing.
		if (getValue() == true) {
			return false;
		}

		// If the above checks passed, return true.
		return true;
	}

	@Override
	public IFormattableTextComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other) {
		if (other.getValue() == this.getValue()) {
			return null;
		}
		return super.getAttributeTitle(false);
	}

	public void addAdditionalTooltipValues(List<ITextComponent> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			tooltip.add(new StringTextComponent(TextFormatting.GRAY + "Mining Tier +1"));
		}
	}

	@Override
	protected void serializeBaseValue(CompoundNBT nbt) {
		nbt.putBoolean("base_value", baseValue);
	}

	@Override
	protected void deserializeBaseValue(CompoundNBT nbt) {
		baseValue = nbt.getBoolean("base_value");
	}

	public ItemTier modifyItemTier(ItemTier baseTier) {
		// If false, do nothing.
		if (!getValue()) {
			return baseTier;
		}

		// Increase the tier by one.
		if (baseTier == ItemTier.WOOD) {
			return ItemTier.STONE;
		} else if (baseTier == ItemTier.STONE) {
			return ItemTier.IRON;
		} else if (baseTier == ItemTier.IRON) {
			return ItemTier.DIAMOND;
		} else if (baseTier == ItemTier.DIAMOND) {
			return ItemTier.NETHERITE;
		} else if (baseTier == ItemTier.GOLD) {
			return ItemTier.STONE;
		} else if (baseTier == ItemTier.NETHERITE) {
			return ItemTier.NETHERITE;
		} else {
			return baseTier;
		}
	}

}
