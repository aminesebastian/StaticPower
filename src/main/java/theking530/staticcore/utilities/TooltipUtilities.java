package theking530.staticcore.utilities;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TooltipUtilities {

	public static void addSingleValueTooltip(String unlocalizedLabel, String value, List<ITextComponent> tooltip) {
		tooltip.add(new TranslationTextComponent(unlocalizedLabel).appendString(" " + value));
	}

	public static void addFormatedTooltip(String unlocalizedLabel, List<ITextComponent> tooltip, Object... values) {
		TranslationTextComponent rateFormat = new TranslationTextComponent(unlocalizedLabel, values);
		String translatedRateformat = rateFormat.getString();
		tooltip.add(new StringTextComponent(String.format(translatedRateformat, values)));
	}
}
