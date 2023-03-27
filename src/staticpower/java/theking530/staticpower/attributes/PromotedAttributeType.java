package theking530.staticpower.attributes;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierInstance;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.attributes.AttributeValues;

public class PromotedAttributeType extends AttributeType<Boolean> {

	public PromotedAttributeType() {
		super("attribute.staticpower.promoted", ChatFormatting.DARK_AQUA, AttributeValues.Boolean);
	}

	@Override
	public boolean canAcceptModifier(IAttributable attributable, AttributeInstance<Boolean> instance, AttributeModifierInstance<Boolean> modifier) {
		return !instance.getValue();
	}

	@Override
	public boolean isActive(AttributeInstance<Boolean> instance) {
		return instance.getValue();
	}

	@Override
	public MutableComponent getDifferenceLabel(AttributeInstance<Boolean> first, AttributeInstance<Boolean> second) {
		if (first.getValue() == second.getValue()) {
			return null;
		}
		return super.getAttributeTitle(first, false);
	}

	@Override
	public void addAdditionalTooltipValues(AttributeInstance<Boolean> instance, List<Component> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			tooltip.add(Component.literal(ChatFormatting.GRAY + "Mining Tier +1"));
		}
	}

	public Tier modifyItemTier(AttributeInstance<Boolean> instance, Tier baseTier) {
		// If false, do nothing.
		if (!instance.getValue()) {
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
