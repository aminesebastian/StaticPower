package theking530.staticcore.gui.text;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.utilities.ComponentBuilder;

public class TooltipUtilities {

	public static void addSingleValueTooltip(String unlocalizedLabel, String value, List<Component> tooltip) {
		tooltip.add(new TranslatableComponent(unlocalizedLabel).append(" " + value));
	}

	public static void addFormatedTooltip(String unlocalizedLabel, List<Component> tooltip, Object... values) {
		TranslatableComponent rateFormat = new TranslatableComponent(unlocalizedLabel, values);
		String translatedRateformat = rateFormat.getString();
		tooltip.add(new TextComponent(String.format(translatedRateformat, values)));
	}

	public static void addSingleLineBullet(List<Component> tooltip, String key, ChatFormatting color) {
		tooltip.add(new ComponentBuilder().append("• ").append(key, color).append(": ").build());
	}

	public static void addSingleLineBullet(List<Component> tooltip, String key, ChatFormatting color, Component value) {
		tooltip.add(new ComponentBuilder().append("• ").append(key, color).append(": ").append(value).build());
	}
}
