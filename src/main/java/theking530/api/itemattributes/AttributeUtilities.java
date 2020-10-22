package theking530.api.itemattributes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import theking530.api.itemattributes.attributes.AbstractAttributeDefenition;
import theking530.api.itemattributes.capability.CapabilityAttributable;

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
				if (attribute.shouldDisplayOnTooltip()) {
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
		if (attribute.shouldDisplayOnTooltip()) {
			tooltip.add(attribute.getAttributeTitle(showAdvanced));
			attribute.addAdditionalTooltipValues(tooltip, showAdvanced);
		}
	}
}
