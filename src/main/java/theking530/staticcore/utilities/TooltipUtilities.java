package theking530.staticcore.utilities;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TooltipUtilities {

	public static void addSingleValueTooltip(String unlocalizedLabel, String value, List<Component> tooltip) {
		tooltip.add(new TranslatableComponent(unlocalizedLabel).append(" " + value));
	}

	public static void addFormatedTooltip(String unlocalizedLabel, List<Component> tooltip, Object... values) {
		TranslatableComponent rateFormat = new TranslatableComponent(unlocalizedLabel, values);
		String translatedRateformat = rateFormat.getString();
		tooltip.add(new TextComponent(String.format(translatedRateformat, values)));
	}
}
