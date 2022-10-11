package theking530.api.attributes.defenitions;

import java.awt.TextComponent;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.BooleanAttributeModifier;
import theking530.api.attributes.registration.AttributeRegistration;

@AttributeRegistration("staticpower:promoted")
public class PromotedAttributeDefenition extends AbstractAttributeDefenition<Boolean, BooleanAttributeModifier> {
	public static final ResourceLocation ID = new ResourceLocation("staticpower", "promoted");

	public PromotedAttributeDefenition(ResourceLocation id) {
		super(ID, "attribute.staticpower.promoted", ChatFormatting.DARK_AQUA, BooleanAttributeModifier.class);
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
	public MutableComponent getDifferenceLabel(AbstractAttributeDefenition<?, ?> other) {
		if (other.getValue() == this.getValue()) {
			return null;
		}
		return super.getAttributeTitle(false);
	}

	public void addAdditionalTooltipValues(List<Component> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			tooltip.add(Component.literal(ChatFormatting.GRAY + "Mining Tier +1"));
		}
	}

	@Override
	protected void serializeBaseValue(CompoundTag nbt) {
		nbt.putBoolean("base_value", baseValue);
	}

	@Override
	protected void deserializeBaseValue(CompoundTag nbt) {
		baseValue = nbt.getBoolean("base_value");
	}

	public Tier modifyItemTier(Tier baseTier) {
		// If false, do nothing.
		if (!getValue()) {
			return baseTier;
		}

		// Increase the tier by one.
		if (baseTier == Tiers.WOOD) {
			return Tiers.STONE;
		} else if (baseTier == Tiers.STONE) {
			return Tiers.IRON;
		} else if (baseTier == Tiers.IRON) {
			return Tiers.DIAMOND;
		} else if (baseTier == Tiers.DIAMOND) {
			return Tiers.NETHERITE;
		} else if (baseTier == Tiers.GOLD) {
			return Tiers.STONE;
		} else if (baseTier == Tiers.NETHERITE) {
			return Tiers.NETHERITE;
		} else {
			return baseTier;
		}
	}

}
