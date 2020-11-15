package theking530.api.attributes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;

public class AttributeUtilities {
	public static void addTooltipsForAttribute(ItemStack stack, List<ITextComponent> tooltip, boolean showAdvanced) {
		// Add the tooltips for all attributes.
		stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
			// Allocate the tooltip container.
			List<ITextComponent> attributeTooltips = new ArrayList<ITextComponent>();

			// Add all the attribute tooltips.
			for (ResourceLocation id : attributable.getAllAttributes()) {
				// Only add the tooltip if the attribute requests it.
				AbstractAttributeDefenition<?, ?> attribute = attributable.getAttribute(id);
				if (attribute.isActive()) {
					attributeTooltips.add(attribute.getAttributeTitle(showAdvanced));
					attribute.addAdditionalTooltipValues(attributeTooltips, showAdvanced);
				}
			}

			// Add the header and footer.
			if (attributeTooltips.size() > 0) {
				tooltip.add(new StringTextComponent(""));
				tooltip.addAll(attributeTooltips);
			}
		});
	}

	public static void addTooltipsForAttribute(AbstractAttributeDefenition<?, ?> attribute, List<ITextComponent> tooltip, boolean showAdvanced) {
		// Only add the tooltip if the attribute requests it.
		if (attribute.isActive()) {
			tooltip.add(attribute.getAttributeTitle(showAdvanced));
			attribute.addAdditionalTooltipValues(tooltip, showAdvanced);
		}
	}

	public static boolean safeCheckAttributeValue(IAttributable attributable, ResourceLocation attributeId, Object value) {
		if (!attributable.hasAttribute(attributeId)) {
			return false;
		}

		return attributable.getAttribute(attributeId).getValue() == value;
	}
}
