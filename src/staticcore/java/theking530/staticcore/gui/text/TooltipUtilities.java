package theking530.staticcore.gui.text;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import theking530.staticcore.utilities.ComponentBuilder;

public class TooltipUtilities {

	public static void addSingleValueTooltip(String unlocalizedLabel, String value, List<Component> tooltip) {
		tooltip.add(Component.translatable(unlocalizedLabel).append(" " + value));
	}

	public static void addFormatedTooltip(String unlocalizedLabel, List<Component> tooltip, Object... values) {
		MutableComponent rateFormat = Component.translatable(unlocalizedLabel, values);
		String translatedRateformat = rateFormat.getString();
		tooltip.add(Component.literal(String.format(translatedRateformat, values)));
	}

	public static void addSingleLineBullet(List<Component> tooltip, String key, ChatFormatting color) {
		tooltip.add(new ComponentBuilder().append("• ").append(key, color).append(": ").build());
	}

	public static void addSingleLineBullet(List<Component> tooltip, String key, ChatFormatting color, Component value) {
		tooltip.add(new ComponentBuilder().append("• ").append(key, color).append(": ").append(value).build());
	}

	public static void addSingleLineBullet(List<Component> tooltip, String key, ChatFormatting color, String value) {
		addSingleLineBullet(tooltip, key, color, Component.literal(value));
	}
}
